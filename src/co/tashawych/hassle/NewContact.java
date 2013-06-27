package co.tashawych.hassle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.social.TwitterOAuth;

public class NewContact extends Activity {
	boolean edit_mode = false;
	Contact contact;
	
	EditText name_edit, phone_edit, email_edit, twitter_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);
        
        name_edit = (EditText) findViewById(R.id.name_edit);
    	phone_edit = (EditText) findViewById(R.id.phone_edit);
    	email_edit = (EditText) findViewById(R.id.email_edit);
    	twitter_edit = (EditText) findViewById(R.id.twitter_edit);
    	
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
        	edit_mode = true;
        	contact = DatabaseHelper.getHelper(this).getContact(id);
        	
        	name_edit.setText(contact.name);
        	phone_edit.setText(contact.phone);
        	email_edit.setText(contact.email);
        	twitter_edit.setText(contact.twitter);
        	
        	((Button) findViewById(R.id.btn_create)).setText("Edit Contact");
        }
    }
    
    public void create_clicked(View view) {
    	String name = name_edit.getText().toString();
    	String phone = phone_edit.getText().toString();
    	String email = email_edit.getText().toString();
    	String twitter = twitter_edit.getText().toString();
    	
    	if (edit_mode) {
    		contact.name = name;
    		contact.phone = phone;
    		contact.email = email;
    		contact.twitter = twitter;
    	}
    	else contact = new Contact(name, "", phone, email, twitter);
    	
    	if (name.equals("")) {
    		Toast.makeText(this, "Please enter a name for this contact", Toast.LENGTH_SHORT).show();
    	}
    	else {
    		DatabaseHelper.getHelper(this).updateContact(contact);
    		
    		if (edit_mode) Toast.makeText(this, "Contact Updated!", Toast.LENGTH_SHORT).show();
    		else Toast.makeText(this, "Contact Created!", Toast.LENGTH_SHORT).show();
    		
    		finish();
    	}
    }
    /*
    public void test_clicked(View view) {
    	DatabaseHelper.getHelper(this).getAllContacts();
    }
    */

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
