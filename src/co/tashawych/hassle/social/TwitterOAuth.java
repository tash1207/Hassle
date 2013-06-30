package co.tashawych.hassle.social;

import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import co.tashawych.hassle.R;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class TwitterOAuth extends Activity {
	
    private static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
    private static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
    private static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
    private static final String OAUTH_CALLBACK_URL = "http://api.feathr.co/v1/twitter";
    
    OAuthHmacSigner signer;
    String requestToken, verifier;
    WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		webview = new WebView(this);
		setContentView(webview);
		
		webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (url.startsWith(OAUTH_CALLBACK_URL + "?oauth_token")) {
                        verifier = url.split("oauth_verifier=")[1];
                        new TwitterCallback().execute();
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);                
            }
    });
		
		new TwitterAsync().execute();
	}

	private class TwitterAsync extends AsyncTask<Void, Void, Void> {
        
		@Override
        protected Void doInBackground(Void...voids) {
		try {
			signer = new OAuthHmacSigner();
			signer.clientSharedSecret = getString(R.string.twitter_consumer_secret);
    
	        OAuthGetTemporaryToken temporaryToken = new OAuthGetTemporaryToken(REQUEST_URL);
	        temporaryToken.transport = new ApacheHttpTransport();
	        temporaryToken.signer = signer;
	        temporaryToken.consumerKey = getString(R.string.twitter_consumer_key);
	        temporaryToken.callback = OAUTH_CALLBACK_URL;

            OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
            signer.tokenSharedSecret = tempCredentials.tokenSecret;
            OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(AUTHORIZE_URL);
            authorizeUrl.temporaryToken = tempCredentials.token;
            final String authorizationUrl = authorizeUrl.build();
            
            runOnUiThread(new Runnable() {
                            
                public void run() {
	                webview.loadUrl(authorizationUrl);
	                requestToken = authorizationUrl.split("oauth_token=")[1];
                }
            });
        
        } catch (Exception e) {
                e.printStackTrace();
        }

        return null;
		}
	}
	
	private class TwitterCallback extends AsyncTask<Void, Void, Void> {
        
        private ProgressDialog dialog = new ProgressDialog(TwitterOAuth.this);
        
        protected void onPreExecute() {
        	dialog.setMessage("Adding Twitter account... please wait");
            dialog.setCancelable(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void...voids) {
                
            signer.clientSharedSecret = getString(R.string.twitter_consumer_secret);
	        OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_URL);
	        accessToken.transport = new ApacheHttpTransport();
	        accessToken.temporaryToken = requestToken;
	        accessToken.signer = signer;
	        accessToken.consumerKey = getString(R.string.twitter_consumer_key);
	        accessToken.verifier = verifier;
        
        	try {
	            OAuthCredentialsResponse credentials = accessToken.execute();
	            //Log.d("twitter", "cred = " + credentials.token + " cred sec = " + credentials.tokenSecret);
	            SharedPreferences.Editor editor = getSharedPreferences("Hassle", 0).edit();
	            editor.putString("twitter_cred_token", credentials.token);
	            editor.putString("twitter_cred_token_secret", credentials.tokenSecret);
	            editor.commit();
                        
        	} catch (IOException e) {
                e.printStackTrace();
            }
        
	        String cred_token = getSharedPreferences("Hassle", 0).getString("twitter_cred_token", "");
	        String cred_token_secret = getSharedPreferences("Hassle", 0).getString("twitter_cred_token_secret", "");
        
            AccessToken a = new AccessToken(cred_token, cred_token_secret);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key), 
            		getString(R.string.twitter_consumer_secret));
            twitter.setOAuthAccessToken(a);
            
            try {
            	if (twitter.getScreenName() != null) {
            		SharedPreferences.Editor editor = getSharedPreferences("Hassle", 0).edit();
    	            editor.putString("twitter_screen_name", twitter.getScreenName());
    	            editor.commit();
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        
        protected void onPostExecute(Void result) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            nextActivity();
        }
	}
	
	public void nextActivity() {
        Intent done = new Intent();
        setResult(RESULT_OK, done);
        finish();
	}
}
