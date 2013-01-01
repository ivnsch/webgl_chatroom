

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ChatServer extends WebSocketServer {

	private int connectionCounter = 0;
	
//	private List<Player> players = new ArrayList<Player>(); 
	
	private Map<Integer, Player> players = new HashMap<Integer, Player>();
	
	public ChatServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public ChatServer( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		conn.send("conn " + connectionCounter);
//		this.sendToAll("conn " + connectionCounter);
		System.out.println( conn + " entered the room!" );
		connectionCounter++;
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println( conn + ": " + message );
		
		int firstIndex = message.indexOf(" ");
	  String type = message.substring(0, firstIndex);
	  String body = message.substring(firstIndex + 1, message.length());
	  
	  if (type.equals("addPlayer")) {
	  	String[] parts = body.split(" ");
	  	int id = Integer.valueOf(parts[0]);
	  	String name = parts[1];
	  	float x = Float.valueOf(parts[2]);
	  	float y = Float.valueOf(parts[3]);
	  	float z = Float.valueOf(parts[4]);
	  	
	  	players.put(id, new Player(id, name, x, y, z));
	  	
	  	System.out.println("send players: " + "players " + getPlayersString());
	  	
	  	conn.send("players " + getPlayersString());
	  	sendToAll(message);
	  	
	  } else if (type.equals("chat")) {
	  	sendToAll(message);
	  
	  } else if (type.equals("move")) {
	  	String[] parts = body.split(" ");
	  	int id = Integer.valueOf(parts[0]);
	  	float x = Float.valueOf(parts[1]);
	  	float y = Float.valueOf(parts[2]);
	  	float z = Float.valueOf(parts[3]);
	  	
	  	players.get(id).setPosition(x, y, z);
	  	
	  	sendToAll(message);
	  
	  } else {
	  	sendToAll(message);
	  	
	  }
	}

	
	
	public String getPlayersString() {
		String playersStr = "";
		System.out.println("getPlayersString() size: " + players.values().size());
		for (Player player : players.values()) {
				playersStr += player.serialize() + "#";
		}
		//cut last #
		if (playersStr.length() > 0) {
			playersStr = playersStr.substring(0, playersStr.length() - 1);
		}
		return playersStr;
	}
	
	public void setDebug(boolean debug) {
		WebSocket.DEBUG = debug;
	}
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocket.DEBUG = true;
		int port = 9999; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		ChatServer s = new ChatServer( port );
		s.start();
		System.out.println( "ChatServer started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.sendToAll( in );
		}
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Set<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
}
	