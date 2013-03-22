package ocept.gogo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class prefs extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
