package eu.wieslander.pax;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Pax extends Activity{
	
	// name of shared_prefs file
	public static final String MYPREFS = "paxIpPrefs";
	public static final String XBMC_IP_PREFS = "XbmcIp";
	public static final String XBMC_PORT_PREFS = "XbmcPort";
	public static SharedPreferences paxIpPrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Get preferences
		paxIpPrefs = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
		
		//Declare EditText's
		final EditText xbmcIp = (EditText) findViewById(R.id.XbmcIp);
		final EditText xbmcPort = (EditText) findViewById(R.id.XbmcPort);
		updateResults(xbmcIp, xbmcPort);
		
	}
	
	public void mySaveButtonHandler (View target){
		final EditText xbmcIp = (EditText) findViewById(R.id.XbmcIp);
		final EditText xbmcPort = (EditText) findViewById(R.id.XbmcPort);
		String newIp = xbmcIp.getText().toString();
		if (newIp.length() > 0 && newIp.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")) {
			SharedPreferences.Editor editor = paxIpPrefs.edit();
			editor.putString(XBMC_IP_PREFS, newIp);
		    editor.commit();
		    xbmcIp.setText(newIp);
		}else{
		xbmcIp.setText(paxIpPrefs.getString(XBMC_IP_PREFS, getString(R.string.xbmcIpString)));	
		}
		String newPort = xbmcPort.getText().toString();
		if (newPort.length() > 0 && newPort.matches("^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[1-9][0-9]?[0-9]?[0-9]?|0)")) {
			SharedPreferences.Editor editor = paxIpPrefs.edit();
			editor.putString(XBMC_PORT_PREFS, newPort);
		    editor.commit();
		    xbmcPort.setText(newPort);
		}else{
		xbmcPort.setText(paxIpPrefs.getString(XBMC_PORT_PREFS, getString(R.string.xbmcPortString)));	
		}
		//Tell user we have saved results
		Toast.makeText(this, R.string.savetoast, Toast.LENGTH_SHORT).show();
	}

	public void updateResults(EditText xbmcIp, EditText xbmcPort) {
		// Update saved settings
		String xbmcIpSave = paxIpPrefs.getString(XBMC_IP_PREFS, getString(R.string.xbmcIpString));
		xbmcIp.setText(xbmcIpSave);
		String xbmcPortSave = paxIpPrefs.getString(XBMC_PORT_PREFS, getString(R.string.xbmcPortString));
		xbmcPort.setText(xbmcPortSave);
		
		
	}

}
