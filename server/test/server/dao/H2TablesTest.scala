package server.dao

import slick.jdbc.StaticQuery
import org.scalatest._
import dao.Suppliers
import dao.Wines
//import slick.driver.H2Driver.api._
import slick.jdbc.ResultSetInvoker
import slick.driver.SQLiteDriver.api._

class H2TablesSuite extends FunSuite with BeforeAndAfter {

  val suppliers = TableQuery[Suppliers]
  val wines = TableQuery[Wines]

  implicit var session: Session = _

  def createSchema() = (suppliers.ddl ++ wines.ddl).create

  def insertSuppliers(implicit session: Session): Unit = {
    // Insert some suppliers
    (StaticQuery.u + "insert into suppliers values(101, 'Acme, Inc.', '99 Market Street', 'Groundsville', 'CA', '95199')").execute
    (StaticQuery.u + "insert into suppliers values(49, 'Superior Coffee', '1 Party Place', 'Mendocino', 'CA', '95460')").execute
    (StaticQuery.u + "insert into suppliers values(150, 'The High Ground', '100 Coffee Lane', 'Meadows', 'CA', '93966')").execute
  }

  before {
    session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()
  }

  test("Creating the Schema works") {
    createSchema()

    val resultSet = session.conn.getMetaData().getTables("", "public", null, null)
    var tables = scala.collection.mutable.ArrayBuffer[String]()
    while (resultSet.next) {
      tables.+=:(resultSet.getString(3).toLowerCase())
    }

    assert(tables.size == 2)
    assert(tables.count(_.equalsIgnoreCase("suppliers")) == 1)
    assert(tables.count(_.equalsIgnoreCase("wines")) == 1)
  }

  test("Inserting a Supplier works") {
    createSchema()
    val insertCount = insertSuppliers(session)
    assert(insertCount == 1)
  }

  test("Query Suppliers works") {
    createSchema()
    insertSuppliers(session)
    val results = suppliers
    assert(results.length == 1)

    ///assert(results.head._1 == 101)
  }

  after {
    session.close()
  }

}