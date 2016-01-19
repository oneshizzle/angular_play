package models

object Supplier {
  case class Supplier(
    id: Int,
    name: String,
    street: String,
    city: String,
    state: String,
    zip: String)

  var suppliers = List(Supplier(1, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
    Supplier(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"))
}