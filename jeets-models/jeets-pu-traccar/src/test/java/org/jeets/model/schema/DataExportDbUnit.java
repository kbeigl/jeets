package org.jeets.model.schema;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
// import org.dbunit.database.QueryDataSet;
// import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * This class can be used to export data from the **production DB** to test directories for testing.
 * The export is not part of the regular test cycle and should be handled with care. Plain Database
 * access without JPA.
 */
public class DataExportDbUnit {

  //	TURN main method into JUnit @Test (see EMgrTest)
  //	and comment - only apply after pu version change
  public static void main(String[] args) throws Exception {
    //		dbunit.sourceforge.net/faq.html#extract

    Properties props = Utils.loadPropsFromFile(Utils.PROP_TEST_FILE);
    System.out.println("loading " + Utils.PROP_TEST_FILE);
    System.out.println("pu.traccar.jdbc.driver: " + props.getProperty("pu.traccar.jdbc.driver"));
    System.out.println("pu.traccar.jdbc.url: " + props.getProperty("pu.traccar.jdbc.url"));
    System.out.println("pu.traccar.jdbc.user: " + props.getProperty("pu.traccar.jdbc.user"));
    System.out.println(
        "pu.traccar.jdbc.password: " + props.getProperty("pu.traccar.jdbc.password"));
    if (props.getProperty("pu.traccar.jdbc.driver").substring(0, 2).equals("${")) {
      System.err.println("Please apply resource filtering to run export!");
      return;
    }

    // database connection
    Class driverClass = Class.forName(props.getProperty("pu.traccar.jdbc.driver"));
    Connection jdbcConnection =
        DriverManager.getConnection(
            props.getProperty("pu.traccar.jdbc.url"),
            props.getProperty("pu.traccar.jdbc.user"),
            props.getProperty("pu.traccar.jdbc.password"));
    IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

    // partial database export
    //		QueryDataSet partialDataSet = new QueryDataSet(connection);
    //		partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
    //		partialDataSet.addTable("BAR");
    //		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

    // full database export
    IDataSet fullDataSet = connection.createDataSet();
    FlatXmlDataSet.write(fullDataSet, new FileOutputStream(Utils.DATA_DIRECTORY + "full.xml"));

    // dependent tables database export: export table X and all tables that
    // have a PK which is a FK on X, in the right order for insertion
    //		String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
    //		IDataSet depDataSet = connection.createDataSet(depTableNames);
    //		FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml"));
  }
}
