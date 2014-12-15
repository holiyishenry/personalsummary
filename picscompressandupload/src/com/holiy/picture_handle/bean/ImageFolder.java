package com.holiy.picture_handle.bean;
/**
 * �����ֻ��а���ͼƬ���ļ���
 * @author holiy
 *
 */
public class ImageFolder{
	/**
	 * �ļ�������
	 */
	private String name;
	/**
	 * ͼƬ����
	 */
	private int imgCount;
	/**
	 * �ļ���·��
	 */
	private String dir;
	/**
	 * �ļ����е�һ��ͼƬ��������ʾͼ��
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
