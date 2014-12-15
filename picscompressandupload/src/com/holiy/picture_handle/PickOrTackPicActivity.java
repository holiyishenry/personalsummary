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
 * 拍照或者选择图库图片
 * @author holiy
 */
public class PickOrTackPicActivity extends Activity {
	private final int TACK_PIC = 1;//拍照
	private final int PICK_PIC = 2;//选择图片
	private final int TACK_OR_PICK_PIC = 3;
	private final int HANDLE_PIC_SCREENSHOT = 4;//处理图片，截图
	private ImageView imgView;
	private String tempPicture;
	/**
	 * 拍照或选择图片的文件名，有可能只有1张图片，可能有多张图片
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
	//拍照后截图
	public void tackPicture(View view){
		//文件名(以当前时间命名)
//		SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.CHINA);
//		String fileName = timeStamp.format(new Date());
		
//		直接用long型时间
		String fileName = "" + new Date().getTime();//强转
		tackAPicture(GlobalUtil.getPicFolder("pic"), fileName);
		
	}
	//从图库中选择图片后截图
	public void pickPicture(View view){
		pickAPicture();
	}
	//一个页面搞定拍照或选择，类似微信更改头像(只有1张)
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
			Toast.makeText(this, "文件不存在", 0).show();
		}
	}
	
	/**
	 * 拍一张图片，参数为保存图片的路径和路径名
	 * @param path
	 * @param picName
	 */
	public void tackAPicture(String path, String picName){
		//sd卡存在
			Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//保存路径
			tempPicture = path + picName;
			File fileURL = new File(tempPicture);
			openCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileURL));
			startActivityForResult(openCamera,TACK_PIC);
	}
	
	/**
	 * 从系统图库中选择1张图片
	 */
	public void pickAPicture(){
		Intent openPicture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(openPicture, PICK_PIC);
	}
	
	/**
	 * 图片处理完后显示到imageview中
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TACK_PIC && resultCode == Activity.RESULT_OK){//拍照完成
			startPhotoZoom(Uri.fromFile(new File(tempPicture)));
		}else if(requestCode == PICK_PIC && resultCode == Activity.RESULT_OK && data != null){//从图库选择图片成功
			//从图库选择图片
			Uri selectedImage = data.getData();
			String[] filePathColumns={MediaColumns.DATA};
			Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null,null, null);
			//--------------------选取1张图片start---------------------------------
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePathColumns[0]);
			tempPicture = c.getString(columnIndex);
			c.close();
			startPhotoZoom(Uri.fromFile(new File(tempPicture)));
			//--------------------选取1张图片end------------------------------------
			
		}else if(requestCode == HANDLE_PIC_SCREENSHOT && resultCode == Activity.RESULT_OK){ //截图成功
			//压缩图片并保存到新的地址
			Bundle extras = data.getExtras();
			if(extras != null){
				Bitmap image = extras.getParcelable("data");//获取截屏过的数据
				uploadThread(image);
			}
		}
	}
	
	/**
	 * 系统方法截取图片大小150*150
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, HANDLE_PIC_SCREENSHOT);
	}
	
	/**
	 * 特备注意：文件名不要出现png或者jpeg等图片格式后缀，不然会在系统的图片库中出现。
	 * 压缩图片后，上传图片到服务器
	 */
	private void uploadThread(Bitmap image){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//实践证明jpeg比png压缩后更小
        FileOutputStream fos = null;
        try {
        	fos = new FileOutputStream(new File(tempPicture),false);//覆盖原本的图片
        	fos.write(baos.toByteArray());
        	fos.flush();
        	Log.i("info", "send head img length" + baos.toByteArray().length);
        	//弹出对话框提示正在保存
        	new Thread(){
        		@Override
				public void run() {
        			//上传图片代码
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
