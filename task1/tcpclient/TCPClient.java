package tcpclient;
import java.net.*;
import java.io.*;
//1. send & receive bytes on OutputStream & InputStream objects
//                  ---OutputStream.write(), InputStream.read()
//2. read all data from the server ----cant know the how much data 
//                                   ---cant use a fixed-size buffer
//store data in buffer that grows dynamically with the amout of data 
//                                  --- ByteArrayOutputStream.write()                            
public class TCPClient {
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
        Socket clientSocket = new Socket(hostname, port);

        byteArrayOutputStream.write(toServerBytes);
        int fromUserLength = toServerBytes.length;

        clientSocket.getOutputStream().write(toServerBytes , 0 , fromUserLength);
        byte[] fromServerBuffer = clientSocket.getInputStream().readAllBytes();
        
        clientSocket.close();
        return fromServerBuffer;
    }
}
