public class foo {
    /**
     * Get the value for the given key, and return as an integer.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     *         cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {

        Integer ret= def;

        try{

          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = cl.loadClass("android.os.SystemProperties");

          //Parameters Types
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= int.class;  

          Method getInt = SystemProperties.getMethod("getInt", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new Integer(def);

          ret= (Integer) getInt.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }
}