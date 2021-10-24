import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.util.*;

/* Stop and Wait UDP - Receiver */

public class Server {

	public static void main(String[] args) throws IOException {

		// Listen for messages from the sender on the same port number as the sender
		try (DatagramSocket socket = new DatagramSocket(9999)) {

			/*
			 * Create a byte array to hold the message received from the sender and use in
			 * the Datagram packet
			 */
			byte[] buf = new byte[256];

			// Holds the end result after all the packets have been received
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			// The socket is always listening for new messages from the sender
			while (true) {

				// Datagram packet to receive the packet from the sender
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				// Receive the packet
				socket.receive(packet);

				/*
				 * Make the acknowledgement fail when the random number is odd to test the stop
				 * and wait application
				 */
				Random random = new Random();
				int probability = random.nextInt(50);

				// Initialise the sequence number
				int SN = 0;
				
				int expectedSN = 0;

				/*
				 * If the random number generated is even the packet will be received and ack
				 * sent back to the sender. Check to see if it is not the end of the file
				 */
				if ((packet.getLength() == 5) & ((probability % 2) == 0)) {

					// Get the sequence number and the character of umbrella as ASCII from the
					// packet
					ByteBuffer message = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
					SN = message.getInt();
					byte payload = message.get();

					// Print results
					System.out.print("RECEIVED FROM CLIENT: Packet number: " + SN);
					System.out.print(" - ");
					System.out.println("ASCII " + payload);

					// Store the message received from the sender so it can be put together at the
					// end
					result.write(payload);

					// Byte array to send acknowledgement of packet received back to the sender
					byte[] sendPacket = new byte[1024];

					// Put the sequence number received into the bytes packet to send back to sender
					sendPacket = ByteBuffer.allocate(4).putInt(SN).array();

					/*
					 * Get the port number and IP address from the packet received, so the receiver
					 * knows where to send the acknowledgement back too
					 */
					InetAddress clientAddress = packet.getAddress();
					int clientPort = packet.getPort();

					// Constructor to create the datagram to send the ack of packet to the sender
					DatagramPacket send = new DatagramPacket(sendPacket, sendPacket.length, clientAddress, clientPort);

					// Send the datagram packet to the server
					socket.send(send);
					
					expectedSN ++;

					// The packet is the last packet sent with only the sequence number to indicate
					// end of file
				} else if (packet.getLength() == 4) {

					// Get the sequence number from the packet
					ByteBuffer message = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
					SN = message.getInt();

					// Print out the result of all the packets in order. The string "umbrella"
					System.out.println("Complete Data from Packages: " + new String(result.toByteArray(), Charset.forName("UTF8")));

					result = new ByteArrayOutputStream();

					/*
					 * If no packets were received then print to screen for reference and no ack is
					 * sent back to the sender
					 */
				} else {
					System.out.println("The packet was not received");
				}
			}
		}
	}
}
