package co.tashawych.hassle;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class TwitterOAuth extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
		setContentView(webview);
	}

}
