public class foo{
    public void sendPackage(Gamer gamerToSend)
        throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);
        
        // Game type
        out.writeInt(1);
        
        // Client
        out.writeInt(gamerToSend.getClientID());
        
        // Position
        out.writeFloat(gamerToSend.getX());
        out.writeFloat(gamerToSend.getY());
        
        DatagramPacket packet = new DatagramPacket
            (
                byteOut.toByteArray(),
                byteOut.size(),
                gamerToSend.getInetAddress(),
                3887
            );
        
        this.socket.send(packet);
        
        byteOut.close();
        out.close();
    }
}