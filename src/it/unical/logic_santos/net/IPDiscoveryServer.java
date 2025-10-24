package it.unical.logic_santos.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class IPDiscoveryServer implements Runnable {
	
	/* private fields */
	private DatagramSocket socket=null;
	private boolean serviceOn=true;
	
	
	/* ... CONSTRUCTORS ... */
	
	public IPDiscoveryServer() {
		socket = null;
		serviceOn = true;
	}
	
	
	/* ... PUBLIC METHODS */
	
	@Override
	public void run() {
		
		try {
			
			socket = new DatagramSocket(NetworkConfig.DISCOVERY_SERVICE_PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			socket.setSoTimeout(100);
			runDiscovery();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void setServiceOn() {
		serviceOn = true;
	}
	
	public void setServiceOff() {
		serviceOn = false;
	}
	
	
	/* ... PRIVATE METHODS ... */
	
	private void runDiscovery() throws IOException {
		
		String recvMessage=null;
		byte[] recvBuffer = new byte [NetworkConfig.DISCOVERY_BUFFER_SIZE];
		
		while (serviceOn) {
			
			DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
			try {
				socket.receive(recvPacket); /* blocking mode */
				recvMessage = new String(recvPacket.getData()).trim();
				if (recvMessage.equals(NetworkConfig.DISCOVERY_REQUEST_BUFFER)) {
					
					byte[] sendBuffer = NetworkConfig.DISCOVERY_RESPONSE_BUFFER.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, recvPacket.getAddress(), recvPacket.getPort());
					socket.send(sendPacket);
					
				}
				
			} catch (Exception e) {
				if (!(e instanceof SocketTimeoutException)) {
					e.printStackTrace();
					serviceOn = false;
				}
			}
			
		}
		System.out.println("IP Discovery System closed");
	}
	
	
	/* ... STATIC METHODS ... */
	
	public static void printInfoCmd() {
		System.out.println(" - 'quit': close the discovery service");
	}
	
	
	/* ... MAIN ... */
	
	public static void main(String[] args) {
		
		IPDiscoveryServer discoveryApp = new IPDiscoveryServer();
		discoveryApp.setServiceOn();
		Thread serviceThread = new Thread(discoveryApp);
		serviceThread.start();
		
		System.out.println("IP Discovery System running on port " + NetworkConfig.DISCOVERY_SERVICE_PORT);
		Scanner inputReader = new Scanner(System.in);
		
		boolean running = true;
		String line;
		
		do {
		
			System.out.print("IP Discovery cmd:$ ");
			line = inputReader.nextLine().trim();
			if (line.equals("quit")) 
				running = false;
			else if (line.equals("?"))
				printInfoCmd();
		} while(running);
		
        discoveryApp.setServiceOff();
        inputReader.close();
        
        try {
			serviceThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     
	}

}