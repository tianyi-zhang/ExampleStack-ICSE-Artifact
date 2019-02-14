public class foo{
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            T pojo = new Gson().fromJson(je, type);

            Field[] fields = pojo.getClass().getDeclaredFields();
            for( Field f : fields ) {
                if( f.getAnnotation(JsonRequired.class) != null ) {
                    try {
                        f.setAccessible(true);
                        if( f.get(pojo) == null ) {
                            throw new JsonParseException("Missing field in JSON: " + f.getName());
                        }
                    } catch( IllegalArgumentException | IllegalAccessException ex ) {
                        ModCntManPack.UPD_LOG.log(Level.WARN, ex, null);
                    }
                }
            }

            return pojo;
        }
}