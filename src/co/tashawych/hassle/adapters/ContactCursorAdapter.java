package co.tashawych.hassle.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import co.tashawych.hassle.Hassle;
import co.tashawych.hassle.R;
import co.tashawych.hassle.db.DatabaseHelper;
import co.tashawych.hassle.misc.Utility;

public class ContactCursorAdapter extends SimpleCursorAdapter {
	Context context;
	int layout;
	Cursor c;

	public ContactCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.layout = layout;
		this.c = c;
	}

	static class ViewHolder {
		protected ImageView image;
		protected TextView name;
		protected TextView phone;
		protected TextView email;
		protected TextView twitter;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.lvw_contacts_image);
            holder.name = (TextView) convertView.findViewById(R.id.lvw_contacts_name);
            holder.phone = (TextView) convertView.findViewById(R.id.lvw_contacts_phone);
            holder.email = (TextView) convertView.findViewById(R.id.lvw_contacts_email);
            holder.twitter = (TextView) convertView.findViewById(R.id.lvw_contacts_twitter);

            convertView.setTag(holder);
		}
		
		else {
            holder = (ViewHolder) convertView.getTag();
        }
		
		if (c.moveToPosition(position)) {
			String picture = c.getString(c.getColumnIndex("picture"));
			if (picture != null) holder.image.setImageBitmap(Utility.getBitmapFromString(picture));
			else holder.image.setImageBitmap(null);
			
			holder.name.setText(c.getString(c.getColumnIndex("name")));
			holder.phone.setText(c.getString(c.getColumnIndex("phone")));
			
			if (c.getString(c.getColumnIndex("email")).equals("")) {
				holder.email.setVisibility(View.GONE);
			}
			else {
				holder.email.setVisibility(View.VISIBLE);
				holder.email.setText(c.getString(c.getColumnIndex("email")));
			}
			
			if (c.getString(c.getColumnIndex("twitter")).equals("")) {
				holder.twitter.setVisibility(View.GONE);
			}
			else {
				holder.twitter.setVisibility(View.VISIBLE);
				holder.twitter.setText("@" + c.getString(c.getColumnIndex("twitter")));
			}
		}
		
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				c.moveToPosition(position);
				Intent hassle = new Intent(context, Hassle.class);
				hassle.putExtra("contact_id", c.getInt(c.getColumnIndex("_id")));
				context.startActivity(hassle);
			}
		});
		
		// TODO instead of long click, swipe to remove from contacts?
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
				c.moveToPosition(position);
            	final Dialog dialog = new Dialog(context);
            	dialog.setContentView(R.layout.dialog_simple);
            	dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            	TextView text = (TextView) dialog.findViewById(R.id.dialog_text);

                // If item is not joined already, add to my schedule
                dialog.setTitle(context.getString(R.string.delete_contact));
                text.setText(String.format(context.getString(R.string.are_you_sure)));
                
                Button btn_yes = (Button) dialog.findViewById(R.id.dialog_yes);
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	DatabaseHelper.getHelper(context).deleteContact(c.getInt(c.getColumnIndex("_id")));
                        c = DatabaseHelper.getHelper(context).getContactsCursor();
                        swapCursor(c);
                        dialog.dismiss();
                    }
                });
                
                Button btn_no = (Button) dialog.findViewById(R.id.dialog_no);
                btn_no.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                
                dialog.show();
                
                return true;
            }
        });
		
		return convertView;
	}
}
