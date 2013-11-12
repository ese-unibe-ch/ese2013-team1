package ch.unibe.sport.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Simple receiver class which helps sending messages and
 * commands or data as key:value set to activity that is specified
 * by activity tag
 * 
 * @version 1.0 2013-08-15
 * @author Aliaksei Syrel
 */
public class BroadcastSender {
	
	public static void send(Context context, String activityName, String[] commands, String[] params){
		assert context != null;
		assert activityName != null;
		assert activityName.length() > 0;
		assert commands.length > 0;
		assert commands.length == params.length;
		
		Intent intent = new Intent(activityName);
		for (int i = 0; i < commands.length; i++){
			intent.putExtra(commands[i],params[i]);
		}
        context.sendBroadcast(intent);
	}
}
