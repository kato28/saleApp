package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import server.Client;

class SocketServiceTest {

	@Test
	void testVerifReq() {
		
		ArrayList<Client> clients = new ArrayList<>();
		clients.add(new Client("ha","az",1432,false));
		clients.add(new Client("nina","az",6524,false));
		assertEquals(true,userLogin("ha","az","9999",clients) );
		
	}
public boolean userLogin(String id, String mdp,String port,ArrayList<Client> clients) {
		
		for(int i=0;i<clients.size();i++) {
			if(clients.get(i).getUsername().equals(id) && clients.get(i).getPassword().equals(mdp)) {
				clients.get(i).setPort(Integer.parseInt(port));
				return true;
			}
		}
		return false;
	}
	@Test
	void testIsExistId() {
		StringBuilder msg= new StringBuilder("33");
		System.out.println("string id"+msg);
		assertEquals(33, StringtoInt(msg));
		

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
	@Test
	void testIsPrice() {
		char [] test1 = new char[988];
		test1[0]='5';
		test1[0]='6';
		test1[0]='9';
		int k=0;
		
		for(int i=0;i<test1.length;i++)
		{
			if(test1[i] ==-1)
				break;
			else
				k++;
		}
		char[] result= new char[k];
		for(int j=0;j<k;j++)
		{
			result[j]=test1[j];
		}
		System.out.println(String.valueOf(result));
		assertEquals(true,isPrice(new String(result)));
	}
	
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
	

}
