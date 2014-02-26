package co.tashawych.hassle;

import java.util.ArrayList;
import java.util.Locale;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.misc.Utility;

public class Hassle extends BaseActivity {
	Contact contact;
	ImageView picture;
	TextView name;
	EditText hassle_edit;

	SharedPreferences prefs;

	boolean text_on = true;
	boolean email_on = true;
	boolean twitter_on = true;
	boolean error_occurred = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hassle);

		prefs = getSharedPreferences("Hassle", 0);

		picture = (ImageView) findViewById(R.id.hassle_picture);
		name = (TextView) findViewById(R.id.hassle_name);
		hassle_edit = (EditText) findViewById(R.id.hassle_edit);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new init_page().execute();
	}

	private class init_page extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			contact = DatabaseHelper.getHelper(Hassle.this).getContact(getIntent().getIntExtra("contact_id", 1));
			return null;
		}

		@Override
		protected void onPostExecute(Void voids) {
			name.setText(contact.name);
			if (contact.picture != null)
				picture.setImageBitmap(Utility.getBitmapFromString(contact.picture));
			hassle_edit.setHint("What do you want to Hassle " + contact.name + " about?");
			return;
		}

	}

	private void request_voice_input() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your Hassle message");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
		startActivityForResult(intent, VOICE_RECOGNITION);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		// VOICE RECOGNITION
		if (result == RESULT_OK && request == VOICE_RECOGNITION) {
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			String mostLikelyResult = results.get(0);
			hassle_edit.setText(mostLikelyResult);
		}
	}

	public void btn_edit_clicked(View v) {
		Intent edit_contact = new Intent(this, NewContact.class);
		edit_contact.putExtra("id", contact.id);
		startActivity(edit_contact);
	}

	public void btn_speak_clicked(View v) {
		request_voice_input();
	}

	public void switch_text(View v) {
		ImageView switch_text = (ImageView) findViewById(R.id.switch_text);
		TextView text = (TextView) findViewById(R.id.send_text);
		if (text_on) {
			text_on = false;
			switch_text.setImageResource(R.drawable.sms_inactive);
			text.setTextColor(getResources().getColor(R.color.text_inactive));
		}
		else {
			text_on = true;
			switch_text.setImageResource(R.drawable.sms_active);
			text.setTextColor(getResources().getColor(R.color.text_active));
		}
	}

	public void switch_email(View v) {
		ImageView switch_email = (ImageView) findViewById(R.id.switch_email);
		TextView email = (TextView) findViewById(R.id.send_email);
		if (email_on) {
			email_on = false;
			switch_email.setImageResource(R.drawable.email_inactive);
			email.setTextColor(getResources().getColor(R.color.text_inactive));
		}
		else {
			email_on = true;
			switch_email.setImageResource(R.drawable.email_active);
			email.setTextColor(getResources().getColor(R.color.text_active));
		}
	}

	public void switch_twitter(View v) {
		ImageView switch_twitter = (ImageView) findViewById(R.id.switch_twitter);
		TextView twitter = (TextView) findViewById(R.id.send_twitter);
		if (twitter_on) {
			twitter_on = false;
			switch_twitter.setImageResource(R.drawable.twitter_inactive);
			twitter.setTextColor(getResources().getColor(R.color.text_inactive));
		}
		else {
			twitter_on = true;
			switch_twitter.setImageResource(R.drawable.twitter_active);
			twitter.setTextColor(getResources().getColor(R.color.text_active));
		}
	}

	public void btn_hassle_clicked(View v) {
		String hassle = hassle_edit.getText().toString();

		String cred_token = prefs.getString("twitter_cred_token", "");
		String cred_token_secret = prefs.getString("twitter_cred_token_secret", "");

		String email = prefs.getString("email", "");
		String password = prefs.getString("password", "");

		// If user hasn't entered a hassle message
		if (hassle.equals("")) {
			Toast.makeText(this, "Please enter something to Hassle " + contact.name + " about.", 
					Toast.LENGTH_SHORT).show();
		}
		// If user hasn't selected a way of hassling the contact
		else if (!text_on && !email_on && !twitter_on) {
			Toast.makeText(this, "You need to enable some way of hassling " + contact.name + ".", 
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to text but contact doesn't have phone number
		else if (text_on && (contact.phone == null || contact.phone.equals(""))) {
			Toast.makeText(this, contact.name + " does not have a phone number to text.", 
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to email but contact doesn't have email address
		else if (email_on && (contact.email == null || contact.email.equals(""))) {
			Toast.makeText(this, contact.name + " does not have an email address to email.", 
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to tweet but contact doesn't have Twitter
		else if (twitter_on && (contact.twitter == null || contact.twitter.equals(""))) {
			Toast.makeText(this, contact.name + " does not have a Twitter handle to tweet at.", 
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to send an email but hasn't connected an email account
		else if (email_on && (email.equals("") || password.equals(""))) {
			Toast.makeText(this, "You need to connect an email account to send emails",
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to send a tweet but hasn't connected a twitter account
		else if (twitter_on && (cred_token.equals("") || cred_token_secret.equals(""))) {
			Toast.makeText(this, "You need to connect a Twitter account to send tweets",
					Toast.LENGTH_SHORT).show();
		}
		// If user wants to email or tweet but doesn't have an Internet connection
		else if ((email_on || twitter_on) && !Utility.hasInternetAccess(this)) {
			Toast.makeText(this, "You need Internet access to send emails and tweets",
					Toast.LENGTH_SHORT).show();
		}
		else {
			if (text_on) {
				try {
					PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Hassle.class), 
							PendingIntent.FLAG_NO_CREATE);
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(contact.phone, null, hassle, pi, null);
	
					ContentValues values = new ContentValues();
					values.put("address", contact.phone);
					values.put("body", hassle);
					values.put("date", System.currentTimeMillis());
					getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				} catch (Exception e) {
					error_occurred = true;
					Toast.makeText(this, "There was an error sending the text message",
							Toast.LENGTH_SHORT).show();
				}
			}

			if (email_on) {
				try {
					new Utility.SendEmail(email, password, hassle, contact.email).execute();
				} catch (Exception e) {
					error_occurred = true;
					Toast.makeText(this, "There was an error sending the email",
							Toast.LENGTH_SHORT).show();
				}
			}

			if (twitter_on) {
				try {
					ConfigurationBuilder cb = new ConfigurationBuilder();
					cb.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
					cb.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
					cb.setOAuthAccessToken(cred_token);
					cb.setOAuthAccessTokenSecret(cred_token_secret);
	
					TwitterFactory tf = new TwitterFactory(cb.build());
					Twitter twitter = tf.getInstance();
	
					// Check if the user's stored screen name begins with the @
					// symbol, if so remove it
					if (contact.twitter.startsWith("@"))
						contact.twitter = contact.twitter.substring(1);
	
					new Utility.ComposeTweet(twitter, "@" + contact.twitter + " " + hassle).execute();
				} catch (Exception e) {
					error_occurred = true;
					Toast.makeText(this, "There was an error sending the tweet",
							Toast.LENGTH_SHORT).show();
				}
			}

			if (!error_occurred) {
				Toast.makeText(this, "Your Hassle has been sent!", Toast.LENGTH_SHORT).show();
				
				Intent hassle_contacts = new Intent(this, FragmentLayout.class);
				hassle_contacts.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(hassle_contacts);
			}
		}
	}

}
