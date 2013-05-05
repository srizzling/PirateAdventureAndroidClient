package com.example.pirateadventure;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.savagelook.android.UrlJsonAsyncTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static String TASKS_URL = "http://10.0.2.2:3000/api/v1/tasks.json?auth_token=";
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

		findViewById(R.id.logoutButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SharedPreferences.Editor editor = mPreferences.edit();
						editor.remove("AuthToken");
						editor.commit();
						onResume();
					}
				});


	}

	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch(item.getItemId()){
		case R.id.action_settings:
			logout();
			break;            

		}
		return true;
	}

	private void logout() {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.remove("AuthToken");
		editor.commit();
		onResume();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void onResume() {
		super.onResume();

		if (mPreferences.contains("AuthToken")) {
			Log.d("sfd",mPreferences.getString("AuthToken", ""));
			Log.d("sfd",mPreferences.getString("Username", ""));
			String url =TASKS_URL+mPreferences.getString("AuthToken", "");
			Log.d("sfd",TASKS_URL);
			loadTasksFromAPI(url);
			Log.d("SUCK","df");
		} else {
			Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	private void loadTasksFromAPI(String url) {
		GetTasksTask getTasksTask = new GetTasksTask(MainActivity.this);
		getTasksTask.setMessageLoading("Loading tasks...");
		Log.d("processing","dsf");
		getTasksTask.execute(url);

	}

	private class GetTasksTask extends UrlJsonAsyncTask {
		public GetTasksTask(Context context) {
			super(context);
		}

		protected void onPostExecute(JSONObject json) {
			try {
				Log.d(json.toString(),"JSON");
				JSONArray jsonTasks = json.getJSONObject("data").getJSONArray("tasks");
				int length = jsonTasks.length();
				List<String> tasksTitles = new ArrayList<String>(length);

				for (int i = 0; i < length; i++) {
					tasksTitles.add(jsonTasks.getJSONObject(i).getString("title"));
				}

				ListView tasksListView = (ListView) findViewById (R.id.tasks_list_view);
				if (tasksListView != null) {
					tasksListView.setAdapter((ListAdapter) new ArrayAdapter<String>(MainActivity.this,
							android.R.layout.simple_list_item_1, tasksTitles));
				}
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}


}