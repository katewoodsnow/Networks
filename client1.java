import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.File;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;



public class client{

	public static void main(String args[]) throws IOException, SocketException
	{
		/* Get the file path which includes the data to send to the server -
		the string 'umbrella'*/
		File file = new File("file.txt");
		
		// Read the file as bytes and put into an array
		byte[] fileContent = Files.readAllBytes(file.toPath());
		
		/* If the user does not enter one argument when running the program
		the system will quit.*/
		if (args.length!=1) {
			System.out.println("Please enter usage host:port as argument"
					+ ": <host:port>");
			return;
		}

		String[] hostPort = args[0].split(":");
		String host = hostPort[0];
		int port = Integer.parseInt(hostPort[1]);
		InetAddress address = InetAddress.getByName(host);
		
		/* Try with resources to close the socket automatically
		when finished with. Create a new sender socket */
		try (DatagramSocket socket = new DatagramSocket()){
			// Initialise a sequence number to identify each packet
			int SN = 0;
			
			// Send each byte to the server
			/* Loop over each byte (character of umbrella) from the file
		     in the byte array and put each into a payload.*/
			for (int i = 0; i < fileContent.length; i++) {
				
				byte payload = fileContent[i];
				socket.setSoTimeout(5000);
				
				Boolean timer = true;
		
				while (timer) {
					try {
				/* For each byte create a byte buffer 5 bytes long, 4
				bytes for the sequence number and 1 byte for the payload*/
				ByteBuffer message = ByteBuffer.allocate(5);
				// Add the sequence number to the bytebuffer
				message.putInt(SN);
				// Add the payload to the byte buffer
				message.put(payload);	
				/* Encapsulate the data - payload and
				sequence number, the servers port and IP address into a datagram packet*/
				DatagramPacket packet = new DatagramPacket(message.array(),5,address, port);
				// Send the packet
				socket.send(packet);
	
					// Byte array to receive the response from the server
					byte[] receivePacket = new byte[1024];
					/* Create a datagram packet to accept the data from the server's 
			 		response.*/
					DatagramPacket received = new DatagramPacket(receivePacket, receivePacket.length);
					// Receive the data from the server.
					socket.receive( received );
					System.out.println("RECEIVED FROM SERVER: Packet " + SN);
					SN++;
					
				
					timer = false;
					
					
					}catch (SocketTimeoutException e) {
						// No response has been received from the server before the time runs out.
						System.out.println("Packet " +SN + " not received."
								+ "resending...");
						
						
					}
				}
			}
				/* Send an empty packet for eof 4 bytes long to include the
				the sequence number*/
				ByteBuffer message = ByteBuffer.allocate(4);
				// Add the sequence number to the packet
				message.putInt(SN);
				/* Encapsulate the sequence number with the port and IP address
				in a datagram packet */
				DatagramPacket packet = new DatagramPacket(message.array(), 4, address, port);
				// Send the packet to the server
				socket.send(packet);
			
			
		}
		
	}
}
	
