package eu.wieslander.pax;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PaxAlert extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.alert);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
        .setTitle(getString(R.string.alertTitle)) 
        .setMessage(getString(R.string.alertText)) 
        .setPositiveButton(R.string.ButtonStop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	stopService(new Intent("eu.wieslander.pax.SERVICE"));
        		PaxAlert.this.finish();
            }
        })
        .setNeutralButton(R.string.ButtonView, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	startActivity(new Intent(PaxAlert.this, PaxFileBrowser.class));
            	PaxAlert.this.finish();
            }
        })
        .setNegativeButton(R.string.ButtonCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	PaxAlert.this.finish();
            }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
        public void onCancel(DialogInterface dialog) {
        	PaxAlert.this.finish();
        }})
        .show();
		
	}
	
}
