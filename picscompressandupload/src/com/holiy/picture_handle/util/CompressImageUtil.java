package com.holiy.picture_handle.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * ѹ��ͼƬ������
 * @author holiy
 *
 */
public class CompressImageUtil {
	/**
	 * ��ͼƬѹ�����ַ�����,����ѹ��
	 */
	public static byte[] compreImageToBytes(Bitmap image,int size){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024 > size) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
        }  
       // ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        //Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
        return baos.toByteArray(); 
	}
	
	/**
	 * ͼƬ������ѹ����������ַ�����
	 */
	public static byte[] getImageBytes(String srcPath){
		 BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
	        newOpts.inJustDecodeBounds = true;  
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//��ʱ����bmΪ��  
	          
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
	        float hh = 800f;//�������ø߶�Ϊ800f  
	        float ww = 480f;//�������ÿ��Ϊ480f  
	        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
	        int be = 1;//be=1��ʾ������  
	        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
	            be = Math.round(w / ww);  
	        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݸ߶ȹ̶���С����  
	            be = Math.round(h / hh);  
	        }  
	        /*if (be <= 0)  
	            be = 1;  */
	        newOpts.inSampleSize = be;//�������ű���  
	        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
	        return compreImageToBytes(bitmap,80);//ѹ���ñ�����С���ٽ�������ѹ��  
	}
	
	/**
	 * ѭ��ѹ��,ֱ��ͼƬ����100k,��������ʧ
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024 > 30) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
        return bitmap;  
    } 
	
	/**
	 * ͼƬ��������Сѹ������������·����ȡͼƬ��ѹ����
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//��ʱ����bmΪ��  
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
        float hh = 960f;//�������ø߶�Ϊ800f  
        float ww = 540f;//�������ÿ��Ϊ480f  
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
        int be = 1;//be=1��ʾ������  
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
            be = Math.round(w / ww);  
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
            be = Math.round(h / hh);  
        }  
        /*if (be <= 0)  
            be = 1;  */
        newOpts.inSampleSize = be;//�������ű���  
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
    }  
	
	/**
	 * ͼƬ��������Сѹ������������BitmapͼƬѹ����
	 * @param image
	 * @return
	 */
	public static Bitmap comp(Bitmap image) {  
	      
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	    if( baos.toByteArray().length / 1024 > 1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
	        baos.reset();//����baos�����baos  
	        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
	    newOpts.inJustDecodeBounds = true;  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
	    float hh = 853f;//�������ø߶�Ϊ800f  
	    float ww = 480f;//�������ÿ��Ϊ480f  
	    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
	    int be = 1;//be=1��ʾ������  
	    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0)  
	        be = 1;  
	    newOpts.inSampleSize = be;//�������ű���  
	    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
	    isBm = new ByteArrayInputStream(baos.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
	}  
	
	/**
	 * �Ƚ�����ͼƬ�Ƿ���ͬ
	 */
	public static boolean compare2Image(Bitmap bmp,Bitmap srcBmp){
		int iteration = 0;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		if (width != srcBmp.getWidth() || height != srcBmp.getHeight()) {
			return false;
		}
		if (width < height) {
			iteration = width;
		} else {
			iteration = height;
		}
		for (int i = 0; i < iteration; i++) {
			if (bmp.getPixel(i, i) != srcBmp.getPixel(i, i)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean downloadImage(String imageUrl,File filePath){
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(imageUrl);
			URLConnection conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			if (is == null) {
				throw new RuntimeException();
			}
			//�����ļ���·��
			//File downFilePath = new File(path);
			fos = new FileOutputStream(filePath);
			byte[] buffer = new byte[128];
			do {
				int numread = is.read(buffer); 
				if (numread < 0) {
					break;
				}
				fos.write(buffer,0,numread);
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * ����·��ѹ��ͼƬ������
	 */
	public boolean compressImgByPath(String srcImgPath,String targetImgPath){
		byte[] imageBytes = getImageBytes(srcImgPath);
		
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetImgPath);
            fos.write(imageBytes);
            fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	

	//------------------------------ѹ��ͼƬ����--------------------------------------
	public static boolean startCompress(String fromFilePath, File toFile, int fileSize)
	{
		return compressQuality(compressSize(fromFilePath), toFile, fileSize);
	}
	
	private static Bitmap compressSize(String fromFilePath)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;//��ȡ������
		Bitmap bitmap = BitmapFactory.decodeFile(fromFilePath, options);

		options.inJustDecodeBounds = false;
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		float height = 480f;
//		float height = 800f;
		float width = 480f;
		int sampleSize = 1;//������
		
//		if (outWidth > outHeight && outWidth > width)
//		{
//			sampleSize = (int) (outWidth / width);
//		}
//		else if (outWidth < outHeight && outHeight > height)
//		{
//			sampleSize = (int) (outHeight / height);
//		}
		
		if (outWidth > width || outHeight > height)
		{
			if(outWidth>outHeight)sampleSize = (int) (outWidth / width);
			else sampleSize = (int) (outHeight / height);
		}	
		if (sampleSize <= 0)
			sampleSize = 1;
		
		options.inSampleSize = sampleSize;//���ò�����		
		options.inPurgeable = true;//����ͼƬ
		options.inInputShareable = true;//��ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������
		
		bitmap = BitmapFactory.decodeFile(fromFilePath, options);
		return bitmap;
	}
	
	private static boolean compressQuality(Bitmap bmp, File toFile, int fileSize)
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int options = 100;
        if(bmp==null)return false;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        //��ͼƬѹ����fileSize K����
		while (baos.toByteArray().length / 1024 > fileSize && options > 0)
        {   
            baos.reset();  
            options -= 5;  
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }          
        //����ͼƬ
        try {
			FileOutputStream fos = new FileOutputStream(toFile);             
			fos.write(baos.toByteArray());  
			fos.flush();  
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}
        
    }
    	
}
