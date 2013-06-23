package co.tashawych.hassle;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;

public class Hassle extends Activity {
	Contact contact;
	EditText hassle_edit;
	
	boolean text_on = true;
	boolean email_on = true;
	boolean twitter_on = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hassle);
		
		contact = DatabaseHelper.getHelper(this).getContact(getIntent().getIntExtra("contact_id", 1));
		hassle_edit = (EditText) findViewById(R.id.hassle_edit);
		hassle_edit.setHint("What do you want to Hassle " + contact.name + " about?");
	}
	
	public void switch_text(View v) {
		ImageView switch_text = (ImageView) findViewById(R.id.switch_text);
		if (text_on) {
			text_on = false;
			switch_text.setImageResource(R.drawable.sms_inactive);
		}
		else {
			text_on = true;
			switch_text.setImageResource(R.drawable.sms_active);
		}
	}
	
	public void switch_email(View v) {
		ImageView switch_email = (ImageView) findViewById(R.id.switch_email);
		if (email_on) {
			email_on = false;
			switch_email.setImageResource(R.drawable.email_inactive);
		}
		else {
			email_on = true;
			switch_email.setImageResource(R.drawable.email_active);
		}
	}

	public void switch_twitter(View v) {
		ImageView switch_twitter = (ImageView) findViewById(R.id.switch_twitter);
		if (twitter_on) {
			twitter_on = false;
			switch_twitter.setImageResource(R.drawable.twitter_inactive);
		}
		else {
			twitter_on = true;
			switch_twitter.setImageResource(R.drawable.twitter_active);
		}
	}

	public void btn_hassle_clicked(View v) {
		String hassle = hassle_edit.getText().toString();
		
		if (hassle.equals("")) {
			Toast.makeText(this, "Please enter something to Hassle " + contact.name + " about.", 
					Toast.LENGTH_SHORT).show();
		}
		else {
			if (text_on) {
				PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Hassle.class), 0);
				SmsManager sms = SmsManager.getDefault();
			    sms.sendTextMessage(contact.phone, null, hassle, pi, null);
			}
		    
			Toast.makeText(this, "Hassle: '" + hassle + "' ... sent!", 
					Toast.LENGTH_SHORT).show();
		}
	}

}
