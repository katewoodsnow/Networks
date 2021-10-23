import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class receiver {

	public static void main(String[] args) throws IOException {
		
		try(DatagramSocket socket = new DatagramSocket(9999)){
			byte[] buf = new byte[256];
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			
			//initializes packet variables 
			int expectedSN=0;
			
			int ack = 0;
			int [] ackArray;
			ByteBuffer message ;
			
			Boolean eof = false;
			
			while(true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				//The server, upon receipt, should examine the SN and if it 
				//matches the SN it is expecting, print the data to the screen 
				// or file and transmit ACK back to the sender,
				message = ByteBuffer.wrap(packet.getData(),
						0,packet.getLength());
				int SN = message.getInt();
				
				
				
				if ((packet.getLength()==5) && (SN == expectedSN)) {
					byte payload = message.get();
						System.out.print("RECEIVED FROM CLIENT: Packet number:" + SN);
						System.out.print(" - ");
						System.out.println("ASCII " + payload);
						result.write(payload);
						expectedSN ++;
						// Byte array to send packet to client
		            	byte[] sendPacket = new byte[1024 ];
		            	// Put the sequence number received from client into the bytes packet to send back to client
		            	sendPacket = ByteBuffer.allocate(4).putInt(SN).array();
	            	
		            	InetAddress clientAddress = packet.getAddress();
		            	int clientPort = packet.getPort();
	            	
		            	// Constructor to create the datagram to send the packet data from the server to the client
		            	DatagramPacket send= new DatagramPacket(sendPacket, sendPacket.length, clientAddress, clientPort);
		            	// Send the datagram packet to the server
		            	socket.send(send);
		            	System.out.println("Expecting packet:" + expectedSN);
					}
					//else {
						//System.out.println("Received out of order" + SN);
		            	/*byte[] sendPacket = new byte[1024 ];
		            	// Put the sequence number received from client into the bytes packet to send back to client
		            	sendPacket = ByteBuffer.allocate(4).putInt(expectedSN).array();
		            	InetAddress clientAddress = packet.getAddress();
		            	int clientPort = packet.getPort();
	            	
		            	// Constructor to create the datagram to send the packet data from the server to the client
		            	DatagramPacket send= new DatagramPacket(sendPacket, sendPacket.length, clientAddress, clientPort);
		            	// Send the datagram packet to the server
		            	socket.send(send);*/
		            //}
				
	            else if (packet.getLength()==4) {
	            	message = ByteBuffer.wrap(packet.getData(),0,
							packet.getLength());
		            	SN = message.getInt();
					
		            	//System.out.print( sequence);
		            	//System.out.print("-");
		            	System.out.println("Complete Data from Packages: " + new String(result.toByteArray(),
							Charset.forName("UTF8")));
		            	
		            	result = new ByteArrayOutputStream();
		            	eof = true;
	            }
	            else {
	            	System.out.println("Received out of order" + SN);
	            	
	            }
			}
		}
	}
}
