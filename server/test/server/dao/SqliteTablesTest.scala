package server.dao

import slick.jdbc.StaticQuery
import org.scalatest._
import dao.Suppliers
import dao.Wines
import slick.driver.SQLiteDriver.api._
import slick.jdbc.ResultSetInvoker
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.duration.Duration
import org.scalatest.concurrent.PatienceConfiguration._
import org.scalatest.time.{ Seconds, Span }

class SqliteTablesSuite extends FunSuite with BeforeAndAfter with ScalaFutures {

  val suppliers = TableQuery[Suppliers]
  val wines = TableQuery[Wines]

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  implicit var session: Session = _

  def createSchema(implicit session: Session): Unit = {
    (StaticQuery.u + """CREATE TABLE SUPPLIERS (
   SUP_ID INT PRIMARY KEY     NOT NULL,
   SUP_NAME		CHAR(50)    NOT NULL,
   STREET		CHAR(50)    NOT NULL,
   CITY			CHAR(50),
   STATE		CHAR(50),
   ZIP			CHAR(50)
); 
 CREATE TABLE WINES (
   WINE_NAME CHAR(50) PRIMARY KEY     NOT NULL,
   SUP_ID INT    NOT NULL,
   PRICE DOUBLE NULL
);""").execute
  }

  def dropSchema(implicit session: Session): Unit = {
    (StaticQuery.u
      + """drop table suppliers; 
        drop table wines; """).execute
  }

  def insertSuppliers(implicit session: Session): Unit = {
    // Insert some suppliers
    (StaticQuery.u + "insert into suppliers values(101, 'Acme, Inc.', '99 Market Street', 'Groundsville', 'CA', '95199')").execute
    (StaticQuery.u + "insert into suppliers values(49, 'Superior Coffee', '1 Party Place', 'Mendocino', 'CA', '95460')").execute
    (StaticQuery.u + "insert into suppliers values(150, 'The High Ground', '100 Coffee Lane', 'Meadows', 'CA', '93966')").execute
  }

  before {
    session = Database.forURL("jdbc:sqlite:C:\\DEV\\test.db", driver = "org.sqlite.JDBC").createSession()
  }

  test("Dropping then Creating the Schema works") {
    dropSchema(session)

    createSchema(session)

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
    dropSchema(session)
    createSchema(session)

    insertSuppliers(session)

    val db = session.database

    try {
      Await.result(db.run(suppliers.result.map(println)), Duration.Inf)
      //.result(suppliers.filter(_.id.===(101)).map(println), Duration.Inf)
    } finally {
      db.close
    }
  }

  test("Query Suppliers works") {
    dropSchema(session)
    createSchema(session)
    insertSuppliers(session)

    val db: Database = Database.forURL("jdbc:sqlite:C:\\DEV\\test.db", driver = "org.sqlite.JDBC")
    val results = db.run(suppliers.result).futureValue
    println("ADRIEN " + results)

    val results_1 = suppliers.filter(_.id === 101)
    val results_2 = suppliers.filter(_.id === 150)

    val upTo = Compiled { k: Column[Int] =>
      suppliers.filter(_.id <= k).sortBy(_.zip)
    }

    val upToSet = upTo.map(_.andThen(_.to[Set]))

    //assert(2 == 1)
    ///assert(results.take(0). == 101)
  }

  after {
    session.close()
  }

}