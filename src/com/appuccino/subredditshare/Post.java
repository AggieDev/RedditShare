package com.appuccino.subredditshare;

public class Post {
	String title;
	String content;
	String url;	//link to reddit post
	String thumbnailURL;
	
	//constructors
	public Post()
	{
	}
	
	public Post(String title)
	{
		this.title = title;
	}
	
	public Post(String title, String content, String url, String thumbnailURL)
	{
		this.title = title;
		this.content = content;
		this.url = url;
		this.thumbnailURL = thumbnailURL;
	}
	
	//getters
	public String getTitle()
	{
		return title;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public String getThumbnailURL()
	{
		return thumbnailURL;
	}
}
