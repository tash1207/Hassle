package co.tashawych.hassle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import co.tashawych.hassle.adapters.ContactCursorAdapter;
import co.tashawych.hassle.db.DatabaseHelper;

public class ContactList extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		
		ListView lvw_contacts = (ListView) findViewById(R.id.lvw_contacts);
		String[] columns = new String[] {
                "name", "phone"
        };
         
        int[] to = new int[] { 
                R.id.lvw_contacts_name, R.id.lvw_contacts_phone
        };
		ContactCursorAdapter adapter = new ContactCursorAdapter(this, R.layout.lvw_contacts, 
				DatabaseHelper.getHelper(this).getContactsCursor(), columns, to, 0);
		
		lvw_contacts.setAdapter(adapter);
	}
	
	public void add_contact(View v) {
		Intent add_contact = new Intent(this, NewContact.class);
		startActivity(add_contact);
	}

}
