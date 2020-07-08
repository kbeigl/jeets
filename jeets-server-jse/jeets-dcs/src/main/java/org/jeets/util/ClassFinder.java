package org.jeets.util;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Traccar is loading the *Protocol.classes in the org.traccar.ServerManager.
 * It's constructor traverses the file system or a jar file. For a multi module
 * environment (plus test environment) the search had to be extended.
 * <p>
 * problem description<br>
 * When you add the package org.traccar.protocol (with *ProtocolTest classes) in
 * the jeets-dcs test/ folder the original loading method (i.e.
 * constructor) does not find the same package in the included
 * jeets-protocols-traccar.jar .. somewhere in the class path.
 * <p>
 * Implementation from www.torsten-horn.de/techdocs/java-classfinder.htm
 * <p>
 * CAUTION !<br>
 * This ClassFinder may run into problems when the same package exists in main
 * and test environment and both hold the identically named object. Then it
 * needs to be extended to prefer the test object in test environment and main
 * in main. A cleaner solution might be to let (JUnit) class loader resolve it?
 * <p>
 * Also note that the search is restricted to paths containing 'jeets' in order
 * to search only in the jeets world. See javadoc for getPathesFromClasspath
 * method.
 * <p>
 * Another way to detect all subtypes of a type is by using this library: <br>
 * https://github.com/ronmamo/reflections
 */
public final class ClassFinder {

    private ClassFinder() {
    }

    /**
     * Note that this stand alone method will return different results than calling
     * the ClassFinder inside a test or a complex artifact on different machines ...
     */
    public static void main(String[] args) throws Exception {

//      String packageName = (args.length > 0) ? args[0] : null;
//      String classNameSearched = (args.length > 1) ? args[1] : null;

        String packageName = "org.traccar.protocol";
        String classNameSearched = null;
        long start = System.currentTimeMillis();

        List<Class<?>> classes = getClasses(packageName, classNameSearched);
        System.out.println("\n---- " + classes.size() + " found classes:");
        for (Class<?> clazz : classes) {
            System.out.println(clazz);
        }

        List<Object> objects = getInstances(packageName, classNameSearched);
        System.out.println("\n---- " + objects.size() + " instatiated objects");
        for (Object obj : objects) {
            System.out.println(obj.getClass());
        }
        
        System.out.println(System.currentTimeMillis() - start + " millis");
    }

    // Finde Klassen und instanziiere sie:
    public static List<Object> getInstances(String packageName, String classNameSearched)
            throws ClassNotFoundException {
        List<Class<?>> classes = getClasses(packageName, classNameSearched);
        List<Object> objects = new ArrayList<Object>();
        for (Class<?> clazz : classes) {
            if (!clazz.isInterface() && (clazz.getModifiers() & Modifier.ABSTRACT) == 0) {
                try {
                    objects.add(clazz.newInstance());
                } catch (Exception ex) {
                    // nur instanziierbare Klassen sind interessant
                }
            }
        }
        return objects;
    }

    // Finde Klassen (über Interface- oder Klassennamen bzw. Package-Namen):
    public static List<Class<?>> getClasses(String packageName, String classNameSearched)
            throws ClassNotFoundException {
        Class<?> classSearched = (classNameSearched != null) ? Class.forName(classNameSearched) : null;
        return getClasses(packageName, classSearched);
    }

    // Finde Klassen (über Interface oder Klasse bzw. Package-Namen):
    public static List<Class<?>> getClasses(String packageName, Class<?> classSearched) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String path : getPathesFromClasspath()) {
            File fileOrDir = new File(path);
            if (fileOrDir.isDirectory()) {
                classes.addAll(getClassesFromDir(fileOrDir, packageName, classSearched));
            }
            if (fileOrDir.isFile() && (fileOrDir.getName().toLowerCase().endsWith(".jar")
                    || fileOrDir.getName().toLowerCase().endsWith(".zip"))) {
                classes.addAll(getClassesFromJar(fileOrDir, packageName, classSearched));
            }
        }
        return Collections.unmodifiableList(classes);
    }

    /**
     * TODO Update this:<br>
     * Currently only paths containing 'jeets' are respected. This may be fine tuned
     * later, since jeets can be the root directory, in the jar name or in the path
     * to a class file. Anyhow it performs better without going through all third
     * party libraries in the local repository etc.
     */
    public static List<String> getPathesFromClasspath() {
        String classpath = System.getProperty("java.class.path");
        String pathseparator = System.getProperty("path.separator");
        StringTokenizer tokenizer = new StringTokenizer(classpath, pathseparator);
        List<String> pathes = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            String target = tokenizer.nextToken();
//            if (target.contains("jeets")) {
                pathes.add(target);
//            }
        }
        return Collections.unmodifiableList(pathes);
    }

    public static List<Class<?>> getClassesFromJar(File file, String packageName, Class<?> classSearched) {
        if (packageName == null) {
            packageName = "";
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String dirSearched = packageName.replace(".", "/");
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
        } catch (Exception ex) {
            // only zipped files that can be opened are of interest
            return classes;
        }
        for (Enumeration<? extends ZipEntry> zipEntries = zipFile.entries(); zipEntries.hasMoreElements();) {
            String entryName = zipEntries.nextElement().getName();
            if (!entryName.startsWith(dirSearched) || !entryName.toLowerCase().endsWith(".class")) {
                continue;
            }
            entryName = entryName.substring(0, entryName.length() - ".class".length());
            entryName = entryName.replace("/", ".");
            try {
                Class<?> clazz = Class.forName(entryName);
                if (classSearched == null || classSearched.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            } catch (Throwable ex) {
                // System.err.println("unloadable class file in jar: " + entryName);
                // we're only interested in assignable classes
            }
        }
        try {
            zipFile.close();
        } catch (Exception ex) {
            /* wird ignoriert */
        }
        return Collections.unmodifiableList(classes);
    }

    public static List<Class<?>> getClassesFromDir(File dir, String packageName, Class<?> classSearched) {
        if (packageName == null) {
            packageName = "";
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        File dirSearched = new File(dir.getPath() + File.separator + packageName.replace(".", "/"));
        if (dirSearched.isDirectory()) {
            getClassesFromFileOrDirIntern(true, dirSearched, packageName, classSearched, classes);
        }
        return Collections.unmodifiableList(classes);
    }

    private static void getClassesFromFileOrDirIntern(boolean first, File fileOrDir,
            String packageName, Class<?> classSearched, List<Class<?>> classes) {
        if (fileOrDir.isDirectory()) {
            if (!first) {
                packageName = (packageName + "." + fileOrDir.getName()).replaceAll("^\\.", "");
            }
            for (String subFileOrDir : fileOrDir.list()) {
                getClassesFromFileOrDirIntern(false,
                        new File(fileOrDir, subFileOrDir), packageName, classSearched, classes);
            }
        } else {
            if (fileOrDir.getName().toLowerCase().endsWith(".class")) {
                String classFile = fileOrDir.getName();
                classFile = packageName + "." + classFile.substring(0, classFile.length() - ".class".length());
                try {
                    Class<?> clazz = Class.forName(classFile);
                    if (classSearched == null || classSearched.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable ex) {
                    // System.err.println("unloadable class file: " + classFile);
                    // we're only interested in assignable classes
                }
            }
        }
    }
}
