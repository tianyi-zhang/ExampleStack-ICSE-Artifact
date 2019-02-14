public class foo{
      private boolean decodeHeader(Socket socket, BufferedReader in, Properties pre) throws InterruptedException{
         try{
            // Read the request line
            String inLine = in.readLine();
            if(inLine == null)
               return false;
            StringTokenizer st = new StringTokenizer(inLine);
            if(!st.hasMoreTokens())
               sendError(socket, HTTP_BADREQUEST, "Syntax error");
 
            String method = st.nextToken();
            if(!method.equals("GET"))
               return false;
 
            if(!st.hasMoreTokens())
               sendError(socket, HTTP_BADREQUEST, "Missing URI");
 
            while(true) {
               String line = in.readLine();
               if(line==null)
                  break;
   //            if(debug && line.length()>0) log(line);
               int p = line.indexOf(':');
               if(p<0)
                  continue;
               final String atr = line.substring(0, p).trim().toLowerCase();
               final String val = line.substring(p + 1).trim();
               pre.put(atr, val);
               
               if (debug)
            	   log ("header=" + atr + ": " + val);
            }
         }catch(IOException ioe){
            sendError(socket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
         }
         return true;
      }
}