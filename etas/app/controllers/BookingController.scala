package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.ControllerComponents
import mysql.DBConnection
import scala.concurrent.ExecutionContext
import play.api.mvc.AbstractController
import play.api.libs.json.Json
import models.BookingClient
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import models.BookingRequest
import models.UserRequest
import models.Booking
import java.sql.Timestamp
import models.RequestClient

@Singleton
class BookingController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  //val Log = LoggerFactory getLogger getClass

  def reads(json: JsValue): JsResult[BookingRequest] = {
    val sourceLocation = (json \ "sourceLocation").as[String]
    val dateTimeOfJourney = (json \ "dateTimeOfJourney").as[Long]
    val employeeId = (json \ "employeeId").as[Long]
    JsSuccess(BookingRequest(sourceLocation, dateTimeOfJourney, employeeId))
  }

  def requestBooking() = Action { request =>
    val json = request.body.asJson.get
    val res = json.as[BookingRequest]
    val status = true
    val bookRes = Booking(-1L, res.sourceLocation, new Timestamp(res.dateTimeOfJourney), res.employeeId, status)
    val bookingId = Some(1L) //dbc.booking(bookRes)
    val userRequest = UserRequest(-1L, "", Some(""), bookingId, new Timestamp(System.currentTimeMillis()), res.employeeId)
    dbc.insertRequest(userRequest)
    Ok
  }
  
  def getRequest(id: Long) = Action.async { implicit request =>
    dbc.getRequestById(id).map { req =>
      val bookingId = 1L
      val sourceLocation = ""
      val dateTimeOfJourney = 1L
      val res = req.map(r => RequestClient(r.id, r.status, r.comments, bookingId, sourceLocation, dateTimeOfJourney, r.creationDate.getTime, r.requestGenerator))
      Ok(Json.toJson(res))
    }
  }
  
  def getBooking(id: Long) = Action.async { implicit request =>
    dbc.getBookingById(id).map { b =>
      val res = b.map(x => BookingClient(x.id, x.sourceLocation, x.dateTimeOfJourney.getTime, x.employeeId))
      Ok(Json.toJson(res))
    }
  }

  
  
}