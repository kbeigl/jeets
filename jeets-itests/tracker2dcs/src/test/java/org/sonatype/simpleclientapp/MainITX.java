package org.sonatype.simpleclientapp;

import java.io.File;
import org.junit.Test;
import junit.framework.TestCase;

public class MainITX extends TestCase {

    @Test
    public void testExecute() throws Exception {
        System.out.println("testExecute");
        assertEquals(0, execute(new String[] {}));
        assertEquals(1, execute(new String[] { "one" }));
        assertEquals(6, execute(new String[] { "one", "two", "three", "four", "five", "six" }));
    }

    private int execute(String[] args) throws Exception {
//      JUST A SONATYPE TEST TO GET GOING
//      to be replace with maven-ant and command line: java -jar jeets-tracker/target/tracker... <params>
        File jar = new File("target/tracker2dcs-1.1-RC-jar-with-dependencies.jar");

        String[] execArgs = new String[args.length + 3];
        System.arraycopy(args, 0, execArgs, 3, args.length);
        execArgs[0] = "java";
        execArgs[1] = "-jar";
        execArgs[2] = jar.getCanonicalPath();
        Process p = Runtime.getRuntime().exec(execArgs);
        p.waitFor();
        return p.exitValue();
    }

}
