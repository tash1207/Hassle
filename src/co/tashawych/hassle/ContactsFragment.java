package co.tashawych.hassle;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import co.tashawych.hassle.adapters.ContactCursorAdapter;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.social.TwitterOAuth;

public class ContactsFragment extends Fragment {
	boolean mDualPane;
	int mListPosition;
	int mContactId;
	
	SharedPreferences prefs;
	protected boolean has_twitter, has_email;
	
	ImageButton btn_add_twitter, btn_add_email;
	
	ListView lvw_contacts;
	String[] columns;
	int[] to;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
        btn_add_twitter = (ImageButton) getActivity().findViewById(R.id.btn_add_twitter);
        btn_add_email = (ImageButton) getActivity().findViewById(R.id.btn_add_email);

		// Populate list with hassle contacts
		lvw_contacts = (ListView) getActivity().findViewById(R.id.lvw_contacts);
		columns = new String[] {"name", "phone"};
        to = new int[] {R.id.lvw_contacts_name, R.id.lvw_contacts_phone};

		// Check to see if we have a frame in which to embed the hassle
		// fragment directly in the containing UI.
		View hassleFrame = getActivity().findViewById(R.id.hassle);
		mDualPane = hassleFrame != null && hassleFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mListPosition = savedInstanceState.getInt("listPosition", 0);
			mContactId = savedInstanceState.getInt("contactId", 0);
		}

		if (mDualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			lvw_contacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// Make sure our UI is in the correct state.
			showDetails(mListPosition, mContactId);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		prefs = getActivity().getSharedPreferences("Hassle", 0);
		
		String twitter = prefs.getString("twitter_screen_name", "");
        String email = prefs.getString("email", "");
        
        has_twitter = (twitter.equals("")) ? false : true;
        has_email = (email.equals("")) ? false : true;
        
		refresh_icon_drawables();
		
		ContactCursorAdapter adapter = new ContactCursorAdapter(getActivity(), R.layout.lvw_contacts, 
				DatabaseHelper.getHelper(getActivity()).getContactsCursor(), columns, to, 0);
		
		lvw_contacts.setAdapter(adapter);
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("listPosition", mListPosition);
        outState.putInt("contactId", mContactId);
    }
    
    public void refresh_icon_drawables() {
        // Set Twitter and Email buttons to reflect state of user authentication        
        int twitter_img = (has_twitter) ? R.drawable.twitter_active : R.drawable.twitter_inactive;
        int email_img = (has_email) ? R.drawable.email_active : R.drawable.email_inactive;

        btn_add_twitter.setImageResource(twitter_img);
        btn_add_email.setImageResource(email_img);
    }
    
    public void add_twitter() {
    	if (!has_twitter) {
        	Intent add_twitter = new Intent(getActivity(), TwitterOAuth.class);
            this.startActivity(add_twitter);
        }
        else {
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want disconnect your Twitter account?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	SharedPreferences.Editor editor = getActivity().getSharedPreferences("Hassle", 0).edit();
    	            editor.putString("twitter_screen_name", "");
    	            editor.putString("twitter_cred_token", "");
    	            editor.putString("twitter_cred_token_secret", "");
    	            editor.commit();
    	            
    	            // Change twitter icon to greyed out
    	            has_twitter = false;
    	            btn_add_twitter.setImageResource(R.drawable.twitter_inactive);
    	            
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
    }
    
    public void showDetails(int index, int contact_id) {
        mListPosition = index;
        mContactId = contact_id;
        ((FragmentLayout) getActivity()).setContactId(contact_id);
        ((FragmentLayout) getActivity()).email_on = true;
        ((FragmentLayout) getActivity()).text_on = true;
        ((FragmentLayout) getActivity()).twitter_on = true;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            lvw_contacts.setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            HassleFragment hassle = (HassleFragment) getFragmentManager().findFragmentById(R.id.hassle);
            if (hassle == null || hassle.getShownContactId() != contact_id) {
                // Make new fragment to show this selection.
                hassle = HassleFragment.newInstance(contact_id);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.hassle, hassle);
                // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), Hassle.class);
            intent.putExtra("contact_id", contact_id);
            startActivity(intent);
        }
    }
}
