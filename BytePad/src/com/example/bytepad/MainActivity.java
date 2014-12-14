package com.example.bytepad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	Spinner spinner;
	Button search;
	EditText find;
	String array[] = new String[100];
	String paper_url[] = new String[100];
	String paper_title[] = new String[100];
	String searchText;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	String fetchedData;
	int item_selected_position = 0, j, k = 0, click_status = 0;
	JSONObject obj;
	ListView list;
	String data, url;
	ImageView image;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void setListAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setListAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		search = (Button) findViewById(R.id.sear);
		spinner = (Spinner) findViewById(R.id.spin);
		find = (EditText) findViewById(R.id.search);
		list = (ListView) findViewById(android.R.id.list);
		searchText = find.getText().toString();
		image = (ImageView) findViewById(R.id.image);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.category,
				R.layout.support_simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				item_selected_position = pos;
				switch (pos) {
				case 0:
					url = "http://silive.in/bytepad/rest/api/paper/getallpapers?query=rg";
					break;
				case 1:
					url = "http://silive.in/bytepad/rest/api/paper/getcategorypapers?category=put&query=rg";
					break;
				case 2:
					url = "http://silive.in/bytepad/rest/api/paper/getcategorypapers?category=st1&query=rg";
					break;
				case 3:
					url = "http://silive.in/bytepad/rest/api/paper/getcategorypapers?category=st2&query=rg";
					break;
				case 4:
					url = "http://silive.in/bytepad/rest/api/paper/getcategorypapers?category=ut&query=rg";
					break;
				}

				if (click_status == 1)
					try {
						clicked(view);
					} catch (HttpRetryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	@SuppressWarnings("deprecation")
	public void clicked(View view) throws HttpRetryException {

		image.setVisibility(4);
		image.setImageResource(R.drawable.notfound);
		click_status = 1;
		try {
			j = 0;
			k = 0;
			if (connectivity() == false) {
				Log.d("error", "connectivity");
				AlertDialog alertDialog = new AlertDialog.Builder(this)
						.create();
				alertDialog.setTitle("Not Connected To Internet");
				alertDialog.setMessage("Internet connection is required");
				alertDialog.setIcon(R.drawable.error);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				alertDialog.show();
			} else
				new getData().execute(url);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Log log", "Could not create object" + e.toString());
		}
	}

	public boolean connectivity() {
		Log.e("Log log", "Connectivity()");
		ConnectivityManager conn = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo dataPack = conn
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi != null && dataPack != null) {
			if (wifi.isConnectedOrConnecting() && wifi.isAvailable())
				return true;
			else if (dataPack.isConnectedOrConnecting()
					&& dataPack.isAvailable())
				return true;
			else
				return false;

		} else
			return false;
	}

	public void StartDownload(int pos) {
		j = pos;
		int i = 0;
		String s = "";
		while (i < paper_url[j].length()) {
			if (paper_url[j].charAt(i) == ' ')
				s += "%20";
			else
				s += paper_url[j].charAt(i);
			i++;
		}
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(s));
		Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show();
		request.setTitle("Downloading");
		request.setDescription("File is being downloaded");
		Log.d("Downloading", "File is being downloaded");
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, paper_title[j]);
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
		Log.d("Finished", "Download Finished");
		Toast.makeText(this, "Download Finished", Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("DefaultLocale")
	public class getData extends AsyncTask<String, Integer, String> {
		// ArrayList<String> items = new ArrayList<String>();
		// ArrayAdapter<String> itemsView;
		List<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		SimpleAdapter itemsView;
		int img = R.drawable.word;
		String from[] = { "byt", "tv" };
		int to[] = { R.id.byt, R.id.tv };

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream in = null;
			try {
				URL httpUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) httpUrl
						.openConnection();
				conn.connect();
				Log.d("Log log", "Connected to network");
				in = conn.getInputStream();
				data = getStringFromInputStream(in);
				if (data != null)
					Log.d("INFO_TAG", "Received data" + data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String data) {
			// TODO Auto-generated method stub
			HashMap<String, String> hm = null;
			int error_status = 1;
			try {
				JSONArray get = new JSONArray(data);
				int l = get.length();
				while (j < l) {
					obj = get.getJSONObject(j);
					array[j] = obj.getString("Title").toLowerCase();
					String f = find.getText().toString().toLowerCase();
					for (int i = 0; i < array[j].length() - f.length(); i++) {

						if (array[j].substring(i, i + f.length()).equals(f)) {
							String info = obj.getString("Title") + "\n"
									+ obj.getString("Size");
							hm = new HashMap<String, String>();
							hm.put("byt", (Integer.toString(img)));
							hm.put("tv", info);
							items.add(hm);
							paper_title[k] = obj.getString("Title");
							paper_url[k++] = obj.getString("URL");
							i = array[j].length();
							error_status = 0;
						}
					}
					j++;
				}
				if (error_status == 1)
					image.setVisibility(0);
				else
					image.setVisibility(4);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			itemsView = new SimpleAdapter(getBaseContext(), items,
					R.layout.disp, from, to);
			list.setAdapter(itemsView);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					// TODO Auto-generated method stub
					Log.d("log log", "You Selected" + pos + "whose URL is "
							+ paper_url[pos]);
					StartDownload(pos);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub

			super.onProgressUpdate(values);
		}

		public String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				fetchedData = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
						return fetchedData;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return fetchedData;
		}
	}
}
