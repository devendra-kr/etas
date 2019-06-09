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
import models.RequestErrorRes

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
    val bookingConstraint = bookingConstraints(res.dateTimeOfJourney, res.sourceLocation, None)
    val bookingRes = if(bookingConstraint.isEmpty()) {
      val cabInfo = toSimpleOptionForSeq(executeSynchronous(dbc.getAvailableCab(res.sourceLocation), ""))
      if(!cabInfo.isEmpty){
        val cab = cabInfo.head
        val bookRes = Booking(-1L, res.sourceLocation, new Timestamp(res.dateTimeOfJourney), res.employeeId, true, cab.registrationNumber, cab.driverId)
        val bookingId = Await.result(dbc.insertBooking(bookRes), Duration.Inf).id
        dbc.updateCabVacancy(cab.id, cab.vacancy - 1)
        ("", Some(bookingId))
      } else ("CAB_NOT_AVAILBLE", None)
    } else (bookingConstraint, None)
    val userRequest = UserRequest(-1L, if(bookingRes._2.isDefined) "GENERATED" else "FAILED", Some(""), bookingRes._2, res.sourceLocation, new Timestamp(res.dateTimeOfJourney), new Timestamp(System.currentTimeMillis()), res.employeeId)
    val resId = dbc.insertRequest(userRequest).map(x => RequestSuccessRes(x.id))
    if(bookingRes._2.isDefined) Ok(Json.toJson(executeSynchronous(resId, "").getOrElse(RequestSuccessRes(-1L))))
    else Ok(Json.toJson(RequestErrorRes(bookingRes._1)))
  }
      
  /*curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{"sourceLocation": "Chanda Nagar", "dateTimeOfJourney": 1558883264000, "employeeId": 5}' \
    http://localhost:9000/request
    * `
    */
  
  def bookingConstraints(doj: Long, source: String, doc: Option[Long]) = {
    val upperTimeBound = System.currentTimeMillis + 5 * 24 * 60 * 60 * 1000L
    val lowerTimeBound = System.currentTimeMillis + 12 * 60 * 60 * 1000L
    val cancellationTimeBound = 3 * 60 * 60L
    (doj, source) match {
//      case d if(d._1 > upperTimeBound || d._1 < lowerTimeBound) => "REQUEST_NOT_POSSIBLE"
      case d if(!TimeUtils.isValidTripTime(d._1)) => "INVALID_TRIP_TIME"
      case d if(toSimpleOptionForSeq(executeSynchronous(dbc.getLocationByName(d._2), "")).isEmpty) => "SOURCE_INVALID"
      case _ => ""
    }
  }
  
  def getRequest(id: Long) = Action.async { implicit request =>
    dbc.getRequestById(id).map { req =>
      val res = req.map(r => RequestClient(r.id, r.status, r.comments, r.bookingId, r.sourceLocation, r.dateTimeOfJourney.getTime, r.creationDate.getTime, r.requestGenerator))
      if(res.isEmpty) Ok(Json.toJson(RequestErrorRes("INVALID_REQUEST_ID"))) else Ok(Json.toJson(res))
    }
  }
  
  def getBooking(id: Long) = Action.async { implicit request =>
    dbc.getBookingById(id).map { b =>
      val res = b.map(x => {
        val emp = toSimpleOptionForSeq(executeSynchronous(dbc.getEmployeeByIds(List(x.employeeId, x.driverId)), "")).map(x => (x.id, x.fullName)).toMap
        BookingClient(x.id, x.sourceLocation, x.dateTimeOfJourney.getTime,
          getBookingStatus(x.status), emp.get(x.employeeId).getOrElse(""), x.vehicleDetails, emp.get(x.driverId).getOrElse(""))
    })
      if(res.isEmpty) Ok(Json.toJson(RequestErrorRes("INVALID_BOOKING_ID"))) else Ok(Json.toJson(res))
    }
  }
  
  val getBookingStatus = (status: Boolean) => if(status) "CONFIRMED" else "CANCELLED"
    
}