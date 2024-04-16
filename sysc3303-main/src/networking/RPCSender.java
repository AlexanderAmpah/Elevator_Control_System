package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class RPCSender {
	
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	/**
	 * constructor
	 * Constructs a datagram socket and binds it to any available
	 * port on the local host machine
	 */
	public RPCSender() {
		try {

	         sendReceiveSocket = new DatagramSocket();
	         sendReceiveSocket.setSoTimeout(5000);
	      } catch (SocketException se) {   // Can't create the socket.
	         se.printStackTrace();
	         System.exit(1);
	      }
	}
	
	/**
	 * Takes in a two packets for receive and send, a port, a location, and a name for the request to be sent
	 * Sends a requests to the given address and waits for a response
	 * response is stored in the response byte[]
	 * @param message to be sent
	 * @param response to be received
	 * @param port of the destination
	 * @param destination address
	 * @param name 
	 */
	public void rpcSend(byte[] message, byte[] response, int port, InetAddress destination , String name) {
		
		if (message == null) {
			message = new byte[0];
		}

		sendPacket = new DatagramPacket(message, message.length,
		                                 destination, port);
		
		int len = sendPacket.getLength();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sendPacket.getLength(); ++i) {
		    sb.append(String.format("%02x ", sendPacket.getData()[i]));
		}
		//System.out.println(name + ": Received packet containing (string/bytes): "+sb.toString()+"\n");
		
		// Send the datagram packet to the intermediate via the send/receive socket. 
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Construct a DatagramPacket for receiving packets up 
		// to 100 bytes long (the length of the byte array).
		
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		
		try {
			// Block until a datagram is received via sendReceiveSocket.  
			sendReceiveSocket.receive(receivePacket);
		} catch(IOException e) {
			System.out.println("IO Exception: likely socket timeout\n");
			//sendReceiveSocket.close();
			//System.exit(1);
			response[0] = -1;
			return;
		}
		
		// Process the received datagram.
		len = receivePacket.getLength();
		
		// Form a String from the byte array.
		String received = new String(receivePacket.getData(),receivePacket.getOffset(),len);
		sb = new StringBuilder();
		for (int i = 0; i < receivePacket.getLength(); ++i) {
			sb.append(String.format("%02x ", receivePacket.getData()[i]));
		}
		//System.out.println(name + ": Received packet containing (string/bytes): "+sb.toString()+"\n");
		
		
		
		System.arraycopy(receivePacket.getData(), 0, response, 0, len);
		
		// if response was {-1}, data was requested and none was received
	    // sleep and try again
		//TODO: figure out a better way to do this, 
		//something like interrupting from scheduler, but over the network ???
		
	    if (len > 0 && receivePacket.getData()[0] == -1) {
	    	synchronized (this) {	    		  
	    		try {
	    			System.out.println(name + ": waiting");
	    			Thread.sleep(5000);
	    		} catch (InterruptedException e) {
	    			System.out.println(name + ": woke up");
	    		}
	    	}
	    }
	}
}