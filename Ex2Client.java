

/*  Oscar Alcaraz
	CS 380 Networks
	Exercise 2
*/


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;


public class Ex2Client {

	
	//Will Connect to the Server and 
	//receive and send CRC.
    public static void main(String[] args) {

        try {
        	
            Socket socket = new Socket("18.221.102.182",38102);

            System.out.println("\nSuccesfully Connected to Server!");
            System.out.println("\nBytes Received: \n");
            
            long crc = generateCRC( receiveBytes(socket) );
            sendCRC(socket, crc);

            String validResponse = (checkResponse(socket)) ? "Valid Response.\n" : "Invalid Response.\n";
            System.out.println(validResponse);

            socket.close();
            System.out.println("Disconneced from Server!");
            
        } 
        catch (Exception e) { e.printStackTrace(); }
    }

    //Generates a new CRC based on the bytes received from the server
    public static long generateCRC(byte[] bytes) {
    	
        CRC32 redoCRC = new CRC32();
        redoCRC.reset();
        redoCRC.update(bytes, 0, 100);
        
        long crc = redoCRC.getValue();
        System.out.println( "\n\nGenerated CRC32: " + Long.toHexString(crc).toUpperCase() );
        
        return crc;
    }
    
    //Receives the Bytes from the Server
    //will only get one half at a time.
    public static byte[] receiveBytes(Socket socket) {
    	
        try {
            
            InputStream inStream = socket.getInputStream();
            
            int count = 0;
            byte[] messageByte = new byte[100];
            
            for(int k = 0; k < 100; ++k) {

                if(count == 10) {
                	
                    System.out.println();
                    count = 0;
                }

                //Shift bytes to meet requirements of message
                int originalFirstHalf = inStream.read();
                int firstHalf = 16 * originalFirstHalf;
                int secondHalf = inStream.read();
           
                firstHalf += secondHalf;
                
                messageByte[k] = (byte) firstHalf;
               
                //Prints the Bytes in HEX.
                String first = Integer.toHexString(originalFirstHalf).substring(0,1).toUpperCase();
                
                String second = Integer.toHexString(secondHalf).substring(0,1).toUpperCase();

                System.out.print(first + second);
                ++count;
                
            }
           
            return messageByte;
            
        } catch (Exception e) {}
        
        return null;
    }

    

    //Sends the CRC to the Server as a byte array
    public static void sendCRC(Socket socket, long crc) {
    	
        try {
        	
            OutputStream outStream = socket.getOutputStream();
            ByteBuffer bytes = ByteBuffer.allocate(4);
            
            bytes.putInt( (int) crc);
            byte[] byteArray = bytes.array();

            outStream.write(byteArray);
            
        } catch (Exception e) {}
    }

    
    //Verifies whether or not the CRC is correct
    public static boolean checkResponse(Socket socket) {
    	
        try {
        	
            InputStream inStream = socket.getInputStream();
            int valid = inStream.read();
            
            return (valid == 1) ? true : false;
            
        } catch (Exception e) { }
        
        return false;
    }
    
}