/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 用于终端接收到的通知详细视图显示
 * 
 * @author lijian
 * @date 2016-7-24 上午9:47:04
 */
public class NotificationDetailsActivity extends Activity {
	private static final String TAG = "NotificationDetailsActivity";

	private String callbackActivityPackageName;

	private String callbackActivityClassName;

	/** 网络请求队列 */
	private RequestQueue mQueue;

	public NotificationDetailsActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPrefs = this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		callbackActivityPackageName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
		callbackActivityClassName = sharedPrefs.getString(
				Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
		
		mQueue = Volley.newRequestQueue(this);

		Intent intent = getIntent();
		String notificationId = intent
				.getStringExtra(Constants.NOTIFICATION_ID);
		String notificationApiKey = intent
				.getStringExtra(Constants.NOTIFICATION_API_KEY);
		String notificationTitle = intent
				.getStringExtra(Constants.NOTIFICATION_TITLE);
		String notificationMessage = intent
				.getStringExtra(Constants.NOTIFICATION_MESSAGE);
		String notificationUri = intent
				.getStringExtra(Constants.NOTIFICATION_URI);
		String notificationImageUrl = intent
				.getStringExtra(Constants.NOTIFICATION_IMAGE_URL);

		L.d(TAG, "notificationId=" + notificationId);
		L.d(TAG, "notificationApiKey=" + notificationApiKey);
		L.d(TAG, "notificationTitle=" + notificationTitle);
		L.d(TAG, "notificationMessage=" + notificationMessage);
		L.d(TAG, "notificationUri=" + notificationUri);
		L.d(TAG, "notificationImageUrl=" + notificationImageUrl);

		View rootView = createView(notificationTitle, notificationMessage,
				notificationUri, notificationImageUrl);
		setContentView(rootView);
	}

	private View createView(final String title, final String message,
			final String uri, final String imageUrl) {

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setBackgroundColor(0xffeeeeee);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(5, 5, 5, 5);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(layoutParams);

		TextView textTitle = new TextView(this);
		textTitle.setText(title);
		textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		textTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		textTitle.setTextColor(0xff000000);
		textTitle.setGravity(Gravity.CENTER);

		layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(30, 30, 30, 0);
		textTitle.setLayoutParams(layoutParams);
		linearLayout.addView(textTitle);

		TextView textDetails = new TextView(this);
		textDetails.setText(message);
		textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		textDetails.setTextColor(0xff333333);
		textDetails.setGravity(Gravity.CENTER);

		layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(30, 10, 30, 20);
		textDetails.setLayoutParams(layoutParams);
		linearLayout.addView(textDetails);

		Button okButton = new Button(this);
		okButton.setText("Ok");
		okButton.setWidth(100);

		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent;
				if (uri != null
						&& uri.length() > 0
						&& (uri.startsWith("http:") || uri.startsWith("https:")
								|| uri.startsWith("tel:") || uri
									.startsWith("geo:"))) {
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				} else {
					intent = new Intent().setClassName(
							callbackActivityPackageName,
							callbackActivityClassName);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
					// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				}

				NotificationDetailsActivity.this.startActivity(intent);
				NotificationDetailsActivity.this.finish();
			}
		});

		LinearLayout innerLayout = new LinearLayout(this);
		innerLayout.setGravity(Gravity.CENTER);
		innerLayout.addView(okButton);
		
		linearLayout.addView(innerLayout);

		NetworkImageView imageView = new NetworkImageView(this);
		layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(layoutParams);
		linearLayout.addView(imageView);
		ImageLoader imageLoader = new ImageLoader(mQueue, new ImageCache() {
			
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}
			
			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
		});
		imageView.setImageUrl(imageUrl, imageLoader);

		return linearLayout;
	}

	// protected void onPause() {
	// super.onPause();
	// finish();
	// }
	//
	// protected void onStop() {
	// super.onStop();
	// finish();
	// }
	//
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// }
	//
	// protected void onNewIntent(Intent intent) {
	// setIntent(intent);
	// }

}
