package co.tashawych.hassle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;

public class NewContact extends BaseActivity {
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
    
    public void photo_clicked(View v) {
        Intent uploadPic = new Intent(Intent.ACTION_PICK, 
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(uploadPic, "Upload photo using:"), UPLOAD_PIC);
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
    
}
