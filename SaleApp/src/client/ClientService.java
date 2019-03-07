package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientService {
	private BufferedReader br;
	private PrintStream ps;
	private Socket  socket;
	//private String ip;
	//private int port;
	//private int localPort=6547;
	
	public ClientService(String ip,int port) throws UnknownHostException, IOException
	{
		//this.ip=ip;
		//this.port = port;
		socket = new Socket(ip,port);
		//server = new ServerSocket(this.port);
		ps=getPrintStream(socket);
		br=getBufferedReader(socket);
		
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
	
	public String registration(String username,String password) throws IOException
	{
		String response="";
		String req= "REGI "+username+" "+password+"+++";
		ps.print(req);
		response=readReq();
		if(response.equals("OKOK+++"))
		{
			response="success";
		}
		else if (response.equals("KOKO 0+++"))
			response ="id existe deja";
		
		else if( response.equals("KOKO 1+++"))
			response = "mauvais nombre d'arguments";
		
		else 
			response="failed";
		return response;
	}
	
	public String login(String username,String password, int port) throws IOException
	{
		String response="";
		String req= "CONE "+username+" "+password+" "+port+"+++";
		ps.print(req);
		response = readReq();
		if(response.equals("OKOK+++"))
		{
			response="success";
		}
		else if (response.equals("KOKO 1+++"))
			response ="mauvais nombre d'arguments";
		else if(response.equals("KOKO 2+++"))
		{
			response ="mot de passe incorrect";
		}
		else if(response.equals("KOKO 3+++"))
		{
			response ="l'utilisateur n'existe pas";
		}
		return response;
	}
	
	public String postAnnonce(int code,String prix,String descriptif) throws IOException
	    {
	        String response="";
	        char id[]=new char[30];
	        int k=0;
	        if(verifCode(code))
	        {
	        	 String req= "POST "+code+" "+prix+" "+descriptif+"+++";
	 	        ps.print(req);
	 			response =readReq();
	 	        for(int i=5;i<response.length();i++) {
	 	            id[k]=response.charAt(i);
	 	            k++;
	 	        }
	 	        String r[] = response.split(" ");
	 	        return r[1].substring(0, r[1].length()-3);
	        }
	        else {
	        	return "you should enter a code between 1 and 6";
	        }
	       
	    }
	public String suppAnoonce(String id) throws IOException
	{
		String response="";
		String req= "SUPR "+id+"+++";
		ps.print(req);
		response=readReq();
		if(response.equals("OKOK+++"))
		{
			response="success";
		}
		else if (response.equals("KOKO 1+++"))
			response ="mauvais nombre d'arguments";
		else if( response.equals("KOKO 4+++"))
			response = "Format incorrect d’arguments";
		else if (response.equals("KOKO 5+++"))
			response ="L’annonce n’existe pas";
		else if( response.equals("KOKO 6+++"))
			response = "Le client n’est pas propriétaire de l’annonce";
		else 
			response="failed";
		return response;
	}
	public String list() throws IOException
	{
		String req="LIST+++";
		String response;
		String rs;
		ps.print(req);
		boolean fin=false;
		StringBuilder s= new StringBuilder();
		response=readReq();
		for(int i=5;i<response.length();i++)
		{
			if((response.charAt(i)!=' ') && (response.charAt(i)!='+'))
			{
				s.append(response.charAt(i));
			}
			else if (response.charAt(i) == '+')
			{
				fin = verifEnd(response, i);
				if(!fin)
				{
					response="wrong number of arguments";
				}
				else
				{
					break;
				}
			}
		}
		
		int size = StringtoInt(s);
		for(int i=0;i<size;i++)
		{
			rs=readReq();
			String [] anno=rs.split(" ");
			//int k=anno.length;
			System.out.println("annonce "+(i+1));
			System.out.println("annonce id : "+anno[1]);
			if(anno[2].equals("1"))
				System.out.println("annonce code : voiture");
			if(anno[2].equals("2"))
				System.out.println("annonce code : moto");
			if(anno[2].equals("3"))
				System.out.println("annonce code : musique");
			if(anno[2].equals("4"))
				System.out.println("annonce code : électroménager");
			if(anno[2].equals("5"))
				System.out.println("annonce code : téléphone");
			if(anno[2].equals("6"))
				System.out.println("annonce code : autres");
			System.out.println("annonce prix : "+anno[3]);
			System.out.println("annonce description : ");
			for(int j=4;j<anno.length;j++)
			{
				if(j==anno.length-1)
					System.out.println(anno[j].substring(0, anno[j].length()-3));
				else
				System.out.print(anno[j]+" ");
			}
				
			
			System.out.println();
			System.out.println("=======================================");
			
			
		}
		return "success";
	}
	
	public String dispo(int id,Socket s) throws UnknownHostException, IOException
	{
		PrintStream ps = getPrintStream(s);
		String response;
		String req ="DISP "+id+"+++";
		ps.print(req);
		response =readReq(s);
		return response;
	}
	
	public String chating (Socket s,String msg) throws IOException {
		String response;
		PrintStream ps = getPrintStream(s);
		String req ="MSSG "+msg+"+++";
		ps.print(req);
		response = readReq(s);
		String [] req2 = response.split(" ");
		response ="";
		if(req2[0].equals("MSSG"))
		{
			for(int i=1;i<req2.length;i++)
			{
				response+=req2[i];
				if(i!=req2.length-1)
					response+=" ";
			}
		}
		response =response.substring(0, response.length()-3);
		return response;
	}
	
	public String buy (Socket s) throws IOException
	{
		String response;
		PrintStream ps = getPrintStream(s);
		String req="ACHA+++";
		ps.print(req);
		response =readReq(s);
		if(response.equals("OKOK+++"))
		{
			return "Accepted";
		}
		else if (response.equals("KOKO 1+++"))
		{
			return "bad number of argument";
		}
		return "Rejected";
	}
	
	public void goodBye(Socket s) throws IOException
	{
		PrintStream ps = getPrintStream(s);
		String req ="GBYE+++";
		ps.print(req);
		s.close();
	}
	
	
	public String deconnecter() throws IOException
	{
		String req= "DECO+++";
		ps.print(req);
		String response=readReq();
		if(response.equals("GBYE+++"))
			return "good bye";
		else 
			return "failed";
	}
	public String information(String id) throws IOException
	{
		String req="INTR "+id+"+++";
		String err;
		ps.print(req);
		String response=readReq();
		String [] info = response.split(" ");
		if(info[0].equals("CONT"))
		{
			response ="adresseIp : "+info[1]+" port : "+info[2].substring(0, info[2].length()-3);
		}
		else if(info[0].equals("NCON"))
		{
			response ="user is not connected";
		}
		else if(info[0].equals("KOKO"))
		{
			err = info[1].substring(0, info[1].length()-3);
			if(err.equals("1"))
				response="wrong number of arguments";
			else 
				response="the annonce doesn't exist";
		}
		
		return response;
	}

	private String readReq() throws IOException
	{
		String response="";
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
	
	private String readReq(Socket s) throws IOException
	{
		String response="";
		int l,c;
		BufferedReader br = getBufferedReader(s);
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
	
	public boolean verifCode(int code)
	{
		if( code >=1 && code <=6)
			return true;
		return false;
	}
	
	public boolean verifPrice(String price)
	{
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
	
	public boolean verifEnd(String msg, int index)
	{
		boolean fin=false;
		for(int t=index;t<msg.length();t++) {
			if(msg.charAt(t) != '+') {
				fin = false;
				break;
			}
			else if(msg.charAt(t) == '+' && (t==(index+2)))
			{
				fin = true;
			}
			
		}
		return fin;
	}
}
