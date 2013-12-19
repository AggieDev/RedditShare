package com.appuccino.subredditshare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RetrievePosts extends AsyncTask<String, Integer, ArrayList<Post>>{

	//this class parses posts from JSON formatting
	
	private MainActivity context;
	String url;
	
	public RetrievePosts(MainActivity context, String url)
	{
		this.context = context;
		this.url = url;
	}
	
	//thread's work, returns list of posts
	@Override
	protected ArrayList<Post> doInBackground(String... arg0) 
	{		
		//returns the json string
		String readReddit = readReddit();
		
		ArrayList<Post> postList = new ArrayList<Post>();
		try
		{
			//return each post
			JSONObject data = new JSONObject(readReddit).getJSONObject("data");
			JSONArray children = data.getJSONArray("children");
			String after = data.getString("after");
			
			for(int i = 0; i<children.length(); i++)	//for each post, make a Post object from elements in JSON and add to list
			{
				JSONObject current = children.getJSONObject(i).getJSONObject("data");
				
				Post thisPost = new Post(current.getString("title"), "submitted by " + current.getString("author")
						, current.getString("url"), current.getString("thumbnail"));
				Log.i("wbbug",current.getString("thumbnail"));
				postList.add(thisPost);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		    
		return postList;
	}
	
	//returns JSON string 
	private String readReddit()
	{
		StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
	    try 
	    {
	    	HttpResponse response = client.execute(httpGet);
	    	StatusLine statusLine = response.getStatusLine();
	    	int statusCode = statusLine.getStatusCode();
	    	if (statusCode == 200) 
	    	{
		        HttpEntity entity = response.getEntity();
		        InputStream content = entity.getContent();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        String line;
		        while ((line = reader.readLine()) != null) 
		        {
		        	builder.append(line);
		        }
	      	} 
	    	else 
	    	{
	    		Log.e("wbbug", "Failed to download file");
	      	}
	    } 
	    catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } 
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return builder.toString();
	}
}
