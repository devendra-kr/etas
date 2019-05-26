package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import mysql.DBConnection
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import models.CabClient
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import models.Cab
import models.Utilities._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CabController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCabs = Action.async { implicit request =>
    dbc.getAllCabs().map { cab =>
      val res = cab.map(c => CabClient(c.id, c.registrationNumber, c.driverId, setStatus(c.cabStatus), c.comments, c.vacancy))
      Ok(Json.toJson(res))
    }
  }

  val setStatus = (x: Boolean) => if (x) "AVAILABLE" else "UNAVAILABLE"
    
  val setStatusBool = (x: String) => x.toUpperCase.equals("AVAILABLE")

  def getCab(id: Long) = Action.async { implicit request =>
    dbc.getCabById(id).map { cab =>
      val res = cab.map(c => CabClient(c.id, c.registrationNumber, c.driverId, setStatus(c.cabStatus), c.comments, c.vacancy))
      Ok(Json.toJson(res))
    }
  }

  def reads(json: JsValue): JsResult[CabClient] = {
    val id = (json \ "cabId").as[Long]
    val registrationNumber = (json \ "registrationNumber").as[String]
    val driverId = (json \ "driverId").as[Long]
    val cabStatus = (json \ "cabStatus").as[String]
    val comments = (json \ "comments").as[String]
    val varancy = (json \ "varancy").as[Int]
    JsSuccess(CabClient(id, registrationNumber, driverId, cabStatus, Some(comments), varancy))
  }
  
  def saveCab = Action { request =>
    val json = request.body.asJson.get
    val cab = json.as[CabClient]
    val newCab = Cab(cab.cabId, cab.registrationNumber, cab.driverId, setStatusBool(cab.cabStatus), cab.comments, cab.vacancy)
    dbc.insertCab(newCab)
    Ok
  }
  
  /*curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{"cabId": -1,"registrationNumber": "XYZ_123_ABC","driverId": 3,"cabStatus": "AVAILABLE","comments": "","vacancy": 4}' \
    http://localhost:9000/cabs
    * 
		*/
  
  def updateCab = Action { request =>
    val json = request.body.asJson.get
    val cab = json.as[CabClient]
    val newCab = Cab(cab.cabId, cab.registrationNumber, cab.driverId, setStatusBool(cab.cabStatus), cab.comments, cab.vacancy)
    dbc.updateCab(newCab)
    Ok
  }
  
  /*curl \
    --header "Content-type: application/json" \
    --request PUT \
    --data '{"cabId": 1,"registrationNumber": "XYZ_123_ABCDE","driverId": 3,"cabStatus": "UNAVAILABLE","comments": "Updated","varancy": 4}' \
    http://localhost:9000/cabs
    * 
		*/
  
  def updateCabStatusAsActive(id: Long) = updateCabStatus(id, true)

  def updateCabStatusAsInActive(id: Long) = {
    updateCabStatus(id, false)
    val location = toSimpleOptionForSeq(executeSynchronous(dbc.getLocationByCabId(id), "")).map(_.location).headOption
    if(location.isDefined)
      allocateAnotherCab(location.get)
  }
  
  private def updateCabStatus(id: Long, status: Boolean) = Action { request =>
    dbc.updateCab(id, status)
    Ok
  }
  
  def allocateAnotherCab(sourceLocation: String) = {
    val cabInfo = toSimpleOptionForSeq(executeSynchronous(dbc.getAvailableCab(sourceLocation), ""))
    if(!cabInfo.isEmpty){
      val cab = cabInfo.head
      dbc.updateBooking(cab.id, cab.registrationNumber, cab.driverId)
    }
  }
   
   /* curl -X PUT "http://localhost:9000/cabs/2/unavailable" */
  
   def deleteCab(id: Long) = Action.async { implicit request =>
    dbc.deleteCab(id).map { cab => Ok(Json.toJson(cab))
    }
  }
  
  /* curl -X DELETE "http://localhost:9000/cabs/2" */
}
