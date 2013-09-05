package co.tashawych.hassle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pushbots.push.Pushbots;

public class PushReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// Handle Push Message when opened
		if (action.equals(Pushbots.MSG_OPENED)) {
			Intent launch = new Intent(Intent.ACTION_MAIN);
			launch.setClass(Pushbots.getInstance().appContext, ContactList.class);
			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Pushbots.getInstance().appContext.startActivity(launch);
		}
	}

}
