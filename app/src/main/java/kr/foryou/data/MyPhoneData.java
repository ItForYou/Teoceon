package kr.foryou.data;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

public class MyPhoneData {
	static public String getPhoneNumber(Context context) {
		TelephonyManager systemService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			//return TODO;
		}
		String PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
		return PhoneNumber="0"+PhoneNumber;
	}
	
	static public String GetDeviceID(Context context)
	{
		try {
			return (String) Build.class.getField("SERIAL").get(null); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		String time = ""+ System.currentTimeMillis() + ""+ Math.random() + ""; 
		return time;
	}
	
	public final static int INET4ADDRESS = 1;
	public final static int INET6ADDRESS = 2;

	public static String getLocalIpAddress(int type) {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						switch (type) {
						case INET6ADDRESS:
							if (inetAddress instanceof Inet6Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						case INET4ADDRESS:
							if (inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						}
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}
	
	public static boolean isOnline(Context context) { // network 연결 상태 확인
		try {

			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = conMan.getNetworkInfo(1).getState(); // wifi
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				return true;
			}

			State mobile = conMan.getNetworkInfo(0).getState(); // mobile
			if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
				return false;
			}

		} catch (NullPointerException e) {
			return false;
		}
		return false;

	}
	
	public static String GetCurrentTime() {
		
		SimpleDateFormat sdfNow = new SimpleDateFormat("a KK:mm", Locale.KOREA); 
		String time = sdfNow.format(new Date(System.currentTimeMillis())); 
		return time;

	}

	public static String GetCurrentDay() {
		SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA); 
		String time = sdfNow.format(new Date(System.currentTimeMillis())); 
		return time;
	}
	
	public static String GetCurrentTime(String str) {
		SimpleDateFormat original_format = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		SimpleDateFormat new_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
		
		try {
			Date original_date = original_format.parse(str);
			String new_date = new_format.format(original_date);
			return new_date;
		} catch (Exception e) {
			
		}
		return "";
	}
	
	public static String GetUpLoadTime() {
		String str = GetCurrentTime();
		SimpleDateFormat original_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
		SimpleDateFormat new_format = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		
		try {
			Date original_date = original_format.parse(str);
			String new_date = new_format.format(original_date);
			return new_date;
		} catch (Exception e) {
			
		}
		return "";
	}
}
