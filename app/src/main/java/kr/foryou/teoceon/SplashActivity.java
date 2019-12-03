package kr.foryou.teoceon;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import kr.foryou.util.RetrofitService;
import kr.foryou.util.ServerPost;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {
    private static final int APP_PERMISSION_STORAGE = 9787;
    private final int APPS_PERMISSION_REQUEST=1000;
    final int SEC=3000;//다음 화면에 넘어가기 전에 머물 수 있는 시간(초)
    TextView itemCountTxt,orderCountTxt;
    int itemCount=0,itemMaxCount,orderCount=0,orderMaxCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseMessaging.getInstance().subscribeToTopic("teoceon");
        //버전별 체크를 한 후 마시멜로 이상이면 퍼미션 체크 여부
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermission();
            } else {
                goHandler();
            }
        } catch (Exception e) {
        }
        itemCountTxt=(TextView)findViewById(R.id.itemCountTxt);
        orderCountTxt=(TextView)findViewById(R.id.orderCountTxt);
        getData();
    }
    public void getData(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(MainActivity.MAIN_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Map map=new HashMap();
        map.put("division","item_count");
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<ServerPost> call=retrofitService.getPush(map);

        call.enqueue(new Callback<ServerPost>() {
            @Override
            public void onResponse(Call<ServerPost> call, Response<ServerPost> response) {
                //서버에 데이터 받기가 성공할시
                if(response.isSuccessful()){

                    ServerPost repo=response.body();

                    if(Boolean.parseBoolean(repo.getSuccess())){
                        itemCountTxt.setText(repo.getItemCount());
                        orderCountTxt.setText(repo.getOrderCount());
                        itemMaxCount=Integer.parseInt(repo.getItemCount());
                        orderMaxCount=Integer.parseInt(repo.getOrderCount());



                    }else{

                    }
                }else{

                }
            }
            //데이터 받기가 실패할 시
            @Override
            public void onFailure(Call<ServerPost> call, Throwable t) {

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(){
        try {

            //권한이 없는 경우
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED
                    )
            {
                //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이유를 설명
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }

                //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
                requestPermissions(new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.RECEIVE_SMS
                        },
                        APP_PERMISSION_STORAGE);

            }
            //권한이 있는 경우
            else {
                goHandler();

                //writeFile();
            }
        }catch(Exception e){
            goHandler();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSION_STORAGE:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    goHandler();
                }else{

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivityForResult(intent,APPS_PERMISSION_REQUEST);
                    //startActivity(intent);

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==APPS_PERMISSION_REQUEST){
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    goHandler();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //핸들러로 이용해서 3초간 머물고 이동이 됨
    public void goHandler() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        }, SEC);
    }


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            itemCount++;
            if(itemCount<itemMaxCount) {
                itemCountTxt.setText(itemCount);
                mHandler.sendEmptyMessageDelayed(0,1);
            }else{
                mHandler.removeMessages(0);
            }
        }
    };
    Handler mHandler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            orderCount++;
            if(orderCount<orderMaxCount) {
                orderCountTxt.setText(itemCount);
                mHandler2.sendEmptyMessageDelayed(0,1);
            }else{
                mHandler2.removeMessages(0);
            }
        }
    };
}
