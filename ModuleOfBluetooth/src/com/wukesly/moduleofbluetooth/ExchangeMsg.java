package com.wukesly.moduleofbluetooth;



import java.util.ArrayList;

import com.wukesly.moduleofbluetooth.R;
import com.wukesly.moduleofbluetooth.R.id;
import com.wukesly.moduleofbluetooth.R.layout;
import com.wukesly.moduleofbluetooth.R.menu;
import com.wukesly.moduleofbluetooth.R.string;
import com.wukesly.moduleofbluetooth.test.Test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;  
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ��������ģ�飬���ݽ�������
 * @author 123
 */
public class ExchangeMsg extends Activity {
    
    // ����
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // �����豸������
    private String mConnectedDeviceName = null;
    // ��Ϣ��¼������
    private MsgListAdpter msgListAdpter = null;
    private ArrayList<CMsg> msgList = null;
    
    // �����Ϣ����?
    private StringBuffer mOutStringBuffer;
    // ��������������
    private BluetoothAdapter mBluetoothAdapter = null;
    // ��������ͨѶ����
    private BluetoothSharingService mChatService = null;
    //�Ƿ�Ϊ�ⲿ��������
    private boolean isSetUpSent = false;
    //�ļ���������
    private COperatorOfFile opFile;
    
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Cconst.D) Log.e(Cconst.TAG, "+++ ON CREATE +++");
        Log.i("TileBar", "00");
        // ���ô��ڲ���
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i("TileBar", "01");
        try{
        	setContentView(R.layout.main);
        }catch(Exception e){
        	e.printStackTrace();
        }
        Log.i("TileBar", "1");
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        Log.i("TileBar", "2");
        // ���ñ�����
        //mTitle = (TextView) findViewById(R.id.title_left_text);
        //mTitle.setText(R.string.app_name);
        	mTitle = (TextView) findViewById(R.id.title_right_text);
        //��ȡ���������豸
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // ������Ϊ�գ���֧�������豸
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // ��ʼ��ͨѶ��¼������
        mConversationView = (ListView) findViewById(R.id.in);
        if(msgList==null){
        	msgList = new ArrayList<CMsg>();
        }
        if(msgListAdpter==null){
        	msgListAdpter = new MsgListAdpter(this, msgList);
        }
        mConversationView.setAdapter(msgListAdpter);
        
    }

    @SuppressLint("NewApi")
	@Override
    public void onStart() {
        super.onStart();
        if(Cconst.D) Log.e(Cconst.TAG, "++ ON START ++");

        // ��������Ѵ򿪣���ʼ��Ӧ��?
        // ���δ�򿪣����ڻص������г�ʼ��?
        if (!mBluetoothAdapter.isEnabled()) {
        	//mBluetoothAdapter.enable();
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Cconst.REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();//��ʼ��
        }
     
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(Cconst.D) Log.e(Cconst.TAG, "+ ON RESUME +");
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothSharingService.STATE_NONE) {
              // ���������������?
              mChatService.start();
              //�ж��Ƿ����ⲿ������������
              Intent i = getIntent();
              if(i !=null){
            	  int type = i.getIntExtra(Cconst.DATA_TYPE, -1);
            	  Log.i("TYpe", ""+type);
            	  if(type>=0){
            		  isSetUpSent = true;
            		  //���������豸
            		  //��������䵽���ͱ༭����?
            		  switch (type) {
            		  case Cconst.TYPE_COMMONDATA:
            			 String msg="";
            			  if(i.getStringExtra(Cconst.DATA_ATTR).equals(Cconst.ATTR_STRUCT)){
            				  ObjAndBytes Oab = new ObjAndBytes();
            				  msg = ((ContactsData)Oab.toObject(i.getByteArrayExtra(Cconst.DATA_DATA))).name+"����Ƭ";
            			  }else{
            				  msg = new String(i.getByteArrayExtra(Cconst.DATA_DATA));
            			  }
            			  mOutEditText.setText(msg);
            			  break;
            		  case Cconst.TYPE_FILE:
            			  String path = i.getStringExtra(Cconst.DATA_FILENAME);
            			  Log.i("TYpe", ""+path);
            			  if(path!=null){
                			  mOutEditText.setText(path.substring(path.lastIndexOf('/')+1));
            			  }
      					  break;
            		  }
      				 mOutEditText.setEnabled(false);//���ɱ༭
            	  }
              }
            }
        }
    }

    @SuppressLint("NewApi")
	private void setupChat() {
        Log.d(Cconst.TAG, "setupChat()");

        //��ʼ��Ӧ���ļ�������
        opFile = new COperatorOfFile(getApplicationContext(), Cconst.STORE_PATH);
        //�༭�򣬺ͼ�����
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);
        mConversationView.setAdapter(msgListAdpter);
        mConversationView.invalidate();
        // ���Ͱ�ť,�ͼ�����
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(isSetUpSent){//�ⲿ��������
            		sendMessage(getIntent());
            		 Log.i("OnClich", "�����ļ�");
            		}else{//���ͱ༭������
            		  TextView view = (TextView) findViewById(R.id.edit_text_out);
            		  String message = view.getText().toString();
            		  Log.i("OnClich", message);
            		  sendMessage(message);
            	}
            }
        });
        // ��ʼ����������Ѷ����
        mChatService = new BluetoothSharingService(this, mHandler);
        // ��ʼ���������?
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(Cconst.D) Log.e(Cconst.TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Cconst.D) Log.e(Cconst.TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // ֹͣ����ͨѶ����
        if (mChatService != null) mChatService.stop();
        if(Cconst.D) Log.e(Cconst.TAG, "--- ON DESTROY ---");
    }

    @SuppressLint("NewApi")
	private void ensureDiscoverable() {
        if(Cconst.D) Log.d(Cconst.TAG, "ensure discoverable");
        //����������������ɼ�?
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    
    
    /**
     * ������������ͨѶ�����Intent �������ݣ�Intent�д����Ҫ���͵����ݣ�type+attr+data/filepath��
     * @param intent �ⲿ������Intent�����д����Ҫ���͵�����?
     */
    private void sendMessage(Intent intent)
    {
    	  if (mChatService.getState() != BluetoothSharingService.STATE_CONNECTED) {
	            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
	           return;
	        }
    	if(intent == null){
    		Toast.makeText(this, "������Ϣ���Ϸ���", Toast.LENGTH_LONG).show();
    		finish();
    	}else{
    		//��ȡ��������
    		int type = intent.getIntExtra(Cconst.DATA_TYPE, -1);//��ȡ����
    		String attr = intent.getStringExtra(Cconst.DATA_ATTR);//��ȡ����,�ļ���or��������
    		if(type>=0&&attr!=null){
    			if(type == Cconst.TYPE_FILE){
    				//��ȡ�ļ�·��,����������
    				String filepath = intent.getStringExtra(Cconst.DATA_FILENAME);
    				if(filepath!=null){
    					byte[] data = opFile.readFile(filepath);
    					if(data!=null){
    						//��������
    						Log.i("��", "type is "+type+"&&attr is "+attr+"&& datalen is "+data.length);
    						mChatService.write(mChatService.Packaging(type, attr, data));
    						//���ͳɹ�
    						}else{
    							Toast.makeText(this, "�����ļ������ڣ�", Toast.LENGTH_LONG).show();
    							Log.e("SendData", "�����ļ������ڣ�01");
    							finish();
    						}//�ж��ļ������Ƿ�Ϊ��
    				}else{
    					Toast.makeText(this, "�����ļ������ڣ�", Toast.LENGTH_LONG).show();
    					Log.e("SendData", "�����ļ������ڣ�02");
			    		finish();
    				}//�ļ�·���ǿ�
    			}else{//��һ������
    					byte []data = intent.getByteArrayExtra(Cconst.DATA_DATA);
    					if(data !=null){
    							mChatService.write(mChatService.Packaging(type, attr, data));
    							//���ͳɹ�
    					}else{
    						Toast.makeText(this, "���ݷ����쳣��", Toast.LENGTH_LONG).show();
				    		finish();
    					}//���ݳ��ȶ�ȡ�ɹ�
    			}//�ļ�����
    			isSetUpSent = false;
        		mOutEditText.setEnabled(true);//���Ա༭
        		mOutStringBuffer.setLength(0);
                mOutEditText.setText(mOutStringBuffer);
    		}//���͡����Զ�ȡ�ɹ�
    	}//intent�ǿ�
    }//func end
    
    /**
     * Sends a message.
     * @param message  ���͵��ַ���
     */
    private void sendMessage(String message) {
        // ����Ƿ�������?
    	Log.i("messgae", message);
        if (mChatService.getState() != BluetoothSharingService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
           return;
        }
        
        // ������Ϣ�Ƿ�Ϊ��
        if (message.length() > 0) {
            // ������Ϣת��Ϊ������
            byte[] send = message.getBytes();
            //mChatService.write(send);
            mChatService.write(mChatService.Packaging(Cconst.TYPE_COMMONDATA, "String", message.getBytes()));
            //�������ݣ���������Ϊ0������ΪString
            //int i = mChatService.SendData();
            
           // Log.i("Len", ""+i);
            // ���û���
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // ����������������key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // ������key_up����
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(Cconst.D) Log.i(Cconst.TAG, "END onEditorAction");
            return true;
        }
    };

    // ��Ϣ����
    private final Handler mHandler = new Handler() {
    	
    	CMsg cmsg;
    	
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Cconst.MESSAGE_STATE_CHANGE:
                if(Cconst.D) Log.i(Cconst.TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothSharingService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothSharingService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothSharingService.STATE_LISTEN:
                case BluetoothSharingService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case Cconst.MESSAGE_WRITE://д����
            	
            	Bundle b = (Bundle)msg.obj;
            	CMsg  cmsg;
            	switch (b.getInt(Cconst.DATA_TYPE, -1)) {
				case Cconst.TYPE_COMMONDATA:
					if(b.getString(Cconst.DATA_ATTR).equals(Cconst.ATTR_STRING)){
						cmsg = new CMsg(Cconst.MSG_TYPE_COMMDATA, 
								"Me��"+new String(b.getByteArray(Cconst.DATA_DATA)));
						cmsg.setMeOrHe(Cconst.MEORHE_ME);
						msgList.add(cmsg);
						
					}else if(b.getString(Cconst.DATA_ATTR).equals(Cconst.ATTR_STRUCT)){
						cmsg = new CMsg(Cconst.MSG_TYPE_STRUCT, "Me��"+"��Ƭ���ͳɹ���");
						cmsg.setCd((ContactsData)(new ObjAndBytes().toObject(b.getByteArray(Cconst.DATA_DATA))));
						cmsg.setMeOrHe(Cconst.MEORHE_ME);
						msgList.add(cmsg);
					}else{
						
					}
					mConversationView.invalidate();
					break;

				case Cconst.TYPE_FILE:
					String path = b.getString(Cconst.DATA_ATTR);
					cmsg  = new CMsg(Cconst.MSG_TYPE_FILE, "Me���ļ���"+path+"���ͳɹ���");
					cmsg.setMeOrHe(Cconst.MEORHE_ME);
					cmsg.setPath(path);
					msgList.add(cmsg);
					mConversationView.invalidate();
					break;
				default:
					break;
				}
            	
            	/*
                //byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = msg.obj.toString();//new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);*/
                break;
            case Cconst.MESSAGE_READ://��������
            	Bundle bundle = (Bundle)msg.obj;
            	String attr = bundle.getString(Cconst.DATA_ATTR);
            	int datalen = bundle.getInt(Cconst.DATA_DATALEN, -1);
            	int type = bundle.getInt(Cconst.DATA_TYPE,-1);
            	Log.i("Handle", "attr is "+attr+"      datalen is "+datalen+"	type is "+type);
            	if(datalen>0&&type>=0&&attr!=null)
            	{
            		Log.i("if", "TYpe is "+type+"attr is String?"+attr.equals("String"));
            		byte[] data;
            		switch (type) {
					case Cconst.TYPE_COMMONDATA:
						//��ͨ����
						data = new byte[datalen];
						data = bundle.getByteArray(Cconst.DATA_DATA);
					
						if(attr.equals(Cconst.ATTR_STRING)){
							
							Log.i("Handle", new String(data));
							String readMessage = new String(data, 0, datalen);
							Log.i("Read", "001");
							cmsg = new CMsg(Cconst.MSG_TYPE_COMMDATA, mConnectedDeviceName+":  " + readMessage);
							Log.i("Read", "002");
							cmsg.setMeOrHe(Cconst.MEORHE_HE);
							Log.i("Read", "003");
							msgList.add(cmsg);
							Log.i("Read", "004");
			                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
						}else if(attr.equals(Cconst.ATTR_STRUCT)){
							ObjAndBytes OAB = new ObjAndBytes();
							ContactsData cd = (ContactsData)OAB.toObject(data);
							cmsg = new CMsg(Cconst.MSG_TYPE_STRUCT, "�յ�����"+mConnectedDeviceName+"����Ƭ��");
							cmsg.setMeOrHe(Cconst.MEORHE_HE);
							msgList.add(cmsg);
							/*
							mConversationArrayAdapter.add(mConnectedDeviceName+":\n");
							mConversationArrayAdapter.add("\t\t����"+":"+cd.name+"\n");
							mConversationArrayAdapter.add("\t\t�绰"+":"+cd.phoneNum+"\n");
							mConversationArrayAdapter.add("\t\t��ַ"+":"+cd.addr+"\n");
							mConversationArrayAdapter.add("\t\tEmail"+":"+cd.email+"\n");
							*/
						}else{
							//������������
						}
						break;
					case Cconst.TYPE_FILE:
						//�ļ�����
						data = new byte[datalen];
						Log.i("File", "jinru");
						data = bundle.getByteArray(Cconst.DATA_DATA);
						//Log.i("Handle", new String(data));
						String readMessage = "���յ��ļ���"+attr+"��\t\t\n����·����"+opFile.getFolderPath();//new String(data, 0, datalen);
		                cmsg = new CMsg(Cconst.MSG_TYPE_FILE,mConnectedDeviceName+":  " + readMessage);
						cmsg.setMeOrHe(Cconst.MEORHE_HE);
						
						//mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
		                opFile.createFile(attr, data);//�����ļ�
		                Toast.makeText(ExchangeMsg.this, readMessage, Toast.LENGTH_LONG).show();
		                cmsg.setPath(opFile.getFolderPath()+"/"+attr);
		                msgList.add(cmsg);
						break;
					default:
						break;
					}
            	}
            	mConversationView.invalidate();
                break;
            case Cconst.MESSAGE_DEVICE_NAME:
                // ���������豸������
                mConnectedDeviceName = msg.getData().getString(Cconst.DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case Cconst.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(Cconst.TOAST),
                               Toast.LENGTH_SHORT).show();
                mTitle.setText(R.string.title_not_connected);
                
                break;
            }
        }
    };

    @SuppressLint("NewApi")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Cconst.D) Log.d(Cconst.TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case Cconst.REQUEST_CONNECT_DEVICE:
            // ����һ���豸
            if (resultCode == Activity.RESULT_OK) {
                // ��ȡ�豸�������ַMAC
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // ��ȡ�����豸����
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // �����豸
                mChatService.connect(device);
            }
            break;
        case Cconst.REQUEST_ENABLE_BT:
            // ���������������?
            if (resultCode == Activity.RESULT_OK) {
                // ������
                setupChat();
            } else {
                // δ��
                Log.d(Cconst.TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	//@Override
	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			startActivity(new Intent(ExchangeMsg.this,Test.class));
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            // Ѱ���豸
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, Cconst.REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.discoverable:
            // �򿪿ɼ���
            ensureDiscoverable();
            return true;
        }
        return false;
    }

}