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
 * ͼƬ�鿴������ͼ����ѡ�����ͼƬ�������գ�����΢��6.0ͼƬѡ����
 * @author holiy
 */
public class PickAllPictureFromLibActivity extends Activity implements OnImageDirSelected{
	private ProgressDialog progressDialog;//��ͼ��ʱ����
	/**
	 * ͼƬ����
	 */
	private int mPicsSize;
	/**
	 * �ļ����������ļ���·��
	 */
	private File mMaxCountFolderDir;
	/**
	 * ����ͼƬ
	 */
	private List<String> mImgs;
	
	private GridView gridView;
	private ShowPicsAdaptar mAdaptar;
	
	/**
	 * ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();
	/**
	 * ɨ���õ����е�ͼƬ�ļ���
	 */
	private List<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
	
	private TextView folderNameTv, mImageCount;
	private Button finishBtn, previewBtn;
	
	private int totalCount;//ͼƬ����
	private int mScreenHeight;
	
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			initData();//������
			// ��ʼ��չʾ�ļ��е�popupWindw
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
	 * ��ȡ����ͼƬ���ڻ�ȡͼƬ�ĸ��ļ��У���¼��ͬ�ļ���
	 */
	private void getImages(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog = ProgressDialog.show(this, null, "���ڼ���...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PickAllPictureFromLibActivity.this.getContentResolver();
				// ֻ��ѯjpeg��png��ͼƬ
				Cursor cursor = mContentResolver.query(mImageUri, null ,
						MediaStore.Images.Media.MIME_TYPE + "=? or " 
				+ MediaStore.Images.Media.MIME_TYPE + "=?", 
				new String[]{"image/jpeg", "image/png"}, 
				MediaStore.Images.Media.DATE_MODIFIED);
//				Log.i("count:", "count:" + cursor.getCount());
				while(cursor.moveToNext()){
					//��ȡͼƬ·��
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//��ȡ��·����
					File parentFile = new File(path).getParentFile();
					if(parentFile == null){
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();
					ImageFolder imgFolder = null;
					// ����һ��HashSet��ֹ���ɨ��ͬһ���ļ���
					if (mDirPaths.contains(dirPath))
					{
						continue;
					}else{
						mDirPaths.add(dirPath);
						imgFolder = new ImageFolder();;
						imgFolder.setFirstImagePath(path);
						imgFolder.setDir(dirPath);
					}
					//���˳�ͼƬ
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
					imgFolder.setImgCount(picsSize);//ÿ���ļ�����ͼƬ����
					mImageFolders.add(imgFolder);
					
					if(picsSize > mPicsSize){
						mPicsSize = picsSize;
						mMaxCountFolderDir = parentFile;
					}
				}
				cursor.close();
				// ɨ����ɣ�������HashSetҲ�Ϳ����ͷ��ڴ���
				mDirPaths = null;

				// ֪ͨHandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
		
	}
	/**
	 * ������
	 */
	private void initEvents(){
		folderNameTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(folderNameTv, 0, 0);
				// ���ñ�����ɫ�䰵
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
	 * ������
	 */
	private void initData(){
		if(mMaxCountFolderDir == null){
			Toast.makeText(getApplicationContext(), "����һ��ͼƬûɨ�赽",
					Toast.LENGTH_SHORT).show();
			return;
		}
		List<String> templist = Arrays.asList(mMaxCountFolderDir.list());
		mImgs = new ArrayList<String>();
		mImgs.add("����");//��ӱ�ʶ,��ʶ����
		mImgs.addAll(templist);
		templist = null;
		
		/**
		 * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
		 */
		mAdaptar = new ShowPicsAdaptar(getApplicationContext(),
				mImgs, R.layout.show_pics_grid_item, mMaxCountFolderDir.getAbsolutePath(), finishBtn);
		gridView.setAdapter(mAdaptar);
		mImageCount.setText("��" + totalCount + "��");
	}
	/**
	 * ��ʼ���ļ���
	 */
	private void initListDirPopupWindw(){
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), 
				mImageFolders, LayoutInflater.from(this).inflate(R.layout.popup_folder_list_dir, null), mMaxCountFolderDir);
		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// ���ñ�����ɫ�䰵
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		
		// ����ѡ���ļ��еĻص�,�����select����
		mListImageDirPopupWindow.setOnImageDirSelected(this);
		
	}
	

	@Override
	public void selected(ImageFolder folder) {
		mMaxCountFolderDir = new File(folder.getDir());
		mListImageDirPopupWindow.setCurrentFile(mMaxCountFolderDir);//��֪popupwindow����ǰ�����ļ���
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
		mImageCount.setText("��:" + folder.getImgCount() + "��");
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
