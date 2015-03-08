package com.wukesly.moduleofbluetooth;


import java.io.File;
import java.util.ArrayList;

import com.wukesly.moduleofbluetooth.R;
import com.wukesly.moduleofbluetooth.R.array;
import com.wukesly.moduleofbluetooth.R.id;
import com.wukesly.moduleofbluetooth.R.layout;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Contacts;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MsgListAdpter extends BaseAdapter{

	private Context ctx;
	/**
	 * ��Ϣ�б�
	 */
	private ArrayList<CMsg> msgList;
	private LinearLayout layout_father;
	private LayoutInflater vi;
	private LinearLayout layout_child;
	private TextView tvDate;
	private TextView tvText;
	
	public MsgListAdpter(Context context,ArrayList<CMsg> list){
		this.ctx = context;
		vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		msgList = list;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		CMsg msg = msgList.get(position);
		//��ȡ��Ϣ������
		int own = msg.getMeOrHe();
		int itemLayout = -1;
		//���ò���
		if(own==Cconst.MEORHE_HE){
			itemLayout = R.layout.list_say_he_item;
		}else{
			itemLayout = R.layout.list_say_me_item;
		}
		
		layout_father = new LinearLayout(ctx);
		vi.inflate(itemLayout, layout_father, true);

		layout_father.setBackgroundColor(Color.TRANSPARENT);
		layout_child = (LinearLayout) layout_father.findViewById(R.id.layout_bj);
		
		tvText = (TextView) layout_father.findViewById(R.id.messagedetail_row_text);
		tvText.setText(msg.getDispMsg());

		tvDate = (TextView) layout_father.findViewById(R.id.messagedetail_row_date);
		tvDate.setText(msg.getDate());

		addListener(tvText, tvDate, layout_child, msg);

		return layout_father;

	}
	
	public void addListener(final TextView tvText, final TextView tvDate, LinearLayout layout_bj, final CMsg msg){

		layout_bj.setOnClickListener(new OnClickListener(){
			public void onClick(View v){

			}
		});

		layout_bj.setOnLongClickListener(new OnLongClickListener(){
			public boolean onLongClick(View v){
				tvText.setTextColor(0xffffffff);
				switch (msg.getMSG_TYPE()) {
				case Cconst.MSG_TYPE_COMMDATA:
					showListDialog(new String[]{"�����ı�����","ɾ��","��������¼"},3, msg);
					break;
				case Cconst.MSG_TYPE_FILE:
					showListDialog(new String[]{"�����ı�����","ɾ��","�鿴�ļ�","��������¼"},4, msg);
					break;
				case Cconst.MSG_TYPE_STRUCT:
					showListDialog(new String[]{"�����ı�����","ɾ��","������ϵ��","�鿴��ϵ����ϸ","��������¼"},5, msg);
					break;
				default:
					break;
				}
				
				return true;
			}
		});

		layout_bj.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				switch (event.getAction()){

				case MotionEvent.ACTION_DOWN:


				case MotionEvent.ACTION_MOVE:
					tvText.setTextColor(0xffffffff);
					break;

				default:
					tvText.setTextColor(Color.BLACK);
					break;
				}
				return false;
			}
		});
	}

	private void showListDialog(final String[] arg, final int count,final CMsg msg){
		new AlertDialog.Builder(ctx).setTitle("��Ϣѡ��").setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				//ǰ����
				if(which<2){
					switch(which){

					case 0://�����ı�����
						ClipboardManager cmb = (ClipboardManager) ctx.getSystemService(ctx.CLIPBOARD_SERVICE);
						cmb.setText(msg.getDispMsg());
						break;

					case 1://ɾ����Ϣ !!
						
						int index = msgList.indexOf(msg);
						if(index>=0&&index<msgList.size()){
							msgList.remove(index);
							//layout_father.removeViewAt(index);
						}
						//msgList.remove(msg);
						layout_father.invalidate();
						break;
					}
				}else if(which == count-1){//���һ��?
					//layout_father.removeAllViews();
					Log.i("RmAllView", "001");
					for (int i=0;i<msgList.size();i++) {	
						msgList.remove(i);
						//layout_father.removeViewAt(i);
						Log.i("RmAllView", "002");
					}
					layout_father.invalidate();
				}else if(arg[which].equals("�鿴�ļ�")){
					
					COperatorOfFile copf = new COperatorOfFile(ctx, 
							msg.getPath().substring(msg.getPath().lastIndexOf('/')+1));
					File currentPath = new File(msg.getPath());
					Log.i("path", msg.getPath());
					if(currentPath!=null&&currentPath.isFile())
	                {
	                    String fileName = currentPath.toString();
	                    Intent intent;
	                    if(copf.checkEndsWithInStringArray(fileName, ctx.getResources().
	                            getStringArray(R.array.fileEndingImage))){
	                        intent = copf.getImageFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingWebText))){
	                        intent = copf.getHtmlFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingPackage))){
	                        intent = copf.getApkFileIntent(currentPath);
	                        ctx.startActivity(intent);

	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingAudio))){
	                        intent = copf.getAudioFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingVideo))){
	                        intent = copf.getVideoFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingText))){
	                        intent = copf.getTextFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingPdf))){
	                        intent = copf.getPdfFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingWord))){
	                        intent = copf.getWordFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingExcel))){
	                        intent = copf.getExcelFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else if(copf.checkEndsWithInStringArray(fileName,  ctx.getResources().
	                            getStringArray(R.array.fileEndingPPT))){
	                        intent = copf.getPPTFileIntent(currentPath);
	                        ctx.startActivity(intent);
	                    }else
	                    {
	                        Toast.makeText(ctx, "�޷��򿪣��밲װ��Ӧ�������?", Toast.LENGTH_LONG).show();
	                    }
	                }else
	                {
	                	Toast.makeText(ctx, "�Բ����ⲻ���ļ���", Toast.LENGTH_LONG).show();
	                }
					
					
					
				}else if(arg[which].equals("������ϵ��")){
					ContactsData cd = msg.getCd();
					if(cd!=null){
						Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
						Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
						//����ָ������
						intent.setType(Contacts.People.CONTENT_TYPE);
						intent.putExtra(Contacts.Intents.Insert.NAME, cd.name);
						intent.putExtra(Contacts.Intents.Insert.EMAIL, cd.email);
						intent.putExtra(Contacts.Intents.Insert.PHONE, cd.phoneNum);
						intent.putExtra(Contacts.Intents.Insert.COMPANY, cd.addr);
						ctx.startActivity(intent);
					}
				}else if(arg[which].equals("�鿴��ϵ����ϸ")){
					ContactsData cd = msg.getCd();
					if(cd!=null){
						new AlertDialog.Builder(ctx).setTitle("��ϵ����Ϣ")
						.setMessage("\t������"+cd.name
								+"\n\t�绰��"+cd.phoneNum
								+"\t\n���䣺"+cd.email
								+"\t\n��˾(��ַ)��"+cd.addr)
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).create().show();
					}
				}else{
					
				}
				
			}
		}).show();
	}

}
