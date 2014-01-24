package co.tashawych.hassle;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.misc.Utility;

public class HassleFragment extends Fragment {
	boolean isNull;
	Contact contact;
	ImageView picture;
	TextView name;
	EditText hassle_edit;
	
	SharedPreferences prefs;
	
	boolean text_on = true;
	boolean email_on = true;
	boolean twitter_on = true;

    /**
     * Create a new instance of HassleFragment, initialized to
     * show the info for 'contact_id'.
     */
    public static HassleFragment newInstance(int contact_id) {
        HassleFragment f = new HassleFragment();

        // Supply contact_id input as an argument.
        Bundle args = new Bundle();
        args.putInt("contact_id", contact_id);
        f.setArguments(args);

        return f;
    }
    
    public int getShownContactId() {
        return getArguments().getInt("contact_id", 0);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			isNull = true;
			return null;
		}
		else return inflater.inflate(R.layout.hassle, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		prefs = getActivity().getSharedPreferences("Hassle", 0);
		if (!isNull) new init_page().execute();
	}
	
	private class init_page extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			contact = DatabaseHelper.getHelper(getActivity()).getContact(getShownContactId());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void voids) {
			if (contact != null) {
				picture = (ImageView) getActivity().findViewById(R.id.hassle_picture);
				name = (TextView) getActivity().findViewById(R.id.hassle_name);
				hassle_edit = (EditText) getActivity().findViewById(R.id.hassle_edit);	
				
				name.setText(contact.name);
				if (contact.picture != null) picture.setImageBitmap(Utility.getBitmapFromString(contact.picture));
				hassle_edit.setHint("What do you want to Hassle " + contact.name + " about?");
			}
			return;
		}
		
	}
}
