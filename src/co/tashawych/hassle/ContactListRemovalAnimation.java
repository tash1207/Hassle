package co.tashawych.hassle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import co.tashawych.hassle.adapters.StableArrayAdapter;
import co.tashawych.hassle.datatypes.Contact;

public class ContactListRemovalAnimation extends Activity {
	
	final ArrayList<View> mCheckedViews = new ArrayList<View>();
	StableArrayAdapter mAdapter;
	ListView mListView;
	BackgroundContainer mBackgroundContainer;
	boolean mSwiping = false;
	final HashMap<View, Integer> mTopMap = new HashMap<View, Integer>();
	
	private static final int SWIPE_DURATION = 250;
	private static final int MOVE_DURATION = 150;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		
		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.lvw_contacts_image); //TODO
		mListView = (ListView) findViewById(R.id.lvw_contacts);
		
		Contact contact1 = new Contact("Andrew Kennedy", "", "9548297565", "andrew@feathr.co", "L1fescape");
		Contact contact2 = new Contact("Tasha Hankewych", "", "9548297565", "tasha@feathr.co", "tashawych");
		Contact contact3 = new Contact("Tommy Goode", "", "9548297565", "tasha@feathr.co", "tashawych");
		Contact contact4 = new Contact("Tasha Thankewych", "", "9548297565", "tasha@feathr.co", "tashawych");
		Contact contact5 = new Contact("Tasha Tashawych", "", "9548297565", "tasha@feathr.co", "tashawych");

		ArrayList<Contact> contacts = new ArrayList<Contact>();
		contacts.add(contact1);
		contacts.add(contact2);
		contacts.add(contact3);
		contacts.add(contact4);
		contacts.add(contact5);

		mAdapter = new StableArrayAdapter(this, R.layout.lvw_contacts, R.id.lvw_contacts_name, 
				contacts, mTouchListener);
		mListView.setAdapter(mAdapter);
	}
	
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		float mDownX;
		private int mSwipeSlop = -1;
		
		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(ContactListRemovalAnimation.this).getScaledTouchSlop();
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mSwiping) {
					return false;
				}
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setAlpha(1);
				v.setTranslationX(0);
				break;
			case MotionEvent.ACTION_MOVE:
				{
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);
					if (!mSwiping) {
						if (deltaXAbs > mSwipeSlop) {
							mSwiping = true;
							mListView.requestDisallowInterceptTouchEvent(true);
							mBackgroundContainer.showBackground(v.getTop(),  v.getHeight());
						}
					}
					if (mSwiping) {
						v.setTranslationX(x - mDownX);
						v.setAlpha(1 - deltaXAbs / v.getWidth());
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			{
				// User let go - figure out whether to animate the view out, or back into place
				if (mSwiping) {
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);
					float fractionCovered;
					float endX;
					float endAlpha;
					final boolean remove;
					if (deltaXAbs > v.getWidth() / 4) {
						// Greater than a quarter of the width - animate it out
						fractionCovered = deltaXAbs / v.getWidth();
						endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
						endAlpha = 0;
						remove = true;
					}
					else {
						// Not far enough - animate it back
						fractionCovered = 1 - (deltaXAbs / v.getWidth());
						endX = 0;
						endAlpha = 1;
						remove = false;
					}
					// Animate position and alpha
					long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
					v.animate().setDuration(duration).alpha(endAlpha).translationX(endX)
						.withEndAction(new Runnable() {
							
							@Override
							public void run() {
								// Restore animated values
								v.setAlpha(1);
								v.setTranslationX(0);
								if (remove) {
									// Delete the item from the adapter
									int position = mListView.getPositionForView(v);
									mAdapter.remove(mAdapter.getItem(position));
									// Animate everything else into place
									animateOtherViews(mListView, v);
								}
								else {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
								}
							}
						});
				}
			}
			break;
			default:
				return false;
			}
			return false;
		}
	};
	
	/**
	 * This method animates all other views in the ListView container into their final positions
	 * @param listview
	 * @param ignoreView
	 */
	private void animateOtherViews(final ListView listview, View ignoreView) {
		for (int i = 0; i < listview.getChildCount(); ++i) {
			View child = listview.getChildAt(i);
			if (child != ignoreView) {
				mTopMap.put(child, child.getTop());
				// Use a transient state to avoid recycling during upcoming layout
				child.setHasTransientState(true);
			}
		}
		final ViewTreeObserver observer = listview.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				observer.removeOnPreDrawListener(this);
				boolean firstAnimation = true;
				for (int i = 0; i < listview.getChildCount(); ++i) {
					final View child = listview.getChildAt(i);
					Integer startTop = mTopMap.get(child);
					if (startTop != null) {
						int top = child.getTop();
						if (startTop != top) {
							int delta = startTop - top;
							child.setTranslationY(delta);
							child.animate().setDuration(MOVE_DURATION).translationY(0);
							if (firstAnimation) {
								child.animate().withEndAction(new Runnable() {
									
									@Override
									public void run() {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
									}
								});
								firstAnimation = false;
							}
						}
					}
					else {
						// Animate new views along with the others
						int top = child.getTop();
						View prevChild;
						if (i > 0) {
							// entering from the bottom
							prevChild = listview.getChildAt(i - 1);
							int prevChildStartTop = mTopMap.get(prevChild);
							startTop = prevChildStartTop + prevChild.getHeight() + listview.getDividerHeight();
						}
						else {
							// entering from the top
							prevChild = listview.getChildAt(i + 1);
							int prevChildStartTop = mTopMap.get(prevChild);
							startTop = prevChildStartTop - listview.getDividerHeight() - child.getHeight();
						}
						int delta = startTop - top;
						child.setTranslationY(delta);
						child.animate().setDuration(MOVE_DURATION).translationY(0);
						if (firstAnimation) {
							child.animate().withEndAction(new Runnable() {
								
								@Override
								public void run() {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
								}
							});
							firstAnimation = false;
						}
					}
				}
				for (View view : mTopMap.keySet()) {
					view.setHasTransientState(false);
				}
				mTopMap.clear();
				return true;
			}
		});
	}
	
	public void add_contact(View v) {
		Intent add_contact = new Intent(this, NewContact.class);
		startActivity(add_contact);
	}
}
