package org.androidpn.client;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.demoapp.R;
import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 通知历史
 * 
 * @author lijian
 * @date 2016-12-11 下午7:37:26
 */
public class NotificationHistoryActivity extends Activity {

	private ListView mListView;

	private NotificationHistoryAdapter mAdapter;

	private List<NotificationHistory> mList = new ArrayList<NotificationHistory>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_history);
		mList = DataSupport.findAll(NotificationHistory.class);
		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NotificationHistory history = mList.get(position);
				Intent intent = new Intent(NotificationHistoryActivity.this,
						NotificationDetailsActivity.class);
				// intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
				intent.putExtra(Constants.NOTIFICATION_API_KEY,
						history.getApiKey());
				intent.putExtra(Constants.NOTIFICATION_TITLE,
						history.getTitle());
				intent.putExtra(Constants.NOTIFICATION_MESSAGE,
						history.getMessage());
				intent.putExtra(Constants.NOTIFICATION_URI, history.getUri());
				intent.putExtra(Constants.NOTIFICATION_IMAGE_URL,
						history.getImageUrl());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mAdapter = new NotificationHistoryAdapter(this, 0, mList);
		mListView.setAdapter(mAdapter);
		registerForContextMenu(mListView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 0, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
					.getMenuInfo();
			int position = menuInfo.position;
			NotificationHistory history = mList.get(position);
			history.delete();
			mList.remove(position);
			mAdapter.notifyDataSetChanged();
		}
		return super.onContextItemSelected(item);
	}

	class NotificationHistoryAdapter extends ArrayAdapter<NotificationHistory> {

		public NotificationHistoryAdapter(Context context, int resource,
				List<NotificationHistory> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NotificationHistory history = getItem(position);
			View view;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(
						R.layout.notification_history_item, null);
			} else {
				view = convertView;
			}
			TextView titleTextView = (TextView) view
					.findViewById(R.id.tv_title);
			TextView timeTextView = (TextView) view.findViewById(R.id.tv_time);
			titleTextView.setText(history.getTitle());
			timeTextView.setText(history.getTime());
			return view;
		}
	}
}
