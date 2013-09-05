package co.tashawych.hassle;

import java.util.ArrayList;
import java.util.Locale;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.misc.Utility;
import co.tashawych.hassle.social.GmailSender;

public class Hassle extends BaseActivity {
	Contact contact;
	ImageView picture;
	TextView name;
	EditText hassle_edit;
	
	SharedPreferences prefs;
	
	boolean text_on = true;
	boolean email_on = true;
	boolean twitter_on = true;
	
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
			if (contact.picture != null) picture.setImageBitmap(Utility.getBitmapFromString(contact.picture));
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
			ArrayList<String> results = 
					data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
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
		else {
			try {
			if (text_on) {
				PendingIntent pi = PendingIntent.getActivity(this, 0, null, 0);
				SmsManager sms = SmsManager.getDefault();
			    sms.sendTextMessage(contact.phone, null, hassle, pi, null);
			    
			    ContentValues values = new ContentValues();
			    values.put("address", contact.phone);
			    values.put("body", hassle);
			    values.put("date", System.currentTimeMillis());
			    getContentResolver().insert(Uri.parse("content://sms/sent"), values);
			}
			
			if (email_on) {
                new SendEmail(email, password, hassle).execute();
			}
			
			if (twitter_on) {
				ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
                cb.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
                cb.setOAuthAccessToken(cred_token);
                cb.setOAuthAccessTokenSecret(cred_token_secret);
                
                TwitterFactory tf = new TwitterFactory(cb.build());
                Twitter twitter = tf.getInstance();
                
                // Check if the user's stored screen name begins with the @ symbol, if so remove it
                if (contact.twitter.startsWith("@")) contact.twitter = contact.twitter.substring(1);
                
                new ComposeTweet(twitter, "@" + contact.twitter + " " + hassle).execute();
			}
		    
			Toast.makeText(this, "Your Hassle has been sent!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Intent new_contact = new Intent(this, ContactList.class);
				startActivity(new_contact);
			}
		}
	}
	
    private class SendEmail extends AsyncTask<Void, Void, Boolean> {
    	String email;
    	String password;
    	String message;
    	public SendEmail(String email, String password, String message) {
    		this.email = email;
    		this.password = password;
    		this.message = message;
    	}

        @Override
        protected Boolean doInBackground(Void... voids) {
			try {
                GmailSender sender = new GmailSender(email, password);
                sender.sendMail("You have received a Hassle!", message, email, contact.email);
                return true;
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
                return false;
            }
        }
    }
	
    private class ComposeTweet extends AsyncTask<Void, Void, Boolean> {
    	Twitter twitter;
    	String tweet;
    	public ComposeTweet(Twitter twitter, String tweet) {
    		this.twitter = twitter;
    		this.tweet = tweet;
    	}

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                StatusUpdate status = new StatusUpdate(tweet);
                twitter.updateStatus(status);
                return true;
            } catch (TwitterException e) {
            	e.printStackTrace();
            }
            return false;
        }
    }

}
