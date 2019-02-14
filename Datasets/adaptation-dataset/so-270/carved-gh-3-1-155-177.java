public class foo{
    /**
     * Hack to override mutability of the map returned by {@code System.getenv()}
     * <p>
     * See {@linkplain http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java StackOverflow article}
     * @param envValue new value for VOLTDB_OPTS
     * @throws Exception
     */
    public static void setVoltDbOpts(String envValue) throws Exception
    {
        Map<String, String> newenv = new HashMap<String, String>(System.getenv());
        newenv.put("VOLTDB_OPTS", envValue);
        Map<String, String> env = System.getenv();
        Class<?> cl = env.getClass();
        if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Object obj = field.get(env);
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) obj;
            map.clear();
            map.putAll(newenv);
        }
    }
}