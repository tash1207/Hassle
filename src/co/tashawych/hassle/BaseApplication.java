package co.tashawych.hassle;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// PUSHBOTS
		/*
		try {
			Pushbots.init(this, getString(R.string.SENDER_ID), getString(R.string.PUSHBOTS_APPLICATION_ID));
			Pushbots.getInstance().setMsgReceiver(PushReceiver.class);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		*/

	}
}
