package co.tashawych.hassle;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import co.tashawych.hassle.datatypes.Contact;
import co.tashawych.hassle.db.DatabaseHelper;

public class PhoneContactsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    
	@SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
			Phone.DISPLAY_NAME,
			Phone.NUMBER
    };
    private final static int[] TO_IDS = {
        	R.id.phone_contacts_name,
        	R.id.phone_contacts_no
    };
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
    		Phone._ID, 
    		Phone.DISPLAY_NAME,
    		Phone.NUMBER
    };
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Phone.DISPLAY_NAME + " NOT LIKE ?";
            
    private String mSearchString;
    private String[] mSelectionArgs = { mSearchString };
    
    private final String sort_order = Phone.DISPLAY_NAME + " ASC";
    
    ListView mContactsList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private SimpleCursorAdapter mCursorAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_contacts, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mContactsList = (ListView) getActivity().findViewById(R.id.phone_contacts_lvw);        
        mContactsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
		        Cursor cursor = mCursorAdapter.getCursor();
		        cursor.moveToPosition(position);
		        
		        String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
		        String phone = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));

		        Contact contact = new Contact(name, "", phone, "", "");
		        DatabaseHelper.getHelper(getActivity()).updateContact(contact);
		        
		        Toast.makeText(getActivity(), name + " has been added to your Hassle contacts", Toast.LENGTH_SHORT).show();
			}
		});
        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.phone_contacts_list_item, null,
                FROM_COLUMNS, TO_IDS, 0);
        mContactsList.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
		mSearchString = "#";
		mSelectionArgs[0] = mSearchString + "%";
        // Starts the query		
        return new CursorLoader(getActivity(), Phone.CONTENT_URI, PROJECTION, SELECTION, mSelectionArgs, sort_order);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mCursorAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorAdapter.swapCursor(null);
	}

}
