package com.orm.system;

/**
 * ��ܵ�������
 * @author Administrator
 *
 */
public class SystemInit {
	private static boolean isStartUp = false ;//�Ƿ�����
	
	public static void systemInit(){
		if( isStartUp == false ){
			startUp();
			isStartUp = true ;
		}
	}
	
	//��ʼ��applicationContext.xml(���ݿ�������Ϣ)
	//ʵ������ݿ���ӳ��
	//��ʼ�����ݿ����ӳ���
	private static boolean startUp(){
		return false ;
	}
}