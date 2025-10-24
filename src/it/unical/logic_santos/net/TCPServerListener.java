/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Agostino
 *
 */
public class TCPServerListener extends Thread {

	private TCPServerCommunicator serverCommunicator=null;
	
	public TCPServerListener( TCPServerCommunicator serverCommunicator ) {
		this.serverCommunicator = serverCommunicator;
	}
	
	@Override
	public void run() {
		
		try {
			Socket incoming = serverCommunicator.getServerSocket().accept();
			serverCommunicator.onConnectionAccepted( incoming );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
