package eu.wieslander.plonk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText mKey;
    Button mConfirm;
    Socket kkSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    Boolean a100;
    
	public static final String MYPREFS = "plonkIpPreferences";

	private static final String PCH_IP_PREF = "Pch_Ip";
	private static final String LLINK_IP_PREF = "Llink_Ip";
	private static final String PCH_VERSION_PREF = "Pch_Version";
	
	public static String PCH_IP = "0.0.0.0";
	public static String PCH_VERSION = "";
	
	public static int PCH_PORT_COMMAND = 30000;
	public static int PCH_PORT_PEACH_IR = 30002;
	
	private SharedPreferences plonkIpPreferences;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
		// Get preferences
		plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
		
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
        setContentView(R.layout.plonk_remote);
        
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
    public void mySourceButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("DD"));
		} else {
			sendCommand("B");
		}
    }
    
    public void myUpButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("A8"));
		} else {
			sendCommand("U");
		}
    }
    public void myHomeButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("D0"));
		} else {
			sendCommand("O");
		}
    }
    public void myLeftButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("AA"));
		} else {
			sendCommand("L");
		}
    }
    public void myOkButtonHandler(View target){
    	if (a100) {
    		sendCommand("13\n");
		} else {
			sendCommand("\n");
		}
    }
    public void myRightButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("AB"));
		} else {
			sendCommand("R");
		}
    }
    public void myInfoButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("95"));
		} else {
			sendCommand("i");
		}
    }
    public void myDownButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("A9"));
		} else {
			sendCommand("D");
		}
    }
    public void myBackButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("8D"));
		} else {
			sendCommand("v");
		}
    }
    public void myPlayButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("E9"));
		} else {
			sendCommand("y");
		}
    }
    public void myPauseButtonHandler(View target){
    	if (a100) {
    		sendCommand(toCode("EA"));
		} else {
			sendCommand("p");
		}
    }
	public void myStopButtonHandler(View target){
    	if (a100) {
    		sendCommand("212");
		} else {
			sendCommand("s");
		}
    }
    //End of buttons
	
    private void errorMessage(String msg) {
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
        
        try {
            //more test
      	  if (kkSocket != null) {
                //if (kkSocket. isConnected())
                     kkSocket.close();
           }
      	  //testing creating socket later
      	  kkSocket = new Socket(PCH_IP, PCH_PORT_COMMAND);
      	   // 
             out = new PrintWriter(kkSocket.getOutputStream(), false);
             out.print(command);
             out.flush();
             //testing closing socket
             kkSocket.close();
             
        } catch (UnknownHostException e) {
             errorMessage("Unknown host" + PCH_IP);
        } catch (IOException e) {
             errorMessage("Couldn't get I/O for the connection to: " + PCH_IP);

        }

   }
    @Override
    protected void onPause() {
         // TODO Auto-generated method stub
         super.onPause();

         try {
              kkSocket.close();
         } catch (IOException e) {
              errorMessage("Couldn't close I/O for the connection to: " + PCH_IP);
         }

    }

    @Override
    protected void onResume() {
         super.onResume();
         try {
              //testar lite mera
             if (kkSocket != null) {
                      kkSocket.close();
            }
       	  //
       	   //kkSocket = new Socket(ipAddress, port);
         //} catch (UnknownHostException e) {
          //    errorMessage("Unknown host " + ipAddress);
         } catch (IOException e) {
              errorMessage("Couldn't get I/O for the connection to: " + PCH_IP);
         }
    }

    @Override
    protected void onStop() {

         super.onStop();
         try {
              if (kkSocket != null) {
                   //if (kkSocket. isConnected())
                        kkSocket.close();
              }
         } catch (IOException e) {
              errorMessage("Couldn't close I/O for the connection to: " + PCH_IP);

         }
    }
   
}
