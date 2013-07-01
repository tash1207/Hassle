package co.tashawych.hassle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddEmail extends BaseActivity {
	
	EditText edit_email, edit_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_email);
		
		edit_email = (EditText) findViewById(R.id.edit_email);
		edit_password = (EditText) findViewById(R.id.edit_password);
		
		edit_email.setText(getSharedPreferences("Hassle", 0).getString("email", ""));
		
		final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
        	LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        	layout.setVisibility(View.VISIBLE);
      		Animation fade_in = AnimationUtils.loadAnimation(AddEmail.this, android.R.anim.fade_in);
      		layout.startAnimation(fade_in);
          }
        }, 850);
	}
	
	public void btn_submit_clicked(View v) {
		SharedPreferences.Editor edit = getSharedPreferences("Hassle", 0).edit();
		edit.putString("email", edit_email.getText().toString());
		edit.putString("password", edit_password.getText().toString());
		edit.commit();
		
		finish();
	}

}
