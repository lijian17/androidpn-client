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
package org.androidpn.demoapp;

import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * androidpn使用
 * 
 * @author lijian
 * @date 2016-7-23 上午8:56:43
 */
public class DemoAppActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 设置
		Button okButton = (Button) findViewById(R.id.btn_settings);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ServiceManager.viewNotificationSettings(DemoAppActivity.this);
			}
		});

		// 启动服务
		ServiceManager serviceManager = new ServiceManager(this);
		serviceManager.setNotificationIcon(R.drawable.notification);
		serviceManager.startService();
		serviceManager.setAlias("IT");
	}

}