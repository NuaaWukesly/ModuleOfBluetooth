package com.wukesly.moduleofbluetooth.test;


import java.io.File;
import java.util.ArrayList;


import com.wukesly.moduleofbluetooth.Cconst;
import com.wukesly.moduleofbluetooth.ContactsData;
import com.wukesly.moduleofbluetooth.ExchangeMsg;
import com.wukesly.moduleofbluetooth.ObjAndBytes;
import com.wukesly.moduleofbluetooth.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author 123
 *
 */
public class Test extends Activity {

	private Button retroot_bnt;
	private Button recPage_bnt;
	private Button senddata_bnt;
	private ListView lv;
	private fileAdapter fAdapter; 
	private ArrayList<myFile> fileList = new ArrayList<myFile>();
	private String rootPath= "/";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test);
		retroot_bnt = (Button)findViewById(R.id.retroot_bnt);
		recPage_bnt = (Button)findViewById(R.id.recevice_filepage_bnt);
		senddata_bnt = (Button)findViewById(R.id.sendData_bnt);
		lv =(ListView)findViewById(R.id.filelist);
		// ��ʾ�ļ����
		
		retroot_bnt.setEnabled(false);
		
		fileList = getFile(rootPath);
		recPage_bnt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Test.this,ExchangeMsg.class);
				startActivity(i);
				finish();
			}
		});
		
		senddata_bnt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContactsData cd = new ContactsData();
				ObjAndBytes Oab = new ObjAndBytes();
				cd.name = "������";
				cd.email = "wxl901018@163.com";
				cd.phoneNum = "18061608810";
				cd.addr = "�Ϻ�";
				Intent i = new Intent(Test.this,ExchangeMsg.class);
				i.putExtra(Cconst.DATA_TYPE, Cconst.TYPE_COMMONDATA);
				i.putExtra(Cconst.DATA_ATTR, Cconst.ATTR_STRUCT);
				i.putExtra(Cconst.DATA_DATA, Oab.toByteArray(cd));
				startActivity(i);
			}
		});
		
		retroot_bnt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(rootPath);
				
				rootPath = file.getParent();
				fileList = getFile(rootPath);
				Log.i("path", file.getParent());
				if(rootPath.equals("/")){
					retroot_bnt.setEnabled(false);
					}
			}
		});
		
	lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (fileList.size() > arg2 && arg2 >= 0) {
					File file = new File(fileList.get(arg2).getFilePath());
					if (file.canRead()) {
						if (file.isDirectory()) {
							//������ļ��о��ٽ�ȥ��ȡ 
							rootPath = fileList.get(arg2).getFilePath();
							fileList = getFile(rootPath);
							retroot_bnt.setEnabled(true);
							Toast.makeText(Test.this, rootPath, Toast.LENGTH_LONG);
						} else {
							//������ļ� 
							final String path = fileList.get(arg2).getFilePath();
							Log.i("���", path);
							Dialog dialog = new AlertDialog
									.Builder(Test.this)
									.setMessage(path)
									.setTitle("������ʾ")
									.setNegativeButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}
									})
									.setPositiveButton("����", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											Intent i = new Intent(Test.this,ExchangeMsg.class);
											i.putExtra(Cconst.DATA_TYPE, Cconst.TYPE_FILE);
											i.putExtra(Cconst.DATA_ATTR, path.substring(path.lastIndexOf('/')+1));
											i.putExtra(Cconst.DATA_FILENAME,path);
											
											startActivity(i);
											Log.i("�ļ�", path+"*****"+path.substring(path.lastIndexOf('/')));
											dialog.dismiss();
										}
									})
									.create();
							dialog.show();
						}
					} else {
						// ����AlertDialog��ʾȨ�޲���
						Toast.makeText(getApplicationContext(), "û��Ȩ��", Toast.LENGTH_LONG).show();
					}
				}
			}
		});// End
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}
	
	
	
	/**
	 * ���path�µ��ļ�
	 * 
	 * @param path
	 *            ���Ŀ¼
	 * @return �ļ��б���ǰĿ¼��
	 */
	private ArrayList<myFile> getFile(String path) {
		// TODO Auto-generated method stub
		Log.i("into", "����"+path);
		File file = new File(path);
		ArrayList<myFile> list = new ArrayList<myFile>();
		if (file.exists()) {
			File[] files = file.listFiles();
			String filename;
			String filepath;
			int id;
			if (files.length != 0) {
				for (int i = 0; i < files.length; i++) {
					filepath = files[i].getPath();
					filename = files[i].getName();
					if (new File(filepath).isDirectory()) {
						// ���ļ���
						id = R.drawable.ic_fold;
					} else if (filepath.endsWith("MP3")
							|| filepath.endsWith("mp3")) {
						// �����ļ�
						id = R.drawable.ic_mp3;
					} else if (filepath.endsWith("3gp")
							|| filepath.endsWith("3GP")
							|| filepath.endsWith("MP4")
							|| filepath.endsWith("mp4")) {
						id = R.drawable.ic_mp4;
					} else {
						// �ļ�
						id = R.drawable.ic_copyfile;
					}
					list.add(new myFile(filename, id, filepath));
				}
			}
			fAdapter = new fileAdapter(Test.this, list);
			Log.i("listLen", "����Ϊ		" + list.size()+fAdapter);
			lv.setAdapter(fAdapter);
			Log.i("fileAdapter", "����");
		}
		return list;
	}

}
