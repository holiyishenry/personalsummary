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
 * 压缩图片工具类
 * @author holiy
 *
 */
public class CompressImageUtil {
	/**
	 * 把图片压缩成字符数组,质量压缩
	 */
	public static byte[] compreImageToBytes(Bitmap image,int size){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024 > size) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
       // ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        //Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return baos.toByteArray(); 
	}
	
	/**
	 * 图片按比例压缩，编码成字符数组
	 */
	public static byte[] getImageBytes(String srcPath){
		 BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	        newOpts.inJustDecodeBounds = true;  
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
	          
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
	        float hh = 800f;//这里设置高度为800f  
	        float ww = 480f;//这里设置宽度为480f  
	        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	        int be = 1;//be=1表示不缩放  
	        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	            be = Math.round(w / ww);  
	        } else if (w < h && h > hh) {//如果高度高的话根据高度固定大小缩放  
	            be = Math.round(h / hh);  
	        }  
	        /*if (be <= 0)  
	            be = 1;  */
	        newOpts.inSampleSize = be;//设置缩放比例  
	        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
	        return compreImageToBytes(bitmap,80);//压缩好比例大小后再进行质量压缩  
	}
	
	/**
	 * 循环压缩,直到图片少于100k,质量有损失
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024 > 30) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    } 
	
	/**
	 * 图片按比例大小压缩方法（根据路径获取图片并压缩）
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 960f;//这里设置高度为800f  
        float ww = 540f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = Math.round(w / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = Math.round(h / hh);  
        }  
        /*if (be <= 0)  
            be = 1;  */
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }  
	
	/**
	 * 图片按比例大小压缩方法（根据Bitmap图片压缩）
	 * @param image
	 * @return
	 */
	public static Bitmap comp(Bitmap image) {  
	      
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	    if( baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免再生成图片（BitmapFactory.decodeStream）时溢出    
	        baos.reset();//重置baos即清空baos  
	        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
	    float hh = 853f;//这里设置高度为800f  
	    float ww = 480f;//这里设置宽度为480f  
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;//be=1表示不缩放  
	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0)  
	        be = 1;  
	    newOpts.inSampleSize = be;//设置缩放比例  
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    isBm = new ByteArrayInputStream(baos.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
	}  
	
	/**
	 * 比较两张图片是否相同
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
			//下载文件的路径
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
	 * 根据路径压缩图片并保存
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
	

	//------------------------------压缩图片工具--------------------------------------
	public static boolean startCompress(String fromFilePath, File toFile, int fileSize)
	{
		return compressQuality(compressSize(fromFilePath), toFile, fileSize);
	}
	
	private static Bitmap compressSize(String fromFilePath)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;//读取边属性
		Bitmap bitmap = BitmapFactory.decodeFile(fromFilePath, options);

		options.inJustDecodeBounds = false;
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		float height = 480f;
//		float height = 800f;
		float width = 480f;
		int sampleSize = 1;//采样率
		
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
		
		options.inSampleSize = sampleSize;//设置采样率		
		options.inPurgeable = true;//净化图片
		options.inInputShareable = true;//当系统内存不够时候图片自动被回收
		
		bitmap = BitmapFactory.decodeFile(fromFilePath, options);
		return bitmap;
	}
	
	private static boolean compressQuality(Bitmap bmp, File toFile, int fileSize)
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int options = 100;
        if(bmp==null)return false;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        //将图片压缩至fileSize K以下
		while (baos.toByteArray().length / 1024 > fileSize && options > 0)
        {   
            baos.reset();  
            options -= 5;  
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }          
        //保存图片
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
