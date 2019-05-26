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
import models.UserRequest
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.Source

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

  def getAllEmployees(): Future[Seq[Employee]] = db.run {
    Employees.result
  }

  def getEmployeeById(id: Long): Future[Seq[Employee]] = db.run {
    Employees.filter(f => f.id === id).result
  }
  
  def getEmployeeById(id: List[Long]): Future[Seq[Employee]] = db.run {
    Employees.filter(f => f.id inSet(id)).result
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
    def vacancy = column[Int]("vacancy")
    def * = (id, registrationNumber, driverId, status, comments, vacancy) <> ((Cab.apply _).tupled, Cab.unapply)
  }

  private val Cabs = TableQuery[CabTable]

  def getAllCabs(): Future[Seq[Cab]] = db.run {
    Cabs.result
  }

  def getCabById(id: Long): Future[Seq[Cab]] = db.run {
    Cabs.filter(f => f.id === id).result
  }
  
  def getAvailableCab(location: String): Future[Seq[Cab]] = {
    val cabs = for {
    (c, l) <- Cabs.filter(f => f.status && f.vacancy > 0).join(Sources.filter(f => f.location === location)).on(_.id === _.cabId)
    } yield (c)
    db.run { cabs.result }
  }

  def insertCab(cab: Cab) = {
    Await.result(db.run(DBIO.seq(
      Cabs += cab,
      Cabs.result.map(println))), Duration.Inf)
  }
  
  def updateCab(cab: Cab) = {
      val newCab = for (c <- Cabs if c.id === cab.id) yield (c)
      db.run(newCab.update(cab)) map { _ > 0 }
  }
  
  def updateCabVacancy(id: Long, vacancy: Int) = {
      val newCab = for (c <- Cabs if c.id === id) yield (c.vacancy)
      db.run(newCab.update(vacancy)) map { _ > 0 }
  }
  
  def updateCab(id: Long, status: Boolean) = {
      val newCab = for (c <- Cabs if c.id === id) yield (c.status)
      db.run(newCab.update(status)) map { _ > 0 }
  }
   
  def deleteCab(id: Long) = {
      db.run(Cabs.filter(_.id === id).delete) map { _ > 0 }
  }
  
  private class SourceTable(tag: Tag) extends Table[Source](tag, "source") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def cabId = column[Long]("cabId")
    def location = column[String]("location")
    def * = (id, cabId, location) <> ((Source.apply _).tupled, Source.unapply)
  }
  
  private val Sources = TableQuery[SourceTable]
  
  def getSourceByLocation(location: String): Future[Seq[Source]] = db.run {
    Sources.filter(f => f.location === location).result
  }


  private class BookingTable(tag: Tag) extends Table[Booking](tag, "booking") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def sourceLocation = column[String]("source_location")
    def datetimeJourney = column[Timestamp]("datetime_journey")
    def employeeId = column[Long]("employee_id")
    def status = column[Boolean]("status")
    def registrationNumber = column[String]("registration_number")
    def driverId = column[Long]("driver_id")
    def * = (id, sourceLocation, datetimeJourney, employeeId, status, registrationNumber, driverId) <> ((Booking.apply _).tupled, Booking.unapply)
  }

  private val Bookings = TableQuery[BookingTable]

  def getBookingById(id: Long): Future[Seq[Booking]] = db.run {
    Bookings.filter(f => f.id === id).result
  }
  
  def insertBooking(book: Booking) = db.run(Bookings returning Bookings.map(_.id) += book)
  .map(id => book.copy(id = id))

  private class RequestTable(tag: Tag) extends Table[UserRequest](tag, "request") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def status = column[String]("status")
    def comments = column[Option[String]]("comments")
    def bookingId = column[Option[Long]]("booking_id")
    def sourceLocation = column[String]("source_location")
    def datetimeJourney = column[Timestamp]("datetime_journey")
    def creationDate = column[Timestamp]("creation_date")
    def generator = column[Long]("generator")
    def * = (id, status, comments, bookingId, sourceLocation, datetimeJourney, creationDate, generator) <> ((UserRequest.apply _).tupled, UserRequest.unapply)
  }

  private val Requests = TableQuery[RequestTable]

  def getRequestById(id: Long): Future[Seq[UserRequest]] = db.run {
    Requests.filter(f => f.id === id).result
  }
  
  /*def insertRequest(req: UserRequest) = {
    Await.result(db.run(DBIO.seq(
      Requests += req,
      Requests.result.map(println))), Duration.Inf)
  }*/
  
  def insertRequest(req: UserRequest) = db.run(Requests returning Requests.map(_.id) += req)
  .map(id => req.copy(id = id))

}









