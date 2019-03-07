package server;

import java.io.IOException;

import server.SocketService;

public class Server {

	public static void main(String[] args) throws IOException {
		SocketService ss;
		int port=2345;

		ss=new SocketService();
		ss.serve(port);
		
	}
}
