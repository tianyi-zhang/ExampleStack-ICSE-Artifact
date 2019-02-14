public class foo {
    /**
     * Get the value for the given key, and return as a long.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     *         cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {

        Long ret= def;

        try{

          ClassLoader cl = context.getClassLoader();
          @SuppressWarnings("rawtypes")
              Class SystemProperties= cl.loadClass("android.os.SystemProperties");

          //Parameters Types
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= long.class;  

          Method getLong = SystemProperties.getMethod("getLong", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new Long(def);

          ret= (Long) getLong.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }
}