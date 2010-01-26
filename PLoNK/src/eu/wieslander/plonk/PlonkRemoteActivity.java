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
import android.content.SharedPreferences;

public class PlonkRemoteActivity extends Activity {
    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
	// name of shared_prefs file

    //private final String ipAddress = "192.168.0.11";  // ip of server pc.
    //private final int port = 30000;

    Boolean a100;
    
    static final String TAG = "PLoNK";
    
	public static final String MYPREFS = "plonkIpPreferences";

	private static final String PCH_IP_PREF = "Pch_Ip";
	private static final String LLINK_IP_PREF = "Llink_Ip";
	private static final String PCH_VERSION_PREF = "Pch_Version";
	
	public static String PCH_IP = "0.0.0.0";
	public static String PCH_VERSION = "";
	
	public static int PCH_PORT_COMMAND = 30000;
	public static int PCH_PORT_PEACH_IR = 30002;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
		// Get preferences
        SharedPreferences plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
		
		PCH_IP = plonkIpPreferences.getString(PCH_IP_PREF, getString(R.string.add_pch_ip_hint));
        PCH_VERSION = plonkIpPreferences.getString(PCH_VERSION_PREF, getString(R.string.pch_default_version));
        
        if (PCH_VERSION.compareTo(getString(R.string.pch_A100_version))== 0) {
			a100 = true;
			PCH_PORT_COMMAND = PCH_PORT_PEACH_IR;
		}else {
			a100 = false;
			//TODO fix this
			PCH_PORT_COMMAND = 30000;
		}
        Log.d(TAG, "PCH IP: " + PCH_IP);
        Log.d(TAG, "PCH VERSION: " + PCH_VERSION);
        Log.d(TAG, "PCH port: " + PCH_PORT_COMMAND);
        Log.d(TAG, "a100? " + a100);
        
        setContentView(R.layout.plonk_remote);
        
        findViewById(R.id.MenuButton).setOnClickListener(new ButtonListener(0xDD, "B"));
        findViewById(R.id.UpButton).setOnClickListener(new ButtonListener(0xA8, "U"));
        findViewById(R.id.SourceButton).setOnClickListener(new ButtonListener(0xD0, "O"));
        findViewById(R.id.LeftButton).setOnClickListener(new ButtonListener(0xAA, "L"));
        findViewById(R.id.OkButton).setOnClickListener(new ButtonListener(0x0D, "\n")); // TODO: Test
        findViewById(R.id.RightButton).setOnClickListener(new ButtonListener(0xAB, "R"));
        findViewById(R.id.InfoButton).setOnClickListener(new ButtonListener(0x95, "i"));
        findViewById(R.id.DownButton).setOnClickListener(new ButtonListener(0xA9, "D"));
        findViewById(R.id.BackButton).setOnClickListener(new ButtonListener(0x8D, "v"));
        findViewById(R.id.PlayButton).setOnClickListener(new ButtonListener(0xE9, "y"));
        findViewById(R.id.PauseButton).setOnClickListener(new ButtonListener(0xEA, "p"));
        findViewById(R.id.StopButton).setOnClickListener(new ButtonListener(0x1B, "s")); // TODO: test
    }
/*   Defining all buttons....
     A100
     * Play 	 $((0xE9)) 	 Pause 	 $((0xEA))
     Stop 	$((0x1B)) 	OK 	$((0x0D))
     Up 	$((0xA8)) 	Down 	$((0xA9))
     Left 	$((0xAA)) 	Right 	$((0xAB))
     Back 	$((0x8D)) 	Menu 	$((0x09))
     Home 	$((0xD0)) 	Rewind 	$((0xD5))
     Forward 	$((0xD6)) 	Previous 	$((0xDB))
Next 	$((0xDC)) 	Subtitle 	$((0xEB))
Audio 	$((0xD8)) 	Info 	$((0x95))
Setup 	$((0x8C)) 	Source 	$((0xDD))
Power 	$((0xD2)) 	Red 	$((0xDE))
Green 	$((0xDF)) 	Yellow 	$((0xE0))
Blue 	$((0xE2)) 	Del 	$((0x08))
Caps 	$((0xFC)) 	Timeseek 	$((0x91))
Zoom 	$((0xDA)) 	Repeat 	$((0x90))
Angle 	$((0xEC)) 	TV Mode 	$((0x8F))
Eject 	$((0xEF)) 	Volume Up 	$((0x9E))
Volume Down 	$((0x9F)) 	Mute 	$((0xE1))

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
    
    //Convert hex to int to string
    private String toCode(String hex) {
    	int intValue = Integer.parseInt(hex, 16);
	return String.valueOf(intValue);
    }
    
    //Call sendCommand to send commands to the PCH
    public void sendCommand(String command) {
        Log.d(TAG, "Sending command '" + command + "'");
        try {
      	  //testing creating socket later
          Log.v(TAG, "Opening socket to " + PCH_IP + ":" + PCH_PORT_COMMAND);
      	  Socket kkSocket = new Socket(PCH_IP, PCH_PORT_COMMAND); // TODO: Close in finally block
      	   // 
             Log.v(TAG, "Writing command to socket"); 
             PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), false);
             if(a100)
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
             errorMessage("Unknown host" + PCH_IP);
        } catch (IOException e) {
             android.util.Log.d(TAG, "Error sending", e);
             errorMessage("Couldn't get I/O for the connection to: " + PCH_IP);

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
            sendCommand(a100 ? command100Series : command200Series);
        }
    }
}
