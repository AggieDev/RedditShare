package com.appuccino.subredditshare;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Menu menu;
	private SearchView searchView;
	private ArrayList<Post> postList;	//list of reddit posts
	private CustomListAdapter listAdapter;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		//set list to adapter
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				shareDialog(arg2);
			}			
		});		
		//intent for searching
		handleIntent(getIntent());
	}
	
	//dialog displayed when a post is clicked
	public void shareDialog(final int item)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
	    LayoutInflater inflater = getLayoutInflater();
	    View dialogLayout = null;
	    
		dialogLayout= inflater.inflate(R.layout.share_dialog, null);
	    
        builder.setView(dialogLayout);
        
        //retrieve views from layout and fonts
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/bebasneue.ttf");
        Typeface customFont2 = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface customFont3 = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        TextView title = (TextView)dialogLayout.findViewById(R.id.shareTitle);
        TextView subtitle = (TextView)dialogLayout.findViewById(R.id.shareSubtitle);
        TextView emailText = (TextView)dialogLayout.findViewById(R.id.emailText);
        TextView emailDesc = (TextView)dialogLayout.findViewById(R.id.emailDesc);
        TextView smsText = (TextView)dialogLayout.findViewById(R.id.smsText);
        TextView smsDesc = (TextView)dialogLayout.findViewById(R.id.smsDesc);
        TextView redditText = (TextView)dialogLayout.findViewById(R.id.redditText);
        TextView redditDesc = (TextView)dialogLayout.findViewById(R.id.redditDesc);
        LinearLayout emailButton = (LinearLayout)dialogLayout.findViewById(R.id.shareEmail);
        LinearLayout smsButton = (LinearLayout)dialogLayout.findViewById(R.id.shareSMS);
        LinearLayout redditButton = (LinearLayout)dialogLayout.findViewById(R.id.viewPost);
        
        //set fonts
        title.setTypeface(customFont);
        subtitle.setTypeface(customFont2);
        emailDesc.setTypeface(customFont2);
        smsDesc.setTypeface(customFont2);
        redditDesc.setTypeface(customFont2);
        emailText.setTypeface(customFont3);
        smsText.setTypeface(customFont3);
        redditText.setTypeface(customFont3);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        //email button clicked
        emailButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				sendEmail(item);
			}
        });
        
      //sms button clicked
        smsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				sendSMS(item);
			}
        });
        
      //reddit button clicked
        redditButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				viewPost(item);
			}
        });
	}

	public void sendEmail(int item)
	{
		//format body text
		String messageBody;
		messageBody = postList.get(item).getTitle(); 
		if(postList.get(item).getContent() != null && postList.get(item).getURL() != null)
			messageBody += "\n\n" + postList.get(item).getContent() + "\n\n" + postList.get(item).getURL();
		
		//start intent to send email
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, "Check out this Reddit post!");
		i.putExtra(Intent.EXTRA_TEXT   , messageBody);
		//try/catch, in case an email client isn't installed
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void sendSMS(int item)
	{
		//format body text
		String textBody;
		textBody = postList.get(item).getTitle();
		if(postList.get(item).getContent() != null && postList.get(item).getURL() != null)
			textBody += ":\n\n" + postList.get(item).getContent() + "\n\n" + postList.get(item).getURL();

		//start intent to send sms
        String uri= "smsto:";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        intent.putExtra("sms_body", textBody);
        intent.putExtra("compose_mode", true);
        startActivity(intent);
        finish();
	}
	
	public void viewPost(int post)
	{
		String url = "http://www.reddit.com" + postList.get(post).getURL();
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
	
	//handling for search query intents
	@Override
	public void onNewIntent(Intent intent)
	{
		setIntent(intent);
		handleIntent(getIntent());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    
	    //when menu initially created, do initial search for r/funny
	    doSearch("funny");
	    
		return true;
	}
	
	public void doSearch(String query)
	{
		MenuItem searchMenuItem = menu.findItem(R.id.search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
		
		//set hint in search box, collapse search box once query is made
		searchView.setQuery(query, false);
		searchView.setQueryHint(query);
		searchMenuItem.setTitle("r/" + query);
		searchMenuItem.collapseActionView();
		
		//start task on a new thread to retrieve reddit posts in the queried subreddit
		AsyncTask<String, Integer, ArrayList<Post>> rc = new RetrievePosts(this, "http://www.reddit.com/r/" + query + "/.json").execute("");
		try {
			postList = rc.get();	//asynctask complete
			updateList(postList);	//set list
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//called once new queried subreddit post list is retrieved
	public void updateList(ArrayList<Post> postList)
	{
		listAdapter = new CustomListAdapter(this, 
                R.layout.list_row, postList);
		
		//set list to adapter
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				shareDialog(arg2);
			}			
		});		
	}
	
	//handles search queries
	private void handleIntent(Intent intent)
	{				
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("wbbug","Search query: " + query);
            doSearch(query);
        }
	}
}
