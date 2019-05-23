package mysql

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import javax.inject.Inject
import javax.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf
import org.slf4j.LoggerFactory
import models.Employee
import java.sql.Timestamp
import models.Cab
import models.Booking
import models.Request
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 *
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class DBConnection @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val Log = LoggerFactory getLogger getClass
  import profile.api._
  /**
   * Here we define the table.
   */
  private class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fullName = column[String]("full_name")
    def designation = column[String]("designation")
    def joiningDate = column[Timestamp]("joining_date")
    def email = column[String]("email")
    def phone = column[String]("phone")
    def address = column[Option[String]]("address")
    def * = (id, fullName, designation, joiningDate, email, phone, address) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  private val Employees = TableQuery[EmployeeTable]

  def employees(): Future[Seq[Employee]] = db.run {
    Employees.result
  }

  def employee(id: Long): Future[Seq[Employee]] = db.run {
    Employees.filter(f => f.id === id).result
  }

  def insertEmployee(emp: Employee) = {
    Await.result(db.run(DBIO.seq(
      Employees += emp,
      Employees.result.map(println))), Duration.Inf)
  }
  
  def updateEmployee(emp: Employee) = {
      val newEmp = for (e <- Employees if e.id === emp.id) yield (e)
      db.run(newEmp.update(emp)) map { _ > 0 }
  }
  
  def deleteEmployee(id: Long) = {
      db.run(Employees.filter(_.id === id).delete) map { _ > 0 }
  }

  private class CabTable(tag: Tag) extends Table[Cab](tag, "cab") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def registrationNumber = column[String]("registration_number")
    def driverId = column[Long]("driver_id")
    def status = column[Boolean]("status")
    def comments = column[Option[String]]("comments")
    def varancy = column[Int]("varancy")
    def * = (id, registrationNumber, driverId, status, comments, varancy) <> ((Cab.apply _).tupled, Cab.unapply)
  }

  private val Cabs = TableQuery[CabTable]

  def cabs(): Future[Seq[Cab]] = db.run {
    Cabs.result
  }

  def cab(id: Long): Future[Seq[Cab]] = db.run {
    Cabs.filter(f => f.id === id).result
  }

  def cab(cab: Cab) = {
    Await.result(db.run(DBIO.seq(
      Cabs += cab,
      Cabs.result.map(println))), Duration.Inf)
  }
  
  def updateCab(cab: Cab) = {
      val newCab = for (c <- Cabs if c.id === cab.id) yield (c)
      db.run(newCab.update(cab)) map { _ > 0 }
  }
  
  def updateCab(id: Long, status: Boolean) = {
      val newCab = for (c <- Cabs if c.id === id) yield (c.status)
      db.run(newCab.update(status)) map { _ > 0 }
  }
   
  def deleteCab(id: Long) = {
      db.run(Cabs.filter(_.id === id).delete) map { _ > 0 }
  }

  private class BookingTable(tag: Tag) extends Table[Booking](tag, "booking") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def sourceLocation = column[String]("source_location")
    def datetimeJourney = column[Timestamp]("datetime_journey")
    def employeeId = column[Long]("employee_id")
    def status = column[Boolean]("status")
    def * = (id, sourceLocation, datetimeJourney, employeeId, status) <> ((Booking.apply _).tupled, Booking.unapply)
  }

  private val Bookings = TableQuery[BookingTable]

  def bookings(): Future[Seq[Booking]] = db.run {
    Bookings.result
  }

  private class RequestTable(tag: Tag) extends Table[Request](tag, "request") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def status = column[String]("status")
    def comments = column[Option[String]]("comments")
    def bookingId = column[Option[Long]]("booking_id")
    def creationDate = column[Timestamp]("creation_date")
    def generator = column[String]("generator")
    def * = (id, status, comments, bookingId, creationDate, generator) <> ((Request.apply _).tupled, Request.unapply)
  }

  private val Requests = TableQuery[RequestTable]

  def requests(): Future[Seq[Request]] = db.run {
    Requests.result
  }

}