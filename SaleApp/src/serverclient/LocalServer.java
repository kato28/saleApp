package serverclient;

import java.io.IOException;
import java.util.Scanner;

public class LocalServer {
	public final static int PORT=9070;

	public static void main (String[] args) throws IOException
	{
		Scanner scanner = new Scanner(System.in);
		//System.out.print("enter the port number : ");
		//int port = scanner.nextInt();
		ClientServer server = new ClientServer();
		server.serve(PORT,scanner);
		
	}
	
}
