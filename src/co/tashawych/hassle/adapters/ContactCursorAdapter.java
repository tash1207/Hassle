package co.tashawych.hassle.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.tashawych.hassle.Hassle;
import co.tashawych.hassle.R;

public class ContactCursorAdapter extends SimpleCursorAdapter {
	Context context;
    int layout;
    Cursor c;
	
	public ContactCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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
			holder.name.setText(c.getString(c.getColumnIndex("name")));
			holder.phone.setText(c.getString(c.getColumnIndex("phone")));
			holder.email.setText(c.getString(c.getColumnIndex("email")));
			holder.twitter.setText("@" + c.getString(c.getColumnIndex("twitter")));
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
		
		return convertView;
	}

}
