package serverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;



public class ClientServer {

	private SSLServerSocket serverSocket; 
	//private ServerSocket serverSocket = null;	

	private Thread serverThread = null;
	private boolean running = false;
	Scanner scanner;
	
	public void serve(int port,Scanner scan) throws IOException {
		SSLServerSocketFactory socketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault(); 
        serverSocket = (SSLServerSocket) socketFactory.createServerSocket(port); 
        serverSocket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites()); 
		//serverSocket = new ServerSocket(port);
		scanner=scan;
		serverThread= makeServerThread();
		serverThread.start();
		//InetAddress ip = InetAddress.getLocalHost();
		System.out.println("server created, listen on "+port+" adress ip : "+serverSocket.getInetAddress().getHostAddress());
	}
	
	private Thread makeServerThread() {
		return new Thread (
				new Runnable() {
					
					@Override
					public void run() {
						running = true;
						while(running) {
							acceptAndServerConnection();
						}
					}
			});
	}
	
	private void acceptAndServerConnection() {
		try {
			SSLSocket s = (SSLSocket) serverSocket.accept();
			new Thread(new ServiceListening(s)).start();
			
		} catch (IOException e) {
			System.err.println("Could not accept");
			e.printStackTrace();
		}	
	}
	
	class ServiceListening implements Runnable {
		private SSLSocket itsSocket;
		private String msg="";
		private boolean log=true;
		String [] requete;
		BufferedReader br;
		PrintStream ps;
		
		ServiceListening(SSLSocket s) {
			itsSocket = s;
		}
		
		public void run()
		{
			try {
				while(log)
				{
					msg = readReq(itsSocket);
					requete = msg.split(" ");
					log = !chat(requete,itsSocket);			
				}
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
	private boolean chat(String[] requete, SSLSocket itsSocket) throws IOException
	{
		String text="";
		String avis;
		int idAnn;
		PrintStream ps = getPrintStream(itsSocket);
		if(requete[0].equals("MSSG")){
			for(int i=1;i<requete.length;i++)
			{
				text+=requete[i];
				if(i!=requete.length-1)
				text+=" ";
			}
			text = text.substring(0,text.length()-3);
			System.out.print("Buyer : ");
			System.out.println(text	);
			System.out.print("You : ");
			text = inputStr();
			ps.print("MSSG "+text+"+++");
			return false;
			
		}
		else if(requete[0].equals("ACHA+++")) {
			if(requete.length==1) {
				System.out.println("si vous ête d'accord : yes \nsi vous n'êtes pas d'accord : no");
				avis=inputStr();
				if(avis.equals("yes")) {
					ps.print("OKOK+++");
					System.out.println("achat accepted");
					itsSocket.close();
					return true;
				}else{
					ps.print("KOKO 8+++");
					itsSocket.close();
					System.out.println("chat closed");
					return true;}
			}else {
				ps.print("KOKO 1+++");
				itsSocket.close();
				System.out.println("chat closed");
			}
			return false;
			
		}else if (requete[0].equals("GBYE+++")) {
			ps.print("GBYE+++");
			itsSocket.close();
			System.out.println("chat closed");
			return true;
		}
		else if (requete[0].equals("DISP"))
		{
			if(requete.length==2)
			{
				idAnn = Integer.parseInt(requete[1].substring(0, requete[1].length()-3));
				System.out.println("is it the annonce "+idAnn+" disponible ?");
				avis = inputStr();
				if(avis.equals("yes")) {
					ps.print("OKOK+++");
					return false;
				}
				else {
					ps.print("KOKO 5+++");
					return false;
				}
			}
			else {
				ps.print("KOKO 1+++");
				return false;
			}
			
		}
		else {
			ps.print("KOKO 7+++");
		}
		return false;
	}
	

	private String readReq(SSLSocket s) throws IOException
	{
		String response="";
		BufferedReader br = getBufferedReader(s);
		int l,c;
		while(true)
		{
			c = br.read();
			response+=(char) c;
			l=response.length();
			if(l>=3 && response.substring(l-3, l).equals("+++"))
				break;
		}
		return response;
	}
	
	public static PrintStream getPrintStream(SSLSocket s) throws IOException {
		OutputStream os = s.getOutputStream();
		PrintStream ps = new PrintStream(os);
		return ps;
	}
	
	public static BufferedReader getBufferedReader(SSLSocket s) throws IOException {
		InputStream is = s.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	public String inputStr()
    {
        String result = "";
        while(!scanner.hasNextLine()) {
        	System.out.println("while ");
        }
        result = scanner.nextLine();
        //sc.close();
        return result;
    }
}
