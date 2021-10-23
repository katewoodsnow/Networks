import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.File;


public class sender
{

	public static void main(String[] args) throws IOException {
		
		//takes the file name as command line arguments
		/* Get the file path which includes the data to send to the server -
		the string 'umbrella'*/
		File file = new File("file.txt");
		// Read the file as bytes and put into an array
		byte[] fileContent;
		
		fileContent = Files.readAllBytes(file.toPath());
		

		//takes the port number as command line arguments
		if (args.length!=1) {
			System.out.println("Please enter usage host:port as argument"
					+ ": <host:port>");
			return;
		}

		String[] hostPort = args[0].split(":");
		String host = hostPort[0];
		int port = Integer.parseInt(hostPort[1]);
		InetAddress address = InetAddress.getByName(host);

		
		//create client socket
		/* Try with resources to close the socket automatically
		when finished with. Create a new sender socket */
		try (DatagramSocket socket = new DatagramSocket()){
			socket.setSoTimeout(5000);

			//initializes window variables (upper and lower window bounds, position of next seq number)
			int base=0;
			int SN=0;
			int windowSize=5;
			ByteBuffer message;
		
			// List of sequence numbers sent
			ArrayList<byte[]> sent = new ArrayList<byte[]>();
			System.out.print(sent);
			
			

			//while the array list is full or it is not end of the file
			// should not enter when all the packets have been received because it deletes
			// them out of the array list, so array list becomes empty
			// but it should enter at the beginning because it is 
			// not end of file
			while((!sent.isEmpty()) || (SN <= fileContent.length)) {
					//check if the window is full	or EOF has reached should stop
				// entering when the window is full or end of file.
					while((SN<base+windowSize) && (SN <= fileContent.length)) {
						//Send data
						
						//create packet(seqnum,data,checksum)
						/* For each byte create a byte buffer 5 bytes long, 4
						bytes for the sequence number and 1 byte for the payload*/
						message = ByteBuffer.allocate(5);
						// Add the sequence number to the bytebuffer
						message.putInt(SN);
						byte payload = fileContent[SN];
						// Add the payload to the byte buffer
						message.put(payload);	
						/* Encapsulate the data - payload and
						sequence number, the servers port and IP address into a datagram packet*/
						DatagramPacket packet = new DatagramPacket(message.array(),5,address, port);
						
						
						// make the acknowledgement fail when the random number is odd to test application
			            Random random = new Random();
			            int chance = random.nextInt(100);
			            //if ((chance % 2) == 0)	{	
						// Send the packet
						socket.send(packet);
			            //}
						System.out.print("Sent data " + SN  + System.lineSeparator());
						//append packet to window array list
						byte [] sentMessage = message.array();
						sent.add(sentMessage);
						SN++;
			            

					}

					Boolean timer = true;
					
					while (timer) {
						try {
							//RECEIPT OF AN ACK
							
							// Byte array to receive the response from the server
							byte[] receivePacket = new byte[1024];
							/* Create a datagram packet to accept the data from the server's 
							 response.*/
							DatagramPacket received = new DatagramPacket(receivePacket, receivePacket.length);
							// Receive the data from the server.
							socket.receive( received );
							message = ByteBuffer.wrap(received.getData(),
									0, received.getLength());
							int receivedSN = message.getInt();
							System.out.println("RECEIVED FROM SERVER: Packet " + receivedSN);
		
							//slide window and reset timer
							sent.remove(0);
							base ++;
							timer = false;
							
							
					
						}catch (SocketTimeoutException e) {
						// No response has been received from the server before the time runs out.
							for (int i = base; i < SN; i ++){
								byte[] notSent = sent.get(i);
								//ByteBuffer buffer = ByteBuffer.wrap(notSent);
								DatagramPacket packet = new DatagramPacket(notSent,5,address, port);
								// Send the packet
								socket.send(packet);	
							}	
						}	
					}	
			}
					
					/* Send an empty packet for eof 4 bytes long to include the
					the sequence number*/
					ByteBuffer Lastmessage = ByteBuffer.allocate(4);
					// Add the sequence number to the packet
					Lastmessage.putInt(SN);
					/* Encapsulate the sequence number with the port and IP address
					in a datagram packet */
					DatagramPacket packet = new DatagramPacket(Lastmessage.array(), 4, address, port);
					// Send the packet to the server
					socket.send(packet);		
		}
	}		
}		
