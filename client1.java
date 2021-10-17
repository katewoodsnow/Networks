import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.File;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;



public class client1{

	public static void main(String args[]) throws IOException
	{
		File file = new File("file.txt");
		
		byte[] fileContent = Files.readAllBytes(file.toPath());
		
		//if (args.length!=1) {
		//	System.out.println("Usage: <host:port>");
		//	return;
		//}
		
		//String[] hostPort = args[0].split(":");
		//String host = hostPort[0];
		//int port = Integer.parseInt(hostPort[1]);
		int port = 9999;
		//InetAddress address = InetAddress.getByName(host);
		InetAddress address = InetAddress.getByName("localhost");
		try (DatagramSocket socket = new DatagramSocket()){
			int sequence = 0;
			
			for (byte payload : fileContent) {
				ByteBuffer message = ByteBuffer.allocate(5);
				message.putInt(sequence);
				message.put(payload);
				
				DatagramPacket packet = new DatagramPacket(message.array(),5,address, port);
				socket.send(packet);
				
				sequence++;
	
			}
			
			// Send an empty packet for eof
			ByteBuffer message = ByteBuffer.allocate(4);
			message.putInt(sequence);
			DatagramPacket packet = new DatagramPacket(message.array(), 4, address, port);
			socket.send(packet);
			
		}
	}
}
	