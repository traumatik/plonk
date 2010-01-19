package eu.wieslander.plonk;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PlonkSettings extends Activity {

	// name of shared_prefs file
	public static final String MYPREFS = "plonkIpPreferences";

	private static final String PCH_IP_PREF = "Pch_Ip";
	private static final String LLINK_IP_PREF = "Llink_Ip";
	private static final String PCH_VERSION_PREF = "Pch_Version";

	private SharedPreferences plonkIpPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plonk_settings);

		// Get preferences
		plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
		
		//Declare BUttons and text view's
		Button pchIpSaveButton = (Button) findViewById(R.id.pchIpSaveButton);
		Button llinkIpSaveButton = (Button) findViewById(R.id.llinkIpSaveButton);
		final EditText pchIpInput = (EditText) findViewById(R.id.pchIpInput);
		final EditText llinkIpInput = (EditText) findViewById(R.id.llinkIpInput);
        final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
        final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
		// Get stored ip's
		updateResults(pchIpInput, llinkIpInput, checkBoxA, checkBoxC);
		
		
	
		// Handle button-click
		// Check out http://developer.android.com/guide/topics/data/data-storage.html
		pchIpSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String newUrl = pchIpInput.getText().toString();
				if (newUrl.length() > 0 && newUrl.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")) {
					SharedPreferences.Editor editor = plonkIpPreferences.edit();
					editor.putString(PCH_IP_PREF, newUrl);
				    editor.commit();
				    pchIpInput.setText(newUrl);
					showToast();			    
				}else{
				pchIpInput.setText("");	
				}
				
			}


		

		});
		// Handle button-click
		llinkIpSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String newUrl = llinkIpInput.getText().toString();
				if (newUrl.length() > 0 && newUrl.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):(6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[1-9][0-9]?[0-9]?[0-9]?|0)")) {
					SharedPreferences.Editor editor = plonkIpPreferences.edit();
					editor.putString(LLINK_IP_PREF, newUrl);
				    editor.commit();
				    llinkIpInput.setText(newUrl);
                    showToast();
				}else{
				llinkIpInput.setText("");	
				}
				
			}
		

		});
	}
	
/*    public void myCheckBoxAhandler(View target){
        final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
        final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
        if (checkBoxA.isChecked()) {
            checkBoxA.setChecked(false);
            checkBoxC.setChecked(true);
            SharedPreferences.Editor editor = plonkIpPreferences.edit();
            editor.putString(PCH_VERSION, getString(R.string.pch_C200_version));
            editor.commit();
        }else if (!checkBoxA.isChecked()){
            checkBoxA.setChecked(true);
            checkBoxC.setChecked(false);
            SharedPreferences.Editor editor = plonkIpPreferences.edit();
            editor.putString(PCH_VERSION, getString(R.string.pch_A100_version));
            editor.commit();
        }
    }
    public void myCheckBoxChandler(View target){
            final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
            final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
            if (checkBoxC.isChecked()) {
                checkBoxA.setChecked(true);
                checkBoxC.setChecked(false);
                SharedPreferences.Editor editor = plonkIpPreferences.edit();
                editor.putString(PCH_VERSION, getString(R.string.pch_A100_version));
                editor.commit();
            }else if (!checkBoxC.isChecked()) {
                checkBoxA.setChecked(false);
                checkBoxC.setChecked(true);
                SharedPreferences.Editor editor = plonkIpPreferences.edit();
                editor.putString(PCH_VERSION, getString(R.string.pch_C200_version));
                editor.commit();
            }   
    }*/
	
    public void myCheckBoxAhandler(View target){
        final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
        final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
        if (checkBoxA.isChecked()) {
            checkBoxA.setChecked(true);
            checkBoxC.setChecked(false);
            SharedPreferences.Editor editor = plonkIpPreferences.edit();
            editor.putString(PCH_VERSION_PREF, getString(R.string.pch_A100_version));
            editor.commit();
        }else{
            checkBoxA.setChecked(false);
            checkBoxC.setChecked(true);
            SharedPreferences.Editor editor = plonkIpPreferences.edit();
            editor.putString(PCH_VERSION_PREF, getString(R.string.pch_C200_version));
            editor.commit();
        }
    }
    public void myCheckBoxChandler(View target){
            final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
            final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
            if (checkBoxC.isChecked()) {
                checkBoxA.setChecked(false);
                checkBoxC.setChecked(true);
                SharedPreferences.Editor editor = plonkIpPreferences.edit();
                editor.putString(PCH_VERSION_PREF, getString(R.string.pch_C200_version));
                editor.commit();
            }else{
                checkBoxA.setChecked(true);
                checkBoxC.setChecked(false);
                SharedPreferences.Editor editor = plonkIpPreferences.edit();
                editor.putString(PCH_VERSION_PREF, getString(R.string.pch_A100_version));
                editor.commit();
            }   
    }
	
    /**
     * Gets the stored results from the shared preferences
     * @param add_pch_ip_result
     * @param add_llink_ip_result
     */
	private void updateResults(EditText pchIpInput,
			EditText llinkIpInput, CheckBox checkBoxA, CheckBox checkBoxC) {
		String pch_ip = plonkIpPreferences.getString(PCH_IP_PREF, getString(R.string.add_pch_ip_hint));
		pchIpInput.setText(pch_ip);
		String llink_ip = plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint));
		llinkIpInput.setText(llink_ip);
        String pch_version = plonkIpPreferences.getString(PCH_VERSION_PREF, getString(R.string.pch_default_version));
        String a100 = getString(R.string.pch_A100_version);
        if (pch_version.compareTo(a100) == 0) {
            checkBoxA.setChecked(true);
            checkBoxC.setChecked(false);
        }else {
            checkBoxA.setChecked(false);
            checkBoxC.setChecked(true);
        }
	}
	/**
	 * Displays a toast with the android robot and settings saved message
	 */
	private void showToast() {
		Context context = getApplicationContext();
		String msg = getString(R.string.settings_saved_toast);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, msg, duration);
		int offsetX = 0;
		int offsetY = 0;
		toast.setGravity(Gravity.BOTTOM, offsetX, offsetY);
		LayoutInflater inflater = LayoutInflater.from(context);
		View toastview = inflater.inflate(R.layout.toast_view, null);
		toast.setView(toastview);
		toast.show();	
	}
}
