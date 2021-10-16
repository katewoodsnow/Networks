import java.io.*;
import java.nio.file.Files;
import java.net.*;
import java.nio.ByteBuffer;


public class client
{
	public static void main(String args[]) throws Exception
	{
		
		
		// Create instance of inetaddress to set the destination ip address to the computer the program is running off,
		// - local host as not connected by LAN and set by it's name.
		InetAddress destination = InetAddress.getByName("localHost");
		
		// Call the method passing in the file path and the ip address of the server.
		// This method creates the data package and also calls the method to send the package to the 
		// server.
		createDataPackets("src/file.txt", destination);
		
		
		
	}

	// Method to send package from client to server with 3 parameters, data (character of "umbrella" from the file) 
	// plus the sequence number, the ip address of the server and the port number.
	private static void sendMessage(byte[] message, InetAddress destination, int port) throws SocketException, IOException {
		
		try{
			// Constructor to create datagram socket to open communication and send packets from sender to receiver.
			// Create a socket to open communication
			DatagramSocket socket = new DatagramSocket();
			System.out.println("Client started....");
			
			// set timer for the amount of time to wait for a response from the server after sending the packet
			// from client to server.
			socket.setSoTimeout(1000);
			
			//Queue<Byte[]> queue
            //= new LinkedList<>();
			
			//for (int i = 0; i <message.length; i++) {
				//queue.add(message[i]);
			//}
			
			
			//for (int i = 0; i < message.length; i++) {
				
			// Set the timer to true
			Boolean timer = true;
			// While in the time send the packet to the server and receive the response from the server.
			while(true) {
					//i++;
					
					try {
				
				
						// constructor to create a datagram packet object to Send the UDP Packet to the server
						// The data being transferred is encapsulated in a datagram which is sent over the network in byte
						// format
						// Datagram includes the port and ip address of the server and the sequence number and 
						// character of umbrella.
						DatagramPacket packet = new DatagramPacket(message, message.length, destination, port);
						// Sends datagram packet using datagram socket class method passing in the datagram packet.
						socket.send( packet );
						System.out.println ("Sending packet...");
	
						// Byte array to receive the response from the server
						byte[] receivePacket = new byte[1024];
						
						// Constructor to create a datagram packet
						// to accept the data from the server's packet when sending the response from the server 
						// back to client
						DatagramPacket received = new DatagramPacket(receivePacket, receivePacket.length);
						// Datagram socket method to receive the data from the server.
						socket.receive( received );
						
						
						
						// Get the message from the server's packet
						ByteBuffer buffer = ByteBuffer.wrap( received.getData( ));
						int seq = buffer.getInt();
						/*int payload = buffer.get();
						byte payload1 = buffer.get();
						byte payload2 = buffer.get();
						byte payload3 = buffer.get();
						byte payload4 = buffer.get();*/
							
					
						//char c = (char) (returnMessage1 & 0xFF);
						// Prints out the data received from the server
						System.out.println( "FROM SERVER:" +  seq);
						// Come out of the loop as the package has been successfully sent and response received
						timer = false;
			
					} catch (SocketTimeoutException e) {
						// No response has been received from the server before the time runs out.
						System.out.println("Timeout");
						sendMessage(message, destination, 9876);	
					}
			}
				
			
			//Stop session communication
			socket.close();
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
		}
		
	private static void createDataPackets(String filename, InetAddress destination) throws FileNotFoundException, IOException {
		// Declare message byte array to cache string from file as bytes.
		
		
		try {
			
			// Create a file object passing in the file path and name
			File file = new File(filename);
		
			// Read the bytes of the file into a byte array
			byte[] fileContent = Files.readAllBytes(file.toPath());
		
			// Create a Byte array for the data package that will be sent in the datagram to the server
			byte[] message;
		
			// Iterate over the array holding the content of the file
			for(int i = 0; i < fileContent.length; i++) {
	
				// Create a byte buffer that is 5 bytes long, put the sequence number, which is the current
				// iteration in the first 4 bytes and the data from the array holding the file content
				// of the current iteration, array element, (the character of umbrella) in the 5th byte.
				message = ByteBuffer.allocate(5).putInt(i).put(fileContent[i]).array();
				
			
				// Gets the sequence number of the package
	        	//int seq = buffer.getInt();
	        	//buffer.rewind();
				
	        	//byte payload = buffer.get();
				
				/*System.out.println(message);
				 System.out.println("Contents of byte Array = " + Arrays.toString(message));
				
		String test = Arrays.toString(message);
		System.Text.Encoding.ASCII.GetString(message);
		
		  
	       
        System.out.println("last character: " +
                           test.substring(test.length() - 3)); */
	
		
		
				 
				 
				
				// Call the send message method, passing in the sequence number and the payload, the ip address of
				// server and the port number.
				sendMessage(message, destination, 9876);
			
			
			}
			
			
		
		
		}catch(Exception e){
			//Deal with exceptions.
		}
}

	private static char getCharFromString(String test, int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	}
		
		//byte[][] message = new byte[8][1024];
		// File reader instance
		//Reader reader = new FileReader (filename);
		
		//try{
		      
			// Reads a single character from the fileReader as ASCII value.
			//int data = reader.read();
			// Declare an iterator for inserting bytes into sequential indexes in the message array.
			//int i = 0;
			
			// Checks the value to see if not end of file
			//while(data != -1) {
				
				//System.out.print(data + System.lineSeparator());
				// Reads next character 
				//data = reader.read();
				//byte dataByte = (byte)data;
				
				//create payload
				//byte[] payload = new byte[5];
				//set sequence number (first 4 bytes of payload)
				//payload = ByteBuffer.allocate(4).putInt(i).array();
				//set final byte as the char e.g. "U"
				//payload[4] = dataByte;
				
				//message[i] = payload;
				//i++;
			//}
		//}catch(Exception e){
			//Deal with exceptions.
		/*}
		finally{
			//close the file reader
			reader.close();
			return message;
		}
	}
}*/


		// Create socket
		//DatagramSocket clientSocket = new DatagramSocket();
      
		// Get the server IP address
		//InetAddress IPAddress = InetAddress.getByName("localhost");
		
		// Create send and receive buffers
		//byte[] sendData = new byte[1024];
		//byte[] receiveData = new byte[1024];
      
		
		//String sentence = inFromUser.readLine();
        
		// not sure how get data one character at a time convert data to bytes and store in send buffer.
		// i should be able to get characters from file straight as bytes. not sure how yet.
		//sendData = sentence.getBytes();
      
		//
		/*DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);
      clientSocket.close();
   }
}*/

