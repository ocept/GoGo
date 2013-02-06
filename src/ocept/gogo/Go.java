package ocept.gogo;

import java.util.Date;

//Stores data for a single Go object
public class Go {
	public String Name;
	public String Desc;
	public Long LastChecked;
	public int KeyId;
	public Go(String name, String desc, long dateLong){
		Name = name;
		Desc = desc;
		LastChecked = dateLong;
	}
	public Go(String name, String desc, long dateLong, int id){
		Name = name;
		Desc = desc;
		LastChecked = dateLong;
		KeyId = id;
	}
	public Go(){}
}
