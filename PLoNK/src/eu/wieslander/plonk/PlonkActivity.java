package eu.wieslander.plonk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;

import android.text.util.Linkify;
import android.util.Log;
import android.util.Xml;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemLongClickListener;


public class PlonkActivity extends ListActivity {

    /**
     * Custom list adapter that fits our PLoNK data into the list.
     */
    private PlonkListAdapter mAdapter;
    
    /**
     * Url edit text field.
     */
    private EditText mUrlText;

    /**
     * Status text field.
     */
    private TextView mStatusText;

    /**
     * Handler used to post runnables to the UI thread.
     */
    static Handler mHandler;

    /**
     * Currently running background network thread.
     */
    private PlonkWorker mWorker;
    
    /**
     * Currently running background PCH network thread.
     */
    private PchWorker mPworker;
    
    /**
     * PlonkItem holder for the alert dialog
     */
    private PlonkItem selectedItem;
    
    // Take this many chars from the front of the description.
    public static final int SNIPPET_LENGTH = 90;
    
    
    // Keys used for data in the onSaveInstanceState() Map.
    public static final String STRINGS_KEY = "strings";

    public static final String SELECTION_KEY = "selection";

    public static final String URL_KEY = "url";
    
    public static final String STATUS_KEY = "status";
    
    //Keys for menu items
    private static final int SHOW_SETTINGS = Menu.FIRST;
    private static final int SHOW_REMOTE = Menu.FIRST+1;
    
    // name of shared_prefs file
    public static final String MYPREFS = "plonkIpPreferences";
    // name of the llink key in plonkIpPreferences
    private static final String LLINK_IP_PREF = "Llink_Ip";
    // name of the pch key in plonkIpPreferences
    private static final String PCH_IP_PREF = "Pch_Ip";
    // name of the pch-version key in plonkIpPreferences
    private static final String PCH_VERSION_PREF = "Pch_Version";
    
    public static String LLINK_IP = "0.0.0.0";
    public static String LLINK_PORT = "8009";
    public static String PCH_IP = "0.0.0.0";
    public static int PCH_PORT_COMMAND = 30000;
    public static int PCH_PORT__PEACH_GAYA = 30001;
    public static int PCH_PORT_PEACH_IR = 30002;
    public static String PCH_VERSION = "";

    // Constants for different dialogs
	private static final int FOLDER_VIEW = 1;
	private static final int FILE_VIEW = 2;

    
	// Parent and Current directory name holder string
    private static String PARENT_DIR = "";
    private static String CURRENT_DIR = "";
    
    //Progress indicator in title bar
    private boolean mToggleIndeterminate = false;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
		
		// Some code that could be usefull for the Plonk-reader part [platform/development.git] / samples / RSSReader / src / com / example / android / rssreader /
        
        // The above layout contains a list id "android:list"
        // which ListActivity adopts as its list -- we can
        // access it with getListView().

        // Install our custom PlonkListAdapter.
        List<PlonkItem> items = new ArrayList<PlonkItem>();
        mAdapter = new PlonkListAdapter(this, items);
        getListView().setAdapter(mAdapter);

        //LongClickListener to show Items when hold for a "long" time

        ListView lv = getListView();
        

        // Then you can create a listener like so:
        lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){
        	
        	public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
        		onLongListItemClick(v,pos,id);
        		return false;
        	}
        });

        // Prepare for progress in title bar
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(mToggleIndeterminate);
        
        // REMOVE
        // Get pointers to the UI elements in the rss_layout
        //mUrlText = (EditText)findViewById(R.id.urltext);
        mStatusText = (TextView)findViewById(R.id.statustext);
        
        //Button download = (Button)findViewById(R.id.download);
        //download.setOnClickListener(new OnClickListener() {
            //public void onClick(View v) {
        // Run the doPlonk method with the ip for llink
        
        // Get preferences
		SharedPreferences plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
		LLINK_IP = plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint));
		PCH_IP = plonkIpPreferences.getString(PCH_IP_PREF, getString(R.string.add_pch_ip_hint));
		PCH_VERSION = plonkIpPreferences.getString(PCH_VERSION_PREF, getString(R.string.pch_default_version));

        //TODO add a check if no preferences, then dont doPlonk 
		doPlonk("http://" + plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint)));
		//doPlonk("http://feeds.idg.se/idg/JYvw?format=xml");
            //}
        //});

        // Need one of these to post things back to the UI thread.
        mHandler = new Handler();
    }
	
    /**
     * ArrayAdapter encapsulates a java.util.List of T, for presentation in a
     * ListView. This subclass specializes it to hold PlonkItems and display
     * their title/description data in a TwoLineListItem.
     */
    public class PlonkListAdapter extends ArrayAdapter<PlonkItem> {
        private LayoutInflater mInflater;

        public PlonkListAdapter(Context context, List<PlonkItem> objects) {
            super(context, 0, objects);

            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * This is called to render a particular item for the on screen list.
         * Uses an off-the-shelf TwoLineListItem view, which contains text1 and
         * text2 TextViews. We pull data from the PlonkItem and set it into the
         * view. The convertView is the view from a previous getView(), so
         * we can re-use it.
         * 
         * @see ArrayAdapter#getView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TwoLineListItem view;

            // Here view may be passed in for re-use, or we make a new one.
            if (convertView == null) {
                view = (TwoLineListItem) mInflater.inflate(R.layout.simple_list_item_2, null);
            } else {
                view = (TwoLineListItem) convertView;
            }
            
            PlonkItem item = this.getItem(position);

            // Set the item title and description into the view.
            // This example does not render real HTML, so as a hack to make
            // the description look better, we strip out the
            // tags and take just the first SNIPPET_LENGTH chars.
            view.getText1().setText(item.getTitle());
            String descr = new String();
            
            // We have to make sure when description is missing we don't screw things up
            // and gets NullPointException
           if (!(item.getDescription() == null )) {
            	descr = item.getDescription().toString();
			} else {
				descr = "";
			}
            
            descr = removeTags(descr);
            view.getText2().setText(descr.substring(0, Math.min(descr.length(), SNIPPET_LENGTH)));

            return view;
        }

    }

    /**
     * Simple code to strip out <tag>s -- primitive way to sortof display HTML as
     * plain text.
     */
    public String removeTags(String str) {
        str = str.replaceAll("<.*?>", " ");
        str = str.replaceAll("\\s+", " ");
        // llink escapes all ampersands with a slash, we remove this
        str = str.replaceAll("\\\\", "");
        return str;
    }

    /**
     * Called when user clicks an item in the list. Opens a new list view
     * Of folder contents
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        PlonkItem item = mAdapter.getItem(position);
        CharSequence category = item.getCategory();
        if (category.equals("directory")) {
            doPlonk(item.getLink());
            // Need one of these to post things back to the UI thread.
            mHandler = new Handler();	
		}else {
			selectedItem = mAdapter.getItem(position);
			showDialog(FOLDER_VIEW);
		}

    }


    
    
    protected void onLongListItemClick(View v,int pos,long id) {
    	Log.i( "###", "onLongListItemClick id=" + id + "pos=" + pos);
    	selectedItem = mAdapter.getItem(pos);
    	showDialog(FOLDER_VIEW);
    	}
    
    
    
    @Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
    	switch (id) {
		case FOLDER_VIEW:{
			LayoutInflater li = LayoutInflater.from(this);
			View itemView = li.inflate(R.layout.plonk_item_details, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Item Title");
			builder.setIcon(android.R.drawable.ic_dialog_info);
/*	        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//TODO nothing?
	            }
	        });*/
			builder.setView(itemView);
			return builder.create();
		}
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case FOLDER_VIEW: {
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Window window = alertDialog.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
					WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

			TextView textView = (TextView) alertDialog
					.findViewById(R.id.plonkItemView);
			//textView.setAutoLinkMask(Linkify.WEB_URLS);
			textView.setText("Play: " + selectedItem.getDescription() + "\n"
					+ selectedItem.getTitle());
			alertDialog.setTitle(selectedItem.getTitle());
			alertDialog.setView(textView);
			ImageButton playButton = (ImageButton) alertDialog
			    .findViewById(R.id.buttonPlay);
			playButton.setOnClickListener(new OnClickListener (){
				public void onClick(View view){
					sendCommand(selectedItem.getLink(),"playfile");
                    remoteStart();
					alertDialog.cancel();
				}
			});
			Button cancelButton = (Button) alertDialog.findViewById(R.id.buttonCancel);
			cancelButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
				}
			});
/*			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
        	    alertDialog.cancel();
            }});*/
			break;
		}
		}
	}

	protected void remoteStart() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PlonkRemoteActivity.class);
		startActivity(intent);
	}

	/**
	 * Sends a command and link to the pch plonk_nmt server php script
	 * executing the command
	 * @param link
	 * @param command
	 */
	
	protected void sendCommand(CharSequence link, String command) {
		// TODO Auto-generated method stub
		Log.i("#PLyCommand", command);
		PchWorker pWorker = new PchWorker(link, command);
		setCurrentPchWorker(pWorker);
		mStatusText.setText("Loading\u2026");
		pWorker.start();
	}
	
    /**
     * Sets the currently active running worker. Interrupts any earlier worker,
     * so we only have one at a time.
     * 
     * @param pWorker the new worker
     */
	public synchronized void setCurrentPchWorker(PchWorker pWorker){
		if (mPworker != null) mPworker.interrupt();
		mPworker = pWorker;
	}

    /**
     * Is the given pWorker the currently active one.
     */
    public synchronized boolean isCurrentPchWorker(PchWorker pWorker) {
        return (mPworker == pWorker);
    }
	
    
    /**
     * Worker thread takes in an llink url string, send the command to the PCH
     * and communicates back the success or failure.
     */
    private class PchWorker extends Thread {
        private CharSequence mLink;
        private String mCommand;

        public PchWorker(CharSequence link, String command) {
            mLink = link;
            mCommand = command;
        }
        @Override
        public void run() {
        	String status = "PCH Command";
            try {
                // Code from http://svn.apache.org/repos/asf/httpcomponents/httpclient/
            	// trunk/httpclient/src/examples/org/apache/http/examples/client/ClientAuthentication.java
                DefaultHttpClient httpclient = new DefaultHttpClient();

                //httpclient.getCredentialsProvider().setCredentials(
                        //new AuthScope("localhost", 443),
                //		new AuthScope(null, -1),
                //        new UsernamePasswordCredentials("email@mail.com", "1234678"));
                // "http://" + plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint))
                
                // Get preferences
        		SharedPreferences plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        		PCH_VERSION = plonkIpPreferences.getString(PCH_VERSION_PREF, getString(R.string.pch_default_version));
        		
                // http://PCH_IP:8883/plonk_nmt.cgi?act=play&url=http://llink_ip:8001/The.Nice.movie.avi
              
                HttpGet httpget = new HttpGet("http://" + plonkIpPreferences.getString(PCH_IP_PREF, getString(R.string.add_pch_ip_hint)) +
                		//":8883/plonk_nmt.cgi?act=" + mCommand + "&url=" + mLink);
                		":9999/PLoNK_web/plonk_nmt.php?act=" + mCommand + "&device=" + PCH_VERSION + "&url=" + mLink);
                Log.i("#htpgetet", httpget.getRequestLine().toString());
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                //System.out.println("----------------------------------------");
                //System.out.println(response.getStatusLine());
                Log.i("#responce.getStatusline", response.getStatusLine().toString());
                if (entity != null) {
                    //System.out.println("Response content length: " + entity.getContentLength());
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
                    status = "Command Sent" ;
                }
                if (entity != null) {
                    entity.consumeContent();
                }

                // When HttpClient instance is no longer needed, 
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpclient.getConnectionManager().shutdown();
                Log.i("#httpclientConMan", "shutdown");
            } catch (Exception e) {
            	status = "failed:" + e.getMessage();
            	//Toast.makeText(noip.this, "failed:" + e.getMessage(),
	            //        Toast.LENGTH_LONG).show();
            }
            // Send status to UI (unless a newer worker has started)
            // To communicate back to the UI from a worker thread,
            // pass a Runnable to handler.post().
            final String temp = status;
            if (isCurrentPchWorker(this)) {
                mHandler.post(new Runnable() {
                    public void run() {
    				    Toast.makeText(PlonkActivity.this, temp,
    		                    Toast.LENGTH_LONG).show();
                    	
                    }
                });
            }
    }
    }
     /**
     * Resets the output UI -- list and status text empty.
     */
    public void resetUI() {
        // Reset the list to be empty.
        List<PlonkItem> items = new ArrayList<PlonkItem>();
        mAdapter = new PlonkListAdapter(this, items);
        getListView().setAdapter(mAdapter);

        //mStatusText.setText("");
        //mUrlText.requestFocus();
    }

    /**
     * Sets the currently active running worker. Interrupts any earlier worker,
     * so we only have one at a time.
     * 
     * @param worker the new worker
     */
    public synchronized void setCurrentWorker(PlonkWorker worker) {
        if (mWorker != null) mWorker.interrupt();
        mWorker = worker;
    }

    /**
     * Is the given worker the currently active one.
     * 
     * @param worker
     * @return
     */
    public synchronized boolean isCurrentWorker(PlonkWorker worker) {
        return (mWorker == worker);
    }

    /**
     * Given an Plonk url string, starts the Plonk-download-thread going.
     * 
     * @param PlonkUrl
     */
    private void doPlonk(CharSequence plonkUrl) {
        PlonkWorker worker = new PlonkWorker(plonkUrl);
        setCurrentWorker(worker);

        resetUI();
        //Top left status message
        mStatusText.setText("Loading\u2026");
        
        //Pop up with a download message
        //Toast.makeText(PlonkActivity.this, "Downloading\u2026",
          //      Toast.LENGTH_SHORT).show();
        //Top right spinning progress
        mToggleIndeterminate = !mToggleIndeterminate;
        setProgressBarIndeterminateVisibility(mToggleIndeterminate);

        //we show a toast
//        Context context = getApplicationContext();
//		String msg = getString(R.string.downloading_toast);
//		int duration = Toast.LENGTH_SHORT;
//		Toast toast = Toast.makeText(context, msg, duration);
//		int offsetX = 0;
//		int offsetY = 0;
//		toast.setGravity(Gravity.CENTER, offsetX, offsetY);
//		LayoutInflater inflater = LayoutInflater.from(context);
//		View toastview = inflater.inflate(R.layout.toast_view, null);
//		toast.setView(toastview);
//		toast.show();
        
        worker.start();
    }

    /**
     * Runnable that the worker thread uses to post PlonkItems to the
     * UI via mHandler.post
     */
    class ItemAdder implements Runnable {
        PlonkItem mItem;

        ItemAdder(PlonkItem item) {
            mItem = item;
        }

        public void run() {
            mAdapter.add(mItem);
        }

        // NOTE: Performance idea -- would be more efficient to have he option
        // to add multiple items at once, so you get less "update storm" in the UI
        // compared to adding things one at a time.
    }

    /**
     * Worker thread takes in an Plonk url string, downloads its data, parses
     * out the Plonk items, and communicates them back to the UI as they are read.
     */
    private class PlonkWorker extends Thread {
        private CharSequence mUrl;

        public PlonkWorker(CharSequence url) {
            mUrl = url;
        }

        @Override
        public void run() {
            String status = "";
            try {
                // Standard code to make an HTTP connection.
                URL url = new URL(mUrl.toString());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(10000);

                connection.connect();
                InputStream in = connection.getInputStream();

                parsePlonk(in, mAdapter);
                status = CURRENT_DIR;
            } catch (Exception e) {
                status = "failed:" + e.getMessage();
            }

            // Send status to UI (unless a newer worker has started)
            // To communicate back to the UI from a worker thread,
            // pass a Runnable to handler.post().
            final String temp = status;
            if (isCurrentWorker(this)) {
                mHandler.post(new Runnable() {
                    public void run() {
                        mStatusText.setText(temp);
                        mToggleIndeterminate = !mToggleIndeterminate;
                        setProgressBarIndeterminateVisibility(mToggleIndeterminate);
                    }
                });
            }
        }
    }
    
    
/*    *//**
     * Puts text in the url text field and gives it focus. Used to make a Runnable
     * for each menu item. This way, one inner class works for all items vs. an
     * anonymous inner class for each menu item.
     *//*
    private class PlonkMenu implements MenuItem.OnMenuItemClickListener {
        private CharSequence mUrl;

        PlonkMenu(CharSequence url) {
            mUrl = url;
        }

        public boolean onMenuItemClick(MenuItem item) {
            mUrlText.setText(mUrl);
            mUrlText.requestFocus();
            return true;
        }
    }*/


/*    *//**
     * Called for us to save out our current state before we are paused,
     * such a for example if the user switches to another app and memory
     * gets scarce. The given outState is a Bundle to which we can save
     * objects, such as Strings, Integers or lists of Strings. In this case, we
     * save out the list of currently downloaded Plonk data, (so we don't have to
     * re-do all the networking just because the user goes back and forth
     * between aps) which item is currently selected, and the data for the text views.
     * In onRestoreInstanceState() we look at the map to reconstruct the run-state of the
     * application, so returning to the activity looks seamlessly correct.
     * TODO: the Activity javadoc should give more detail about what sort of
     * data can go in the outState map.
     * 
     * @see android.app.Activity#onSaveInstanceState
     *//*
    @SuppressWarnings("unchecked")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Make a List of all the PlonkItem data for saving
        // NOTE: there may be a way to save the PlonkItems directly,
        // rather than their string data.
        int count = mAdapter.getCount();

        // Save out the items as a flat list of CharSequence objects --
        // title0, link0, descr0, title1, link1, ...
        ArrayList<CharSequence> strings = new ArrayList<CharSequence>();
        for (int i = 0; i < count; i++) {
            PlonkItem item = mAdapter.getItem(i);
            strings.add(item.getTitle());
            strings.add(item.getLink());
            strings.add(item.getDescription());
        }
        outState.putSerializable(STRINGS_KEY, strings);

        // Save current selection index (if focussed)
        if (getListView().hasFocus()) {
            outState.putInt(SELECTION_KEY, Integer.valueOf(getListView().getSelectedItemPosition()));
        }

        // Save url
        outState.putString(URL_KEY, mUrlText.getText().toString());
        
        // Save status
        outState.putCharSequence(STATUS_KEY, mStatusText.getText());
    }

    *//**
     * Called to "thaw" re-animate the app from a previous onSaveInstanceState().
     * 
     * @see android.app.Activity#onRestoreInstanceState
     *//*
    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Note: null is a legal value for onRestoreInstanceState.
        if (state == null) return;

        // Restore items from the big list of CharSequence objects
        List<CharSequence> strings = (ArrayList<CharSequence>)state.getSerializable(STRINGS_KEY);
        List<PlonkItem> items = new ArrayList<PlonkItem>();
        for (int i = 0; i < strings.size(); i += 3) {
            items.add(new PlonkItem(strings.get(i), strings.get(i + 1), strings.get(i + 2)));
        }

        // Reset the list view to show this data.
        mAdapter = new PlonkListAdapter(this, items);
        getListView().setAdapter(mAdapter);

        // Restore selection
        if (state.containsKey(SELECTION_KEY)) {
            getListView().requestFocus(View.FOCUS_FORWARD);
            // todo: is above right? needed it to work
            getListView().setSelection(state.getInt(SELECTION_KEY));
        }
        
        // Restore url
        mUrlText.setText(state.getCharSequence(URL_KEY));
        
        // Restore status
        mStatusText.setText(state.getCharSequence(STATUS_KEY));
    }*/

    
//#START#REMOVE#####################################################    
/*    *//**
     * Does rudimentary Plonk parsing on the given stream and posts Plonk items to
     * the UI as they are found. Uses Android's XmlPullParser facility. This is
     * not a production quality Plonk parser -- it just does a basic job of it.
     * 
     * @param in stream to read
     * @param adapter adapter for ui events
     *//*
    void parsePlonk(InputStream in, PlonkListAdapter adapter) throws IOException,
            XmlPullParserException {
        // TODO: switch to sax

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(in, "ISO-8859-1");  // null = parser figures out encoding

        int eventType;
        String title = "";
        String link = "";
        String description = "";
        eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    title = link = description = "";
                } else if (tag.equals("title")) {
                    xpp.next(); // Skip to next element -- assume text is directly inside the tag
                    title = xpp.getText();
                } else if (tag.equals("link")) {
                    xpp.next();
                    link = xpp.getText();
                } else if (tag.equals("description")) {
                    xpp.next();
                    description = xpp.getText();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                // We have a comlete item -- post it back to the UI
                // using the mHandler (necessary because we are not
                // running on the UI thread).
                String tag = xpp.getName();
                if (tag.equals("item")) {
                    PlonkItem item = new PlonkItem(title, link, description);
                    mHandler.post(new ItemAdder(item));
                }
            }
            eventType = xpp.next();
        }
    }*/
// #END#REMOVE###############################################################################

/*    private class PlonkHandler extends DefaultHandler {
        PlonkListAdapter mAdapter;
        
        String mTitle;
        String mLink;
        String mDescription;
        
        StringBuilder mBuff;
        
        boolean mInItem;
        
        public PlonkHandler(PlonkListAdapter adapter) {
            mAdapter = adapter;
            mInItem = false;
            mBuff = new StringBuilder();
        }
        
        public void startElement(String uri,
                String localName,
                String qName,
                Attributes atts)
                throws SAXException {
            String tag = localName;
            Log.i("Tag1", "tag");
            if (tag.equals("")) tag = qName;
            Log.i("Tag2", "tag");
            // If inside <item>, clear out buff on each tag start
            if (mInItem) {
                mBuff.delete(0, mBuff.length());
            }
            
            if (tag.equals("item")) {
                mTitle = mLink = mDescription = "";
                mInItem = true;
            }
        }
        
        public void characters(char[] ch,
                      int start,
                      int length)
                      throws SAXException {
    		String theString = new String(ch,start,length);
    		Log.i("RSSReader","characters[" + theString + "]" +mInItem);
            // Buffer up all the chars when inside <item>
            if (mInItem) mBuff.append(ch, start, length);
        }
                      
        public void endElement(String uri,
                      String localName,
                      String qName)
                      throws SAXException {
            String tag = localName;
            if (tag.equals("")) tag = qName;
            
            // For each tag, copy buff chars to right variable
            if (tag.equals("title")) mTitle = mBuff.toString();
            else if (tag.equals("link")) mLink = mBuff.toString();
            //Log.i("###########dfdg", mLink);
            if (tag.equals("description")) mDescription = mBuff.toString();
            
            // Have all the data at this point .... post it to the UI.
            if (tag.equals("item")) {
                PlonkItem item = new PlonkItem(mTitle, mLink, mDescription);
                Log.i("###########dfdg", mLink + "dfsf");
                mHandler.post(new ItemAdder(item));
                mInItem = false;
            }
        }
    }
    */

    private class PlonkHandler extends DefaultHandler{
        PlonkListAdapter mAdapter;
        
        String mTitle;
        String mLink;
        String mDescription;
        String mParent;
        
        //private String urlString;
        //private PlonkChannel channel;
        private StringBuilder text;
        private PlonkItem item;
        //private boolean imgStatus;
        
        StringBuilder mBuff;
        
        boolean mInItem;
        boolean mInChannel;
        
        public PlonkHandler(PlonkListAdapter adapter) {
            mAdapter = adapter;
            mInItem = false;
            mInChannel = false;
            text = new StringBuilder();
        }
    	
        //Code from http://learningandroid.org/tutorial/2009/02/writing-sax-based-rss-and-podcast-parser
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            /** First lets check for the channel */
            if (localName.equalsIgnoreCase("channel")) {
            	mTitle = mLink = mParent = "";
                mInChannel = true;
            }
           
            /** Now lets check for a parent dir */
//            if (localName.equalsIgnoreCase("parent_dir") && (mInChannel)) {
//                mParent = "";
//            }
            
            /** Now lets check for an item */
            if (localName.equalsIgnoreCase("item") && (mInChannel)) {
                mTitle = mLink = mDescription = "";
                mInItem = true;
                item = new PlonkItem();
            }
           
               
        }
        
        public void characters(char[] ch, int start, int length) {
            text.append(ch, start, length);
        }
        
        
        /**
         * This is where we actually parse for the elements contents
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            /** Check we have an RSS Feed */
            if (!mInChannel) {
                return;
            }
           
            /** Check are at the end of an item */
            if (localName.equalsIgnoreCase("item")) {
                    mInItem = false;
                    mHandler.post(new ItemAdder(item));
                    
            }
               
           
            /** Now we need to parse which title we are in */
            if (localName.equalsIgnoreCase("title"))
            {
                    /** We are an item, so we set the item title */
                if (mInItem){
                    item.setTitle(text.toString().trim());
                }
            }      
           
            /** Now we are checking for a link */
            if (localName.equalsIgnoreCase("link")) {
                    /** Check we are in an item **/
                if (mInItem) {
                    item.setLink(text.toString().trim());
                }
            }      
           
            /** Checking for a description */
            if (localName.equalsIgnoreCase("description")) {
                    /** Lets check we are in an item */
                if (mInItem) {
                    item.setDescription(text.toString().trim());
                }
            }
           
            /** Checking for a category */
            if (localName.equalsIgnoreCase("category")) {
                    /** Lets check we are in an item */
                if (mInItem) {
                    item.setCategory(text.toString().trim());
                }
            }
            
            /** Put all tags here */
            
            /** 
             * <item>
             * #<title>Serier</title>
             * #<link>http://192.168.0.7:8008/Serier</link>
             * #<description></description> 
             * #<category>directory</category>
             * <pubDate>Fri Mar 27 20:02:00 2009</pubDate>
             * <size>4.0KB</size>
             * *<detail_size>4096</detail_size>
             * <pubDate>Fri Mar 27 20:02:00 2009</pubDate>
             * <tvid>1</tvid>
             * <ext></ext>
             * *<icon>http://192.168.0.7:8008//generic.jpg</icon>
             * <name>Serier</name>
             * *<title_local></title_local>
             * <country></country>
             * <length></length>
             * <tagline></tagline>
             * <plot></plot>
             * *<genres></genres>
             * <date></date>
             * <directors></directors>
             * <cast></cast>
             * *<rating></rating>
             * *<imdb_url></imdb_url>
             * <short_url>Serier</short_url>
             * *<visited>text</visited>
             * <guid>http://192.168.0.7:8008/Serier</guid>
             * </item>
             * */
            
            /** Check for the parent dir */
            if (localName.equalsIgnoreCase("parent_dir") && (!mInItem)) {
               PARENT_DIR = text.toString().trim();
            }
           
            /** Check for the current dir */
            if (localName.equalsIgnoreCase("curr_dir") && (!mInItem)) {
                CURRENT_DIR = text.toString().trim();
            }
            
            text.setLength(0);
        }
        

    }
    
    public void parsePlonk(InputStream in, PlonkListAdapter adapter) throws IOException, ParserConfigurationException, SAXException, FactoryConfigurationError {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        DefaultHandler handler = new PlonkHandler(adapter);
        //this fixes the encoding problem, dont know why 
        Log.i("XML PARSING", "#########");
        Xml.parse(in, Xml.Encoding.ISO_8859_1, handler);
		//parser.parse(in, handler);
        // TODO: does the parser figure out the encoding right on its own?
    } 
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, SHOW_SETTINGS, Menu.NONE, getString(R.string.menu_settings)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, SHOW_REMOTE, Menu.NONE, getString(R.string.menu_remote)).setIcon(android.R.drawable.ic_media_play);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
//		case REFRESH_ITEMS: {
////			refreshRssItems();
//			return true;
//		}
//		case DELETE_ITEMS: {
//			Item deleteItem = (Item) itemListView.getSelectedItem();
//			itemList.remove(deleteItem);
//			aa.notifyDataSetChanged();
//			return true;
//		}
		case SHOW_SETTINGS: {
			Intent explicitIntent = new Intent(this, PlonkSettings.class);
			startActivity(explicitIntent);			
			return true;
		}
		case SHOW_REMOTE: {
			Intent explicitIntent = new Intent(this, PlonkRemoteActivity.class);
			startActivity(explicitIntent);			
			return true;
		}
		}
		return false;
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences plonkIpPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
            //doPlonk("http://" + plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint)));
            //Uggly hack to get the parent dir
            Log.i("######Link Parent###", PARENT_DIR);
            String link = ("http://" + plonkIpPreferences.getString(LLINK_IP_PREF, getString(R.string.add_llink_ip_hint)) + PARENT_DIR);
            Log.i("######Link###", link);
            doPlonk(link);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}