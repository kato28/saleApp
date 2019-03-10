package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {

	public  final static String IP="localhost";
	public final static int PORT=2345;
	public final static int localPort=9070;

	public static void main(String [] args) throws UnknownHostException, IOException
	{
		String reader;
		boolean login=true;
		boolean connected = false;
		boolean chatting;
		String username;
		String password;
		String response;
		int code;
		String price;
		Scanner scanner = new Scanner(System.in);
		ClientService cs = new ClientService(IP,PORT);

		while(login)
		{
			System.out.println("to sign in : 1 \nto sign up : 2 \nto quit : 3");
			reader=scanner.nextLine();
			if(reader.equals("1"))
			{
				System.out.print("enter your username : ");
				username = scanner.nextLine();
				System.out.print("enter your password :");
				password = scanner.nextLine();
				reader = cs.registration(username, password);	
				if (reader.equals("success"))
				{
					System.out.println(reader);
				}
				else 
					System.out.println(reader);
			}
			else if(reader.equals("2"))
			{
				System.out.print("enter your username : ");
				username = scanner.nextLine();
				System.out.print("enter your password :");
				password = scanner.nextLine();
				reader= cs.login(username, password,localPort);
				if(reader.equals("success"))
				{
					connected =true;
					login=false;
					System.out.println(reader);
				}
				else 
					System.out.println(reader);
			}
			else if(reader.equals("3"))
			{
				login=false;
				connected=false;
				System.out.println("good bye");
			}
			while(connected)
			{
				System.out.println("pour poster une annonce : 1 ");
				System.out.println("pour supprimer une annonce : 2");
				System.out.println("pour récupérer toutes les annonces : 3");
				System.out.println("pour récupérer les informations sur une annonce : 4");
				System.out.println("pour se déconnecter : 5");
				reader =scanner.nextLine();
				if(reader.equals("1"))
				{
					System.out.println("enter the domain of the annonce");
					System.out.println("voiture= \"1\" moto= \"2\" musique= \"3\" électroménager= \"4\" téléphone= \"5\" autres= \"6\"");
					code=Integer.parseInt(scanner.nextLine());
					if(cs.verifCode(code))
					{
						System.out.println("enter the price");
						reader = scanner.nextLine();
						if(cs.verifPrice(reader)) {
							price=reader;
							System.out.println("write a brief description");
							reader = scanner.nextLine();
							response = cs.postAnnonce(code, price, reader);

							System.out.println("the annonce id :"+response);
							System.out.println();
							//server.addAnnonce(Integer.parseInt(response));

						}
						else
							System.out.println("price incorrect");

					}
					else
						System.out.println("code incorrect");
				}
				else if(reader.equals("2"))
				{
					System.out.print("enter the annonce id : ");
					reader= scanner.nextLine();
					response = cs.suppAnoonce(reader);
					System.out.println(response);

				}
				else if(reader.equals("3"))
				{
					response=cs.list();
				}
				else if(reader.equals("4")) 
				{
					System.out.print("enter the annonce id : ");
					reader=scanner.nextLine();

					response=cs.information(reader);

					System.out.println(response);
					String req[] = response.split(" ");
					int id = Integer.parseInt(reader);
					String ipAnn = req[2];
					int portAnn = Integer.parseInt(req[5]);
					System.out.println("do you want to contact him ? y/n");
					reader= scanner.nextLine();
					if (reader.equals("y"))
					{
						SSLSocketFactory socketFactory = (SSLSocketFactory) 
								SSLSocketFactory.getDefault(); 
						SSLSocket s = (SSLSocket)socketFactory.createSocket(ipAnn, portAnn);
						s.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());

						//Socket s = new Socket(ipAnn,portAnn);
						response = cs.dispo(id,s);
						System.out.println(response);
						chatting=true;
						if(response.equals("OKOK+++")) {
							System.out.println("to quit press q,to buy press buy");
							while(chatting)
							{
								System.out.print("You : ");
								reader= scanner.nextLine();
								if(reader.equals("q")) {
									chatting = false;
									cs.goodBye(s);
								}
								else if(reader.equals("buy"))
								{
									chatting =false;
									response=cs.buy(s);
									System.out.println(response);
								}
								else {
									response=cs.chating(s, reader);
									System.out.print("Seller : "+response);
									System.out.println();

								}
							}
						}	
					}
					System.out.println();

				}
				else if(reader.equals("5"))
				{
					response=cs.deconnecter();
					if(response.equals("good bye"))
					{
						System.out.println(response);
						scanner.close();
						connected = false;
						login = false;
					}
					else 
						System.out.println(response);
				}
			}
		}

	}
}
