public class foo{
    // http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
    public static int getPid() {
        try {
            java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
            java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
            java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
            pid_method.setAccessible(true);
            return  (Integer) pid_method.invoke(mgmt);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }
}