package co.tashawych.hassle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
		Intent add_phone_contact = new Intent(this, PhoneContactsList.class);
		startActivity(add_phone_contact);
	}
	
	public void view_contacts(View view) {
		Intent view_contacts = new Intent(this, FragmentLayout.class);
		startActivity(view_contacts);
	}

}
