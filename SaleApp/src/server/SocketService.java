package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketService {
	
	private ServerSocket serverSocket = null;	
	private Thread serverThread = null;
	private boolean running = false;
	static int count=0;
	private ArrayList<Client> clients = new ArrayList<>();
	private ArrayList<Annonce> annonces = new ArrayList<>();

	public void serve(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverThread= makeServerThread();
		serverThread.start();
		String ip = InetAddress.getLocalHost().getHostAddress();
		System.out.println("server created, listen on "+port+" adress ip : "+ip);
	}
	//print data 
	public static PrintStream getPrintStream(Socket s) throws IOException {
		PrintStream ps = new PrintStream(s.getOutputStream());
		return ps;
	}
	//read data from socket
	public static BufferedReader getBufferedReader(Socket s) throws IOException {
		InputStream is = s.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}
	/*
	 * server on listening 
	 */
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
	/*
	 * accept new connections and start new thread related to the new socket
	 */
	private void acceptAndServerConnection() {
		try {
			Socket s = serverSocket.accept();
			count++;
			//new Thread(new ServiceRunnable(s)).start();
			new Thread(new ServiceListening(s)).start();

		} catch (IOException e) {
			System.err.println("Could not accept");
			e.printStackTrace();
		}

	}

	/*
	 * thread running and verify if there is new clients
	 * if that the case it notifies the client 
	 */

	class ServiceListening implements Runnable {
		private Socket itsSocket;
		private boolean connected=false;
		private String username="unknown";
		private String msg="";
		private boolean log=true;
		String [] requete;
		ServiceListening(Socket s) {
			itsSocket = s;
		}
		public void run()
		{
			try {
				while(log)
				{
					msg=readReq(itsSocket);

					System.out.println(msg);
					requete = msg.split(" ");

					username =verifReq(requete,itsSocket);
					System.out.println(username);
					if(!username.equals("not connected"))
					{
						connected=true;
						log=false;
					}
				}
				while(connected)
				{
					msg=readReq(itsSocket);
					System.out.println(msg);
					requete = msg.split(" ");
					connected = afterLog(requete, itsSocket, username);
					if(!connected)
						System.out.println("godd bye");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	//after a successful login
	private boolean afterLog(String []req,Socket s,String username) throws IOException
	{
		String requete;
		PrintStream ps=getPrintStream(s);
		if(req[0].equals("DECO+++")) {
			disconnected(username);
			ps.print("GBYE+++");
			s.close();
			return false;
		}
		else if(req[0].equals("POST")) {
			return postAnnonce(req, s, username);
		}
		else if (req[0].equals("SUPR")) {
			return suppAnnonce(req, s, username);
		}
		else if (req[0].equals("LIST+++")) {
			ps.print("NBAN "+annonces.size()+"+++");
			for(int i=0;i<annonces.size();i++)
			{
				requete = "ANNO "+annonces.get(i).getId()+" "+annonces.get(i).getCode()+
						" "+annonces.get(i).getPrix()+" "+annonces.get(i).getDesc()+"+++";
				System.out.println(requete);
				ps.print(requete);
			}
			return true;
		}
		else if (req[0].equals("INTR")) 
		{
			String annoID = req[1].substring(0, req[1].length()-3);
			requete=contactUser(Integer.parseInt(annoID));
			if(requete.equals("")){
				ps.print("KOKO 5+++");
			}
			else if(requete.equals("off")){
				ps.print("NCON +++");
			}
			else {
				System.out.println("CONT "+requete+"+++");
				ps.print("CONT "+requete+"+++");
			}
		}
		return true;
	}
	
	//before login ; regi & login
	public String verifReq(String[] req,Socket s) throws IOException
	{
		String user;
		if(req[0].equals("REGI"))
		{
			registration(req, s);
			return "not connected";
		}
		else if(req[0].equals("CONE")) {
			user =login(req,s);
			return user;
		}
		else
		{
			return "not connected";
		}
	}

	private void registration(String [] msg,Socket s) throws IOException
	{	
		PrintStream ps=getPrintStream(s);
		String username=msg[1];
		String pwd = msg[2].substring(0, msg[2].length()-3);

		if(isExistId(username))
		{
			System.out.println("koko 0");
			ps.print("KOKO 0+++");

		}
		else {
			clients.add(new Client(username,pwd,0,false));
			System.out.println("okok");
			ps.print("OKOK+++");
		}	
	}

	private String login(String[] msg,Socket s) throws IOException
	{
		boolean verif;
		PrintStream ps=getPrintStream(s);
		String username = msg[1];
		String pwd = msg[2];
		String port = msg[3].substring(0, msg[3].length()-3);
		if(isExistId(username))
		{
			String ip = s.getInetAddress().getHostAddress();
			verif = userLogin(username,pwd, port,ip);
			if(verif)
			{
				connected(username, Integer.parseInt(port));
				System.out.println("ok ok");
				ps.print("OKOK+++");
				return username;
			}
			else {
				System.out.println("koko 2");
				ps.print("KOKO 2+++");
				return "not connected";
			}

		}
		else {
			ps.print("KOKO 3+++");
			return "not connected";
		}		
	}

	private boolean postAnnonce(String[] msg,Socket s, String username) throws IOException
	{
		int code;		
		PrintStream ps=getPrintStream(s);
		String desc="";
		for(int i=3;i<msg.length;i++)
		{
			if(i==msg.length-1)
			{
				desc+=msg[i].substring(0, msg[i].length()-3);
			}
			else
				desc+=msg[i]+" ";
		}
		code = Integer.parseInt(msg[1]);
		if( code <1 || code >6 )
		{
			ps.print("KOKO 1+++");
		}
		else 
		{							
			if(isPrice(msg[2])) 
			{
				annonces.add(new Annonce(username,code,Float.parseFloat(msg[2]),desc));
				ps.print("OKOK "+annonces.get(annonces.size()-1).getId()+"+++");	

			}
			else {ps.print("KOKO 4+++");}

		}
		return true;
	}

	private boolean suppAnnonce(String[] msg,Socket s,String username) throws IOException
	{
		String id = msg[1].substring(0, msg[1].length()-3);
		PrintStream ps=getPrintStream(s);
		int k = deleteAnnonce(Integer.parseInt(id),username);

		if(k==1)
		{
			ps.print("OKOK+++");
		}
		else if(k==2){
			ps.print("KOKO 6+++");
		}
		else if(k==3) {
			ps.print("KOKO 5+++");
		}
		return true;
	}

	public boolean isExistId(String id) {
		for(int i=0;i<clients.size();i++)
		{
			if (clients.get(i).getUsername().equals(id))
				return true;
		}
		return false;
	}

	public int deleteAnnonce(int id,String username)
	{
		for(int i=0;i<annonces.size();i++)
		{
			System.out.print("annonce = "+annonces.get(i).getId());
			if(annonces.get(i).getId()==id)
			{
				if(annonces.get(i).getUsername().equals(username))
				{
					annonces.remove(i);
					return 1;
				}
				else {
					return 2;
				}
			}
		}
		return 3;
	}
	/*
	 * contact other user trying to buy product
	 */
	public String contactUser(int id)
	{
		Client c;
		String response="";
		for(int i=0;i<annonces.size();i++) {
			if(annonces.get(i).getId() == id)
			{
				c= searchById(annonces.get(i).getUsername());
				if(c.isConnected())
				{
					response=c.getAddrIp()+" "+c.getPort();
					break;
				}
				else {
					response="off";
				}
			}
		}
		return response;
	}

	/*
	 * search users
	 */
	public Client searchById(String username)
	{
		for(int i=0;i<clients.size();i++)
		{
			if(clients.get(i).getUsername().equals(username))
				return clients.get(i);
		}
		return null;
	}

	/*
	 * login
	 */
	public void connected(String username,int port)
	{
		for(int i=0;i<clients.size();i++)
		{
			if(clients.get(i).getUsername().equals(username))
			{
				clients.get(i).setConnected(true);
				clients.get(i).setPort(port);
			}

		}
	}
	/*
	 * logout
	 */
	public void disconnected(String username)
	{
		for(int i=0;i<clients.size();i++)
		{
			if(clients.get(i).getUsername().equals(username))
			{
				clients.get(i).setConnected(false);
			}

		}
	}
	/*
	 * verification of price is in a good form 
	 */
	public boolean isPrice (String price) {
		int count =0;
		boolean verif=false;
		for(int i=0;i<price.length();i++)
		{
			if((int)price.charAt(0) == 46) {
				verif=false;
				break;
			}
			else if ((int)price.charAt(price.length()-1)==46)
			{
				verif = false;
				break;
			}
			else if(((int)price.charAt(i)<48 || (int)price.charAt(i)>57) && (int)price.charAt(i)!=46)
			{
				verif =false;
				break;
			}
			else if((int)price.charAt(i)>=48 && (int)price.charAt(i)<=57 && count<2)
			{
				verif=true;
			}
			else if((int)price.charAt(i) == 46) {
				count++;
			}
			else if (count>=2) {
				verif=false;
			}

		}
		return verif;
	}
	/*
	 * get thhe price from the message 
	 */
	public int StringtoInt(StringBuilder msg)
	{
		int id=0;
		int l=msg.length()-1;
		int k=(int) Math.pow(10.0, l);

		for(int i=0;i<msg.length();i++)
		{
			id+=Character.getNumericValue(msg.charAt(i))*k;
			l--;
			k=(int)Math.pow(10.0, l);	
		}
		return id;
	}
	
	/*
	 * login clients 
	 */
	public boolean userLogin(String id, String mdp,String port,String ip) {

		for(int i=0;i<clients.size();i++) {
			if(clients.get(i).getUsername().equals(id) && clients.get(i).getPassword().equals(mdp)) {
				clients.get(i).setPort(Integer.parseInt(port.trim()));
				System.out.println(ip);
				clients.get(i).setAddrIp(ip);
				return true;
			}
		}
		return false;
	}
	/*
	 * update the ip and port number of users
	 */
	private void updateIP(String id,String mdp,int port) {
		for(int i=0;i<clients.size();i++) {
			if(clients.get(i).getUsername().equals(id) && clients.get(i).getPassword().equals(mdp)) {
				clients.get(i).setPort(port);
			}
		}
	}
	/*
	 * read messages
	 */
	private String readReq(Socket s) throws IOException
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

}
