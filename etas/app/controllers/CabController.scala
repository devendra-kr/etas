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
import org.slf4j.LoggerFactory

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CabController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  val Log = LoggerFactory getLogger getClass

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
    try {
      dbc.insertCab(newCab)
      Ok("Cab Details Inserted Successfully.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Log info "unique key constraint violation"
        Ok("Registration_number already exist into DB or DriverId not the Employee")
    }
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
    try {
      dbc.updateCab(newCab)
      Ok("Cab Details Updated Successfully.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Log info "unique key constraint violation"
        Ok("Registration_number already exist into DB or DriverId not the Employee")
    }
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
    val location = toSimpleOptionForSeq(executeSynchronous(dbc.getLocationByCabId(id), "")).map(_.name).headOption
    if (location.isDefined)
      allocateAnotherCab(id, location.get)
    updateCabStatus(id, false)
  }

  private def updateCabStatus(id: Long, status: Boolean) = Action { request =>
    dbc.updateCab(id, status)
    Ok("Cab Status Changed")
  }

  def allocateAnotherCab(canceledCabId: Long, sourceLocation: String) = {
    val cabInfo = toSimpleOptionForSeq(executeSynchronous(dbc.getAvailableCab(sourceLocation), ""))
    val canceledCabInfo = toSimpleOptionForSeq(executeSynchronous(dbc.getCabById(canceledCabId), ""))
    if (!cabInfo.isEmpty && !canceledCabInfo.isEmpty && canceledCabInfo.head.cabStatus) {
      val cab = cabInfo.head
      val oldCabInfo = canceledCabInfo.head
      dbc.updateBooking(oldCabInfo.registrationNumber, oldCabInfo.driverId, cab.registrationNumber, cab.driverId)
      dbc.updateCabVacancy(cab.id, cab.vacancy - 4 + oldCabInfo.vacancy, if (cab.vacancy == 1) false else true)
      if ((oldCabInfo.vacancy + -4 + cab.vacancy) < 0) dbc.updateBookingStatus(oldCabInfo.registrationNumber, oldCabInfo.driverId, -(oldCabInfo.vacancy + -4 + cab.vacancy), false)
    }
  }

  /* curl -X PUT "http://localhost:9000/cabs/2/unavailable" */

  def deleteCab(id: Long) = Action.async { implicit request =>
    dbc.deleteCab(id).map { cab => Ok(Json.toJson(cab))
    }
  }

  /* curl -X DELETE "http://localhost:9000/cabs/2" */
}
