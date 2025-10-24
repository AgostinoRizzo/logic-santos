/**
 * 
 */
package it.unical.logic_santos.net;

/**
 * @author Agostino
 *
 */
public class NetworkConfig {

	public static final int     PORT_NUMBER = 3000;
	public static final String  SERVER_NAME = "127.0.0.1";
	
	public static final boolean USE_IP_DISCOVERY = true;
	public static final String DISCOVERY_SEND_BUFFER = "DISCOVERY_REQUEST";
	public static final String DISCOVERY_RECV_BUFFER = "DISCOVERY_RESPONSE";
	public static final String DISCOVERY_REQUEST_BUFFER = "DISCOVERY_REQUEST";
	public static final String DISCOVERY_RESPONSE_BUFFER = "DISCOVERY_RESPONSE";
	public static final int DISCOVERY_SERVICE_PORT = 2000;
	public static final int DISCOVERY_BUFFER_SIZE = 1024;
	public static final int DISCOVERY_RECEIVE_TIMEOUT = 10000;
}
