package com.wukesly.moduleofbluetooth;


import java.io.Serializable;

/**
 * ��ϵ�����ݽṹ��
 * @author 123
 *
 */

public class ContactsData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ����
	 */
	public String name;
	/**
	 * �绰����
	 */
	public String phoneNum;
	/**
	 * ��ַ
	 */
	public String addr;
	/**
	 * ����
	 */
	public String email;
	

	public ContactsData() {
		// TODO Auto-generated constructor stub
		name = "";
		phoneNum = "";
		addr = "";
		email = "";
	}

}
