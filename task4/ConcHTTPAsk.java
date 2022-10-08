import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
public class ConcHTTPAsk implements Runnable{
    public Socket connectSocket;
    public ConcHTTPAsk(Socket connectSocket){
        this.connectSocket = connectSocket;
    }
    public void run() {
        int BUFFERSIZE = 1;
	    boolean shutdown = false;
	    Integer limit = null;
	    Integer timeout = null;
	    String hostname = null;
	    int portnumber = 0;
	    String stringstr1 = null;
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] fromClientBuffer = new byte[BUFFERSIZE];
            boolean bool = true;
            int a = 0;
            while(bool == true){
                connectSocket.getInputStream().read(fromClientBuffer);
                byteArrayOutputStream.write(fromClientBuffer);
                if(fromClientBuffer[0] == '\n' || fromClientBuffer[0] == '\r')
                    a++;
                if(a == 4) bool = false;
            }
            byte[] buffer = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            for(int i = 0; i < buffer.length-1 ; i++){
                if(buffer[i] != '\r' && buffer[i+1] != '\n' ){
                    byteArrayOutputStream.write(buffer[i]);
                }
            }
            byte[] msg = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            String Sentence = new String(msg,StandardCharsets.UTF_8);
            //System.out.println("Sentense check = " + Sentence);
            if( Sentence.contains("hostname=") == false || Sentence.contains("port=") == false || Sentence.contains("/ask?") == false|| Sentence.contains("GET") == false){
                String badrequ = "HTTP/1.1 400 Bad Request\r\n\r\n";
                connectSocket.getOutputStream().write(badrequ.getBytes(StandardCharsets.UTF_8));
            }else{
                String[] annd = Sentence.split("&");
                String[] divid =annd[0].split("hostname=");
                hostname = divid[1];
                String[] p = Sentence.split("port=");
                if(p[1].contains("&") == false){
                    String[] po = p[1].split(" ");
                    String por = po[0];
                    portnumber = Integer.parseInt(por);
                }else{
                    String[] po = p[1].split("&");
                    String por = po[0];
                    portnumber = Integer.parseInt(por);
                }
                //System.out.println("portnumber check = " + portnumber);
                try{
                    if(Sentence.contains("&limit=") == true){
                        String[] divide = Sentence.split("&limit=");
                        if(divide[1].contains("&")){
                            String[] divide2 = divide[1].split("&");
                            limit = Integer.parseInt(divide2[0]);
                        }else{
                            String[] divide2 = divide[1].split(" ");
                            limit = Integer.parseInt(divide2[0]);
                        }
                    }
                    //System.out.println("limit check = " + limit);
                    if(Sentence.contains("&shutdown")==true){
                        shutdown = true;
                    }
                    //System.out.println("shutdown check = " + shutdown);
                    if(Sentence.contains("&timeout=") == true){
                        String[] divide = Sentence.split("&timeout=");
                        if(divide[1].contains("&")){
                            String[] divide2 = divide[1].split("&");
                            timeout = Integer.parseInt(divide2[0]);
                        }else{
                            String[] divide2 = divide[1].split(" ");
                            timeout = Integer.parseInt(divide2[0]);
                        }
                    }
                    //System.out.println("timeout check = " + timeout);
                    if(Sentence.contains("&string=") == true){
                        String[] divide = Sentence.split("&string=");
                        if(divide[1].contains("&")){
                            String[] divide2 = divide[1].split("&");
                            String stringstr = divide2[0];
                            stringstr1 = stringstr+"\n";
                        }else{
                            String[] divide2 = divide[1].split(" ");
                            String stringstr = divide2[0];
                            stringstr1 = stringstr+"\n";
                        }
                        byte[] string = stringstr1.getBytes(StandardCharsets.UTF_8);
                        String stringcheck = new String(string);
                        System.out.println("string check= " + stringcheck);
                        System.out.println("shutdown ="+ shutdown + " timeout ="+timeout +" limit =" + limit);
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        System.out.println("hostname ="+ hostname+ " portnumber ="+portnumber +" string =" + stringcheck);
                        byte[] output = tcpClient.askServer(hostname, portnumber, string);
                        byteArrayOutputStream.write(output);
                    }else{
                        byte[] string = new byte[0];
                        String stringcheck = new String(string);
                        System.out.println("string check= " + stringcheck);
                        System.out.println("shutdown ="+ shutdown + " timeout ="+timeout +" limit =" + limit);
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        System.out.println("hostname ="+ hostname+ " portnumber ="+portnumber +" string =" + stringcheck);
                        byte [] output = tcpClient.askServer(hostname, portnumber, string);
                        byteArrayOutputStream.write(output);
                    }
                    byte[] output = byteArrayOutputStream.toByteArray();
                    String outputcheck = new String(output);
                    System.out.println("output check ="+ outputcheck);
                    String ok = "HTTP/1.1 200 OK\r\n\r\n";
                    byte[] okbyte = ok.getBytes(StandardCharsets.UTF_8);
                    byte[] out = new byte[okbyte.length + output.length];
                    System.arraycopy(okbyte, 0 , out, 0, okbyte.length);
                    System.arraycopy(output, 0 , out, okbyte.length, output.length);
                    //String o = new String(out);
                    //System.out.println(o);
                    connectSocket.getOutputStream().write(out);
                }catch(Exception exception){
                    String notf = "HTTP/1.1 404 Not Found\r\n\r\n";
                    byte[] notbyte = notf.getBytes(StandardCharsets.UTF_8);
                    connectSocket.getOutputStream().write(notbyte);
                }
            }
            byteArrayOutputStream.close();
            connectSocket.close();
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    public static void main(String[] args) throws IOException{
        int port =  Integer.parseInt(args[0]);
        ServerSocket welcomeSocket = new ServerSocket(port);
        while(true){
            Socket connectSocket = welcomeSocket.accept();
            (new Thread(new ConcHTTPAsk(connectSocket))).start();
        }
    }
}
