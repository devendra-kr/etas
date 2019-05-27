package controllers

import scala.concurrent.ExecutionContext

import javax.inject.Inject
import javax.inject.Singleton
import models.EmployeeClient
import mysql.DBConnection
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import views.html.helper.CSRF
import models.Employee
import java.sql.Timestamp
import org.slf4j.LoggerFactory

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class EmployeeController @Inject() (cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  val Log = LoggerFactory getLogger getClass

  def getEmployees = Action.async { implicit request =>
    dbc.getAllEmployees().map { emp =>
      val res = emp.map(e => EmployeeClient(e.id, e.fullName, e.designation, e.joiningDate.getTime, e.email, e.phone, e.address))
      Ok(Json.toJson(res))
    }
  }

  def getEmployee(id: Long) = Action.async { implicit request =>
    dbc.getEmployeeById(id).map { emp =>
      val res = emp.map(e => EmployeeClient(e.id, e.fullName, e.designation, e.joiningDate.getTime, e.email, e.phone, e.address))
      Ok(Json.toJson(res))
    }
  }

  def reads(json: JsValue): JsResult[EmployeeClient] = {
    val id = (json \ "id").as[Long]
    val fullName = (json \ "fullName").as[String]
    val designation = (json \ "designation").as[String]
    val joiningDate = (json \ "joiningDate").as[Long]
    val email = (json \ "email").as[String]
    val phone = (json \ "phone").as[String]
    val address = (json \ "address").as[String]
    JsSuccess(EmployeeClient(id, fullName, designation, joiningDate, email, phone, Some(address)))
  }

  def saveEmployee = Action { request =>
    val json = request.body.asJson.get
    val emp = json.as[EmployeeClient]
    val newEmp = Employee(emp.id, emp.fullName, emp.designation, new Timestamp(emp.joiningDate), emp.email, emp.phone, emp.address)
    try {
      dbc.insertEmployee(newEmp)
    } catch {
      case ex: Exception => ex.printStackTrace()
      Log info "unique key constraint violation"
    }
    Ok
  }

  /*
   * curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{"id": -1,"fullName": "Mahendra Kumar","designation": "SE","joiningDate": 1558796864000,"email": "mahik@gmail.com","phone": "9505398922","address": ""
}' \
    http://localhost:9000/employee
    *
    */
  
  def updateEmployee = Action { request =>
    val json = request.body.asJson.get
    val emp = json.as[EmployeeClient]
    val newEmp = Employee(emp.id, emp.fullName, emp.designation, new Timestamp(emp.joiningDate), emp.email, emp.phone, emp.address)
    try {
      dbc.updateEmployee(newEmp)
    } catch {
      case ex: Exception => ex.printStackTrace()
      Log info "unique key constraint violation"
    }
    Ok
  }
  
  /*curl \
    --header "Content-type: application/json" \
    --request PUT \
    --data '{"id": 3,"fullName": "Mahendra Kr","designation": "SSD","joiningDate": 1558796864000,"email": "mahik171@gmail.com","phone": "9505398921","address": "updated address"
}' \
    http://localhost:9000/employee
    * 
    */
  
  def deleteEmployee(id: Long) = Action.async { implicit request =>
    dbc.deleteEmployee(id).map { emp => Ok(Json.toJson(emp))
    }
  }
  
  /* curl -X DELETE "http://localhost:9000/employee/1" */


}
