package networking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import floorSim.FloorSubsystem;

public class TestListener {

	private DatagramPacket receivePacket, sendPacket;
	private DatagramSocket receiveSocket, sendReceiveSocket;
	private int listenPort;
	
	public TestListener(int port) {
		listenPort = port;
	}
	
	public DataInputStream listenAndRespond(byte[] spoofedResponse) {
		try {
			receiveSocket = new DatagramSocket(listenPort);
		} catch (SocketException e) {
			e.printStackTrace();
			receiveSocket.close();
			return null;
		}
		
		// create the response
		byte buff[] = new byte[100];
		receivePacket = new DatagramPacket(buff, buff.length);
		
		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			receiveSocket.close();			
		}
		
		ByteArrayInputStream byteIn = new ByteArrayInputStream(receivePacket.getData());
		DataInputStream dataIn = new DataInputStream(byteIn);
		
		// reply with spoofed response
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}
		
		sendPacket = new DatagramPacket(spoofedResponse, spoofedResponse.length,
				receivePacket.getAddress(), receivePacket.getPort());
        try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return dataIn;
	}
	
	public void asyncSend(byte[] msg, String addrName, int port) {
		try {
			sendPacket = new DatagramPacket(msg, msg.length, 
					InetAddress.getByName(addrName), port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
}
