import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* Go-Back-N retransmission, sliding window of 5. Receiver */
public class Receiver {

	public static void main(String[] args) throws IOException {

		// Listen for messages from the sender on the same port number as the sender
		try (DatagramSocket socket = new DatagramSocket(9999)) {

			/*
			 * Create a byte array to hold the message received from the sender and use it in the
			 * Datagram packet
			 */
			byte[] buf = new byte[256];

			// Holds the end result after all the packets have been received
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			/*
			 * Variable to hold the sequence number of the next packet expected to be
			 * received, to ensure the packets are received in order or has not be lost.
			 */
			int expectedSN = 0;

			ByteBuffer message;

			// The socket is always listening for new messages from the sender
			while (true) {

				// Datagram packet to receive the packet from the sender
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				// Receive the packet
				socket.receive(packet);

				// Get the sequence number from the packet received
				message = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
				int SN = message.getInt();

				/*
				 * Check to see if the packet received is not the last one to indicate that it
				 * is the end of the file. Check to see if the sequence number from the packet
				 * received is the same as the sequence number that the receiver is expecting,
				 * to ensure that no packets previously have been lost or this one is out of
				 * order
				 */
				if ((packet.getLength() == 5) && (SN == expectedSN)) {

					/*
					 * Get the payload from the packet - this is a character of the string
					 * "Umbrella" in ASCII
					 */
					byte payload = message.get();

					// Print out the sequence number and ASCII character
					System.out.print("RECEIVED FROM CLIENT: Packet number:" + SN);
					System.out.print(" - ");
					System.out.println("ASCII " + payload);

					// Store each character received, which should be in order of them being sent
					result.write(payload);

					/*
					 * Go to the next expected sequence number that should be received so it can be
					 * compared to the actual sequence number received.
					 */
					expectedSN++;

					// Byte array to send an acknowledgement of receipt of the packet back to the
					// sender.
					byte[] sendPacket = new byte[1024];

					// Put the sequence number received from the sender into the bytes packet to
					// send back to the sender
					sendPacket = ByteBuffer.allocate(4).putInt(SN).array();

					/*
					 * Get the port number and IP address from the packet received, so the receiver
					 * knows where to send the acknowledgement back too
					 */
					InetAddress clientAddress = packet.getAddress();
					int clientPort = packet.getPort();

					// Constructor to create the datagram to send the acknowledgement to the sender
					DatagramPacket send = new DatagramPacket(sendPacket, sendPacket.length, clientAddress, clientPort);

					// Send the datagram packet to the server
					socket.send(send);
				}

				/*
				 * Check to see if the packet received is the last packet. It contains just the
				 * last sequence number but no data
				 */
				else if (packet.getLength() == 4) {
					message = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
					SN = message.getInt();

					// Print out the string of all the characters received from the sender. Should
					// be "umbrella"
					System.out.println("Complete Data from Packages: " + new String(result.toByteArray(), Charset.forName("UTF8")));

					result = new ByteArrayOutputStream();
				}
				/*
				 * If not a packet that is the end of the file or that matches the sequence
				 * number expected, then the packet has failed to be sent in order or at all.
				 * Print fail to screen for reference and no ack is sent back to the sender.
				 */
				else {
					System.out.println("Failed");

				}
			}
		}
	}
}
