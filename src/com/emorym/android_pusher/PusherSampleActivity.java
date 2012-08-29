package com.emorym.android_pusher;

/*	Copyright (C) 2011 Emory Myers
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  Contributors: Martin Linkhorst
 */

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PusherSampleActivity extends Activity {
	/* the log tag constant */
	private static final String LOG_TAG = "Pusher";

	private static final String PUSHER_APP_KEY = "0ad923c1f1da378fe30b";
	private static final String PUSHER_APP_SECRET = "5d4a3aa26a02cee66ef7";

	private static final String PUBLIC_CHANNEL = "test_channel";
	private static final String PRIVATE_CHANNEL = "test_channel";

	private Pusher mPusher;

	private EditText eventNameField;
	private EditText eventDataField;
	private EditText channelNameField;

	private Button sendButton;
	private ToggleButton togglePublicChannelButton;
	private ToggleButton togglePrivateChannelButton;

	/* lifecycle callbacks */

	/* create/destroy lifecycle loop */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPusher = new Pusher(PUSHER_APP_KEY, PUSHER_APP_SECRET);
		mPusher.bindAll(new PusherCallback() {
			@Override
			public void onEvent(String eventName, JSONObject eventData, String channelName) {
				Toast.makeText(PusherSampleActivity.this,
							   "Received\nEvent: " + eventName + "\nChannel: " + channelName + "\nData: " + eventData.toString(),
							   Toast.LENGTH_LONG).show();
				
				Log.d(LOG_TAG, "Received " + eventData.toString() + " for event '" + eventName + "' on channel '" + channelName + "'.");
			}
		});

		ConnectivityManager cm = (ConnectivityManager) getSystemService(PusherSampleActivity.this.CONNECTIVITY_SERVICE);
	    if (cm.getActiveNetworkInfo().isConnectedOrConnecting())
	    {
	    	Toast.makeText(PusherSampleActivity.this, "help",Toast.LENGTH_LONG).show();
	    }
	    else
	    {
	    	Toast.makeText(PusherSampleActivity.this, "help fail",Toast.LENGTH_LONG).show();
	    }
	    Toast.makeText(PusherSampleActivity.this, "help",Toast.LENGTH_LONG).show();
	    	/* setup the text fields */
		eventNameField = (EditText) findViewById(R.id.event_name);
		eventDataField = (EditText) findViewById(R.id.event_data);
		

		channelNameField = (EditText) findViewById(R.id.channel_name);

		/* Setup some toggles to subscribe/unsubscribe from our channel */
		togglePublicChannelButton = (ToggleButton) findViewById(R.id.toggleButton1);
		togglePrivateChannelButton = (ToggleButton) findViewById(R.id.toggleButton2);
		sendButton = (Button) findViewById(R.id.send_button);

		/* setup the button actions */
		togglePublicChannelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (togglePublicChannelButton.isChecked()) {
					mPusher.subscribe(PUBLIC_CHANNEL);
				} else {
					mPusher.unsubscribe(PUBLIC_CHANNEL);
				}
			}
		});

		togglePrivateChannelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (togglePrivateChannelButton.isChecked()) {
					mPusher.subscribe(PRIVATE_CHANNEL);
					mPusher.mLocalChannels.get(PUBLIC_CHANNEL).bindAll(new PusherCallback() {
						@Override
						public void onEvent(String eventName, JSONObject eventData, String channelName) {
							Toast.makeText(PusherSampleActivity.this,
										   "Received\nEvent: " + eventName + "\nChannel: " + channelName + "\nData: " + eventData.toString(),
										   Toast.LENGTH_LONG).show();
							
							Log.d(LOG_TAG, "Received " + eventData.toString() + " for event '" + eventName + "' on channel '" + channelName + "'.");
						}
					});
				} else {
					mPusher.unsubscribe(PRIVATE_CHANNEL);
				}
			}
		});

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String eventName = eventNameField.getText().toString();
					String channelName = channelNameField.getText().toString();
					JSONObject eventData = new JSONObject(eventDataField.getText().toString());
					mPusher.sendEvent(eventName, eventData, channelName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mPusher.connect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(LOG_TAG, "onDestroy");
		mPusher.disconnect();
	}
}