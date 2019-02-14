public class foo{
    // http://stackoverflow.com/a/723914/648955
    private Process startOtherProcess(File mapFile) throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath);
        String className = ExitHookTest.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className, mapFile.getAbsolutePath());
        builder.inheritIO();
        return builder.start();
    }
}