package co.tashawych.hassle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	// Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        
        String twitter_screen_name = getSharedPreferences("Hassle", 0).getString("twitter_screen_name", "");
        if (twitter_screen_name.equals("")) {
        	menu.add(0, R.id.menu_twitter, 0, this.getResources().getString(R.string.menu_twitter));
        }
        else {
        	menu.add(0, R.id.menu_twitter, 0, ("Disconnect " + twitter_screen_name + "?"));
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_twitter:
        	String twitter_screen_name = getSharedPreferences("Hassle", 0).getString("twitter_screen_name", "");
            if (twitter_screen_name.equals("")) {
            	Intent intent = new Intent(this, TwitterOAuth.class);
                this.startActivity(intent);
            }
            else {
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want disconnect your Twitter account?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	SharedPreferences.Editor editor = getSharedPreferences("Hassle", 0).edit();
        	            editor.putString("twitter_screen_name", "");
        	            editor.putString("twitter_cred_token", "");
        	            editor.putString("twitter_cred_token_secret", "");
        	            editor.commit();
        	            
                        dialog.dismiss();
                    }
                });
                
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                });
                
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            
            break;
        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }
    
}
