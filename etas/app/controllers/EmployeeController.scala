package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import mysql.DBConnection
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import models.EmployeeClient

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class EmployeeController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  //val Log = LoggerFactory getLogger getClass

  def getEmployees = Action.async { implicit request =>
    dbc.employees().map { emp =>
      val res = emp.map(e => EmployeeClient(e.id, e.fullName, e.designation, e.joiningDate.getTime, e.email, e.phone, e.address))
      Ok(Json.toJson(res))
    }
  }
  
  def getEmployee(id: Long) = Action.async { implicit request =>
    dbc.employee(id).map { emp =>
      val res = emp.map(e => EmployeeClient(e.id, e.fullName, e.designation, e.joiningDate.getTime, e.email, e.phone, e.address))
      Ok(Json.toJson(res))
    }
  }

}
