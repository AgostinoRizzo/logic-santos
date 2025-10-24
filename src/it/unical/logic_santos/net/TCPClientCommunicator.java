/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class TCPClientCommunicator extends TCPNetworkCommunicator {

	private Socket serverSocket=null;
	private boolean isConnected=false;

	
	public TCPClientCommunicator(LogicSantosApplication application) {
		super(application);
	}
	
	public boolean connect() {
		try {
			
			String serverName;
			if ( NetworkConfig.USE_IP_DISCOVERY ) {
				try {
					serverName = discoverIpAddress();
				} catch (Exception e) {
					e.printStackTrace();
					serverName=null;
				}
			} else
				serverName = NetworkConfig.SERVER_NAME;
			
			if ( serverName==null )
				serverName = NetworkConfig.SERVER_NAME;
			
			serverSocket = new Socket( serverName, NetworkConfig.PORT_NUMBER );
			in = new DataInputStream( serverSocket.getInputStream() );
			out = new DataOutputStream( serverSocket.getOutputStream() );
			isConnected = true;
			runningFlag = true;
			start();
			return true;
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			serverSocket = null;
			in = null;
			out = null;
			isConnected = false;
			runningFlag = false;
			return false;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			serverSocket = null;
			in = null;
			out = null;
			isConnected = false;
			runningFlag = false;
			return false;
			
		}
	}
	
	public boolean closeClient() {
		try {
			isConnected = false;
			runningFlag = false;
			if ( in!=null )
				in.close();
			if ( out!=null )
				out.close();
			serverSocket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
	@Override
	public boolean isMaster() {
		return false;
	}
	
	@Override
	protected void onReadStreamException() {
		closeClient();
	}
	

    private String discoverIpAddress() throws Exception {

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);
        datagramSocket.setSoTimeout(NetworkConfig.DISCOVERY_RECEIVE_TIMEOUT );


        byte[] sendBuffer = NetworkConfig.DISCOVERY_SEND_BUFFER.getBytes();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {

            NetworkInterface networkInterface = interfaces.nextElement();
            if ( (!networkInterface.isLoopback()) && (networkInterface.isUp()) ) {

                for(InterfaceAddress interfaceAddress: networkInterface.getInterfaceAddresses()) {

                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast != null) {
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, broadcast, NetworkConfig.DISCOVERY_SERVICE_PORT);
                        datagramSocket.send(sendPacket);
                    }
                }
            }
        }

        String IpAddress=null;
        
        /* wait for a response */
        byte[] recvBuffer = new byte[NetworkConfig.DISCOVERY_BUFFER_SIZE];
        DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
        boolean read;
        do {
            datagramSocket.receive(recvPacket);
            String recvData = new String(recvPacket.getData()).trim();

            if (recvData.equals(NetworkConfig.DISCOVERY_RECV_BUFFER)) {
                IpAddress = recvPacket.getAddress().getHostAddress();
                Thread.sleep(1000);
                read = false;
            } else
                read = true;
        } while(read);

        datagramSocket.close();
        
        return IpAddress;
    }


}
