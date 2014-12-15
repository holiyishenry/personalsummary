package com.holiy.picture_handle.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
/**
 * 1���ļ�������������Ŀ�и��������ļ��У����Ҫ���ֲ�ͬ�����ļ���д�����ļ���childFolderName���������������ڹ���Ŀ¼��
 * 2���ж�ĳ���ļ��Ƿ���ڣ����ȼ������SD�����ټ������SD��
 * @author holiy
 *
 */
public class GlobalUtil{
	/**
	 * ��������new������
	 */
	private GlobalUtil(){
		/* cannot be instantiated */   
        throw new UnsupportedOperationException("cannot be instantiated");
	}
	
	/**
	 * �Լ�app�����ļ��ĵ�ַ������������ƣ�ֱ��������ģ�һ��һ��app����1���ܵ�folder
	 */
	private static final String MYAPPFOLDERNAME = "/holiyutil/";
	
	/**
	 * �����ж��ֻ��Ƿ����sd�������sd�������ڣ��򽫴������ļ�·��ָ��Ϊ�ֻ����ش洢
	 * @param childFolderName
	 * @return
	 */
	public static String getPicFolder(String childFolderName){
		String childFoler;
		if(childFolderName == null || "".equals(childFolderName)){
			childFoler = "";
		}else{
			childFoler = childFolderName + "/";
		}
		String sdStatus = Environment.getExternalStorageState();
		if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd������
			String path =  Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ MYAPPFOLDERNAME + childFoler;
			File filePath = new File(path);
			if(!filePath.exists()){//��������ڸ�·�����Զ�����
				filePath.mkdirs();
			}
			return path;
		}else{//sd��������
			String localPath = "";
			localPath = getLocalPath(childFoler);
			File localFilePath = new File(localPath);
			if(!localFilePath.exists()){
				localFilePath.mkdirs();
			}
			return localPath;
		}
	}
	
	/**
	 * ��ȡ�ֻ�����SD����ַ
	 * @return
	 */
	private static String getLocalPath(String childFoler){
		String localPath = "";
		File mntFolder = new File("/mnt");
		String folders[] = mntFolder.list();//��ȡ��Ŀ¼
		for(int i = 0; i < folders.length; i++){
			if(("/mnt/" + folders[i]).equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
				continue;//����SD��
			}
			//���� DICM ���� Pictures�ļ��е�Ϊ���ô洢��·��
			File dcimFile = new File("/mnt/" + folders[i] + "/DCIM");
			if(dcimFile.exists()){
				localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + childFoler;
				break;
			}
			File dcimFile1 = new File("/mnt/" + folders[i] + "/dcim");
			if(dcimFile1.exists()){
				localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + childFoler;
				break;
			}
			File picturesFile = new File("/mnt/" +folders[i] + "/Pictures");
			if(picturesFile.exists()){
				localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + picturesFile;
				break;
			}
		}
		//�쳣�����ʲô��û�ҵ�
		if("".equals(localPath)){
			localPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ MYAPPFOLDERNAME + childFoler;
		}
		return localPath;
	} 
	
	/**
	 * �ж��ļ��Ƿ���ڣ���������򷵻��ļ�·��
	 * @param childFolderName �����ļ���
	 * @param picName �ļ���
	 * @return
	 */
	public static String getPicPath(String childFolderName, String picName){
		String childFoler;
		if(childFolderName == null || "".equals(childFolderName)){
			childFoler = "";
		}else{
			childFoler = childFolderName + "/";
		}
		
		String sdStatus = Environment.getExternalStorageState();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ MYAPPFOLDERNAME + childFoler;
		String localPath = getLocalPath(childFoler);//�����ұ��ص�ַʧ��ʱ��localPathΪĬ��sd����ַ
		if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd������
			File filePath = new File(sdPath,picName);
			if(filePath.exists()){
				return sdPath + picName;//��SD����
			}else{
				if(sdPath.equals(localPath)){
					return "NOTEXIST";//���治��ִ����
				}
				File localFilePath = new File(localPath, picName);
				if(localFilePath.exists()){//�ڱ��ش洢��
					return localPath + picName;
				}else{
					return "NOTEXIST";
				}
			}
		}else{
			File localFilePath = new File(localPath, picName);
			if(localFilePath.exists()){//�ڱ��ش洢��
				return localPath + picName;
			}else{
				return "NOTEXIST";
			}
		}
	}
}
