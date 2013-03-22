package ocept.gogo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class prefs extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);         
		
		PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(getString(R.string.BOUNTY_PREFS));
        prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);
        
        
		addPreferencesFromResource(R.xml.prefs);
	}
}
