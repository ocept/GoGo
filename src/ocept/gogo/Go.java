package ocept.gogo;

import java.util.Date;

//Stores data for a single Go object
public class Go {
	public String Name;
	public String Desc;
	public Long LastChecked;
	public int KeyId;
	public int Bounty;
	public Go(String name, String desc, long dateLong, int id, int bounty){
		Name = name;
		Desc = desc;
		LastChecked = dateLong;
		KeyId = id;
		Bounty = bounty;
	}
	public Go(){}
}
