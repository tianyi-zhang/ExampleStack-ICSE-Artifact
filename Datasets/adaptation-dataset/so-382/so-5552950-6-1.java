public class foo {
    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    public static void set(Context context, String key, String val) throws IllegalArgumentException {

        try{

          @SuppressWarnings("unused")
          DexFile df = new DexFile(new File("/system/app/Settings.apk"));
          @SuppressWarnings("unused")
          ClassLoader cl = context.getClassLoader(); 
          @SuppressWarnings("rawtypes")
          Class SystemProperties = Class.forName("android.os.SystemProperties");

          //Parameters Types
          @SuppressWarnings("rawtypes")
              Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= String.class;  

          Method set = SystemProperties.getMethod("set", paramTypes);

          //Parameters         
          Object[] params= new Object[2];
          params[0]= new String(key);
          params[1]= new String(val);

          set.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            //TODO
        }

    }
}