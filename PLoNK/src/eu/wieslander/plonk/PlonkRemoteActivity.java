package eu.wieslander.plonk;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;

public class PlonkRemoteActivity extends Activity {

    /** Logging tag */
    static final String TAG = "PlonkRemoteActivity";

    /** The configuration */
    private PlonkCfg cfg;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.plonk_remote);
        
        cfg = PlonkCfg.getConfig(this); // Get preferences
        registerKeys(); // Register listeners for buttons
    }

/* Defining all buttons for C-200

 x=On/Off    j=Eject

T=TV Mode  e=Setup h=BT

         1=1 2=2 3=3
         4=4 5=5 6=6
         7=7 8=8 9=9
c=Delete  0=0 l=CAPS/NUM
O=Home g=File Mode H=Time Seek
P=Video G=Music Y=Photo K=All

v=Return B=Source

              U=Up
L=Left Enter=OK R=Right
             D=Down
i=INFO s=Stop

+=PgUp(Vol+)----y=Play
-=PgDn(Vol- )----p=Pause

u=Mute w=Rev f=FWD
m=Menu E=Prev n=Next
t=Title r=Repeat d=Slow
N=Angle a=Audio b=Subtitle z=Zoom
     * 
     */
    
    /**
     * A-100/A-110: See http://www.networkedmediatank.com/wiki/index.php/Remote_Control_using_Telnet 
     */
    private void registerKeys() {
        registerKey(R.id.MenuButton,   0xDD, 'B');
        registerKey(R.id.UpButton,     0xA8, 'U');
        registerKey(R.id.SourceButton, 0xD0, 'O');
        registerKey(R.id.LeftButton,   0xAA, 'L');
        registerKey(R.id.OkButton,     0x0D, '\n');
        registerKey(R.id.RightButton,  0xAB, 'R');
        registerKey(R.id.InfoButton,   0x95, 'i');
        registerKey(R.id.DownButton,   0xA9, 'D');
        registerKey(R.id.BackButton,   0x8D, 'v');
        registerKey(R.id.PlayButton,   0xE9, 'y');
        registerKey(R.id.PauseButton,  0xEA, 'p');
        registerKey(R.id.StopButton,   0x1B, 's');
    }

    /** Create button listener for a single button */
    private void registerKey(int id, int a100Command, char c200Command) {
        findViewById(id).setOnClickListener(new ButtonListener(a100Command, c200Command));
    }

    private void errorMessage(String msg) {
        Log.e(TAG, msg);
        // this.showAlert("Error!", msg, "OK", true);
		//We show a short alert.
		Toast.makeText(PlonkRemoteActivity.this, msg, Toast.LENGTH_LONG).show();
   }
    
    /** Call this method to send commands to the PCH */
    public void sendCommand(ButtonListener buttonListener) {
        final String command = cfg.isA100() ? buttonListener.getCommand100Series() : buttonListener.getCommand200Series();
        Log.d(TAG, "Sending command '" + command + "'");
        Socket socket = null;
        PrintWriter out = null;
        try {
            Log.v(TAG, "Opening socket to " + cfg.getPchIp() + ":" + cfg.getPchPort());
      	    socket = new Socket(cfg.getPchIp(), cfg.getPchPort());
            Log.v(TAG, "Writing command to socket"); 
            out = new PrintWriter(socket.getOutputStream(), false);
            out.print(command);
            if(cfg.isA100())  // TODO: Confirm no newline for C-200
               out.println();
            out.flush();
            Log.v(TAG, "Done sending command");
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error connecting", e);
            errorMessage("Unknown host: " + cfg.getPchIp());
        } catch (IOException e) {
            Log.e(TAG, "Error sending", e);
            errorMessage("Couldn't get I/O for the connection to: " + cfg.getPchIp());
        }
        finally {
            if(out != null)
                out.close();
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing socket", e);
                }
            }
        }

   }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload prefs, just in case settings have changed
        cfg = PlonkCfg.getConfig(this); // Get preferences
    }

    /** Button listener with commands for 100-series and 200-series */
    private class ButtonListener implements View.OnClickListener {
        
        private final String command100Series;
        
        private final String command200Series;

        private ButtonListener(int command100Series, char command200Series) {
            this.command100Series = Integer.toString(command100Series);
            this.command200Series = Character.toString(command200Series);
        }

        public String getCommand100Series() {
            return command100Series;
        }

        public String getCommand200Series() {
            return command200Series;
        }

        public void onClick(View view) {
            sendCommand(this);
        }
    }
}
