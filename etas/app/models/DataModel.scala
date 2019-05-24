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

case class Booking(id: Long, sourceLocation: String, dateTimeOfJourney: Timestamp, employeeId: Long, status: Boolean)
case class BookingClient(id: Long, sourceLocation: String, dateTimeOfJourney: Long, employeeId: Long)

case object BookingClient {
  implicit val bookingClientFormat = Json.format[BookingClient]
}

case class UserRequest(id: Long, status: String, comments: Option[String], bookingId: Option[Long], creationDate: Timestamp, requestGenerator: Long)
case class RequestClient(id: Long, requestStatus: String, comments: Option[String], bookingId: Long, sourceLocation: String, dateTimeOfJourney: Long, requestCreationDate: Long, requestGenerator: Long)
case object RequestClient {
  implicit val requestClientFormat = Json.format[RequestClient]
}

case class BookingRequest(sourceLocation: String, dateTimeOfJourney: Long, employeeId: Long)
case object BookingRequest {
  implicit val bookingRequestFormat = Json.format[BookingRequest]
}

case class BookingResponse(requestId: Long)
case class BookingErrorCode(ERROR_CODE: String)

  /*
create table request(
	id BIGINT NOT NULL,
	status VARCHAR(15) NOT NULL,
	commnets VARCHAR(500),
	booking_id BIGINT,
	creation_date TIMESTAMP NOT NULL,
	generator VARCHAR(50) NOT NULL
*/