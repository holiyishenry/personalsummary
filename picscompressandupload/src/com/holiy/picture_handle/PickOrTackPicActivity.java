package com.holiy.picture_handle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.holiy.picture_handle.util.GlobalUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ���ջ���ѡ��ͼ��ͼƬ
 * @author holiy
 */
public class PickOrTackPicActivity extends Activity {
	private final int TACK_PIC = 1;//����
	private final int PICK_PIC = 2;//ѡ��ͼƬ
	private final int TACK_OR_PICK_PIC = 3;
	private final int HANDLE_PIC_SCREENSHOT = 4;//����ͼƬ����ͼ
	private ImageView imgView;
	private String tempPicture;
	/**
	 * ���ջ�ѡ��ͼƬ���ļ������п���ֻ��1��ͼƬ�������ж���ͼƬ
	 */
	private List<String> pictures;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_or_tack_pic);
		imgView = (ImageView)findViewById(R.id.showImage);
		pictures = new ArrayList<String>();
	}
	//���պ��ͼ
	public void tackPicture(View view){
		//�ļ���(�Ե�ǰʱ������)
//		SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.CHINA);
//		String fileName = timeStamp.format(new Date());
		
//		ֱ����long��ʱ��
		String fileName = "" + new Date().getTime();//ǿת
		tackAPicture(GlobalUtil.getPicFolder("pic"), fileName);
		
	}
	//��ͼ����ѡ��ͼƬ���ͼ
	public void pickPicture(View view){
		pickAPicture();
	}
	//һ��ҳ��㶨���ջ�ѡ������΢�Ÿ���ͷ��(ֻ��1��)
	public void tackOrPickPic(View view){
		Intent intent = new Intent(this, PickAllPictureFromLibActivity.class);
		this.startActivity(intent);
	}
	public void getPicture(View view){
		String path = GlobalUtil.getPicPath("pic", "1417703447042");
		if(!path.equals("NOTEXIST")){
			File file = new File(path);
			Uri uri = Uri.fromFile(file);
			imgView.setImageURI(uri);
		}else{
			Toast.makeText(this, "�ļ�������", 0).show();
		}
	}
	
	/**
	 * ��һ��ͼƬ������Ϊ����ͼƬ��·����·����
	 * @param path
	 * @param picName
	 */
	public void tackAPicture(String path, String picName){
		//sd������
			Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//����·��
			tempPicture = path + picName;
			File fileURL = new File(tempPicture);
			openCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileURL));
			startActivityForResult(openCamera,TACK_PIC);
	}
	
	/**
	 * ��ϵͳͼ����ѡ��1��ͼƬ
	 */
	public void pickAPicture(){
		Intent openPicture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(openPicture, PICK_PIC);
	}
	
	/**
	 * ͼƬ���������ʾ��imageview��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TACK_PIC && resultCode == Activity.RESULT_OK){//�������
			startPhotoZoom(Uri.fromFile(new File(tempPicture)));
		}else if(requestCode == PICK_PIC && resultCode == Activity.RESULT_OK && data != null){//��ͼ��ѡ��ͼƬ�ɹ�
			//��ͼ��ѡ��ͼƬ
			Uri selectedImage = data.getData();
			String[] filePathColumns={MediaColumns.DATA};
			Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null,null, null);
			//--------------------ѡȡ1��ͼƬstart---------------------------------
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePathColumns[0]);
			tempPicture = c.getString(columnIndex);
			c.close();
			startPhotoZoom(Uri.fromFile(new File(tempPicture)));
			//--------------------ѡȡ1��ͼƬend------------------------------------
			
		}else if(requestCode == HANDLE_PIC_SCREENSHOT && resultCode == Activity.RESULT_OK){ //��ͼ�ɹ�
			//ѹ��ͼƬ�����浽�µĵ�ַ
			Bundle extras = data.getExtras();
			if(extras != null){
				Bitmap image = extras.getParcelable("data");//��ȡ������������
				uploadThread(image);
			}
		}
	}
	
	/**
	 * ϵͳ������ȡͼƬ��С150*150
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, HANDLE_PIC_SCREENSHOT);
	}
	
	/**
	 * �ر�ע�⣺�ļ�����Ҫ����png����jpeg��ͼƬ��ʽ��׺����Ȼ����ϵͳ��ͼƬ���г��֡�
	 * ѹ��ͼƬ���ϴ�ͼƬ��������
	 */
	private void uploadThread(Bitmap image){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//ʵ��֤��jpeg��pngѹ�����С
        FileOutputStream fos = null;
        try {
        	fos = new FileOutputStream(new File(tempPicture),false);//����ԭ����ͼƬ
        	fos.write(baos.toByteArray());
        	fos.flush();
        	Log.i("info", "send head img length" + baos.toByteArray().length);
        	//�����Ի�����ʾ���ڱ���
        	new Thread(){
        		@Override
				public void run() {
        			//�ϴ�ͼƬ����
        		};
        	}.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			imgView.setImageBitmap(image);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pick_or_tack_pic, menu);
		return true;
	}

}
