public class foo{
      private void handleResponse(Socket socket){
         try{
            InputStream inS = socket.getInputStream();
            if(inS == null)
               return;
            byte[] buf = new byte[8192];
            int rlen = inS.read(buf, 0, buf.length);
            if(rlen <= 0)
               return;
 
            // Create a BufferedReader for parsing the header.
            ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
            BufferedReader hin = new BufferedReader(new InputStreamReader(hbis));
            Properties pre = new Properties();
 
            // Decode the header into params and header java properties
            if(!decodeHeader(socket, hin, pre))
               return;
            
            String range = pre.getProperty("range");
 
            Properties headers = new Properties();
            if(fileSize!=-1)
               headers.put("Content-Length", String.valueOf(fileSize));
            headers.put("Accept-Ranges", canSeek ? "bytes" : "none");
 
            int sendCount;
 
            String status;
            if(range==null || !canSeek) {
               status = "200 OK";
               sendCount = (int)fileSize;
            }else {
               if(!range.startsWith("bytes=")){
                  sendError(socket, HTTP_416, null);
                  return;
               }
               if(debug)
                  log(range);
               range = range.substring(6);
               long startFrom = 0, endAt = -1;
               int minus = range.indexOf('-');
               if(minus > 0){
                  try{
                     String startR = range.substring(0, minus);
                     startFrom = Long.parseLong(startR);
                     String endR = range.substring(minus + 1);
                     endAt = Long.parseLong(endR);
                  }catch(NumberFormatException nfe){
                  }
               }
 
               if(startFrom >= fileSize){
                  sendError(socket, HTTP_416, null);
                  inS.close();
                  return;
               }
               if(endAt < 0)
                  endAt = fileSize - 1;
               sendCount = (int)(endAt - startFrom + 1);
               if(sendCount < 0)
                  sendCount = 0;
               status = "206 Partial Content";
               ((RandomAccessInputStream)is).seek(startFrom);
 
               headers.put("Content-Length", "" + sendCount);
               String rangeSpec = "bytes " + startFrom + "-" + endAt + "/" + fileSize;
               headers.put("Content-Range", rangeSpec);
            }
            sendResponse(socket, status, fileMimeType, headers, is, sendCount, buf, null);
            inS.close();
            if(debug)
               log("Http stream finished");
         }catch(IOException ioe){
            if(debug)
               ioe.printStackTrace();
            try{
               sendError(socket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }catch(Throwable t){
            }
         }catch(InterruptedException ie){
            // thrown by sendError, ignore and exit the thread
            if(debug)
               ie.printStackTrace();
         }
      }
}