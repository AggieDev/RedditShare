package com.appuccino.subredditshare;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageLoader extends AsyncTask<String, Void, Bitmap>{

	//class used to keep thumbnail loading off main thread
	
	Post thisPost;
	
	public ImageLoader()
	{
	}
	
	public ImageLoader(Post post)
	{
		thisPost = post;
	}
	
	//thread's work
	@Override
	protected Bitmap doInBackground(String... arg0) {
		URL imgurl;
		Bitmap image = null;
    	try {
			imgurl = new URL(thisPost.getThumbnailURL());
			image = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}

}
