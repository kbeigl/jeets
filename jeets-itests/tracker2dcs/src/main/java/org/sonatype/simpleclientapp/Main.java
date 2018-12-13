package org.sonatype.simpleclientapp;

/**
 * https://blog.sonatype.com/2009/06/integration-tests-with-maven-part-1-failsafe-plugin/
 */
public class Main
    {
        public static void main( String[] args )
        {
            System.out.println("execute " + args);
            System.exit( execute( args ) );
        }

        public static int execute( String[] args )
        {
            System.out.println("return " + args.length);
            return args.length;
        }
    }