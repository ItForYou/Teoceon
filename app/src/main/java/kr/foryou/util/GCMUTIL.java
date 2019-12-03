package kr.foryou.util;



import android.content.Context;

import kr.foryou.teoceon.MainActivity;

public class GCMUTIL {
	public final static String passkey = "foryou_passkey_321!@#";
	private ForYouConfig d_config = null;
	private Context context = null;

	public final static String Server_URL = MainActivity.MAIN_URL + "plugin/gcm/";
	public final static String insert_php = "insert.php";
	public final static String R_KEY = "885975414796";
	
	public final static String _get_data = "get_data";

	public final static int Json_Not_Miss_Match = -1;
	public final static int Json_Error = 0;
	public final static int Json_Get = 2;
	public final static int Json_Insert = 3;
	public final static int Json_get_data = 5;
	
	public GCMUTIL(Context context){
		d_config = new ForYouConfig(context);
		this.context = context;
	}
	
	public String getServerID(){
		return d_config.pref_get("ServerID", "");
	}
	
	public void setServerID(String ServerID){
		d_config.pref_save("ServerID", ServerID);
	}
	
	public int is_Check(String k){
		int return_int=Json_Not_Miss_Match;
		
		if(k.startsWith("{\"error\":"))
			return_int = Json_Error;
		else if(k.startsWith("{\"get\":"))
			return_int = Json_Get;
		else if(k.startsWith("{\"insert\":"))
			return_int = Json_Insert;
		else if(k.startsWith("{\"get_data\":"))
			return_int = Json_get_data;
		
		return return_int;
	}

}
