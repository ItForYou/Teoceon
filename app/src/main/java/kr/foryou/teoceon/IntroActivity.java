package kr.foryou.teoceon;

import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent miIntent = new Intent(this, MainActivity.class);
		startActivity(miIntent);
		finish();
	}
}
