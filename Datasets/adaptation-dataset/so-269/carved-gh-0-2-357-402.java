public class foo{
   /**
    * Sends given response to the socket, and closes the socket.
    */
   private static void sendResponse(Socket socket, String status, String mimeType, Properties header, InputStream isInput, int sendCount, byte[] buf, String errMsg){
      try{
         OutputStream out = socket.getOutputStream();
         PrintWriter pw = new PrintWriter(out);
 
         {
            String retLine = "HTTP/1.0 " + status + " \r\n";
            pw.print(retLine);
         }
         if(mimeType!=null) {
            String mT = "Content-Type: " + mimeType + "\r\n";
            pw.print(mT);
         }
         if(header != null){
            Enumeration<?> e = header.keys();
            while(e.hasMoreElements()){
               String key = (String)e.nextElement();
               String value = header.getProperty(key);
               String l = key + ": " + value + "\r\n";
               if(debug) log(l);
               pw.print(l);
            }
         }
         pw.print("\r\n");
         pw.flush();
         if(isInput!=null)
            copyStream(isInput, out, buf, sendCount);
         else if(errMsg!=null) {
            pw.print(errMsg);
            pw.flush();
         }
         out.flush();
         out.close();
      }catch(IOException e){
         if(debug)
            log(e.getMessage());
      }finally {
         try{
            socket.close();
         }catch(Throwable t){
         }
      }
   }
}