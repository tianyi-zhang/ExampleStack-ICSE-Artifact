public class foo {
/**
 * Will take a url such as http://www.stackoverflow.com and return www.stackoverflow.com
 * 
 * @param url
 * @return
 */
public static String getHost(String url){
    if(url == null || url.length() == 0)
        return "";

    int doubleslash = url.indexOf("//");
    if(doubleslash == -1)
        doubleslash = 0;
    else
        doubleslash += 2;

    int end = url.indexOf('/', doubleslash);
    end = end >= 0 ? end : url.length();

    int port = url.indexOf(':', doubleslash);
    end = (port > 0 && port < end) ? port : end;

    return url.substring(doubleslash, end);
}
}