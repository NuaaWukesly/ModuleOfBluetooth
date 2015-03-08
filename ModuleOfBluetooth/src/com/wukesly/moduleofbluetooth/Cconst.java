package com.wukesly.moduleofbluetooth;


/**
 * ����ͨѶģ�鳣���࣬�������ڴ�Ÿ��ֳ���?
 * @author 123
 *@see Cconst
 */
public class Cconst {
	
	/**
	 * Ӧ��·��������·��
	 */
	public static final String STORE_PATH = "/sdcard/SecureContact/BT/recFile";
	/**
	 * ����
	 */
	public static final String DECODE = "UTF-8";
	
	// Debugging
    public static final String TAG = "BluetoothChat";
    public static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
	
	
	//ͨѶЭ��������������ݳ�������ʱ֧��������ͨ���ݸ��?
	/**
	 * ��������ͨ��������
	 */
	public static final int TYPE_COMMONDATA = 0;
	/**
	 * �������ļ�����
	 */
	public static final int TYPE_FILE = 1;
	
	/**
     * �����򣬱�ʾ��ͨ���ݵ�String��������
     */
    public static final String ATTR_STRING = "String";
    /**
     * �����򣬱�ʾ��ͨ���ݵ�Int��������
     */
    public static final String ATTR_INT="Int";
    /**
     * �����򣬱�ʾ��ͨ���ݵ�Byte������������
     */
    public static final String ATTR_BYTES = "BYTE";
    /**
     * �����򣬱�ʾ��ͨ���ݵ���ϵ�˽ṹ������
     */
    public static final String ATTR_STRUCT = "STRUCT";
    
    //���������ݴ���ʹ��Blunder�������Ǵ������ݶ�Ӧ�ļ�(key)
    /**
     * Blunder��ȡ����ʱ�����������ݵļ�������Ϊint
     */
    public static final String DATA_TYPE = "DATA_TYPE";
    /**
     * Blunder��ȡ����ʱ�������򳤶������ݵļ�������Ϊint
     */
    public static final String DATA_DATALEN = "DATA_DATALEN";
    /**
     * Blunder��ȡ����ʱ�������򳤶������ݵļ�������Ϊint
     */
    public static final String DATA_ATTRLEN = "DATA_ATTRLEN";
    /**
     * Blunder��ȡ����ʱ�����������ݵļ�������ΪString
     */
    public static final String DATA_ATTR = "DATA_ATTR";
    /**
     * Blunder��ȡ����ʱ�����������ݵļ�������Ϊbyte[]
     */
    public static final String DATA_DATA = "DATA_DATA;";
	public static final String DATA_FILENAME = "DATA_FILENAME";
    
	//��Ϣ���ͳ���
	/**
	 * ��ͨ������Ϣ
	 */
	public static final int MSG_TYPE_COMMDATA = 0;
	/**
	 * ��ϵ����Ϣ��Ϣ
	 */
	public static final int MSG_TYPE_STRUCT = 1;
	/**
	 * �ļ���Ϣ
	 */
	public static final int MSG_TYPE_FILE = 2;
	//��Ϣ��������
	/**
	 * ��Ϣ��������
	 */
	public static final int MEORHE_ME = 0;
	/**
	 * ��Ϣ�����ڶԷ�
	 */
	public static final int MEORHE_HE = 1;
	
	
}
