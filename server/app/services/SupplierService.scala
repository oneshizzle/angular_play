package services

import play.api.libs.json.Json
import play.api._
import play.api.mvc._
import models.Supplier._

class SupplierService extends Controller {

  implicit val supplierWrites = Json.writes[Supplier]
  implicit val supplierReads = Json.reads[Supplier]

  def listSuppliers = Action {
    Ok(Json.toJson(suppliers))
  }

}