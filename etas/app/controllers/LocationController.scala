package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.ControllerComponents
import mysql.DBConnection
import scala.concurrent.ExecutionContext
import play.api.mvc.AbstractController
import play.api.libs.json.Json

import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess

import org.slf4j.LoggerFactory
import models.Location

@Singleton
class LocationController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  val Log = LoggerFactory getLogger getClass

  def getLocation = Action.async {
    Log info "get location"
    dbc.getLocation.map { loc =>
      Ok(Json.toJson(loc))
    }
  }

  def reads(json: JsValue): JsResult[Location] = {
    val id = (json \ "id").as[Long]
    val cabId = (json \ "cabId").as[Long]
    val name = (json \ "name").as[String]
    JsSuccess(Location(id, cabId, name))
  }

  def saveLocation() = Action { request =>
    val json = request.body.asJson.get
    val location = json.as[Location]
    try {
      dbc.insertLocation(location)
      Ok("Location Details Inserted Successfully.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Log info "Forien unique key constraint violation"
        Ok("CabID not exist the Cab table.")
    }
  }

  def updateLocation = Action { request =>
    val json = request.body.asJson.get
    val loc = json.as[Location]
    try {
      dbc.updateLocation(loc)
      Ok("Loction Details Updated Successfully.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Log info "Forien unique key constraint violation"
        Ok("CabID not exist the Cab table.")
    }
  }
}