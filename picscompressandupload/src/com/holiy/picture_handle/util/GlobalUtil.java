package com.holiy.picture_handle.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
/**
 * 1、文件操作，整个项目有个公共的文件夹，如果要区分不同类型文件，写入子文件（childFolderName），输入空则代表在公共目录下
 * 2、判断某个文件是否存在，首先检查外置SD卡，再检查内置SD卡
 * @author holiy
 *
 */
public class GlobalUtil{
	/**
	 * 不给别人new工具类
	 */
	private GlobalUtil(){
		/* cannot be instantiated */   
        throw new UnsupportedOperationException("cannot be instantiated");
	}
	
	/**
	 * 自己app创建文件的地址，如需更改名称，直接在这里改，一般一个app创建1个总的folder
	 */
	private static final String MYAPPFOLDERNAME = "/holiyutil/";
	
	/**
	 * 智能判断手机是否存在sd卡，如果sd卡不存在，则将创建的文件路径指定为手机本地存储
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
		if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd卡存在
			String path =  Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ MYAPPFOLDERNAME + childFoler;
			File filePath = new File(path);
			if(!filePath.exists()){//如果不存在该路径则自动创建
				filePath.mkdirs();
			}
			return path;
		}else{//sd卡不存在
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
	 * 获取手机内置SD卡地址
	 * @return
	 */
	private static String getLocalPath(String childFoler){
		String localPath = "";
		File mntFolder = new File("/mnt");
		String folders[] = mntFolder.list();//获取子目录
		for(int i = 0; i < folders.length; i++){
			if(("/mnt/" + folders[i]).equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
				continue;//外置SD卡
			}
			//存在 DICM 或者 Pictures文件夹的为内置存储卡路径
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
		//异常情况，什么都没找到
		if("".equals(localPath)){
			localPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ MYAPPFOLDERNAME + childFoler;
		}
		return localPath;
	} 
	
	/**
	 * 判断文件是否存在，如果存在则返回文件路径
	 * @param childFolderName 所在文件夹
	 * @param picName 文件名
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
		String localPath = getLocalPath(childFoler);//当查找本地地址失败时，localPath为默认sd卡地址
		if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd卡存在
			File filePath = new File(sdPath,picName);
			if(filePath.exists()){
				return sdPath + picName;//在SD卡里
			}else{
				if(sdPath.equals(localPath)){
					return "NOTEXIST";//后面不用执行了
				}
				File localFilePath = new File(localPath, picName);
				if(localFilePath.exists()){//在本地存储里
					return localPath + picName;
				}else{
					return "NOTEXIST";
				}
			}
		}else{
			File localFilePath = new File(localPath, picName);
			if(localFilePath.exists()){//在本地存储里
				return localPath + picName;
			}else{
				return "NOTEXIST";
			}
		}
	}
}
