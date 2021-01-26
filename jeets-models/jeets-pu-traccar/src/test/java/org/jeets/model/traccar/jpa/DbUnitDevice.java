package org.jeets.model.traccar.jpa;

import java.io.FileInputStream;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
// import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

//	some dbunit testing .. test should NOT run via mvn test
//	but does run via project > JUnit with errors as no schema is created (yet)
public class DbUnitDevice extends DBTestCase {

  //	DBUnit tests are under construction
  //	this import can be applied on a production db
  //	to preload a test db etc.

  public DbUnitDevice(String name) {
    super(name);
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.h2.Driver");
    System.setProperty(
        PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:h2:./target/dbunit");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
  }

  //	test.xml add:
  //	<jdbc:initialize-database data-source="dataSource">
  //		<jdbc:script location="init-datasource.sql"/>
  //	</jdbc:initialize-database>

  @Override
  protected IDataSet getDataSet() throws Exception {
    return new FlatXmlDataSetBuilder()
        .build(new FileInputStream("src/test/resources/data/device.xml"));
  }

  @Test
  public void testExample() throws Exception {
    assertEquals(false, false);
  }
  /*
  * By default, Dbunit performs a CLEAN_INSERT operation before executing each
  * test and performs no cleanup operation afterward. You can modify this
  * behavior by overriding getSetUpOperation() and getTearDownOperation().
  protected DatabaseOperation getSetUpOperation() throws Exception
  { return DatabaseOperation.REFRESH; }
  protected DatabaseOperation getTearDownOperation() throws Exception
  { return DatabaseOperation.NONE; }
  */
}
