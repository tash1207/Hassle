package co.tashawych.hassle.misc;

import java.io.ByteArrayOutputStream;
import java.util.List;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import co.tashawych.hassle.social.GmailSender;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class Utility {

	public static Bitmap getRoundedCorners(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 5;
        
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        
        return output;
    }
	
	public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
	        
    public static Bitmap getBitmapFromString(String imgString) {
    	byte[] b = Base64.decode(imgString, Base64.DEFAULT);
    	return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
    
    public static boolean doCrop(Context context, Uri mImageCaptureUri, 
    		int outputX, int outputY, int aspectX, int aspectY, int requestCode) {
    	
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
		int size = list.size();
		
		if (size == 0) {
		    Toast.makeText(context, "Cannot find photo cropper", Toast.LENGTH_SHORT).show();
		    return false;
		}
		
		else {
		    intent.setData(mImageCaptureUri);
		    intent.putExtra("outputX", outputX);
		    intent.putExtra("outputY", outputY);
		    intent.putExtra("aspectX", aspectX);
		    intent.putExtra("aspectY", aspectY);
		    intent.putExtra("scale", true);
		    intent.putExtra("return-data", true);
		    
		    try {
		    	Intent i = new Intent(intent);
		        ResolveInfo res = list.get(0);
		        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        ((Activity) context).startActivityForResult(i, requestCode);
		        return true;
		     } catch (Exception e) {
		    	 Toast.makeText(context, "Problem with photo cropper", Toast.LENGTH_SHORT).show();
		    	 return false;
		     }
		}
    }
    
    public static boolean hasInternetAccess(Context context) {
    	  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	  NetworkInfo ni = cm.getActiveNetworkInfo();
    	  if (ni == null || !ni.isConnected()) return false; // There are no active/connected networks
    	  else return true;
    }
    
    static public class SendEmail extends AsyncTask<Void, Void, Boolean> {
    	String email;
    	String password;
    	String message;
    	String recipient_email;
    	public SendEmail(String email, String password, String message, String recipient_email) {
    		this.email = email;
    		this.password = password;
    		this.message = message;
    		this.recipient_email = recipient_email;
    	}

        @Override
        protected Boolean doInBackground(Void... voids) {
			try {
                GmailSender sender = new GmailSender(email, password);
                sender.sendMail("You have received a Hassle!", message, email, recipient_email);
                return true;
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
                return false;
            }
        }
    }
	
    static public class ComposeTweet extends AsyncTask<Void, Void, Boolean> {
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
