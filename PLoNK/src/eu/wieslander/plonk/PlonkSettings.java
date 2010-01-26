package eu.wieslander.plonk;


import android.app.Activity;
import android.content.Context;
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
    
    private PlonkCfg cfg;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plonk_settings);

		cfg = PlonkCfg.getConfig(this);
        
        //Declare BUttons and text view's
		Button pchIpSaveButton = (Button) findViewById(R.id.pchIpSaveButton);
		Button llinkIpSaveButton = (Button) findViewById(R.id.llinkIpSaveButton);
		final EditText pchIpInput = (EditText) findViewById(R.id.pchIpInput);
		final EditText llinkIpInput = (EditText) findViewById(R.id.llinkIpInput);
        final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
        final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
		// Get stored ip's
		updateResults(pchIpInput, llinkIpInput, checkBoxA, checkBoxC);
		
		checkBoxA.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (checkBoxA.isChecked()) {
                    checkBoxA.setChecked(true);
                    checkBoxC.setChecked(false);
                    cfg.setPchVersion(PlonkCfg.VERSION_A100);
                }else{
                    checkBoxA.setChecked(false);
                    checkBoxC.setChecked(true);
                    cfg.setPchVersion(PlonkCfg.VERSION_C200);
                }
                cfg.store(PlonkSettings.this);
            }
        });
        checkBoxC.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final CheckBox checkBoxA = (CheckBox) findViewById(R.id.CheckBoxA100);
                final CheckBox checkBoxC = (CheckBox) findViewById(R.id.CheckBoxC200);
                if (checkBoxC.isChecked()) {
                    checkBoxA.setChecked(false);
                    checkBoxC.setChecked(true);
                    cfg.setPchVersion(PlonkCfg.VERSION_C200);
                }else{
                    checkBoxA.setChecked(true);
                    checkBoxC.setChecked(false);
                    cfg.setPchVersion(PlonkCfg.VERSION_A100);
                }
                cfg.store(PlonkSettings.this);
            }
        });
	
		// Handle button-click
		// Check out http://developer.android.com/guide/topics/data/data-storage.html
		pchIpSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String newUrl = pchIpInput.getText().toString();
				if (newUrl.length() > 0 && newUrl.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")) {
                    cfg.setPchIp(newUrl);
                    cfg.store(PlonkSettings.this);
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
                    cfg.setLlinkIp(newUrl);
                    cfg.store(PlonkSettings.this);
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
            editor.putString(PCH_VERSION, eu.wieslander.plonk.PlonkCfg.VERSION_C200);
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
                editor.putString(PCH_VERSION, eu.wieslander.plonk.PlonkCfg.VERSION_C200);
                editor.commit();
            }   
    }*/
	
    /**
     * Gets the stored results from the shared preferences
     */
	private void updateResults(EditText pchIpInput,
			EditText llinkIpInput, CheckBox checkBoxA, CheckBox checkBoxC) {
        PlonkCfg cfg = PlonkCfg.getConfig(this); // Re-read
		pchIpInput.setText(cfg.getPchIp());
		llinkIpInput.setText(cfg.getLlinkIp());
        checkBoxA.setChecked(cfg.isA100());
        checkBoxC.setChecked(! cfg.isA100());
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
