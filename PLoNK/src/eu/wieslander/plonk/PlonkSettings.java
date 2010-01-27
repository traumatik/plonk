package eu.wieslander.plonk;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import java.util.regex.Pattern;

public class PlonkSettings extends Activity {
    
    private static final String TAG = "PlonkSettings";

    private static final String IP_REGEXP = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
    
    private static final Pattern IP_AND_PORT_PATTERN = Pattern.compile(IP_REGEXP + ":(6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|[1-9][0-9]?[0-9]?[0-9]?|0)");
    
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
        final RadioGroup pchVersionGroup = (RadioGroup) findViewById(R.id.PchVersionGroup);
        
        // Update fields with current settings
        updateResults(pchIpInput, llinkIpInput, pchVersionGroup);

        // Create change listeners
        
        pchVersionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                final String pchVersion =
                        (checked == R.id.RadioButtonA100) ? PlonkCfg.VERSION_A100 :
                        PlonkCfg.VERSION_C200;
                Log.d(TAG, "Setting PCH version to " + pchVersion);
                cfg.setPchVersion(pchVersion);
                cfg.store(PlonkSettings.this);
            }
        });
        
		// Handle button-click
		pchIpSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String newIp = pchIpInput.getText().toString();
				if (newIp.length() > 0 && IP_PATTERN.matcher(newIp).matches()) {
                    Log.d(TAG, "Setting PCH IP to " + newIp);
                    cfg.setPchIp(newIp);
                    cfg.store(PlonkSettings.this);
				    // pchIpInput.setText(newIp);
					showToast();			    
				} else {
                    Log.i(TAG, "Invalid PCH IP: " + newIp);
                    errorMessage(R.string.invalid_ip);
				    pchIpInput.setText("");
                    pchIpInput.requestFocus();
				}
			}
		});

		// Handle button-click
		llinkIpSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String newUrl = llinkIpInput.getText().toString();
				if (newUrl.length() > 0 && IP_AND_PORT_PATTERN.matcher(newUrl).matches()) {
                    Log.d(TAG, "Setting Llink IP to " + newUrl);
                    cfg.setLlinkIp(newUrl);
                    cfg.store(PlonkSettings.this);
				    // llinkIpInput.setText(newUrl);
                    showToast();
				} else {
                    Log.i(TAG, "Invalid LLink IP: " + newUrl);
                    errorMessage(R.string.invalid_ip);
				    llinkIpInput.setText("");
                    llinkIpInput.requestFocus();
				}
			}
		});
        
        // TODO: Consider global OK/Cancel button
	}
	
    /**
     * Gets the stored results from the shared preferences
     */
	private void updateResults(EditText pchIpInput, EditText llinkIpInput, RadioGroup pchVersionGroup) {
		pchIpInput.setText(cfg.getPchIp());
		llinkIpInput.setText(cfg.getLlinkIp());
        pchVersionGroup.check(cfg.isA100() ? R.id.RadioButtonA100 : R.id.RadioButtonC200);
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

    private void errorMessage(int resId) {
        errorMessage(getString(resId));
    }
    
    private void errorMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
   }
}
