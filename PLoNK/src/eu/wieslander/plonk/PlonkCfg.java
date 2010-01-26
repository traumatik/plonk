package eu.wieslander.plonk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Mattias Jiderhamn
 */
public class PlonkCfg {
    
    /** Tag for logging */
    private static final String TAG = "PLoNKCfg";
    
    /** Key for preferences*/
    public static final String MYPREFS = "plonkIpPreferences";
    
    /** Key for PCH IP preference */
    private static final String PCH_IP_PREF = "Pch_Ip";
    
    /** name of the llink key in plonkIpPreferences */
    private static final String LLINK_IP_PREF = "Llink_Ip";
    
    /** Key for PCH version preference */
    private static final String PCH_VERSION_PREF = "Pch_Version";

    private static final int PCH_PORT_COMMAND = 30000;
    
    public static final int PCH_PORT_PEACH_GAYA = 30001;

    private static final int PCH_PORT_PEACH_IR = 30002;
    
    public static final String VERSION_A100 = "a100";
    
    public static final String VERSION_C200 = "c200";
    
    private static final String PCH_DEFAULT_VERSION = VERSION_A100; 
    
    private String pchVersion;
    
    // private final boolean a100;
    
    private String pchIp;
    
    private String llinkIp;
    
    private int llinkPort = 8009;

    public PlonkCfg(String pchVersion, String pchIp, String llinkIp) {
        this.pchVersion = pchVersion;
        this.pchIp = pchIp;
        this.llinkIp = llinkIp;
    }

    public String getPchVersion() {
        return pchVersion;
    }

    public boolean isA100() {
        return VERSION_A100.equals(pchVersion);
    }

    public String getPchIp() {
        return pchIp;
    }

    public int getPchPort() {
        return isA100() ? PlonkCfg.PCH_PORT_PEACH_IR : PlonkCfg.PCH_PORT_COMMAND;
    }

    public String getLlinkIp() {
        return llinkIp;
    }

    public int getLlinkPort() {
        return llinkPort;
    }

    public void setPchVersion(String pchVersion) {
        this.pchVersion = pchVersion;
    }

    public void setPchIp(String pchIp) {
        this.pchIp = pchIp;
    }

    public void setLlinkIp(String llinkIp) {
        this.llinkIp = llinkIp;
    }

    public void setLlinkPort(int llinkPort) {
        this.llinkPort = llinkPort;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void store(Context ctx) {
        SharedPreferences plonkIpPreferences = ctx.getSharedPreferences(PlonkCfg.MYPREFS, Activity.MODE_PRIVATE);        SharedPreferences.Editor editor = plonkIpPreferences.edit();
        editor.putString(PCH_VERSION_PREF, pchVersion);
        editor.putString(PCH_IP_PREF, pchIp);
        editor.putString(LLINK_IP_PREF, llinkIp);
        editor.commit();
        
    }

    public static PlonkCfg getConfig(Context ctx) {
        SharedPreferences plonkIpPreferences = ctx.getSharedPreferences(PlonkCfg.MYPREFS, Activity.MODE_PRIVATE);
		
		String pchIp = plonkIpPreferences.getString(PlonkCfg.PCH_IP_PREF, ctx.getString(R.string.add_pch_ip_hint)); // TODO: Consider constant instead 
        String pchVersion = plonkIpPreferences.getString(PlonkCfg.PCH_VERSION_PREF, PCH_DEFAULT_VERSION);
        String llinkIp = plonkIpPreferences.getString(LLINK_IP_PREF, ctx.getString(R.string.add_llink_ip_hint)); // TODO: Consider constant instead
        
        Log.d(TAG, "PCH IP: " + pchIp);
        Log.d(TAG, "PCH VERSION: " + pchVersion);
        // TODO: llink
        
        return new PlonkCfg(pchVersion, pchIp, llinkIp);
    }
}