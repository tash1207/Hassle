package co.tashawych.hassle.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import co.tashawych.hassle.R;
import co.tashawych.hassle.datatypes.Contact;

public class StableArrayAdapter extends ArrayAdapter<Contact> {
	
	Context context;
	int layout;
	List<Contact> contacts;
	HashMap<Contact, Integer> mIdMap = new HashMap<Contact, Integer>();
	View.OnTouchListener mTouchListener;
	
	public StableArrayAdapter(Context context, int layoutResourceId, int textViewResourceId, 
			List<Contact> objects, View.OnTouchListener listener) {
		super(context, layoutResourceId, textViewResourceId, objects);
		this.context = context;
		this.layout = layoutResourceId;
		contacts = objects;
		
		mTouchListener = listener;
		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
		}
	}
	
	@Override
	public long getItemId(int position) {
		Contact item = getItem(position);
		return mIdMap.get(item);
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//View view = super.getView(position, convertView, parent);
		View view = View.inflate(context, layout, null);
		if (view != convertView) {
			// Add touch listener to every new view to track swipe motion
			view.setOnTouchListener(mTouchListener);
		}
		
		TextView name = (TextView) view.findViewById(R.id.lvw_contacts_name);
        TextView phone = (TextView) view.findViewById(R.id.lvw_contacts_phone);
        TextView email = (TextView) view.findViewById(R.id.lvw_contacts_email);
        TextView twitter = (TextView) view.findViewById(R.id.lvw_contacts_twitter);
        
        name.setText(contacts.get(position).name);
        phone.setText(contacts.get(position).phone);
        email.setText(contacts.get(position).email);
        twitter.setText(contacts.get(position).twitter);

		return view;
	}

}
