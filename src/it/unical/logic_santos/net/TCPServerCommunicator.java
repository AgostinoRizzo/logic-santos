/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import it.unical.IPDiscovery.IPDiscoveryApp;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class TCPServerCommunicator extends TCPNetworkCommunicator {

	private ServerSocket serverSocket=null;
	private TCPServerListener serverListener=null;
	private Socket clientSocket=null;
	private boolean isConnected=false;
	
	
	
	private IPDiscoveryServer discoveryApp=null;
	private Thread serviceThread=null;
	
	public TCPServerCommunicator(LogicSantosApplication application) {
		super(application);
	}
	
	public boolean startServer() {
		try {
			
			serverSocket = new ServerSocket( NetworkConfig.PORT_NUMBER );
			serverListener = new TCPServerListener( this );
			runningFlag = true;
			serverListener.start();
			
			/*IPDiscoveryServer discoveryApp = new IPDiscoveryServer();
			discoveryApp.setServiceOn();
			Thread serviceThread = new Thread(discoveryApp);
			serviceThread.start();*/
			
			
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			runningFlag = false;
			return false;
		}
	}
	
	public boolean closeServer() {
		try {
			isConnected = false;
			this.runningFlag = false;
			if ( in!=null )
				in.close();
			if ( out!=null )
				out.close();
			if ( clientSocket!=null )
				clientSocket.close();
			if ( serverSocket!=null )
				serverSocket.close();
			
			if ( discoveryApp!=null )
				discoveryApp.setServiceOff();
					        
			try {
				if ( serviceThread!=null )
					serviceThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		        
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
		return true;
	}
	
	@Override
	protected void onReadStreamException() {
		closeServer();
	}
	
	protected ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	protected void onConnectionAccepted( Socket incoming ) {
		try { 
			//JOptionPane.showMessageDialog(null, "CONNECTION ACCEPTED!");
			clientSocket = incoming;
			in = new DataInputStream( clientSocket.getInputStream() );
			out = new DataOutputStream( clientSocket.getOutputStream() );
			isConnected = true;
			runningFlag = true;
			start();
		} catch (IOException e) {
			e.printStackTrace();
			clientSocket = null;
			in = null;
			out = null;
			isConnected = false;
			runningFlag = false;
		}
	}

}
