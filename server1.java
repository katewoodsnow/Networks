import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;


public class server1
{

	public static void main(String[] args) throws IOException {
		
		try(DatagramSocket socket = new DatagramSocket(9999)){
			byte[] buf = new byte[256];
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			
			while(true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
			
				InetAddress clientAddress = packet.getAddress();
				int clientPort = packet.getPort();
				
				if (packet.getLength()==5) {
					ByteBuffer message = ByteBuffer.wrap(packet.getData(),
							0,packet.getLength());
					int sequence = message.getInt();
					byte payload = message.get();
					
					System.out.print(sequence);
					System.out.print("-");
					System.out.println(payload);
					result.write(payload);
				} else if(packet.getLength() == 4) {
					ByteBuffer message = ByteBuffer.wrap(packet.getData(),0,
							packet.getLength());
					int sequence = message.getInt();
					
					System.out.print(sequence);
					System.out.print("-");
					System.out.println(new String(result.toByteArray(),
							Charset.forName("UTF8")));
					
					result = new ByteArrayOutputStream();
				}
			}
		}
	}
}			