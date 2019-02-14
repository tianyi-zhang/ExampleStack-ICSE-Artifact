package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.JOptionPane;



/**
 *
 * @author Nuno Brito, 10th August 2010 in Coimbra, Portugal.
 */
public class internet {

    static internet action = new internet();

    final String[] test_connectivity = {
        "http://google.com",
        "http://cnn.com",
        "http://digg.com",
        "http://wordpress.com",
        "http://www.w3.org",
        "http://yahoo.com",
    };

    Boolean debug = true;

    /**
     * Gets the contents of a file on the Internet into a string
     * @origin http://www.avajava.com/tutorials/lessons/how-do-i-convert-a-web-page-to-a-string.html
     * @copyright Deron Eriksson
     * @license NOASSERTION
     * @retrieved 2014-04-26
     * @retriever Nuno Brito
     * @param TextFileURL
     * @return 
     */
     public static String getTextFile(String TextFileURL){
         String result = "";
         try {
			URL url = new URL(TextFileURL);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuilder sb = new StringBuilder();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			result = sb.toString();

                        is.close();
                        isr.close();
                        sb = null;
                        urlConnection = null;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
                        return "";
		} catch (IOException e) {
                    return "";
			//e.printStackTrace();
		}
         return result;
     }
    
     /**
      * Downloads a file from the Internet to the disk
      * @param filename
      * @param URL
      * @copyright Ben Noland
      * @retrieved 2015-08-29
      * @retriever Nuno Brito
      * @origin http://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
      */
    public static void downloadFile(final File filename, final String URL){
        try {
            BufferedInputStream in = null;
            FileOutputStream fout = null;

            in = new BufferedInputStream(new URL(URL).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
                in.close();
                fout.close();
        }
        catch(Exception e){
        }
}
     
    
/** sends email using an external PHP script, useful to bypass proxies */
public static String sendEmail(String emailAddress, 
        String subject, String body){
    String website = "http://nunobrito.eu/email/egos.php"
                    + "?address=" + emailAddress
                    + "&subject=" + subject
                    + "&body=" + body
                    ;
    String result = webget(website);
    //String result = webget("http://google.com");
    System.out.println(website);
    return result;
}
        
        
/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application
 * in the user's default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "http://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code>
 * Latest Version: <a href="http://www.centerkey.com/java/browser/">www.centerkey.com/java/browser</a><br>
 * Author: Dem Pilafian<br>
 * Public Domain Software -- Free to Use as You Like
 * @version 3.0, February 7, 2010
 */
   static final String[] browsers = { "google-chrome", "firefox", "opera",
      "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla" };
   static final String errMsg = "Error attempting to launch web browser";

   /**
    * Opens the specified web page in the user's default browser
    * @param url A web address (URL) of a web page (ex: "http://www.google.com/")
    */
   public static void openURL(String url) {
      try {  //attempt to use Desktop library from JDK 1.6+ (even if on 1.5)
         Class<?> d = Class.forName("java.awt.Desktop");
         d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke(
            d.getDeclaredMethod("getDesktop").invoke(null),
            new Object[] {java.net.URI.create(url)});
         //above code mimics:
         //   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
         }
      catch (Exception ignore) {  //library not available or failed
         String osName = System.getProperty("os.name");
         try {
            if (osName.startsWith("Mac OS")) {
               Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                  "openURL", new Class[] {String.class}).invoke(null,
                  new Object[] {url});
               }
            else if (osName.startsWith("Windows"))
               Runtime.getRuntime().exec(
                  "rundll32 url.dll,FileProtocolHandler " + url);
            else { //assume Unix or Linux
               boolean found = false;
               for (String browser : browsers)
                  if (!found) {
                     found = Runtime.getRuntime().exec(
                        new String[] {"which", browser}).waitFor() == 0;
                     if (found)
                        Runtime.getRuntime().exec(new String[] {browser, url});
                     }
               if (!found)
                  throw new Exception(Arrays.toString(browsers));
               }
            }
         catch (Exception e) {
            JOptionPane.showMessageDialog(null, errMsg + "\n" + e.toString());
            }
         }
      }


   

   /** Get the login status */
   static public boolean isLogged(final String URL){
       // add our specific URL for the component and required parameter
       String target = URL + "/user?action=isLogged";
       // get the file
       String result = utils.internet.getTextFile(target);
        // output the result
        boolean out = result.equalsIgnoreCase("true");
       return out;
   }


       
   /** 
    * Get a given page from an URL address
    * @param address
    * @return  
    */
   static public String webget(String address) {
        // perhaps in the future we can use something like http://goo.gl/03WQp
        // provide a holder for the reply
        String result = "";

        try {
            URL webpage = new URL(address);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(webpage.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Process each line.
                result = result.concat(inputLine);
            }
            in.close();
        } catch (Exception me) {
            return me.toString();
        }
        return result;
    }

private void log(String gender, String message){
                  System.out.println
                          ("internet ["+gender+"] "+message);
                    }
 static private void debug(String message){
                  if(action.debug)
                      action.log("debug",message);
 }





    /** Do a threaded webget to prevent congestions.
     *   This method is not working as originally intended but at least is still
     *   useful for making requests that do not require a reply back.
     */
    public static void threadedWebGet(String URL){
        // start a new thread for this purpose
        WebGetThread a = new WebGetThread(URL);
        a.start();
    }

    
   // derived from http://stackoverflow.com/questions/8765578/get-local-ip-address-without-connecting-to-the-internet
   static public String getLocalIP(){
        String result = "";
        try{
       Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()){
        NetworkInterface current = interfaces.nextElement();
        //System.out.println(current);
        if (!current.isUp() || current.isLoopback() || current.isVirtual()) 
            continue;
        Enumeration<InetAddress> addresses = current.getInetAddresses();
        while (addresses.hasMoreElements()){
            InetAddress current_addr = addresses.nextElement();
            if (current_addr.isLoopbackAddress()) 
                continue;
            if (current_addr instanceof Inet4Address)
              result = result.concat(current_addr.getHostAddress() + "\n");
            //else if (current_addr instanceof Inet6Address)
            // System.out.println(current_addr.getHostAddress());
            //System.out.println(current_addr.getHostAddress());
        }
    }
        }catch (Exception e){
        }
    return result;
   }
 
   
     
}
class WebGetThread extends Thread{
    private String URL = "";  // the target URL address

    /** Public constructor */
    public WebGetThread(String URL){
        this.URL = URL;
    }

    @Override
    public void run(){
        // get the page onto a string
        utils.internet.getTextFile(URL);
    }
    
     
}