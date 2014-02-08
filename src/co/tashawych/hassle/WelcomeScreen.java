package co.tashawych.hassle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WelcomeScreen extends BaseActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);
	}
	
	public void create_new_contact(View view) {
		Intent add_contact = new Intent(this, NewContact.class);
		startActivity(add_contact);
	}
	
	public void add_phone_contact(View view) {
		Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
	}
	
	public void view_contacts(View view) {
		Intent view_contacts = new Intent(this, FragmentLayout.class);
		startActivity(view_contacts);
	}

}
