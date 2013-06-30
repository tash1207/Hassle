package co.tashawych.hassle;

import java.util.List;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.misc.Utility;

public class NewContact extends BaseActivity {
	boolean edit_mode = false;
	Contact contact;
	String imgString = "";
	
	ImageView picture;
	EditText name_edit, phone_edit, email_edit, twitter_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);
        
	    picture = (ImageView) findViewById(R.id.contact_photo);
        name_edit = (EditText) findViewById(R.id.name_edit);
    	phone_edit = (EditText) findViewById(R.id.phone_edit);
    	email_edit = (EditText) findViewById(R.id.email_edit);
    	twitter_edit = (EditText) findViewById(R.id.twitter_edit);
    	
        int id = getIntent().getIntExtra("id", 0);
        if (id != 0) {
        	edit_mode = true;
        	contact = DatabaseHelper.getHelper(this).getContact(id);
        	
        	picture.setImageBitmap(Utility.getBitmapFromString(contact.picture));
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
    	else contact = new Contact(name, imgString, phone, email, twitter);
    	
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
    
    @Override
	protected void onActivityResult(int request, int result, Intent data) {
    	// UPLOAD PIC
 		if (result == RESULT_OK && request == UPLOAD_PIC && data.getData() != null) {
 			Uri selectedImage = data.getData();
             
            // Check if user has photo cropper
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");
 		    
 		    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
 		    int size = list.size();
 		    
 		    // If not, upload image without cropping it
 		    if (size == 0) {
 		    	String[] filePathColumn = {MediaStore.Images.Media.DATA};
 		
 		        Cursor cursor = getContentResolver().query(
 		        		selectedImage, filePathColumn, null, null, null);
 		        cursor.moveToFirst();
 		
 		        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
 		        String filePath = cursor.getString(columnIndex);
 		        cursor.close();
 		
 		        Bitmap uploaded_photo = BitmapFactory.decodeFile(filePath);
                picture.setImageBitmap(uploaded_photo);
                imgString = Base64.encodeToString(Utility.getBitmapAsByteArray(uploaded_photo), Base64.DEFAULT);
                contact.picture = imgString;
 		    }
             
 		    else {
 		    	Utility.doCrop(this, selectedImage, 140, 140, 1, 1, AFTER_CROP);
 		    }
 		}
 		// AFTER CROP
        else if (result == RESULT_OK && request == AFTER_CROP) {
         	Bundle extras = data.getExtras();
             if (extras != null) {
             	Bitmap uploaded_photo = extras.getParcelable("data");
 	            picture.setImageBitmap(Utility.getRoundedCorners(uploaded_photo));
 	            imgString = Base64.encodeToString(Utility.getBitmapAsByteArray(uploaded_photo), Base64.DEFAULT);
                contact.picture = imgString;
             }
        }
	}
}
