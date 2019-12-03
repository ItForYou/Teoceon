package kr.foryou.teoceon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;*/
import org.json.JSONArray;
import org.json.JSONObject;
/*
import com.kakao.KakaoLink;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
*/
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kyad.adlibrary.AppAllAdvertisingIdClientInfo;
import com.kyad.adlibrary.AppAllOfferwallSDK;
import com.tnkfactory.ad.TnkSession;

import kr.foryou.data.MyPhoneData;
import kr.foryou.util.ExifUtil;
import kr.foryou.util.ForYouConfig;
import kr.foryou.util.GCMUTIL;
import kr.foryou.util.RetrofitItem;
import kr.foryou.util.RetrofitPushService;
import kr.foryou.util.RetrofitService;
import kr.foryou.util.ServerPost;
import kr.foryou.util.StaticRetrofit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity implements OnClickListener,AppAllOfferwallSDK.AppAllOfferwallSDKListener  {
	private boolean end = false;
	private ImageView IvIntro = null;
	private WebView mWebview = null;
	private ArrayList<BottomData> mData = new ArrayList<BottomData>();
	private GCMUTIL m_Gcmutil = null;
	private ProgressDialog m_progressdialog = null;
	private ProgressBar mProgressBar = null;
	/*private KakaoLink kakaoLink;
	private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;*/
	private ForYouConfig mConfig = null;
	public String appName = "";
	public ArrayList<AdInfo> mAdData;
	public AdapterViewFlipper avf;

	private static final String TYPE_IMAGE = "image/*";
	private static final int INPUT_FILE_REQUEST_CODE = 1;

	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mFilePathCallback;
	private String mCameraPhotoPath;

	public boolean isLoading = false;
	private final Handler handler = new Handler();
	private String IntentURL = "";
	private String IntentItem_no = "";
	int file_count = 0;

	
	private boolean isFirst = true;
	
	final int REQ_CODE_SELECT_IMAGE = 100;
	final int MULTI_UPLOAD_IMAGE = 101;
	final int ERROR_CODE = 102;
	final int INTRO_CODE = 103;
	/****************************************************************************************************************/
	// http://www.teocean.com
	final public static String SEVER_URL = "http://www.teocean.com/";
	final public static String MAIN_URL = SEVER_URL + "";
	final public static String COME_URL = MAIN_URL + "s1.htm";

	final private static String TEL = "010-8945-5430";

	private String[] bottomText = new String[] { "", "", "", "", "" };
	private int[] bottomImg = new int[] { R.drawable.bottom_icon_01, R.drawable.bottom_icon_02, R.drawable.bottom_icon_03, R.drawable.bottom_icon_04,
			R.drawable.bottom_icon_05 };
	private String KakaoText = "이거 수정 안 했다.";
	private boolean SettingGcm = false;
	private ArrayList<FileData> uploadFileArray = null;
	private ArrayList<File> fileArrayList = null;

	/****************************************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		init();


		Bottom();
		WebView();
		//getAd();
		gcm();
		
		String url = getIntent().getStringExtra("url");
		if(url != null && url.length() > 0){
			IntentURL = url;
		}
		FirebaseApp.initializeApp(this);
		FirebaseMessaging.getInstance().subscribeToTopic("factstock");
		FirebaseInstanceId.getInstance().getToken();
		Uri uri = getIntent().getData();
	    if(uri != null){ 
	    	String it_id = uri.getQueryParameter("it_id");
	    	if(it_id != null && it_id.length() > 0) IntentItem_no = "http://www.teocean.com/shop/item.php?it_id="+ it_id;
	    	String recom = uri.getQueryParameter("recom");
	    	if(recom != null && recom.length() > 0) IntentItem_no = "http://www.teocean.com/bbs/register_form.php?mb_type=%EC%9D%BC%EB%B0%98&recom="+recom;
	    }
		try {
			if (kr.foryou.util.Common.TOKEN.equals("") || kr.foryou.util.Common.TOKEN.equals(null)) {
				refreshToken();
			} else {
				postPush();
			}
		}catch (Exception e){
			refreshToken();
		}
		if(!kr.foryou.util.Common.getPref(getApplicationContext(),"swiper",false)){
			Intro();
		}
		Log.d("mb_id111",kr.foryou.util.Common.getPref(MainActivity.this,"mb_id","")+"");


	}
	private void refreshToken(){
		FirebaseMessaging.getInstance().subscribeToTopic("factstock");
		kr.foryou.util.Common.TOKEN= FirebaseInstanceId.getInstance().getToken();
		postPush();
	}
	private void postPush(){
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(httpLoggingInterceptor)
				.build();
		Retrofit retrofit=new Retrofit.Builder()
				.baseUrl(getString(R.string.domain))
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		//서버에 보낼 파라미터
		Map map=new HashMap();
		Log.d("TOKEN", kr.foryou.util.Common.TOKEN);
		map.put("token",kr.foryou.util.Common.TOKEN);
		map.put("mb_id",kr.foryou.util.Common.getPref(this,"mb_id",""));
		map.put("DeviceID",kr.foryou.util.Common.getMyDeviceId(this));


		RetrofitPushService retrofitService=retrofit.create(RetrofitPushService.class);
		Call<RetrofitItem> call=retrofitService.getPush(map);

		call.enqueue(new Callback<RetrofitItem>() {
			@Override
			public void onResponse(Call<RetrofitItem> call, Response<RetrofitItem> response) {
				//서버에 데이터 받기가 성공할시
				if(response.isSuccessful()){

				}else{

				}
			}
			//데이터 받기가 실패할 시
			@Override
			public void onFailure(Call<RetrofitItem> call, Throwable t) {

			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.getInstance().startSync();
		}
		if(mWebview.getUrl().startsWith("http://www.teocean.com/bbs/chatting.php")){
		    mWebview.loadUrl("javascript:roomIn();");
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.getInstance().stopSync();
		}
        if(mWebview.getUrl().startsWith("http://www.teocean.com/bbs/chatting.php")){
            mWebview.loadUrl("javascript:roomOut();");
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String url = intent.getStringExtra("url");
		if (url != null && url.length() > 0) {
			loadUrl(url);
		}
		
		Uri uri = intent.getData();
	    if(uri != null){ 
	    	String it_id = uri.getQueryParameter("it_id");
	    	if(it_id != null && it_id.length() > 0) IntentItem_no = "http://www.teocean.com/shop/item.php?it_id="+ it_id;
	    	String recom = uri.getQueryParameter("recom");
	    	if(recom != null && recom.length() > 0) IntentItem_no = "http://www.teocean.com/bbs/register_form.php?mb_type=%EC%9D%BC%EB%B0%98&recom="+recom;
	    }
		
		super.onNewIntent(intent);
	}

	private void init() {
		m_progressdialog = new ProgressDialog(this);
		m_progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_progressdialog.setTitle("");
		m_progressdialog.setCancelable(true);
		m_progressdialog.setMessage("잠시만 기다려주세요");

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_webview);
		mProgressBar.setVisibility(View.GONE);

		mConfig = new ForYouConfig(this);
		appName = getPackageName();
		mAdData = new ArrayList<AdInfo>();

		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		intent.putExtra("badge_count", 0);
		intent.putExtra("badge_count_package_name", "kr.foryou.teocean");
		intent.putExtra("badge_count_class_name", "kr.foryou.teocean.MainActivity");
		sendBroadcast(intent);
		mConfig.pref_save("badgeCount", "0");
	}

	private void loadUrl(String url) {
		String postData = "mobile_app=1";
		postData += "&DeviceID=" + MyPhoneData.GetDeviceID(this);
		
		GpsInfo gps = new GpsInfo(MainActivity.this);
        if (gps.isGetLocation()) {
            Double latitude = gps.getLatitude();
            Double longitude = gps.getLongitude();
            
            postData += "&latitude="+latitude;
            postData += "&longitude="+longitude;
        }
        
		//mWebview.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
		mWebview.loadUrl(url);

	}

	private void WebView() {
		mWebview = (WebView) findViewById(R.id.webView);
		mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebview.setScrollbarFadingEnabled(true);
		mWebview.setHorizontalScrollBarEnabled(false);
		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.setWebViewClient(new WebViewClientClass());
		mWebview.setWebChromeClient(new WebChromeClientClass());
		mWebview.addJavascriptInterface(new WebChromeClientClass(), "androidfile");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {
			mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		try {
			if (Build.VERSION.SDK_INT < 11) {
				ZoomButtonsController zoom_controll = null;
				zoom_controll = (ZoomButtonsController) mWebview.getClass().getMethod("getZoomButtonsController")
						.invoke(mWebview);
				zoom_controll.getContainer().setVisibility(View.GONE);
			} else {
				mWebview.getSettings().getClass().getMethod("setDisplayZoomControls", Boolean.TYPE)
						.invoke(mWebview.getSettings(), false);
			}
		} catch (Exception e) {
		}

		WebSettings webSettings = mWebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSaveFormData(false);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);//
		webSettings.setSupportZoom(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath("data/data/kr.foryou.teocean/databases");
		webSettings.setDomStorageEnabled(true);
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " (XY ClientApp)");
		webSettings.setAllowFileAccess(true);
		webSettings.setSavePassword(false);
		webSettings.setAppCacheEnabled(true);
		webSettings.setAppCachePath("");
		webSettings.setAppCacheMaxSize(5 * 1024 * 1024);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Mobile Safari/537.36");
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		//loadUrl("http://www.dreamforone.com/~teocean/#hash_login");
		loadUrl(MAIN_URL);
	}

	@Override
	public void AppAllOfferwallSDKCallback(int i) {

		switch (i) {
			case AppAllOfferwallSDK.AppAllOfferwallSDK_SUCCES:
				Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show();
				break;
			case AppAllOfferwallSDK.AppAllOfferwallSDK_INVALID_USER_ID:
				Toast.makeText(this, "잘못 된 유저아이디입니다.", Toast.LENGTH_SHORT).show();
				break;
			case AppAllOfferwallSDK.AppAllOfferwallSDK_INVALID_KEY:
				Toast.makeText(this, "오퍼월 KEY를 확인해주세요.", Toast.LENGTH_SHORT).show();
				break;
			case AppAllOfferwallSDK.AppAllOfferwallSDK_NOT_GET_ADID:
				Toast.makeText(this, "고객님의 폰으로는 무료충전소를 이용하실 수 없습니다. 고객센터에 문의해주세요.", Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}

	private class WebViewClientClass extends WebViewClient {
		
		@Override
		public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
			Log.i("TAG", "onReceivedSslError");
			final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage("유효하지 않는 인증서 사이트입니다. 접속을 진행하시겠습니까?");
			builder.setPositiveButton("접속하기", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.proceed();
				}
			});
			builder.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handler.cancel();
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("TAG", "shouldOverrideUrlLoading " + url);
			
			if ( view == null || url == null) {
                // 처리하지 못함
                return false;
            }

			if (url.startsWith("tel:")) {
				Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(call_phone);
				return true;

			} else if (url.startsWith("sms:")) {
				Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
				startActivity(i);
				return true;
			}
			
            if ( url.contains("play.google.com") ) {
              // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
              String[] params = url.split("details");
              if ( params.length > 1 ) {
                  url = "market://details" + params[1];
                  view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                  return true;
              }
            }

            if ( url.startsWith("http:") || url.startsWith("https:") ) {
                // HTTP/HTTPS 요청은 내부에서 처리한다.
            	view.loadUrl(url);
            } else {
                Intent intent;

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    // 처리하지 못함
                    return false;
                }

                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                    if ( url.startsWith("intent:") && intent.getPackage() != null) {
                        url = "market://details?id=" + intent.getPackage();
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
                        return true;
                    } else {
                        // 처리하지 못함
                        return false;
                    }
                }
            }
			return true;
		}
		
		

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			isLoading = true;
			mHandler.sendEmptyMessageDelayed(2, 750);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			
			isLoading = false;
			mHandler.removeMessages(2);
			mHandler.removeMessages(3);
			Log.d("url",url);
			//mProgressBar.setVisibility(View.GONE);

			view.loadUrl("javascript:isApp()");
			
			if (IntentURL.length() > 0) {
				loadUrl(IntentURL);
				IntentURL = "";
			} else if(IntentItem_no.length() > 0){
				loadUrl(IntentItem_no);
				IntentItem_no = "";
			}

			try {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					CookieSyncManager.getInstance().sync();
				} else {
					CookieManager.getInstance().flush();
				}

				//String id = mConfig.pref_get("id", "");
				String id = kr.foryou.util.Common.getPref(MainActivity.this,"mb_id","");
                String Allcookie = CookieManager.getInstance().getCookie(url);
				int first = Allcookie.lastIndexOf("mb_id=");
				Log.d("first",first+"");
				if(0<first) {
					int last = Allcookie.length();

					String mb_idStr = Allcookie.substring(first, last);

					int first1 = mb_idStr.indexOf("=") + 1;
					int last1 = mb_idStr.indexOf(";");

					if (last1 < first1) {
						last1 = mb_idStr.length();
					}

					String mb_id = mb_idStr.substring(first1, last1);
					Log.d("cookie111", mb_id);
					CookieManager.getInstance().setAcceptCookie(true);

					kr.foryou.util.Common.savePref(MainActivity.this, "mb_id", mb_id);
					postPush();
				}else{
					kr.foryou.util.Common.savePref(MainActivity.this, "mb_id", "");
				}
				Log.d("mb_id", kr.foryou.util.Common.getPref(MainActivity.this,"mb_id",""));
				if(!kr.foryou.util.Common.getPref(MainActivity.this,"mb_id","").equals("")) {
                    AppAllOfferwallSDK.getInstance().initOfferWall(MainActivity.this, "8bb108f9778d49ff05243074d0f0151593f3ee29", kr.foryou.util.Common.getPref(MainActivity.this, "mb_id", ""));
                }
			} catch (Exception e) {
				Log.d("error",e.toString());
				// TODO: handle exception
			}
			/*
			if (AppAllOfferwallSDK.getInstance().showAppAllOfferwall(MainActivity.this)) {
				//성공
			} else {
				Toast.makeText(MainActivity.this, "SDK initialization error.", Toast.LENGTH_SHORT).show();
			}*/
			super.onPageFinished(view, url);
		}
		
		@Override
		public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
			Log.i("TAG", "errorCode " + errorCode);
			Log.i("TAG", "failingUrl " + failingUrl);
			//loadUrl("about:blank");
			//Intent mIntent = new Intent(MainActivity.this, ErrorActivity.class);
			//startActivityForResult(mIntent, ERROR_CODE);
			
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		
	}

	private class WebChromeClientClass extends WebChromeClient {
		
		@Override
		public void onCloseWindow(WebView window) {
			Log.i("TAG", "onCloseWindow1 window " + window.getUrl());
			super.onCloseWindow(window);
		}
		@JavascriptInterface
		public void dreamMoneyView(){
			if (AppAllOfferwallSDK.getInstance().showAppAllOfferwall(MainActivity.this)) {
				//성공
			} else {
				Toast.makeText(MainActivity.this, "SDK initialization error.", Toast.LENGTH_SHORT).show();
			}
		}
		
		@JavascriptInterface
		public void share(final String url) {
			handler.post(new Runnable() {
				public void run() {
					Intent msg = new Intent(Intent.ACTION_SEND);
					msg.addCategory(Intent.CATEGORY_DEFAULT);
					msg.putExtra(Intent.EXTRA_TEXT, url);
					msg.setType("text/plain");
					startActivity(Intent.createChooser(msg, ""));
				}
			});
		}
		
		@JavascriptInterface
		public void galleryFile(final String count) {
			handler.post(new Runnable() {
				public void run() {
					Log.i("TAG", "count " + count);
					try {
						file_count = Integer.parseInt(count);
						if(file_count < 8){
							Intent mIntent = new Intent(getApplicationContext(), MultiPhotoSelectActivity.class);
							mIntent.putExtra("number", file_count);
							startActivityForResult(mIntent, MULTI_UPLOAD_IMAGE);
						} else {
							Toast.makeText(getApplicationContext(), "8장까지만 등록가능합니다.", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			});
		}

		@JavascriptInterface
		public void getGPS() {
			handler.post(new Runnable() {
				public void run() {
					GpsInfo gps = new GpsInfo(MainActivity.this);
			        if (gps.isGetLocation()) {
			            Double latitude = gps.getLatitude();
			            Double longitude = gps.getLongitude();
			            mWebview.loadUrl("javascript:setGpsHave('"+latitude+"','"+longitude+"')");
			            mWebview.loadUrl("javascript:setGpsWant('"+latitude+"','"+longitude+"')");
			        } else {
			        	gps.showSettingsAlert();
			        }
				}
			});
		}
		
		@JavascriptInterface
		public void getCount(final String count) {
			handler.post(new Runnable() {
				public void run() {
					file_count = Integer.valueOf(count);
				}
			});
		}
		
		@JavascriptInterface
		public void error() {
			handler.post(new Runnable() {
				public void run() {
					loadUrl("about:blank");
					Intent mIntent = new Intent(MainActivity.this, ErrorActivity.class);
					startActivityForResult(mIntent, ERROR_CODE);
				}
			});
		}
		
		@JavascriptInterface
		public void bottom(final String type) {
			handler.post(new Runnable() {
				public void run() {
					LinearLayout layout = (LinearLayout) findViewById(R.id.layout_bottom);
					if(type.equals("up")){
					} else {
						layout.setVisibility(View.GONE);
					}
				}
			});
		}
		
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			//mProgressBar.setProgress(newProgress);
		}

		// For Android Version < 3.0

		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			// System.out.println("WebViewActivity OS Version : " +
			// Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
			mUploadMessage = uploadMsg;
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType(TYPE_IMAGE);
			startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
		}

		// For 3.0 <= Android Version < 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			// System.out.println("WebViewActivity 3<A<4.1, OS Version : " +
			// Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
			openFileChooser(uploadMsg, acceptType, "");
		}

		// For 4.1 <= Android Version < 5.0
		public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
			Log.d(getClass().getName(), "openFileChooser : " + acceptType + "/" + capture);
			mUploadMessage = uploadFile;
			imageChooser();
		}

		// For Android Version 5.0+
		// Ref:
		// https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
		public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			if (mFilePathCallback != null) {
				mFilePathCallback.onReceiveValue(null);
			}
			mFilePathCallback = filePathCallback;
			imageChooser();
			return true;
		}

		private void imageChooser() {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
					takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
				} catch (IOException ex) {
					// Error occurred while creating the File
					Log.e(getClass().getName(), "Unable to create Image File", ex);
				}

				// Continue only if the File was successfully created
				if (photoFile != null) {
					mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				} else {
					takePictureIntent = null;
				}
			}

			Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
			contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
			contentSelectionIntent.setType(TYPE_IMAGE);

			Intent[] intentArray;
			if (takePictureIntent != null) {
				intentArray = new Intent[] { takePictureIntent };
			} else {
				intentArray = new Intent[0];
			}

			Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
			chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
			chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

			startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
			WebView.HitTestResult result = view.getHitTestResult();
			String url = result.getExtra();
		 
			Log.i("TAG", "onCreateWindow1 " + url);
			
			if(result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
				try{
					Message hrefMsg = new Message();
					hrefMsg.setTarget(new Handler());
					view.requestFocusNodeHref(hrefMsg);
					url = (String)hrefMsg.getData().get("url");
				}
				catch(Exception e){
					
				}
			}
			
			if(url != null && url.length() > 0){
				if(!url.startsWith(SEVER_URL)){
					Uri uri = Uri.parse(url);
					Intent it  = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(it);
					return false;
				}
				
			}
			
			Log.i("TAG", "onCreateWindow2 " + url);

			view.removeAllViews();
			WebView newView = new WebView(MainActivity.this);
			newView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			newView.setScrollbarFadingEnabled(true);
			newView.setHorizontalScrollBarEnabled(false);
			newView.setVerticalScrollBarEnabled(false);
			//newView.setWebViewClient(new WebViewClientClass());
			//newView.setWebChromeClient(new WebChromeClientClass());
			//newView.addJavascriptInterface(new WebChromeClientClass(), "androidfile");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				newView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			} else {
				newView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}
			try {
				if (Build.VERSION.SDK_INT < 11) {
					ZoomButtonsController zoom_controll = null;
					zoom_controll = (ZoomButtonsController) newView.getClass().getMethod("getZoomButtonsController")
							.invoke(newView);
					zoom_controll.getContainer().setVisibility(View.GONE);
				} else {
					newView.getSettings().getClass().getMethod("setDisplayZoomControls", Boolean.TYPE)
							.invoke(newView.getSettings(), false);
				}
			} catch (Exception e) {
			}

			WebSettings webSettings = newView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setSaveFormData(false);
			webSettings.setPluginState(PluginState.ON);
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setUseWideViewPort(true);//
			webSettings.setSupportZoom(true);
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setSupportMultipleWindows(true);
			webSettings.setLoadsImagesAutomatically(true);
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDatabaseEnabled(true);
			webSettings.setDatabasePath("data/data/kr.foryou.teocean/databases");
			webSettings.setDomStorageEnabled(true);
			webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
			webSettings.setUserAgentString(webSettings.getUserAgentString() + " (XY ClientApp)");
			webSettings.setAllowFileAccess(true);
			webSettings.setSavePassword(false);
			webSettings.setAppCacheEnabled(true);
			webSettings.setAppCachePath("");
			webSettings.setAppCacheMaxSize(5 * 1024 * 1024);
			webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Mobile Safari/537.36");
			webSettings.setAllowFileAccess(true);
			webSettings.setAllowContentAccess(true);
			webSettings.setAllowFileAccessFromFileURLs(true);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
			
			newView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			newView.setTag("webview");
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			newView.setLayoutParams(params);
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
			layout.addView(newView);

			
			newView.setWebChromeClient(new WebChromeClientClass() {
				@Override
				public void onCloseWindow(WebView window) {
					super.onCloseWindow(window);
					Log.i("TAG", "new onCloseWindow");
					//window.loadUrl("about:blank");
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
					layout.removeView(window);
					//window = null;
				}

				@Override
				public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
					return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
				}

			});
			newView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Log.i("TAG", "onCreateWindow shouldOverrideUrlLoading " + url);
					
					if ( view == null || url == null) {
		                // 처리하지 못함
		                return false;
		            }

					if (url.startsWith("tel:")) {
						Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(call_phone);
						return true;

					} else if (url.startsWith("sms:")) {
						Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
						startActivity(i);
						return true;
					}
					
		            if ( url.contains("play.google.com") ) {
		              // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
		              String[] params = url.split("details");
		              if ( params.length > 1 ) {
		                  url = "market://details" + params[1];
		                  view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
		                  return true;
		              }
		            }

		            if ( url.startsWith("http:") || url.startsWith("https:") ) {
						Log.d("popup-url",url);
		                // HTTP/HTTPS 요청은 내부에서 처리한다.
		            	view.loadUrl(url);
		            } else {
		                Intent intent;

		                try {
		                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
		                } catch (URISyntaxException e) {
		                    // 처리하지 못함
		                    return false;
		                }

		                try {
		                    view.getContext().startActivity(intent);
		                } catch (ActivityNotFoundException e) {
		                    // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
		                    if ( url.startsWith("intent:") && intent.getPackage() != null) {
		                        url = "market://details?id=" + intent.getPackage();
		                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url) ));
		                        return true;
		                    } else {
		                        // 처리하지 못함
		                        return false;
		                    }
		                }
		            }
					return true;
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					Log.i("TAG", "url " + url);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);

				}
			});
			

			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(newView);
			resultMsg.sendToTarget();
			return true;
		}

	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
		return imageFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		
		if(requestCode == ERROR_CODE){
			if(resultCode == RESULT_OK){
				if(data.getStringExtra("type").equals("1")){
					loadUrl(MAIN_URL);
				} else if(data.getStringExtra("type").equals("2")){
					finish();
				}
			} else {
				finish();
			}
		}
		
		if(requestCode == INPUT_FILE_REQUEST_CODE){
			if(resultCode == RESULT_OK){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if (mFilePathCallback == null) {
						super.onActivityResult(requestCode, resultCode, data);
						return;
					}
					Uri[] results = new Uri[] { getResultUri(data) };

					mFilePathCallback.onReceiveValue(results);
					mFilePathCallback = null;
				} else {
					if (mUploadMessage == null) {
						super.onActivityResult(requestCode, resultCode, data);
						return;
					}
					Uri result = getResultUri(data);

					mUploadMessage.onReceiveValue(result);
					mUploadMessage = null;
				}
			} else {
				if (mFilePathCallback != null)
					mFilePathCallback.onReceiveValue(null);
				if (mUploadMessage != null)
					mUploadMessage.onReceiveValue(null);
				mFilePathCallback = null;
				mUploadMessage = null;
			}
		}
		
		if (requestCode == MULTI_UPLOAD_IMAGE) {
			if (resultCode == RESULT_OK) {
				
				final ArrayList<String> uriArr = data.getStringArrayListExtra("uri");
				uploadFileArray = new ArrayList<FileData>();
				mWebview.loadUrl("javascript:setCount('"+uriArr.size()+"')");


				new Thread(new Runnable() { 
					@Override public void run() {
						
						int i = 1;
						ArrayList<File> requestFileListArray=new ArrayList<>();
						for (String str : uriArr) {
							Uri fileUri=Uri.parse(str);
							Log.d("filePath",fileUri.getPath());
							File file=new File(fileUri.getPath());
							requestFileListArray.add(file);
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 2;
							Bitmap bitmap = BitmapFactory.decodeFile(str, options);
							int oriented = ExifUtil.getOrientation(str);
							int rotationDegrees = 0;
							switch (oriented) {
							case ExifInterface.ORIENTATION_ROTATE_90:
								rotationDegrees = 90;
								break;
							case ExifInterface.ORIENTATION_ROTATE_180:
								rotationDegrees = 180;
								break;
							case ExifInterface.ORIENTATION_ROTATE_270:
								rotationDegrees = 270;
								break;
							}
							
							Matrix matrix = new Matrix();
					        matrix.postRotate(rotationDegrees);

					        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
							
							ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
							bitmap.compress(CompressFormat.JPEG, 95, bos); 
							byte[] bitmapdata = bos.toByteArray();
							ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
							
							String base64Image = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
							final String imgHtml = "data:image/jpeg;base64," + base64Image;
							
							SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMddHHmmss", Locale.KOREA ); 
							Date currentTime = new Date ( );
							String dTime = formatter.format ( currentTime );
							String file_name = dTime + "_" + MyPhoneData.GetDeviceID(getApplicationContext()) + "_" + i + ".jpg";
							
							FileData fData = new FileData();
							fData.file=file;
							fData.uploaded_file = bs;
							fData.imgHtml = imgHtml;
							fData.file_name = file_name;
							uploadFileArray.add(fData);
							
							
							
							Bundle b = new Bundle();
							b.putString("file_name", file_name);
							b.putString("temp", imgHtml);
							
							Message m = new Message();
							m.setData(b);
							m.what = 5;
							mHandler.sendMessage(m);

							i++;
							
						}

						//다시 넣기

						mHandler.sendEmptyMessage(4);
						
				} }).start();
					
				
			}
		}
		
		if(requestCode == INTRO_CODE){
			if (resultCode == RESULT_OK) {
				String btn = data.getStringExtra("btn");
				if(btn.equals("login")){
					mWebview.loadUrl(SEVER_URL + "#hash-login");
				} else if(btn.equals("join")){
					mWebview.loadUrl(SEVER_URL + "bbs/register_form.php?mb_type=%EC%9D%BC%EB%B0%98");
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public byte[] bitmapToByteArray(Bitmap $bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		$bitmap.compress(CompressFormat.PNG, 80, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}


	private Uri getResultUri(Intent data) {
		Uri result = null;
		if (data == null || TextUtils.isEmpty(data.getDataString())) {
			// If there is not data, then we may have taken a photo
			if (mCameraPhotoPath != null) {
				result = Uri.parse(mCameraPhotoPath);
			}
		} else {
			String filePath = "";
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				filePath = data.getDataString();
			} else {
				filePath = "file:" + RealPathUtil.getRealPath(this, data.getData());
			}
			result = Uri.parse(filePath);
		}

		return result;
	}

	public String getPathFromUri(Uri uri) {

		Cursor cursor = getContentResolver().query(uri, null, null, null, null);

		cursor.moveToNext();

		String path = cursor.getString(cursor.getColumnIndex("_data"));

		cursor.close();

		return path;

	}

	private void Intro() {
		IvIntro = (ImageView) findViewById(R.id.iv_intro);
		Animation anima = AnimationUtils.loadAnimation(this, R.anim.intro_alpa);
		IvIntro.startAnimation(anima);
		//IvIntro.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(0, 0);
	}

	private void Bottom() {
		LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout_bottom_icon_1);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout_bottom_icon_2);
		LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout_bottom_icon_3);
		LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout_bottom_icon_4);
		LinearLayout layout5 = (LinearLayout) findViewById(R.id.layout_bottom_icon_5);

		TextView textview1 = (TextView) findViewById(R.id.tv_bottom_icon_1);
		TextView textview2 = (TextView) findViewById(R.id.tv_bottom_icon_2);
		TextView textview3 = (TextView) findViewById(R.id.tv_bottom_icon_3);
		TextView textview4 = (TextView) findViewById(R.id.tv_bottom_icon_4);
		TextView textview5 = (TextView) findViewById(R.id.tv_bottom_icon_5);

		ImageView imgview1 = (ImageView) findViewById(R.id.iv_bottom_icon_1);
		ImageView imgview2 = (ImageView) findViewById(R.id.iv_bottom_icon_2);
		ImageView imgview3 = (ImageView) findViewById(R.id.iv_bottom_icon_3);
		ImageView imgview4 = (ImageView) findViewById(R.id.iv_bottom_icon_4);
		ImageView imgview5 = (ImageView) findViewById(R.id.iv_bottom_icon_5);

		AddData(layout1, textview1, imgview1);
		AddData(layout2, textview2, imgview2);
		AddData(layout3, textview3, imgview3);
		AddData(layout4, textview4, imgview4);
		AddData(layout5, textview5, imgview5);

		for (BottomData data : mData) {
			// data.layout.setVisibility(View.GONE);
		}

		int i = 0;
		for (String str : bottomText) {
			BottomData data = mData.get(i);
			// data.layout.setVisibility(View.VISIBLE);
			data.layout.setOnClickListener(this);
			data.textview.setText(str);
			data.imageview.setImageResource(bottomImg[i]);
			i++;
		}
	}

	@Override
	public void onBackPressed() {

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		for (int i = layout.getChildCount() - 1; i > 0; i--) {
			View view = layout.getChildAt(i);
			if (view.getTag() != null && String.valueOf(view.getTag()).equals("webview")) {
				WebView webview = (WebView) view;
				WebBackForwardList list2 = webview.copyBackForwardList();
				if (list2.getCurrentIndex() <= 0 && !webview.canGoBack()) {
					webview.loadUrl("javascript:self.close()");
					layout.removeView(webview);
					//webview = null;
				} else {
					webview.goBack();
				}
				return;
			}
		}

		WebBackForwardList list = mWebview.copyBackForwardList();
		if (list.getCurrentIndex() <= 0 && !mWebview.canGoBack()) {
			finishApp();
		} else {
			String url = mWebview.getUrl();
			if (url.equals(MAIN_URL) || url.equals(MAIN_URL.substring(0, MAIN_URL.length()-1))) {
				finishApp();
			} else if (url.indexOf("login.php") > -1) {
				loadUrl(MAIN_URL);
			} else if (url.indexOf("board.php?bo_table=") > -1 && url.indexOf("wr_id") > -1) {
				url = url.replaceAll("\\&wr_id=\\d+", "");
				loadUrl(url);
			} else if (url.indexOf("board.php?bo_table=") > -1) {
				loadUrl(MAIN_URL);
			} else {
				mWebview.goBack();
			}
		}
	}

	private void AddData(LinearLayout layout, TextView textview, ImageView imageview) {
		BottomData data = new BottomData();
		data.layout = layout;
		data.imageview = imageview;
		data.textview = textview;
		mData.add(data);
	}

	@Override
	public void onClick(View v) {
		
		String mb_id = mConfig.pref_get("id", "");
		
		switch (v.getId()) {
		case R.id.layout_bottom_icon_1: {
			String url = mWebview.getUrl();
			if(!(url.equals(MAIN_URL) || url.equals(MAIN_URL.substring(0, MAIN_URL.length()-1)) || url.equals(MAIN_URL + "shop/"))){
				loadUrl(MAIN_URL);
			}
			
		}
			break;
		case R.id.layout_bottom_icon_2: {
			if(mb_id.equals("")){
				mWebview.loadUrl("javascript:getHash('hash_login')");
			} else {
				loadUrl(MAIN_URL + "bbs/mypay.php");
			}
		}
			break;
		case R.id.layout_bottom_icon_3: {
			if(mb_id.equals("")){
				mWebview.loadUrl("javascript:getHash('hash_login')");
			} else {
				loadUrl(MAIN_URL + "bbs/mypromote.php");
			}
		}
			break;
		case R.id.layout_bottom_icon_4: {
			mWebview.loadUrl("javascript:getHash('hash_search')");
		}
			break;
		case R.id.layout_bottom_icon_5: {
			if(mb_id.equals("")){
				mWebview.loadUrl("javascript:getHash('hash_login')");
			} else {
				loadUrl(MAIN_URL + "bbs/mypage.php");
			}
		}
			break;
		}
	}

	private void finishApp() {
		if (end == true) {
			end = false;
			finish();
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
			alert.setMessage("앱을 종료하시겠습니까?");
			alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					end = true;
					finish();
				}
			});
			alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			alert.show();
		}
	}

	private void kakao() {
		/*try {
			kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
			kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
			kakaoTalkLinkMessageBuilder.addText(KakaoText);
			kakaoTalkLinkMessageBuilder.addAppButton("설치하기");
			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);
		} catch (Exception e) {
			// TODO: handle exception
		}*/
	}

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				startActivityForResult(new Intent(MainActivity.this, WelcomeActivity.class), INTRO_CODE);
				IvIntro.setVisibility(View.GONE);
			} else if (msg.what == 1) {
				end = false;
			} else if (msg.what == 2) {
				if (isLoading == true) {
					//mProgressBar.setProgress(0);
					//mProgressBar.setVisibility(View.VISIBLE);
					//mHandler.sendEmptyMessageDelayed(3, 500);
				}

			} else if (msg.what == 3) {
				if (isLoading == true) {
					//mProgressBar.setVisibility(View.GONE);
				}
			} else if (msg.what == 4){
				HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
				httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

				//클라이언트 설정
				OkHttpClient client = new OkHttpClient.Builder()
						.addInterceptor(httpLoggingInterceptor)
						.build();
				Retrofit retrofit=new Retrofit.Builder()
						.baseUrl(getString(R.string.domain))
						.client(client)
						.addConverterFactory(GsonConverterFactory.create())
						.build();
				//파라미터 넘길 값 설정
				Map<String, RequestBody> map=new HashMap<>();
				map.put("division", StaticRetrofit.toRequestBody("file_upload"));



				ArrayList<ByteArrayInputStream> uploaded_file = new ArrayList<ByteArrayInputStream>();
				ArrayList<Integer> oriented = new ArrayList<Integer>();
				ArrayList<String> fileName = new ArrayList<String>();
				int no=0;
				for(final FileData data : uploadFileArray){

					uploaded_file.add(data.uploaded_file);
					Log.d("fileName1",data.file_name);
					fileName.add(data.file_name);
					RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), data.file);
					map.put("uploaded_file_"+no+"\"; filename=\""+data.file_name, fileBody);
					no++;
				}




				//레트로핏 서비스 실행하기
				RetrofitService retrofitService=retrofit.create(RetrofitService.class);
				//데이터 불러오기
				Call<ServerPost> call=retrofitService.FileUpload(map);
				call.enqueue(new retrofit2.Callback<ServerPost>() {
					@Override
					public void onResponse(Call<ServerPost> call, Response<ServerPost> response) {
						if(response.isSuccessful()){
							ServerPost repo=response.body();
							Log.d("response",response+"");
							if(Boolean.parseBoolean(repo.getSuccess())==false){
							}else{
								Toast.makeText(MainActivity.this, "파일첨부 성공", Toast.LENGTH_SHORT).show();
							}
						}else{

						}
					}

					@Override
					public void onFailure(Call<ServerPost> call, Throwable t) {

					}
				});

			} else if (msg.what == 5){
				String file_name = msg.getData().getString("file_name");
				String imgHtml = msg.getData().getString("temp");
				mWebview.loadUrl("javascript:callImage('"+file_name+"','"+imgHtml+"')");
			}
		}
	};

	public void gcm() {
		m_Gcmutil = new GCMUTIL(this);
		m_Gcmutil.setServerID(GCMUTIL.R_KEY);
		//m_Gcmutil.register();
	}


	class BottomData {
		LinearLayout layout = null;
		TextView textview = null;
		ImageView imageview = null;
	}


	public class AdInfo {
		String image = "";
		String link = "";
	}

	public class AD_Adapter extends BaseAdapter {

		private final Context mContext;
		LayoutInflater inflater;

		public AD_Adapter(Context c) {
			mContext = c;
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mAdData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AdViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.ad_item, parent, false);
				holder = new AdViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (AdViewHolder) convertView.getTag();
			}

			AdInfo data = mAdData.get(position);
			//imageLoader.displayImage(data.image, holder.getIv());
			holder.getIv().setTag(data.link);
			holder.getIv().setOnClickListener(AdClick);
			return convertView;
		}

		public OnClickListener AdClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(String.valueOf(v.getTag()));
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		};

	}

	public class AdViewHolder {
		private View base;
		private ImageView iv;

		AdViewHolder(View base) {
			this.base = base;
		}

		ImageView getIv() {
			if (iv == null) {
				iv = (ImageView) base.findViewById(R.id.iv_ad);
			}
			return iv;
		}
	}
	
	public class FileData{
		ByteArrayInputStream uploaded_file = null;
		String file_name = "";
		String imgHtml = "";
		File file;
	}
}
