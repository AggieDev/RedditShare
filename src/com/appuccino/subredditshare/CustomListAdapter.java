package com.appuccino.subredditshare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CustomListAdapter extends ArrayAdapter<Post>{

	Context context; 
    int layoutResourceId;    
    ArrayList<Post> postList;
    
    //constructor
    public CustomListAdapter(Context context, int layoutResourceId, ArrayList<Post> postList) {
        super(context, layoutResourceId, postList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.postList = postList;
    }
    
    //called for each row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;	//this is listview_item_row
        
        ImageView thumbnail = null;
        TextView titleText = null;
        TextView contentText = null;
        
        Post thisPost;
        
        if (postList.size() > 0)	//if there are posts to display
        {
        	thisPost = postList.get(position);
        	
        	if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }
        	thisPost = (Post)postList.get(position);
        	//if post exists
        	if(thisPost != null)
        	{
        		//retrieve views for row
        		thumbnail = (ImageView)row.findViewById(R.id.thumbnail);
	            titleText = (TextView)row.findViewById(R.id.title);
	            contentText = (TextView)row.findViewById(R.id.content);
	            
	            //retrieve fonts
	            Typeface customfont = Typeface.createFromAsset(context.getAssets(), "fonts/bebasneue.ttf");
	            Typeface customfont2 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
	            
	            //set fonts and views that exist
	            titleText.setText(thisPost.getTitle());
	            titleText.setTypeface(customfont);
	            if(thisPost.getContent() != null)
	            {
	            	contentText.setText(thisPost.getContent());
	            	contentText.setTypeface(customfont2);
	            }		        
	            //if post has a thumbnail image
	            if(thisPost.getThumbnailURL() != null && !thisPost.getThumbnailURL().isEmpty())
	            {
	            	//keeping image loading off the main thread
	            	AsyncTask<String, Void, Bitmap> imgLoader = new ImageLoader(thisPost).execute("");
	            	Bitmap image;
					try {
						image = imgLoader.get();
						thumbnail.setImageBitmap(image);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
        	}
        }

        return row;
    }
}
