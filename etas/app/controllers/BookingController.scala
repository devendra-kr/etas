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
import models.TimeUtils
import org.slf4j.LoggerFactory
import models.Utilities._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.RequestSuccessRes

@Singleton
class BookingController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  val Log = LoggerFactory getLogger getClass

  def reads(json: JsValue): JsResult[BookingRequest] = {
    val sourceLocation = (json \ "sourceLocation").as[String]
    val dateTimeOfJourney = (json \ "dateTimeOfJourney").as[Long]
    val employeeId = (json \ "employeeId").as[Long]
    JsSuccess(BookingRequest(sourceLocation, dateTimeOfJourney, employeeId))
  }

  def requestBooking() = Action { request =>
    val json = request.body.asJson.get
    val res = json.as[BookingRequest]
    val status = executeSynchronous(dbc.getAvailableCab, "").isDefined
    val bookingId = if(status) {
      val bookRes = Booking(-1L, res.sourceLocation, new Timestamp(res.dateTimeOfJourney), res.employeeId, status)
      val id = Await.result(dbc.insertBooking(bookRes), Duration.Inf).id
      Some(id)
    } else None
    val userRequest = UserRequest(-1L, "", Some(""), bookingId, new Timestamp(System.currentTimeMillis()), res.employeeId)
    val resId = dbc.insertRequest(userRequest).map(x => RequestSuccessRes(x.id))
    Ok(Json.toJson(executeSynchronous(resId, "").getOrElse(RequestSuccessRes(-1L))))
  }
  
  def bookingConstraints(doj: Long, source: String, doc: Option[Long]) = {
    val upperTimeBound = System.currentTimeMillis + 2 * 24 * 60 * 60L
    val lowerTimeBound = System.currentTimeMillis + 12 * 60 * 60L
    val cancellationTimeBound = 3 * 60 * 60L
    doj match {
      case d if(d > upperTimeBound || d < lowerTimeBound) => "REQUEST_NOT_POSSIBLE"
      case d if(!TimeUtils.isValidTripTime(d)) => "INVALID_TRIP_TIME"
    }
  }
  
  def getRequest(id: Long) = Action.async { implicit request =>
    dbc.getRequestById(id).map { req =>
      val sourceLocation = ""
      val dateTimeOfJourney = 1L
      val res = req.map(r => RequestClient(r.id, r.status, r.comments, r.bookingId, sourceLocation, dateTimeOfJourney, r.creationDate.getTime, r.requestGenerator))
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