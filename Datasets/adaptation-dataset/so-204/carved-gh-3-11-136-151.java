public class foo{
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        cookie = new HttpCookie(name, value);
        cookie.setComment((String) in.readObject());
        cookie.setCommentURL((String) in.readObject());
        cookie.setDomain((String) in.readObject());
        cookie.setMaxAge(in.readLong());
        cookie.setPath((String) in.readObject());
        cookie.setPortlist((String) in.readObject());
        cookie.setVersion(in.readInt());
        cookie.setSecure(in.readBoolean());
        cookie.setDiscard(in.readBoolean());
        setHttpOnly(in.readBoolean());
    }
}