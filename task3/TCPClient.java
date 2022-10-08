import java.net.*;
import java.io.*;
public class TCPClient {
    private int timeout = 0;
    private int limit = 0;
    private boolean shutdown = false;
    private int BUFFERSIZE = 1024;
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) throws IOException {
        if(timeout != null){
            this.timeout = timeout;
        }
             
        if(limit != null){
            this.limit = limit;
            BUFFERSIZE = limit;
        }
            
        if(shutdown == true ){
            this.shutdown = shutdown;
        }
    }
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
        ByteArrayOutputStream b2= new ByteArrayOutputStream(); 
        SocketAddress socketAddress = new InetSocketAddress(hostname, port);
        Socket clientSocket = new Socket();
        clientSocket.connect(socketAddress);
        b2.write(toServerBytes);
        int fromUserLength = b2.size();
        clientSocket.getOutputStream().write(toServerBytes , 0 , fromUserLength);
        byte[] buffer = new byte[BUFFERSIZE];

        int len = 0;
        if(this.shutdown){
            clientSocket.shutdownOutput();
            System.out.println("Shutdown");
        }
        clientSocket.setSoTimeout(this.timeout);   
        try{len = clientSocket.getInputStream().read(buffer);
        }
        catch(SocketTimeoutException exception){System.out.println("Timeout");}
        boolean bool = true;
        if(this.limit == 0){
            clientSocket.setSoTimeout(this.timeout);
            while(bool){
                for(int i = 0; i < len ; i++)
                    byteArrayOutputStream.write(buffer[i]); 
                try{len = clientSocket.getInputStream().read(buffer); 
                    if(len == -1)
                        bool = false;
                }
                catch(SocketTimeoutException exception){ 
                    bool = false;
                    System.out.println(exception);}
            }
        }
        else{
            clientSocket.setSoTimeout(this.timeout);
            while(bool){
                for(int i = 0; i < len ; i++)
                    byteArrayOutputStream.write(buffer[i]);
                try{
                    len = clientSocket.getInputStream().read(buffer, 0 , this.limit-len);
                    if(byteArrayOutputStream.size() == this.limit || len <= -1){
                        bool = false;
                    }
                }
                catch(SocketTimeoutException exception){bool = false;}
                catch(java.lang.Exception exception){bool = false;}
            }
        }

        System.out.println("bytesize = " + byteArrayOutputStream.size());
        b2.close();
        byteArrayOutputStream.close();
        clientSocket.close();
        return byteArrayOutputStream.toByteArray();
    }
}

 