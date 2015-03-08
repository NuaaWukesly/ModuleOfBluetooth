package com.wukesly.moduleofbluetooth.test;

public class myFile {

	private String filename;
	private int image_id;
	private String path;
	public myFile() {
		// TODO Auto-generated constructor stub
		super();
		this.filename = "";
		this.image_id =0;
		this.path = "";
	}
	public myFile(String filename,int image_id,String path)
	{
		super();
		this.filename = filename;
		this.image_id = image_id;
		this.path = path;
	}

	public String getFilename()
	{
		return this.filename;
	}
	
	public int getImageId()
	{
		return this.image_id;
	}
	
	public String getFilePath()
	{
		return this.path;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	
	public void setImageId(int id)
	{
		this.image_id = id;
	}
	
	public void setFilePath(String path)
	{
		this.path = path;
	}
}