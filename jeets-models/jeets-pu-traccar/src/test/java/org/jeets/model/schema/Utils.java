package org.jeets.model.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

  public static String DATA_DIRECTORY = "src/test/resources/data/";
  //	test overrides default, default without test file
  public static String PROP_TEST_FILE = "test-pu-traccar.properties";

  public static Properties loadPropsFromFile(String filename) {
    Properties props = new Properties();
    InputStream input = null;
    try {
      //          input = new FileInputStream(filename);  // on classpath
      //        	what about test/resources !? don't mix !
      input = Utils.class.getClassLoader().getResourceAsStream(filename);
      if (input == null) {
        System.err.println(filename + " wasn't found!");
      }
      props.load(input);

    } catch (IOException ex) {
      System.err.println(filename + " wasn't loaded. Stop application.");
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          System.err.println(filename + " wasn't closed.");
        }
      }
    }
    return props;
  }
}
