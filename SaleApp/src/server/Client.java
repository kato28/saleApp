package server;

public class Client {

	private String username;
	private String password;
	private int port;
	private String addrIp;
	private boolean connected;
	//private ServerSocket clientServer;
	public Client(String username, String password, int port, boolean available) {
		super();
		this.username = username;
		this.password = password;
		this.port = port;
		this.connected = available;
		addrIp="127.0.0.1";
		//this.clientServer = clientServer;
	}

	public String getAddrIp() {
		return addrIp;
	}

	public void setAddrIp(String addrIp) {
		this.addrIp = addrIp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	 

	/*public ServerSocket getClientServer() {
		return clientServer;
	}

	public void setClientServer(ServerSocket clientServer) {
		this.clientServer = clientServer;
	}*/
	
}
