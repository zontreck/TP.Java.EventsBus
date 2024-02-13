package dev.zontreck.eventsbus;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Used internally. Do not directly invoke
 * <br/>
 * Accessor is set Protected intentionally!!!!!!!!!
 */
class ClassScanner {
    /**
     * Start the process of scanning the classes and forcing them to load in
     * <br/>
     * This is used by the event dispatcher
     */
    protected static void DoScan() {
        // Scan all classes in the classpath
        Set<Class<?>> scannedClasses = scanClasses();

        // Force loading of all scanned classes
        for (Class<?> clazz : scannedClasses) {
            try {
                // Load the class
                Class.forName(clazz.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static Set<Class<?>> scanClasses() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        Set<Class<?>> scannedClasses = new HashSet<>();
        for (String classpathEntry : classpathEntries) {
            File file = new File(classpathEntry);
            if (file.isDirectory()) {
                scanClassesInDirectory(file, "", scannedClasses);
            } else if (file.isFile() && classpathEntry.endsWith(".jar")) {
                scanClassesInJar(file, scannedClasses);
            }
        }

        return scannedClasses;
    }

    private static void scanClassesInDirectory(File directory, String packageName, Set<Class<?>> scannedClasses) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanClassesInDirectory(file, packageName + "." + file.getName(), scannedClasses);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    scannedClasses.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void scanClassesInJar(File jarFile, Set<Class<?>> scannedClasses) {
        try (JarFile jf = new JarFile(jarFile)) {
            for (JarEntry entry : Collections.list(jf.entries())) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        scannedClasses.add(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
