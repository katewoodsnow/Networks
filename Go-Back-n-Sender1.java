import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.File;

/* Go-Back-N retransmission, sliding window of 5. Sender */

public class Sender {

	// Exceptions in the file are handled here
	public static void main(String[] args) throws IOException {

		/*
		 * Get the file path which includes the data to send to the server - the string
		 * 'umbrella'
		 */
		File file = new File("file.txt");

		// To store the bytes of the file
		byte[] fileContent;
		
		// Read the file as bytes and store in an array
		fileContent = Files.readAllBytes(file.toPath());

		// Checks the user has added the port number and hostname
		if (args.length != 1) {
			System.out.println("Please enter usage host:port as argument" + ": <host:port>");
			return;
		}
		
		/*
		 * The port number and host name of the receiver is inputed at the command line
		 * to make sure the the port is free to use. Used localhost:9999 - the same
		 * port number as the receiver
		 */
		String[] hostPort = args[0].split(":");
		String host = hostPort[0];
		int port = Integer.parseInt(hostPort[1]);
		InetAddress address = InetAddress.getByName(host);

		/*
		 * Create a new socket to open communication. Try with resources used to close
		 * the communication when finished with.
		 */
		try (DatagramSocket socket = new DatagramSocket()) {

			// The start sequence number of the window
			int base = 0;
			// Sequence number of the packet being sent and acknowledged
			int SN = 0;
			// Size of the window - how many packets can be sent at once.
			int windowSize = 5;

			// Keeps a list of data sent to the receiver
			ArrayList<byte[]> sent = new ArrayList<byte[]>();

			/*
			 * A loop to receive all the acknowledgements from the receiver even
			 * after all the packets have been sent. Breaks when the list of
			 * sent packets is empty as each packet is deleted from the list once they are
			 * received. Or when all the data from the file has been sent. It will then enter the loop
			 * initially when no packets have been sent yet.
			 */
			while ((!sent.isEmpty()) || (SN < fileContent.length)) {

				/*
				 * Sends all the packets in the window to the receiver, the loop breaks once the sequence number
				 * is higher than the window size or all data has been sent.
				 */
				while ((SN < base + windowSize) && (SN < fileContent.length)) {

					/*
					 * For each byte, which is a character in the string
					 * 'umbrella', creates a byte buffer 5 bytes long. 4 bytes for the sequence
					 * number and 1 byte for the payload
					 */
					ByteBuffer message = ByteBuffer.allocate(5);
					
					// Add the sequence number to the byte buffer
					message.putInt(SN);
					
					/*
					 * Puts the byte for the character needed to be sent from the content 
					 * of the file
					 */
					message.put(fileContent[SN]);
					
					// Byte array for the buffer, containing the message to send in the datagram
					byte[] sendMessage = message.array();
						
					/*
					 * Encapsulate the data - the payload, sequence number, the servers port and IP
					 * address into a datagram packet
					 */
					DatagramPacket packet = new DatagramPacket(sendMessage, 5, address, port);
					
					/*
					 * Add the payload and sequence number that has been sent to a list to keep
					 * track in case retransmission of the window is needed.
					 */
					sent.add(sendMessage);
					
					/*
					 * Simulate a sent packet failing to demonstrate retransmission of the data in
					 * in the window. It will fail if the random number generated out of 50 is odd
					 */
					Random random = new Random();
					int probability = random.nextInt(50);
					
					if ((probability % 2) == 0) {
						// Send the packet to the receiver
						socket.send(packet);
						System.out.print("Sent data " + SN + System.lineSeparator());
					} else {
						System.out.print("Sent data but failed " + SN + System.lineSeparator());
					}
					// Repeat for all the packets in the window
					SN++;
				}

				Boolean timer = true;
				
				/* Set the time for the sender to wait to receive an acknowledgement from the receiver 
				that the corresponding packet has been sent, if the acknowledgement has not been received within the 
				time, retransmission of all data in the window happens */
				socket.setSoTimeout(2000);
				
				// Receive the packet from the receiver within a time frame
				while (timer) {
					
					// Try catch to deal with failed acknowledgements within the time frame
					try {
						
						// Creates a byte array to use in the datagram to receive the acknowledgement
						byte[] receivePacket = new byte[1024];
						
						/*
						 * Create a datagram packet to accept the packet sent from the receiver
						 */
						DatagramPacket received = new DatagramPacket(receivePacket, receivePacket.length);
						
						// Receive the acknowledgement from the server
						socket.receive(received);
						
						// Get the sequence number of the packet being acknowledged for reference
						ByteBuffer messageReceived = ByteBuffer.wrap(received.getData(), 0, received.getLength());
						int receivedSN = messageReceived.getInt();
						System.out.println("RECEIVED FROM SERVER: Packet " + receivedSN);
						
						/*
						 * Remove the packet that corresponds to the acknowledgement received from the list of 
						 * packets sent. The packets
						 * received should be in the order that they were sent, so remove the first from
						 * the list.
						 */
						sent.remove(0);
						
						/* If an acknowledgement of the corresponding packet has been received, slide the window across by one, to the next sequence number 
						 of the packets so it can be sent.*/
						base++;
						
						/*
						 * Break out of the timer loop if the packet has been received, the program will
						 * then go back to the outer while loop to check if there are more packets to be
						 * received or there is no more data to send
						 */
						timer = false;
						
						// Retransmission of data
					} catch (SocketTimeoutException e) {
						
						/*
						 * If no acknowledgement from the receiver has been received by the sender, it
						 * will time out assume the packet has been lost and resend all the data in the window 
						 * from the missing
						 * acknowledgement/ the ones in the current window.
						 */
						System.out.println("resending...");
						
						// Loops through all the data in the window
						for (int i = 0; i < sent.size(); i++) {
							
							// Place data in a byte array ready to be sent in a datagram packet
							byte[] notSent = sent.get(i);
							
							/*
							 * Encapsulate into a datagram packet along with the port number and IP address
							 */
							DatagramPacket packet = new DatagramPacket(notSent, 5, address, port);
							
							// Resend packet
							socket.send(packet);
							
							// Gets the sequence number for reference
							ByteBuffer wrapped = ByteBuffer.wrap(notSent);
							int resendSN = wrapped.getInt();
							System.out.println("Resent " + resendSN);
						}
					}
				}
			}
			
			/*
			 * Send an empty packet for end of file, create a buffer, 4 bytes long to
			 * include the sequence number
			 */
			ByteBuffer Lastmessage = ByteBuffer.allocate(4);
			
			// Add the sequence number to the data
			Lastmessage.putInt(SN);
			
			/*
			 * Encapsulate the sequence number with the port and IP address in a datagram
			 * packet
			 */
			DatagramPacket packet = new DatagramPacket(Lastmessage.array(), 4, address, port);
			
			// Send the packet to the server
			socket.send(packet);
		}
	}
}
