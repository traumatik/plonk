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
    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */

    private PlonkCfg cfg;

    static final String TAG = "PLoNK";
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
		cfg = PlonkCfg.getConfig(this); // Get preferences
        
        setContentView(R.layout.plonk_remote);
        
        // See http://www.networkedmediatank.com/wiki/index.php/Remote_Control_using_Telnet
        findViewById(R.id.MenuButton).setOnClickListener(new ButtonListener(0xDD, "B"));
        findViewById(R.id.UpButton).setOnClickListener(new ButtonListener(0xA8, "U"));
        findViewById(R.id.SourceButton).setOnClickListener(new ButtonListener(0xD0, "O"));
        findViewById(R.id.LeftButton).setOnClickListener(new ButtonListener(0xAA, "L"));
        findViewById(R.id.OkButton).setOnClickListener(new ButtonListener(0x0D, "\n"));
        findViewById(R.id.RightButton).setOnClickListener(new ButtonListener(0xAB, "R"));
        findViewById(R.id.InfoButton).setOnClickListener(new ButtonListener(0x95, "i"));
        findViewById(R.id.DownButton).setOnClickListener(new ButtonListener(0xA9, "D"));
        findViewById(R.id.BackButton).setOnClickListener(new ButtonListener(0x8D, "v"));
        findViewById(R.id.PlayButton).setOnClickListener(new ButtonListener(0xE9, "y"));
        findViewById(R.id.PauseButton).setOnClickListener(new ButtonListener(0xEA, "p"));
        findViewById(R.id.StopButton).setOnClickListener(new ButtonListener(0x1B, "s"));
    }
/*   Defining all buttons....

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
    
    //End of buttons
	
    private void errorMessage(String msg) {
        Log.e(TAG, msg);
        // this.showAlert("Error!", msg, "OK", true);
		    //We show a short alert.
		    Toast.makeText(PlonkRemoteActivity.this, msg,
               Toast.LENGTH_LONG).show();
        System.err.println("Error:" + msg);
   }
    
    //Call sendCommand to send commands to the PCH
    public void sendCommand(String command) {
        Log.d(TAG, "Sending command '" + command + "'");
        Socket kkSocket = null;
        PrintWriter out = null;
        try {
      	  //testing creating socket later
          Log.v(TAG, "Opening socket to " + cfg.getPchIp() + ":" + cfg.getPchPort());
      	  kkSocket = new Socket(cfg.getPchIp(), cfg.getPchPort());
      	   // 
             Log.v(TAG, "Writing command to socket"); 
             out = new PrintWriter(kkSocket.getOutputStream(), false);
             if(cfg.isA100())
                out.println(command);
             else
                out.print(command); // TODO: Confirm no newline for C-200
             out.flush();
             out.close();
             //testing closing socket
             kkSocket.close();
            Log.v(TAG, "Done sending command");
             
        } catch (UnknownHostException e) {
             Log.d(TAG, "Error sending", e);
             errorMessage("Unknown host" + cfg.getPchIp());
        } catch (IOException e) {
             android.util.Log.d(TAG, "Error sending", e);
             errorMessage("Couldn't get I/O for the connection to: " + cfg.getPchIp());
        }
        finally {
            if(out != null)
                out.close();
            if(kkSocket != null && ! kkSocket.isClosed()) {
                try {
                    kkSocket.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

   }
    @Override
    protected void onPause() {
         // TODO Auto-generated method stub
         super.onPause();


    }

    @Override
    protected void onResume() {
         super.onResume();
    }

    @Override
    protected void onStop() {

         super.onStop();
    }
   
    private class ButtonListener implements View.OnClickListener {
        
        private final String command100Series;
        
        private final String command200Series;

        private ButtonListener(int command100Series, String command200Series) {
            this.command100Series = Integer.toString(command100Series);
            this.command200Series = command200Series;
        }

        public void onClick(View view) {
            sendCommand(cfg.isA100() ? command100Series : command200Series);
        }
    }
}
