import java.io.IOException;
import java.nio.file.Files;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.File;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

/* Stop and wait UDP - Sender */

public class Sender {

	public static void main(String args[]) throws IOException, SocketException {
		
		/*
		 * Get the file path which includes the data to send to the server - the string
		 * 'umbrella'
		 */
		File file = new File("file.txt");

		// Read the file as bytes and put the bytes into an array
		byte[] fileContent = Files.readAllBytes(file.toPath());

		/*
		 * If the user does not enter the port number and hostname of the receiver at the
		 * command line the system will quit.
		 */
		if (args.length != 1) {
			System.out.println("Please enter usage host:port as argument" + ": <host:port>");
			return;
		}

		// Get the hostname and port number at the command line
		String[] hostPort = args[0].split(":");
		String host = hostPort[0];
		int port = Integer.parseInt(hostPort[1]);
		InetAddress address = InetAddress.getByName(host);

		/*
		 * Try with resources to close the socket automatically when finished with.
		 * Create a new sender socket open the communication
		 */
		try (DatagramSocket socket = new DatagramSocket()) {
			
			// Initialise a sequence number to identify each packet
			int SN = 0;

			/*Send each byte to the server
			 * Loop over each byte (character of umbrella) from the file in the byte array
			 */
			for (int i = 0; i < fileContent.length; i++) {
				
				// Store byte
				byte payload = fileContent[i];
				
				/* Set the timer to wait for an acknowledgement from the receiver. If not received
				within the time, will resend the packet which corresponds to the ack sequence number */
				socket.setSoTimeout(2000);

				Boolean timer = true;

				// Waiting for response within the time limit
				while (timer) {
					// try catch to handle the exception if the time runs out without receiving ack
					try {
						
						/*
						 * For each byte in the file create a byte buffer 5 bytes long, 4 bytes for the sequence
						 * number and 1 byte for the character of umbrella - the payload
						 */
						ByteBuffer message = ByteBuffer.allocate(5);
						
						// Add the sequence number to the bytebuffer
						message.putInt(SN);
						
						// Add the payload to the byte buffer
						message.put(payload);
						
						/*
						 * Encapsulate the data - payload and sequence number, the servers port and IP
						 * address into a datagram packet
						 */
						DatagramPacket packet = new DatagramPacket(message.array(), 5, address, port);
						
						// Send the packet to the receiver
						socket.send(packet);

						// Byte array to receive the acknowledgement from the server
						byte[] receivePacket = new byte[1024];
						
						/*
						 * Create a datagram packet to accept the ack from receiver.
						 */
						DatagramPacket received = new DatagramPacket(receivePacket, receivePacket.length);
						
						// Receive the ack from the receiver.
						socket.receive(received);
						System.out.println("RECEIVED FROM SERVER: Ack " + SN);
						
						// Increase the sequence number to the next one for the next packet to send
						SN++;
						
						/* Exit the loop if the ack has been received within the time so the next packet
						can be sent and ack received */
						timer = false;

					/* If an ack is not received, it will timeout and will try to resend the same packet
					with the same sequence number */
					} catch (SocketTimeoutException e) {
						
						System.out.println("Ack " + SN + " not received. " + System.lineSeparator() +
								"resending packet " + SN  + "...");
					}
				}
			}
			
			/*
			 * Send an empty packet to the receiver for eof 4 bytes long to include the the sequence number only
			 */
			ByteBuffer message = ByteBuffer.allocate(4);
			
			// Add the sequence number to the packet
			message.putInt(SN);
			
			/*
			 * Encapsulate the sequence number with the port and IP address in a datagram
			 * packet
			 */
			DatagramPacket packet = new DatagramPacket(message.array(), 4, address, port);
			
			// Send the packet to the server
			socket.send(packet);

		}

	}
}
