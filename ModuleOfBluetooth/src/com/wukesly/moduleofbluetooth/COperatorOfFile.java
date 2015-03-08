package com.wukesly.moduleofbluetooth;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * �ļ��ز����࣬�ṩ�ļ�������һ�㷽��
 * @author 123
 *
 */
public class COperatorOfFile {
	
	/**
	 * �ļ���·��
	 */
	private String folderPath = "";
	/**
	 * SDCard����״̬
	 */
	private boolean SDCardIsEnable = false;
	/**
	 * �ļ�·��
	 */
	private String fileName = "";
	private Context context = null;
	
	private boolean floderIsExists = false;
	private boolean fileIsExists = false;
	
	public COperatorOfFile(Context context,String folderPath) {
		super();
		// TODO Auto-generated constructor stub
		/**
		 * ��ȡSDCard�Ŀ���״̬
		 */
		this.SDCardIsEnable = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		this.context = context;
		this.floderIsExists = setFolderPath(folderPath);
	}
	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}
	/**
	 * �����ļ���·�������·�������ڣ�����·��?
	 * @param folderPath �ļ���·��
	 * @author 123
	 * @return �ļ��д���״̬�����ڷ���true�����򷵻�false
	 */
	public boolean setFolderPath(String folderPath) {
		boolean state = false;
		if(folderPath.length()==0){
			Toast.makeText(context, "�ļ���·������Ϊ��", Toast.LENGTH_LONG);
		}else{
			this.folderPath = folderPath;
			if(SDCardIsEnable)
			{
				File folder = new File(folderPath);
				if(!folder.exists())
				{//�ļ��в����ڣ������ļ���
					state = folder.mkdirs();
					Log.i("�ļ���", folderPath);
				}else{
					state = true;
				}
			}else{
				Toast.makeText(context, "SDCard������", Toast.LENGTH_LONG);
			}
		}
		return state;
		
	}
	/**
	 * @return the sDCardIsEnable
	 */
	public boolean isSDCardIsEnable() {
		return SDCardIsEnable;
	}
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return fileName;
	}
	/**
	 * �����ļ�����
	 * @param fileName �ļ���
	 */
	public boolean setFileName(String fileName) {
		boolean state = false;
		if(fileName.length()==0){
			Toast.makeText(context, "�ļ���·������Ϊ��", Toast.LENGTH_LONG);
		}else{
			this.fileName = fileName;
			state = true;
		}
		return state;
	}
	
	/**
	 * ���ļ����д����ļ���������д���ļ�
	 * @param filename �ļ���
	 * @param data �ļ�����
	 * @return ����״̬���ɹ�����true�����򷵻�false;
	 */
	public boolean createFile(String filename,byte[] data)
	{
		boolean state = false;
		if(setFileName(filename)){
			if(floderIsExists){//�ļ����Ѵ���
				File file = new File(folderPath+"/"+filename);
				if(file.exists()){//�ļ��Ѵ���
					
				}else{
					try {
						if(file.createNewFile()){//�����ļ����ɹ���д������
							FileOutputStream output = new FileOutputStream(file);
							output.write(data, 0, data.length);
							output.close();
							state = true;
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{//�ļ��в�����
				Toast.makeText(context, "�ļ��в�����", Toast.LENGTH_LONG);
			}
		}
		return state;
	}

	/**
	 * ��ȡ�ļ������ݴ���byte����
	 * @param path �ļ�����·��
	 * @return ��ȡ�ɹ��������ļ����ݣ�ʧ�ܷ���null
	 */
	public byte[] readFile(String path)
	{
		byte[] data = null;
		if(path.length()>0){
			File file = new File(path);
			if(file.exists()){
				try {
					FileInputStream input = new FileInputStream(file);
					int size = (int)file.length();
					Log.i("Filesize", ""+size);
					data = new byte[size];
					input.read(data);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(context, "�ļ�������", Toast.LENGTH_LONG);
				Log.e("ReadFile", "�ļ������ڣ�01");
			}
		}else{//�ļ�·���쳣
			Toast.makeText(context, "�ļ�·������Ϊ��", Toast.LENGTH_LONG);
			Log.e("ReadFile", "�ļ�·������Ϊ��02");
		}
		return data;
	}

	
	//android��ȡһ�����ڴ�HTML�ļ���intent
    public static Intent getHtmlFileIntent(File file){
    	Uri uri = Uri.parse(file.toString()).buildUpon().
    			encodedAuthority("com.android.htmlfileprovider").
    			scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }
    //android��ȡһ�����ڴ�ͼƬ�ļ���intent
    public static Intent getImageFileIntent(File file){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
    //android��ȡһ�����ڴ�PDF�ļ���intent
    public static Intent getPdfFileIntent(File file){
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	Uri uri = Uri.fromFile(file);
    	intent.setDataAndType(uri, "application/pdf");
    	return intent;
    }
    //android��ȡһ�����ڴ��ı��ļ���intent
    public static Intent getTextFileIntent(File file){   
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	Uri uri = Uri.fromFile(file);
    	intent.setDataAndType(uri, "text/plain");
    	return intent;
  }

    //android��ȡһ�����ڴ���Ƶ�ļ���intent
    public static Intent getAudioFileIntent(File file){
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra("oneshot", 0);
    	intent.putExtra("configchange", 0);
    	Uri uri = Uri.fromFile(file);
    	intent.setDataAndType(uri, "audio/*");
    	return intent;
    }
    //android��ȡһ�����ڴ���Ƶ�ļ���intent
    public static Intent getVideoFileIntent(File file){
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	intent.putExtra("oneshot", 0);
    	intent.putExtra("configchange", 0);
    	Uri uri = Uri.fromFile(file);
    	intent.setDataAndType(uri, "video/*");
    	return intent;
    }


    //android��ȡһ�����ڴ�CHM�ļ���intent
    public static Intent getChmFileIntent(File file){
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	Uri uri = Uri.fromFile(file);
    	intent.setDataAndType(uri, "application/x-chm");
    	return intent;
    }


  //android��ȡһ�����ڴ�Word�ļ���intent
    public static Intent getWordFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/msword");
      return intent;
    }
  //android��ȡһ�����ڴ�Excel�ļ���intent
    public static Intent getExcelFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/vnd.ms-excel");
      return intent;
    }
  //android��ȡһ�����ڴ�PPT�ļ���intent
    public static Intent getPPTFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
      return intent;
    }
    //android��ȡһ�����ڴ�apk�ļ���intent
    public static Intent getApkFileIntent(File file)
    {
        Intent intent = new Intent(); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        intent.setAction(android.content.Intent.ACTION_VIEW); 
        intent.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive"); 
        return intent;
    }
    
    public boolean checkEndsWithInStringArray(String checkItsEnd,
            String[] fileEndings){
    	for(String aEnd : fileEndings){
    		if(checkItsEnd.endsWith(aEnd))
    			return true;
    	}
    	return false;
}
    
}
