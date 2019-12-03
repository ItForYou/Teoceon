package kr.foryou.teoceon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ErrorActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		
		ImageButton btn_r = (ImageButton) findViewById(R.id.btn_e_main);
		ImageButton btn_e = (ImageButton) findViewById(R.id.btn_e_end);
		
		btn_r.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("type", "1");
				setResult(RESULT_OK, intent);
				finish();
				
			}
		});
		
		btn_e.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("type", "2");
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
