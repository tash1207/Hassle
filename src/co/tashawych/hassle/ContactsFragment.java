package co.tashawych.hassle;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import co.tashawych.hassle.adapters.ContactCursorAdapter;
import co.tashawych.hassle.db.DatabaseHelper;

public class ContactsFragment extends Fragment {
	boolean mDualPane;
	int mListPosition;
	
	ListView lvw_contacts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contact_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Populate list with hassle contacts
		lvw_contacts = (ListView) getActivity().findViewById(R.id.lvw_contacts);
		String[] columns = new String[] {
                "name", "phone"
        };
         
        int[] to = new int[] { 
                R.id.lvw_contacts_name, R.id.lvw_contacts_phone
        };
		ContactCursorAdapter adapter = new ContactCursorAdapter(getActivity(), R.layout.lvw_contacts, 
				DatabaseHelper.getHelper(getActivity()).getContactsCursor(), columns, to, 0);
		
		lvw_contacts.setAdapter(adapter);

		// Check to see if we have a frame in which to embed the hassle
		// fragment directly in the containing UI.
		View hassleFrame = getActivity().findViewById(R.id.hassle);
		mDualPane = hassleFrame != null && hassleFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mListPosition = savedInstanceState.getInt("listPosition", 0);
		}

		if (mDualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			lvw_contacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// Make sure our UI is in the correct state.
			showDetails(mListPosition);
		}
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("listPosition", mListPosition);
    }
    
    void showDetails(int index) {
        mListPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            lvw_contacts.setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            HassleFragment hassle = (HassleFragment) getFragmentManager().findFragmentById(R.id.hassle);
            if (hassle == null || hassle.getShownContactId() != index) {
                // Make new fragment to show this selection.
                hassle = HassleFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                if (index == 0) {
                    ft.replace(R.id.hassle, hassle);
//                }
//                else {
//                    ft.replace(R.id.a_item, hassle);
//                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), Hassle.class);
            intent.putExtra("contact_id", index);
            startActivity(intent);
        }
    }
}
