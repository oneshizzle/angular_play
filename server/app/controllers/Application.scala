package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import models.Supplier._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
