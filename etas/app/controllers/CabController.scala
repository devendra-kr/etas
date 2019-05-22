package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import mysql.DBConnection
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import models.CabClient

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class CabController @Inject()(cc: ControllerComponents, dbc: DBConnection)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  
  def getCabs = Action.async { implicit request =>
    dbc.cabs().map { cab =>
      val res = cab.map(c => CabClient(c.id, c.registrationNumber, c.driverId, setStatus(c.cabStatus), c.comments, c.varancy))
      Ok(Json.toJson(res))
    }
  }
  
  val setStatus = (x: Boolean) => if(x) "AVAILABLE" else "UNAVAILABLE"
  
  
  def getCab(id: Long) = Action.async { implicit request =>
    dbc.cab(id).map { cab =>
      val res = cab.map(c => CabClient(c.id, c.registrationNumber, c.driverId, setStatus(c.cabStatus), c.comments, c.varancy))
      Ok(Json.toJson(res))
    }
  }
}
