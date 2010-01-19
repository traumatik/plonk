package eu.wieslander.pax;

import java.io.BufferedReader;
import java.io.File;  
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList; 
import java.util.Enumeration;
import java.util.List; 

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Activity;
import android.app.AlertDialog; 
import android.app.ListActivity; 
import android.content.DialogInterface; 
import android.content.Intent; 
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;  
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View; 
import android.widget.ArrayAdapter; 
import android.widget.ListView; 
import android.widget.Toast;

public class PaxFileBrowser extends ListActivity { 
	
	private Handler mHandler; 
	
	// Some source from http://www.anddev.org/android_filebrowser__v20-t101-s30.html
     private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }

	private static final int SHOW_SETTINGS = 0;

	private static final int IMAGE = 0;

	private static final int VIDEO = 1;

	private static final int AUDIO = 2; 

     private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE; 
     private List<String> directoryEntries = new ArrayList<String>(); 
     private File currentDirectory = new File("/"); 

     /** Called when the activity is first created. */ 
     @Override 
     public void onCreate(Bundle icicle) { 
          super.onCreate(icicle);
                   
          //Here we catch the intent to send a file to xbmc
          final Intent intent = getIntent();
          final String action = intent.getAction();
          final String type = intent.getType();
          if (Intent.ACTION_SEND.equals(action) && type.contains("image")) {
              // Requested to SEND: set that state, and the data being edited.
              Uri newUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
              Log.i("asdfasfsd", "mUri.getDataString done: " + newUri);
              if (newUri != null){ 
                  String path = newUri.toString();
                  Toast toast = Toast.makeText(getApplicationContext(), path + " Real: " + getRealPathFromURI(newUri) , Toast.LENGTH_LONG);
                  toast.show();
                  while (startService(new Intent(PaxFileBrowser.this, PaxService.class)) == null ) {
                		startService(new Intent(PaxFileBrowser.this, PaxService.class));
                		sendUrl(path, IMAGE);
				}
                  browseToRoot();
              }
          }else {
              // setContentView() gets called within the next line, 
              // so we do not need it here. 
              browseToRoot();
              return;
          } 
     } 
     @Override
     public void onResume(){
    	 super.onResume();
   		//Start service
   		startService(new Intent("eu.wieslander.pax.SERVICE"));
     }
     
  // And to convert the image URI to the direct file system path of the image file
     public String getRealPathFromURI(Uri contentUri) {

     	// can post image
     	String [] proj={MediaStore.Images.Media.DATA};
     	Cursor cursor = managedQuery( contentUri,
     			proj, // Which columns to return
     			null,       // WHERE clause; which rows to return (all rows)
     			null,       // WHERE clause selection arguments (none)
     			null); // Order-by clause (ascending by name)
     	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
     	cursor.moveToFirst();

     	return cursor.getString(column_index);
     }
     
     /** 
      * This function browses to the 
      * root-directory of the file-system. 
      */ 
     private void browseToRoot() { 
          browseTo(new File("/")); 
    } 
      
     /** 
      * This function browses up one level 
      * according to the field: currentDirectory 
      */ 
     private void upOneLevel(){ 
          if(this.currentDirectory.getParent() != null) 
               this.browseTo(this.currentDirectory.getParentFile()); 
     } 
      
     private void browseTo(final File aDirectory){ 
          if (aDirectory.isDirectory()){ 
               this.currentDirectory = aDirectory; 
               fill(aDirectory.listFiles()); 
          }else{
        	  //TODO Catch file endings and send url to xbmc/pch
        	  //http://www.anddev.org/android_filebrowser__v20-t101.html for ending matching
        	  String fileName = aDirectory.getName();
        	  String pathName = aDirectory.getAbsolutePath();
              /* Determine what to do,
               * depending on the FileEndings defined in: 
               * res/values/fileendings.xml. */ 
              if(checkEndsWithInStringArray(fileName, getResources(). 
                                  getStringArray(R.array.fileEndingImage))){ 
                   //TODO something
            	  sendUrl(pathName, IMAGE);
            	  Log.i("PAX", fileName);
              }else if(checkEndsWithInStringArray(fileName, getResources(). 
                                  getStringArray(R.array.fileEndingVideo))){ 
                   //TODO something more
            	  sendUrl(pathName, VIDEO);
              }else if(checkEndsWithInStringArray(fileName, getResources(). 
                                  getStringArray(R.array.fileEndingAudio))){ 
                   //TODO something else
            	  sendUrl(pathName, AUDIO);
              }else{ 
                   //TODO What to do with all other stuff
            	  OnClickListener okButtonListener = new OnClickListener(){ 
                    //@Override 
                    public void onClick(DialogInterface arg0, int arg1) { 
                    	// Lets start an intent to View the file, that was clicked... 
                        PaxFileBrowser.this.openFile(aDirectory); 
                        //TODO crashing here....?
              } 
         }; 
               OnClickListener cancelButtonListener = new OnClickListener(){ 
                    //@Override 
                    public void onClick(DialogInterface arg0, int arg1) { 
                         // Do nothing 
                    } 
               }; 
               new AlertDialog.Builder(this) 
               .setTitle(getString(R.string.file_open_alert_dialog_title)) 
               .setMessage(getString(R.string.file_open_alert_dialog_message) + "\n"+ aDirectory.getName()) 
               .setPositiveButton(R.string.file_open_alert_dialog_ok, okButtonListener) 
               .setNegativeButton(R.string.file_open_alert_dialog_cancel, cancelButtonListener) 
               .show();
              }  
        	   
          } 
     } 

     /** Checks whether checkItsEnd ends with 
      * one of the Strings from fileEndings */ 
     private boolean checkEndsWithInStringArray(String checkItsEnd, 
                         String[] fileEndings){ 
          for(String aEnd : fileEndings){ 
               if(checkItsEnd.toLowerCase().endsWith(aEnd)) 
                    return true; 
          } 
          return false; 
     }
     
     
     private void openFile(File aFile) { 
         Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, 
                   Uri.parse("file://" + aFile.getAbsolutePath()));
         myIntent.setType("image/jpg");
         startActivity(myIntent); 
    }
     
     private void fill(File[] files) { 
          this.directoryEntries.clear(); 
           
          // Add the "." and the ".." == 'Up one level' 
          try { 
               Thread.sleep(10); 
          } catch (InterruptedException e1) { 
               // TODO Auto-generated catch block 
               e1.printStackTrace(); 
          } 
          this.directoryEntries.add("."); 
           
          if(this.currentDirectory.getParent() != null) 
               this.directoryEntries.add(".."); 
           
          switch(this.displayMode){ 
               case ABSOLUTE: 
                    for (File file : files){ 
                         this.directoryEntries.add(file.getPath()); 
                    } 
                    break; 
               case RELATIVE: // On relative Mode, we have to add the current-path to the beginning 
                    int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length(); 
                    for (File file : files){ 
                         this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght)); 
                    } 
                    break; 
          } 
           
          ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, 
        		  android.R.layout.simple_list_item_1, this.directoryEntries);
                    //TODO Decide what works best
        		    //R.layout.file_row, this.directoryEntries); 
                    //android.R.layout.simple_list_item_1
          this.setListAdapter(directoryList); 
     } 

     @Override 
     protected void onListItemClick(ListView l, View v, int position, long id) { 
      int selectionRowID = position; 
         String selectedFileString = this.directoryEntries.get(selectionRowID); 
         if (selectedFileString.equals(getString(R.string.current_level))) 
         { 
              // Refresh 
              this.browseTo(this.currentDirectory); 
         } else if(selectedFileString.equals(getString(R.string.up_one_level))) 
         { 
              this.upOneLevel(); 
         } 
         else 
         { 
              File clickedFile = null; 
              switch(this.displayMode){ 
                   case RELATIVE: 
                        clickedFile = new File(this.currentDirectory.getAbsolutePath() 
                                                           + this.directoryEntries.get(selectionRowID)); 
                        break; 
                   case ABSOLUTE: 
                        clickedFile = new File(selectedFileString); 
                        break; 
              } 
              if(clickedFile != null) 
              { 
                 Log.d("AndroidFileBrowser", "File " + clickedFile + " exists? " + 
                            clickedFile.exists()); 
                 this.browseTo(clickedFile); 
              } 
         } 
     }
     
     @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		super.onCreateOptionsMenu(menu);
 		menu.add(0, SHOW_SETTINGS, Menu.NONE, getString(R.string.menu_settings)).setIcon(android.R.drawable.ic_menu_preferences);
 		return true;
 	}
     
     @Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		super.onOptionsItemSelected(item);
 		switch (item.getItemId()) {
 		case SHOW_SETTINGS: {
 			Intent explicitIntent = new Intent(this, Pax.class);
 			startActivity(explicitIntent);			
 			return true;
 		}
 		}
 		return false;
 	}
     
     public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             upOneLevel();
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
     
     public void sendUrl (String path, int type){
        //Here we fix the path we get from the file browser and create the correct url to send
    	 //http://xbox/xbmcCmds/xbmcHttp?command=PlayFile(F:\music\test.mp3)
    	 //http://xbox/xbmcCmds/xbmcHttp?command=ShowPicture(F:\apps\xbmc\media\splash.png)
    	 //http://pch:9999/PLoNK_web/plonk_nmt.php?act=" + mCommand + "&device=" + PCH_VERSION + "&url=" + mLink
    	SharedPreferences paxIpPrefs = getSharedPreferences(Pax.MYPREFS, Activity.MODE_PRIVATE);
    	StringBuilder sb = new StringBuilder();
    	sb.append("http://");
    	sb.append(paxIpPrefs.getString(Pax.XBMC_IP_PREFS, getString(R.string.xbmcIpString)));
    	sb.append(":");
    	//TODO switch for xbmc or PCH
    	sb.append(paxIpPrefs.getString(Pax.XBMC_PORT_PREFS, getString(R.string.xbmcPortString)));
    	//Commands for XBMC
    	sb.append("/xbmcCmds/xbmcHttp?command=");
    	//TODO command for PCH /PLoNK_web/plonk_nmt.php?act=
    	switch (type) {
		case IMAGE:
			sb.append("ShowPicture");
			break;
		case AUDIO:
			sb.append("PlayFile");
			break;
		case VIDEO:
			sb.append("PlayFile");
			break;
		default:
			break;
		}
    	sb.append("(");
    	//Time for setting up the local url to the PaxService httpd
    	sb.append("http://");
    	sb.append(getLocalIpAddress());
    	//TODO fix the hard coded port Number
    	sb.append(":8080");
    	//sb.append(path);
    	//End 
    	//Fix the path encoding   	
    	String encoded = null;
		try {
			encoded = URLEncoder.encode(path, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    	encoded = encoded.replace("*", "%2A");  
    	encoded = encoded.replace("~", "%7E");  
    	//encoded = encoded.replace("+", "%20");
    	sb.append(encoded);
    	sb.append(")");
    	Log.i("PAX", "sb: " + sb.toString());
    	
    	//TODO make it a URL
    	URI uri = null;
		try {
			uri = new URI(sb.toString());
			Log.i("PAX", "URI: " + uri.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	new SendCommmandTask().execute(uri);
     }

 	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						Log.i("LOG_pawn", inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("LOG_pawn", ex.toString());
		}
		return null;
	}
     
     private class SendCommmandTask extends AsyncTask<URI, Void, Void> {
    	 protected Void doInBackground(URI... uris){
    		 //TODO http request method
 	        	DefaultHttpClient httpclient = new DefaultHttpClient();
 	        	HttpGet httpget = new HttpGet(uris[0]);
 	        	publishProgress();

 	           // Create a response handler
 	           ResponseHandler<String> responseHandler = new BasicResponseHandler();
 	           try {
				String responseBody = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

 	           // When HttpClient instance is no longer needed, 
 	           // shut down the connection manager to ensure
 	           // immediate deallocation of all system resources
 	           httpclient.getConnectionManager().shutdown();  

    		 return null;
    	 }
    	 protected void onProgressUpdate(String progress){
    		 Toast.makeText(PaxFileBrowser.this, R.string.commandSending, Toast.LENGTH_SHORT).show(); 
    	 }
    	 protected void onPostExecute(){
    		 Toast.makeText(PaxFileBrowser.this, R.string.commandSent, Toast.LENGTH_SHORT).show();
    	 }
     }
     
     
     //TODO replace with AsyncTask
     private class PaxSendWorker extends Thread{

    		private CharSequence mPath;
    		private String mType;

    		public PaxSendWorker (CharSequence path, String type){
    			mPath = path;
    			mType = type;
    		}
    		
    		
    		@Override
    	    public void run() {
    			
    	        try { //TODO: http request to xbmc or PCH
    	        	// Get preferences
    	    		SharedPreferences paxIpPrefs = getSharedPreferences(Pax.MYPREFS, Activity.MODE_PRIVATE);
    	        	DefaultHttpClient httpclient = new DefaultHttpClient();
    	        	//http://xbox/xbmcCmds/xbmcHttp?command=ShowPicture(F:\apps\xbmc\media\splash.png)
    	        	HttpGet httpget = new HttpGet("http://" + paxIpPrefs.getString(Pax.XBMC_IP_PREFS, getString(R.string.xbmcIpString)) + 
    	        			 ":" + paxIpPrefs.getString(Pax.XBMC_PORT_PREFS, getString(R.string.xbmcPortString)) +
    	        			 "/xbmcCmds/xbmcHttp?command=ShowPicture(" + "http://thePhoneIP" +  mPath + ")");
    	        	HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    Log.i("#responce.getStatusline", response.getStatusLine().toString());
                    if (entity != null) {
                        Log.i("#Response content length", ("Response content length: " + entity.getContentLength()));
                        
                        //inputStreamTo String http://snippets.dzone.com/posts/show/555
                        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        while ((line = br.readLine()) != null) {
                        sb.append(line);
                        }

                        br.close();
                        
                        Log.i("#Response content Answer", sb.toString());
                        //status = "Command Sent" ;
                        mHandler.post(showUpdate);
                    }
                    if (entity != null) {
                        entity.consumeContent();
                    }
                    httpclient.getConnectionManager().shutdown();
                    Log.i("#httpclientConMan", "shutdown");
    			} catch (Exception e) {
    				// TODO: handle exception
    			}
    		}
    	}

     private Runnable showUpdate = new Runnable(){
        	public void run(){
        	Toast.makeText(PaxFileBrowser.this, R.string.commandSent, Toast.LENGTH_SHORT).show();
         }
     };
}