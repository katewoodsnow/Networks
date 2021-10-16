import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class server{

	

	public static void main(String[] args) throws IOException {
		
		InetAddress destination = InetAddress.getByName("localHost");
		
		receiveMessage(destination, 9876);
	}
		
		
	// Method for the server to receive the packet from the client. 	
	private static void receiveMessage(InetAddress destination, int port) throws SocketException, IOException {
			
		
		// Create a server socket with the port number to start connection
		DatagramSocket serverSocket = new DatagramSocket(port);
		System.out.println("Server started....");

		// Set up byte arrays for sending/receiving data
        
		// Create Byte array of a KB to receive the packet of unknown size.
		byte[] receivePacket = new byte[1024];
        

        // Infinite loop to check for connections 
        while(true){

        	// Create the datagram for the server to receive the packet, passing in the data and the length of the data.
        	DatagramPacket received = new DatagramPacket( receivePacket, receivePacket.length );
          	// Using the receive method of the datagram socket class to receive the packet passing in the packet.
        	serverSocket.receive(received);
        	System.out.println("Receiving packet...");
          	
          	

          	// Get the data from the packet // returns data in datagram as a byte array. and then wraps that
        	// byte array into a buffer. 
        	ByteBuffer buffer = ByteBuffer.wrap(received.getData());
			// Gets the sequence number of the package
        	int seq = buffer.getInt();
        	
			// Gets the 5th byte of package
        	byte payload = buffer.get();
        	
        	//String string = new String (payload, Charset.forName("UTF-8"));
        	
        	//Path path = Paths.get("src/file.txt");
        	//Files.write(path, payload);
        	
        	//receivePacket = received.getData();
        	//String payload = new String(receivePacket, 0, received.getLength()); 
        	//String sentence = new String(received.getData());
        	
        	
			
        	// make the acknowledgement fail when the random number is odd to test application
            Random random = new Random();
            int chance = random.nextInt(100);
            // If random number generated is even the packet will be received and sent back to the client.
            if( ((chance % 2) == 0) ){
            	// Print out the message received with the sequence number
            	System.out.println("CLIENT RECEIVED PACKET: " + seq + payload);

            	// send the packet to client
            	// Get packet's port number
            	int packetPort = received.getPort();

            	// Byte array to send packet to client
            	byte[] sendPacket = new byte[1024 ];
             
            	// Put the sequence number received from client into the bytes packet to send back to client
            	sendPacket = ByteBuffer.allocate(4).putInt(seq).array();

            	// Constructor to create the datagram to send the packet data from the server to the client
            	DatagramPacket send = new DatagramPacket(sendPacket, sendPacket.length, destination, packetPort);
            	// Send the datagram packet to the server
            	serverSocket.send(send); 
            	} else {
            		//The random number was odd and the package was not received by the server and the server does
            		// not send a packet back to the client.
            		System.out.println("Packet: "+ seq + " was dropped");
            	}
            
       	}
        //serverSocket.close();
	}
}
