package com.holiy.picture_handle;

import java.io.File;
import java.io.FilenameFilter;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.holiy.picture_handle.ListImageDirPopupWindow.OnImageDirSelected;
import com.holiy.picture_handle.bean.ImageFolder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 图片查看器，从图库中选择多张图片，或拍照，仿制微信6.0图片选择功能
 * @author holiy
 */
public class PickAllPictureFromLibActivity extends Activity implements OnImageDirSelected{
	private ProgressDialog progressDialog;//打开图集时缓冲
	/**
	 * 图片数量
	 */
	private int mPicsSize;
	/**
	 * 文件数量最多的文件夹路径
	 */
	private File mMaxCountFolderDir;
	/**
	 * 所有图片
	 */
	private List<String> mImgs;
	
	private GridView gridView;
	private ShowPicsAdaptar mAdaptar;
	
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();
	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
	
	private TextView folderNameTv, mImageCount;
	private Button finishBtn, previewBtn;
	
	private int totalCount;//图片总数
	private int mScreenHeight;
	
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			initData();//绑定数据
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choise_more_picture_main);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		
		initView();
		getImages();
		initEvents();
		
	}

	private void initView(){
		gridView = (GridView)findViewById(R.id.show_pics_gridview);
		folderNameTv = (TextView)findViewById(R.id.id_choose_dir);
		mImageCount = (TextView)findViewById(R.id.pic_total_count);
		finishBtn = (Button) findViewById(R.id.pick_finish_btn);
		previewBtn =(Button) findViewById(R.id.pics_preview_bt);
	}
	/**
	 * 获取所有图片，在获取图片的父文件夹，记录不同文件夹
	 */
	private void getImages(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PickAllPictureFromLibActivity.this.getContentResolver();
				// 只查询jpeg和png的图片
				Cursor cursor = mContentResolver.query(mImageUri, null ,
						MediaStore.Images.Media.MIME_TYPE + "=? or " 
				+ MediaStore.Images.Media.MIME_TYPE + "=?", 
				new String[]{"image/jpeg", "image/png"}, 
				MediaStore.Images.Media.DATE_MODIFIED);
//				Log.i("count:", "count:" + cursor.getCount());
				while(cursor.moveToNext()){
					//获取图片路径
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//获取父路径名
					File parentFile = new File(path).getParentFile();
					if(parentFile == null){
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();
					ImageFolder imgFolder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹
					if (mDirPaths.contains(dirPath))
					{
						continue;
					}else{
						mDirPaths.add(dirPath);
						imgFolder = new ImageFolder();;
						imgFolder.setFirstImagePath(path);
						imgFolder.setDir(dirPath);
					}
					//过滤出图片
					int picsSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					
					totalCount += picsSize;
					imgFolder.setImgCount(picsSize);//每个文件夹里图片数量
					mImageFolders.add(imgFolder);
					
					if(picsSize > mPicsSize){
						mPicsSize = picsSize;
						mMaxCountFolderDir = parentFile;
					}
				}
				cursor.close();
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
		
	}
	/**
	 * 绑定数据
	 */
	private void initEvents(){
		folderNameTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(folderNameTv, 0, 0);
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		
		previewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ShowPicsAdaptar.mSelectedImage.size() > 0){
					Intent previewIntent = new Intent(PickAllPictureFromLibActivity.this, PreviewPicByViewpagerActivity.class);
					PickAllPictureFromLibActivity.this.startActivity(previewIntent);
				}
			}
		});
	}
	/**
	 * 绑定数据
	 */
	private void initData(){
		if(mMaxCountFolderDir == null){
			Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}
		List<String> templist = Arrays.asList(mMaxCountFolderDir.list());
		mImgs = new ArrayList<String>();
		mImgs.add("拍照");//添加标识,标识拍照
		mImgs.addAll(templist);
		templist = null;
		
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdaptar = new ShowPicsAdaptar(getApplicationContext(),
				mImgs, R.layout.show_pics_grid_item, mMaxCountFolderDir.getAbsolutePath(), finishBtn);
		gridView.setAdapter(mAdaptar);
		mImageCount.setText("有" + totalCount + "张");
	}
	/**
	 * 初始化文件夹
	 */
	private void initListDirPopupWindw(){
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), 
				mImageFolders, LayoutInflater.from(this).inflate(R.layout.popup_folder_list_dir, null), mMaxCountFolderDir);
		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		
		// 设置选择文件夹的回调,下面的select方法
		mListImageDirPopupWindow.setOnImageDirSelected(this);
		
	}
	

	@Override
	public void selected(ImageFolder folder) {
		mMaxCountFolderDir = new File(folder.getDir());
		mListImageDirPopupWindow.setCurrentFile(mMaxCountFolderDir);//告知popupwindow，当前所在文件夹
		mImgs = Arrays.asList(mMaxCountFolderDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg")
						|| filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		mAdaptar = new ShowPicsAdaptar(getApplicationContext(),
				mImgs, R.layout.show_pics_grid_item, mMaxCountFolderDir.getAbsolutePath(), finishBtn);
		gridView.setAdapter(mAdaptar);
		mImageCount.setText("共:" + folder.getImgCount() + "张");
		folderNameTv.setText(folder.getName());
		mListImageDirPopupWindow.dismiss();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
