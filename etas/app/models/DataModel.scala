package models

import java.sql.Timestamp
import play.api.libs.json._

/* passenger and driver both are the employees of the organization */
case class Employee(id: Long, fullName: String, designation: String, joiningDate: Timestamp, email: String, phone: String, address: Option[String])
case class EmployeeClient(id: Long, fullName: String, designation: String, joiningDate: Long, email: String, phone: String, address: Option[String])

case object EmployeeClient {
  implicit val employeeClientFormat = Json.format[EmployeeClient]
}

case class Cab(id: Long, registrationNumber: String, driverId: Long, cabStatus: Boolean, comments: Option[String], varancy: Int)
case class CabClient(cabId: Long, registrationNumber: String, driverId: Long, cabStatus: String, comments: Option[String], varancy: Int)

case object CabClient {
  implicit val cabClientFormat = Json.format[CabClient]
}

case class Booking(id: Long, sourceLocation: String, dateTimeOfJourney: Timestamp, employeeId: Long, status: Boolean, vehicleDetails: String, driverId: Long)
case class BookingClient(bookingId: Long, sourceLocation: String, dateTimeOfJourney: Long, bookingStatus: String, passengerDetails: String,
      vehicleDetails: String, driverDetails: String)

case object BookingClient {
  implicit val bookingClientFormat = Json.format[BookingClient]
}

case class UserRequest(id: Long, status: String, comments: Option[String], bookingId: Option[Long], sourceLocation: String, dateTimeOfJourney: Timestamp, creationDate: Timestamp, requestGenerator: Long)
case class RequestClient(requestId: Long, requestStatus: String, comments: Option[String], bookingId: Option[Long], sourceLocation: String, dateTimeOfJourney: Long, requestCreationDate: Long, requestGenerator: Long)
case object RequestClient {
  implicit val requestClientFormat = Json.format[RequestClient]
}

case class RequestSuccessRes(requestId: Long)
case object RequestSuccessRes {
  implicit val requestSuccessResFormat = Json.format[RequestSuccessRes]
}
case class RequestErrorRes(ERROR_CODE: String)
case object RequestErrorRes {
  implicit val requestErrorResFormat = Json.format[RequestErrorRes]
}

case class BookingRequest(sourceLocation: String, dateTimeOfJourney: Long, employeeId: Long)
case object BookingRequest {
  implicit val bookingRequestFormat = Json.format[BookingRequest]
}
