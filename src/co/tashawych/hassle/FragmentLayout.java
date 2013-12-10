package co.tashawych.hassle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FragmentLayout extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layout);
	}
	
	public void add_contact(View v) {
		Intent add_contact = new Intent(this, NewContact.class);
		startActivity(add_contact);
	}

}
