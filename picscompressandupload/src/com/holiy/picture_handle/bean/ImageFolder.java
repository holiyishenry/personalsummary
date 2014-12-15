package com.holiy.picture_handle.bean;
/**
 * 描述手机中包含图片的文件夹
 * @author holiy
 *
 */
public class ImageFolder{
	/**
	 * 文件夹名称
	 */
	private String name;
	/**
	 * 图片个数
	 */
	private int imgCount;
	/**
	 * 文件夹路径
	 */
	private String dir;
	/**
	 * 文件夹中第一张图片，用于显示图集
	 */
	private String firstImagePath;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getImgCount() {
		return imgCount;
	}
	public void setImgCount(int imgCount) {
		this.imgCount = imgCount;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}
	public String getFirstImagePath() {
		return firstImagePath;
	}
	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}
	
	
}
