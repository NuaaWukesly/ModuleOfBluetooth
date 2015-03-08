package com.wukesly.moduleofbluetooth;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ����ͨѶ��
 */
public class BluetoothSharingService {
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Member fields
    private final BluetoothAdapter mAdapter;
    /**
     * ��Ϣ����
     */
    private final Handler mHandler;
    /**
     * �ȴ������߳�
     */
    private AcceptThread mAcceptThread;
    /**
     * �����豸�߳�
     */
    private ConnectThread mConnectThread;
    /**
     * ͨѶ�߳�
     */
    private CommunicationThread mCommunicationThread;
    /**
     * ͨѶ״̬
     */
    private int mState;

    // ״̬����
    public static final int STATE_NONE = 0;       // ��ʼ״̬
    public static final int STATE_LISTEN = 1;     // �����ⲿ����״̬
    public static final int STATE_CONNECTING = 2; // ��ʼ�������ⲿ�豸״̬
    public static final int STATE_CONNECTED = 3;  // ���ӵ��ⲿ�豸

    /**
     * ���캯��
     * @param context  ����������
     * @param handler  �������ⲿ��������
     */
    public BluetoothSharingService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * ���õ�ǰ��״̬
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        // ���͵�ǰ״̬
        mHandler.obtainMessage(Cconst.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * ��ȡ״̬.
      */
    public synchronized int getState() {
        return mState;
    }

    /**
     * ����ͨѶ���񣬼����Ƿ����ⲿ����
     */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // ȡ��һ�г��������ⲿ�豸���߳�
        if (mConnectThread != null){
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        	}
        // ȡ��һ��ͨѶ�߳�
        if (mCommunicationThread != null){
        	mCommunicationThread.cancel(); 
        	mCommunicationThread = null;
        	}
        //��ʼ����
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * ��ʼһ�����ӣ���ʼ��һ�����ⲿ�豸������
     * @param device  �����ӵ����豸��Զ���豸��
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // ȡ�����������߳�
        if (mAdapter.getState() == STATE_CONNECTING) {
            if (mConnectThread != null) {
            	mConnectThread.cancel(); 
            	mConnectThread = null;
            	}
        }
        // ȡ��ͨѶ�߳�
        if (mCommunicationThread != null) {
        	mCommunicationThread.cancel(); 
        	mCommunicationThread = null;
        	}
        // ��ʼ�����豸
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * ��ʼͨѶ���񣬹���ͨѶ��Socket
     * @param socket  ���ӵ�Socket
     * @param device  ���ӵ����豸
     */
    public synchronized void communication(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // ȡ�������߳�
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        	}
        // ȡ��ͨѶ�߳�
        if (mCommunicationThread != null) {
        	mCommunicationThread.cancel();
        	mCommunicationThread = null;
        	}
        // ȡ�������߳�
        if (mAcceptThread != null) {
        	mAcceptThread.cancel(); 
        	mAcceptThread = null;
        	}
        // ����
        mCommunicationThread = new CommunicationThread(socket);
        mCommunicationThread.start();

        // ������Ϣ
        Message msg = mHandler.obtainMessage(Cconst.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Cconst.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    /**
     * ֹͣ�����߳�
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        	}
        if (mCommunicationThread != null) {
        	mCommunicationThread.cancel(); 
        	mCommunicationThread = null;
        	}
        if (mAcceptThread != null) {
        	mAcceptThread.cancel();
        	mAcceptThread = null;}
        setState(STATE_NONE);
    }
    
    /**
     * ����������Ϊtype,����Ϊattr,����λdata��֡���?
     * @param type ��������
     * @param attr ��������
     * @param data �����ֽ���
     * @return ��(type+dataLen+attrLen+attr+data)
     */
    public byte[] Packaging(int type,String attr,byte[] data)
    {
    	Log.i("Packagibf", attr+type);
    	int attrLen = attr.length();
    	int dataLen = data.length;
    	//Log.i("Packag", "type is "+type+"	atte is "+attr+"	datalen is "+data);
    	if(type<0||attrLen<=0||dataLen<=0){
    		//���ݲ��Ϸ�
    		return null;
    	}else{
    		//����������ת��Ϊ�ֽ�����
    		byte Btype = (byte)(type&0xff);
    		byte[] BdataLen = new byte[4];
    		BdataLen = IntToBytes(dataLen);
    		//Log.i("Bdatalen", ""+(int)BdataLen[3]+"&&"+(int)BdataLen[2]+"&&"+(int)BdataLen[1]+"&&"+(int)BdataLen[0]);
    		
    		//����������
    		byte[] Battr;
			try {
				Battr = attr.getBytes(Cconst.DECODE);
				attrLen = Battr.length;
				//���Գ���ת��Ϊ�ֽ�����
	    		byte BattrLen = (byte)(attrLen&0xff);
				Log.i("Attr","attr is "+new String(Battr,Cconst.DECODE)+"&&"+attrLen+"&&"+Battr.length);
				//new byte[attrLen];
	    		//Battr = attr.getBytes();
	    		//���?:����+����������+����������+��������+����
	    		int len = 1+4+1+attrLen+dataLen;
	    		byte[] bag = new byte[len];
	    		bag[0] = Btype;
	    		System.arraycopy(BdataLen, 0, bag, 1, 4);
	    		bag[5] = BattrLen;
	    		System.arraycopy(Battr, 0, bag, 6, attrLen);
	    		System.arraycopy(data, 0, bag, 6+attrLen, dataLen);
	    		//Log.i("Packagibf",""+bag.length);
	    		return bag;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} 
    	}
    }
    
    /**
     * ��һ����ת��Ϊ4�ֽڵ�byte����
     * @param num ����
     * @return 4�ֽ�����
     */
    public byte[] IntToBytes(int num)
    {
    	byte[] bytes = new byte[4];
    	bytes[3] = bytes[2] = bytes[1] = bytes[0] = 0;
		//������������ת��Ϊ4�ֽڵ��ֽ�����
		bytes[3] = (byte)((num>>24)&0xff);
		bytes[2] = (byte)((num>>16)&0xff);
		bytes[1] = (byte)((num>>8)&0xff);
		bytes[0] = (byte)(num&0xff);
    	return bytes;
    }
    
    /**
     * ��4�ֽڵ�byte����ת��Ϊ�޷��ŵ�����
     * @param bytes 4�ֽ�����
     * @return ����
     */
    public int BytesToInt(byte[] bytes)
    {
    	int num = 0;
    	//datalen = Integer.parseInt(new String(buff, 0, 4));
		//ǰ�����⣬����������127ʱ���ֽ����λ�?1��Ĭ��Ϊ�з����������Գ��ָ��������?
		//ByteArrayInputStream ins = new ByteArrayInputStream(buff);
		for(int i=0;i<4;i++)
		{
			
			num = num+(bytes[i]&0xff)*(int)Math.pow(2, 8*i);
			Log.i("datalen"+i, ""+num);
		}
		return num;
    }
    
    /**
     * ���ֽ��������ͳ�type+dataLen+attrLen+attr+data��ʽ�������ڷ��ص�Bundle��
     * @param data �ֽ���
     * @return �ֽ����ǿգ�����Bundle�����򷵻�null.
     * @serialData ��ֵ DATA_TYPE ��Ӧ���͵�type;
     * @serialData DATA_DATALEN ��Ӧ���͵�datalen;
     * @serialData DATA_ATTRLEN��Ӧ���͵�dataLen
     * @serialData DATA_ATTR��Ӧ�ַ����Ե�attr
     * @serialData DATA_DATA��Ӧ�ֽ����͵�data
     */
    public Bundle ParseData(byte[] byteStream)
    {
    	if(byteStream==null){
    		return null;
    	}else if(byteStream.length<=0){
    		return null;
    	}else{
    		int type = (int)byteStream[0];//Integer.parseInt(new String(byteStream, 0, 1));
    		int dataLen = 0;// = Integer.parseInt(new String(byteStream, 1, 4));
    		byte[] b = new byte[4];
    		System.arraycopy(byteStream, 1, b, 0, 4);
    		dataLen = BytesToInt(b);
    		int attrLen =(int)(byteStream[5]); //Integer.parseInt(new String(byteStream, 5, 1));
    		Log.i("Parse", "type@"+type+"datalen@"+dataLen+"attrlen"+attrLen);
    		if(type<0||attrLen<=0||dataLen<=0){
    			//���ݲ��Ϸ�
        		return null;
    		}else{
    			Bundle bundle = new Bundle();
    			String attr;
				try {
					attr = new String(byteStream, 6, attrLen,Cconst.DECODE);
					Log.i("Parse","attr is" +attr+"&&datalen is "+dataLen);
	    			byte[] data = new byte[dataLen];
	    			System.arraycopy(byteStream, 6+attrLen, data, 0, dataLen);
	    			bundle.putInt(Cconst.DATA_TYPE, type);
	    			bundle.putInt(Cconst.DATA_DATALEN, dataLen);
	    			bundle.putInt(Cconst.DATA_ATTRLEN, attrLen);
	    			bundle.putString(Cconst.DATA_ATTR, attr);
	    			bundle.putByteArray(Cconst.DATA_DATA, data);
	    			Log.i("parse", "Out");
	    			return bundle;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
    		}
    	}	
    }
    
    /**
     * ������Ϣ����
     * @param bag ��Ϣ���ֽ���
     * @return �ɹ������ֽ������ȣ��񷵻�-1
     */
    public int SendData(byte[] bag)
    {
    	//Log.i("SendData", "���ݳ���"+bag.length+"&&&����"+new String(bag)+"%%State"+mState);
    	// ������ʱ����
        CommunicationThread r;
        // ͬ����ֵͨѶ�߳�
        synchronized (this) 
        {
            if (mState != STATE_CONNECTED||bag.length<=0) 
            	{
            	Log.i("SendData", "State11"+mState);
            		return -1;
            	}
            Log.i("SendData", "State12"+mState);
            r = mCommunicationThread;
        }
        //ͬ����������
        r.write(bag);
    	return bag.length;
    }

    /**
     * �����������ո�ʽ(type+dataLen+attrLen+attr+data)��ȡ����
     * @param in ������
     * @return �ɹ����ش��������ݵ�Bundle,���򷵻�null
     */
    private  Bundle ReadData(InputStream in)
    {
    	Bundle bundle = null;
    	if(in!=null){
    		int type = -1;
    		int datalen = 0;
    		int attrLen = -1;
    		byte[] buff = new byte[4];
    		buff[0] = buff[1] = buff[2] = buff[3] = 0;
    		try {
    			type = in.read();
    			if(type>1)
    				return null;
				Log.i("Read1", "type"+type);
				if(in.read(buff, 0, 4)==4){
					Log.i("buff", ""+buff);
					datalen = BytesToInt(buff);
					Log.i("Read2", "datalen"+datalen+"buff"+buff);
					if(datalen>0){
						attrLen = in.read();
						Log.i("Read3", "attrlen"+attrLen);
						if(attrLen>0){
							int len = 1+4+1+datalen+attrLen;
							Log.i("Read31", "len"+len);
							byte[] data = new byte[len];
							data[0] = (byte)(type&0xff);
							Log.i("Read32", "data[0]"+(int)data[0]);
							System.arraycopy(buff, 0, data, 1, 4);
							data[5] = (byte)(attrLen&0xff);
							in.read(data, 6, attrLen+datalen);
							Log.i("Read4", "data"+data);
							//in.close();
							return ParseData(data);
						}else{
							
						}//���Գ��ȶ�ȡ�쳣
					}else{
						
					}//���ݳ����쳣
				}else{
					
				}//���ݳ��ȶ�ȡ�쳣
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("ReadData", "�������Ǵ���");
				return null;
			}
    	}else{
    		
    	}//�������쳣
    	return bundle;
    }
    
  
    
    /**
     * �ж�Socket�Ƿ�������״̬
     * @return ��������״̬ ����true,���򷵻�false
     */
    private boolean SocketIsConn(BluetoothSocket socket){
    	boolean state = true;
    	if(socket!=null){
    		try {
				socket.getOutputStream().write(0xff);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				state = false;
			}
    	}else{
    		state = false;
    	}
    	
    	return state;
    }
    
    
    
    /**
     * ͬ����������
     * @param ���͵�����
     * @see CommunicationThread#write(byte[])
     */
    public void write(byte[] out) {
        // ������ʱ����
        CommunicationThread r;
        // ͬ������
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mCommunicationThread;
        }
        // ����
        //Log.i("Write", ""+new String(out));
        r.write(out);
        }

    /**
     * ����ʧ��
     */
    private void connectionFailed() {
    	setState(STATE_LISTEN);
        // ����ʧ����Ϣ
        Message msg = mHandler.obtainMessage(Cconst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Cconst.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * ���Ӷ�ʧ
     */
    private void connectionLost() {
        setState(STATE_LISTEN);

        Log.i("Lost", "001");
        if(mCommunicationThread!=null){
        	mCommunicationThread.cancel();
        	mCommunicationThread = null;
        }
        Log.i("Lost", "002");
        start();
        // ������Ϣ
        Message msg = mHandler.obtainMessage(Cconst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Cconst.TOAST, "Device connection was lost");
        msg.setData(bundle);
        Log.i("Lost", "002");
        mHandler.sendMessage(msg);
    }

    /**
     * �����߳�
     */
    private class AcceptThread extends Thread {
        // ����Socket
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // ��������socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // ���δ���ӣ���������?
            while (mState != STATE_CONNECTED) {
                try {
                    // ֱ�����أ�����һֱ����
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }
                // ����������
                if (socket != null) {
                    synchronized (BluetoothSharingService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // ����ͨѶ����
                            communication(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * �����ⲿ�豸�߳�
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // ��ȡBluetoothSocket Ϊ�˺ͺ͸������豸����
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Ϊ�˲�Ӱ���ٶȣ�ȡ��Ѱ��
            mAdapter.cancelDiscovery();

            // ����һ����BluetoothSocket������
            try {
                // ֱ�����أ�����һֱ����
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // �ر�
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // ��������
                BluetoothSharingService.this.start();
                return;
            }
            // ����
            synchronized (BluetoothSharingService.this) {
                mConnectThread = null;
            }
            // ����ͨѶ
            communication(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * ͨѶ�߳�
     */
    private class CommunicationThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        boolean isconn  = true;
        public CommunicationThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            
            
            
            // ��ȡ��
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mCommunicationThread");
            // ����������
            while (true) {
            	if(isconn){
            		try {
                		//����
                		if(/*SocketIsConn(mmSocket)*/true){//�Ƿ�����
                			if(true){//�п�������mmInStream.available()>0
                				Bundle bundle = ReadData(mmInStream);//��ȡ���ݣ�����Ҫ�����ж�����״̬
                				if(bundle!=null){
                					mHandler.obtainMessage(Cconst.MESSAGE_READ, bundle).sendToTarget();
            	            		bundle = null;
                				}else{
                					connectionLost();
            	            		break;
                				}
                			}else{
                				sleep(50);
                				continue;
                			}
                		}else{
                			connectionLost();
    	            		break;
                		}
    				}/* catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    					break;
    					//connectionLost();
    				}*/ catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            	}else{
            		try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		continue;
            	}
            	
            }
        }

        /**
         * д���ݵ������?.
         * @param buffer  ����
         */
        public void write(byte[] buffer) {
        	isconn = false;
        	
            try {
                mmOutStream.write(buffer);
                //mmOutStream.flush();
                // ������Ϣ
                Bundle b = ParseData(buffer);
                mHandler.obtainMessage(Cconst.MESSAGE_WRITE, -1, -1, b)
                .sendToTarget();
                
                
                /*
                Bundle msgb = new Bundle();
                int type = -1;
                
                String msg = "";
                if(b.getInt(Cconst.DATA_TYPE,-1)==Cconst.TYPE_FILE){
                	
                	type = Cconst.MSG_TYPE_FILE;
                	msgb.putString(Cconst.DATA_FILENAME, b.getString(Cconst.DATA_ATTR));
                	
                	//msg = "�ļ���"+b.getString(Cconst.DATA_ATTR)+"���ͳɹ���";
                }else if(b.getInt(Cconst.DATA_TYPE,-1)==Cconst.TYPE_COMMONDATA){
                	//Log.i("Data",new String(b.getByteArray(Cconst.DATA_ATTR)));
                	if(b.getString(Cconst.DATA_ATTR).equals(Cconst.ATTR_STRUCT))
                	{
                		type = Cconst.MSG_TYPE_STRUCT;
                		
                		msg = "��Ƭ���ͳɹ���";
                	}else{
                		//msg = new String(b.getByteArray(Cconst.DATA_DATA));
                		type = Cconst.MSG_TYPE_COMMDATA;
                		msgb.putString(Cconst.DATA_DATA, new String(b.getByteArray(Cconst.DATA_DATA)));
                	}
                }
                msgb.putInt(Cconst.DATA_TYPE, type);
                
                mHandler.obtainMessage(Cconst.MESSAGE_WRITE, -1, -1, msgb)
                        .sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                e.printStackTrace();
                connectionLost();
            }
            isconn = true;
        }

        public void cancel() {
            try {

            	//mmInStream.close();
            	//mmOutStream.close();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
