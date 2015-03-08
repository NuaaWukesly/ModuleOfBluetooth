package com.wukesly.moduleofbluetooth;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.SystemClock;

/**
 * ��Ϣ��
 * @author 123
 *
 */
public class CMsg {
	
	/**
	 * ��Ϣ���ͱ�ʶ
	 * @see Cconst#MSG_TYPE_COMMDATA
	 * @see Cconst#MSG_TYPE_FILE
	 * @see Cconst#MSG_TYPE_STRUCT
	 */
	private int MSG_TYPE = -1;
	/**
	 * ��Ϣ������Me����He
	 */
	private int MeOrHe = -1;
	
	/**
	 * @return the meOrHe
	 */
	public int getMeOrHe() {
		return MeOrHe;
	}



	/**
	 * ������Ϣ�Ĺ���{@link Cconst#MEORHE_HE}����{@link Cconst#MEORHE_ME}
	 * @param meOrHe the meOrHe to set
	 */
	public void setMeOrHe(int meOrHe) {
		MeOrHe = meOrHe;
	}

	/**
	 * ��ʾ����Ϣ
	 */
	private String dispMsg = "";
	/**
	 * ����ϢΪ {@link Cconst#MSG_TYPE_STRUCT}ʱ���洢��ϵ����Ϣ�ṹ��
	 */
	private ContactsData cd=null;
	/**
	 * ����ϢΪ{@link Cconst#MSG_TYPE_FILE}ʱ���洢�ļ���·��
	 */
	private String path = null;
	/**
	 * ʱ���?
	 */
	private Date date = null;
	/**
	 * ��ȡ�ַ�����ʽ�ĵ�ʱ��
	 * @return the date
	 */
	public String getDate() {
		SimpleDateFormat adf = new SimpleDateFormat("MM-dd HH:mm");
		return adf.format(this.date);
	}

	

	/**
	 * ���캯��
	 * @param msgType ��Ϣ����
	 * @param dispMsg ��ʾ����Ϣ
	 */
	public CMsg(int msgType,String dispMsg){
		this.MSG_TYPE = msgType;
		this.dispMsg = dispMsg;
		//ʱ���?
		this.date = new Date(System.currentTimeMillis());
	}

	/**
	 * @return the MSG_TYPE
	 */
	public int getMSG_TYPE() {
		return MSG_TYPE;
	}

	/**
	 * @param MSG_TYPE the mSG_TYPE to set
	 */
	public void setMSG_TYPE(int mSG_TYPE) {
		MSG_TYPE = mSG_TYPE;
	}

	/**
	 * @return the dispMsg
	 */
	public String getDispMsg() {
		return dispMsg;
	}

	/**
	 * @param dispMsg the dispMsg to set
	 */
	public void setDispMsg(String dispMsg) {
		this.dispMsg = dispMsg;
	}

	/**
	 * @return the cd
	 */
	public ContactsData getCd() {
		return cd;
	}

	/**
	 * @param cd the cd to set
	 */
	public void setCd(ContactsData cd) {
		this.cd = cd;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	
	

}
