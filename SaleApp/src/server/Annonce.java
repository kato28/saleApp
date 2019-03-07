package server;

public class Annonce {

	String username;
	int code;
	static int count=0;
	int id;
	float prix;
	String desc;
	
	public Annonce(String username,int code, float prix, String desc) {
		super();
		this.code=code;
		id=++count;
		this.username = username;
		this.prix = prix;
		this.desc = desc;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public static long getCount() {
		return count;
	}
	public static void setCount(int count) {
		Annonce.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getPrix() {
		return prix;
	}
	public void setPrix(float prix) {
		this.prix = prix;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
