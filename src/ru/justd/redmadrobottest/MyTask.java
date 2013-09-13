package ru.justd.redmadrobottest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

class MyTask extends AsyncTask<String, Void, String> {

	private static final String ARRAY_NAME = "data";
	private static final String IMAGE_URL = "profile_picture";

	private String accessToken = "258684555.eaae27b.3d890113126d4f638ac9c60230e27812";
	private String instagramUrl = "https://api.instagram.com/v1/users/search?";
	private String username= "";
	private Activity activity;
	
	private List<MyItem> myItemsList; // = new ArrayList<MyItem>();
	private GridView gridView;
	private ImageView testIV;
	
		public MyTask(String username, Activity activity, GridView gridView, ImageView testIV, List<MyItem> myItemsList) {
		this.username = username;
		this.activity = activity;
		this.gridView = gridView;
		this.testIV = testIV;
		this.myItemsList = myItemsList;
	}
	
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (username.equals("")){
			username = "greg";
			toast("Test launch for the name of \"greg\"");
		}
	}

	@Override
	protected String doInBackground(String... params) {

		try {			 
			return connectToServer(instagramUrl+"q="+username+"&access_token="+accessToken);
			} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		if (null == result || result.length() == 0) {
			toast("No data found from web!!!");
			activity.finish();
		} else {

			try {
				JSONObject mainJson = new JSONObject(result);
				JSONArray jsonArray = mainJson.getJSONArray(ARRAY_NAME);
				
				int count = 0;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject objJson = jsonArray.getJSONObject(i);
					String imageUrl = objJson.getString(IMAGE_URL);

					boolean flag = false;
					if (myItemsList.size()!=0){
					for (MyItem myItem : myItemsList){
						if(myItem.getImageUrl().equals(imageUrl)) {
							System.out.println(imageUrl);
							System.out.println(myItem.getImageUrl());
							flag = true;
							break;
						}
					}
					}
						if (!flag){
							MyItem item = new MyItem(Uri.parse(imageUrl));
							item.setImageUrl(imageUrl);
							myItemsList.add(item);
//							System.out.println(i);
						}
						else{

//							System.out.println("SOVPADENIE "+myItemsList.size());
						}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			setAdapterToGridview();

		}
	}

	public void setAdapterToGridview() {
		ImageAdapter adapter = new ImageAdapter(activity, myItemsList, testIV);
	    gridView.setAdapter(adapter);
	    
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	           if (!v.isEnabled()){
	        	   v.setEnabled(true);
	        	   v.setBackgroundColor(0x00000000);
	           }else{
	        	   v.setEnabled(false);
	        	   v.setBackgroundColor(Color.CYAN);
	           }

	        	System.out.println("pos "+position+" selected "+v.isEnabled());
	        }
	    });
			
	}

	public void toast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}

	private static String connectToServer(String endPointUrl)	throws IOException {
		HttpsURLConnection urlConnection = null;
		URL url;
		try {
			url = new URL(endPointUrl);
			urlConnection = (HttpsURLConnection) url.openConnection();
			String str = readResponse(urlConnection);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}
	
	/**
	 *  Reads a response for a given connection and returns it as a string.
	 * @param connection
	 * @return
	 */
	private static String readResponse(HttpsURLConnection connection) {
		StringBuilder str = new StringBuilder();
		String result = null;
			try {
				

				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = "";
				while ((line = br.readLine()) != null) {
					str.append(line + System.getProperty("line.separator"));
				}
				result = str.toString();
			} catch (IOException e) {
			}
		return result;
	}






}