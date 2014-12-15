package com.holiy.picture_handle;

import java.io.File;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.holiy.picture_handle.bean.ImageFolder;
import com.holiy.picture_handle.widget.utils.BasePopupWindowForListView;
import com.holiy.picture_handle.widget.utils.CommonAdapter;
import com.holiy.picture_handle.widget.utils.ViewHolder;
/**
 * 文件选择popup窗口
 * @author holiy
 *
 */
public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder> {
	private ListView mListDir;
	private File currentFile;
	public ListImageDirPopupWindow(int width, int height,
			List<ImageFolder> datas, View convertView, File currentFile)
	{
		super(convertView, width, height, true, datas);
		this.currentFile = currentFile;
	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 供调用者更新状态
	 */
	public void setCurrentFile(File currentFile){
		this.currentFile = currentFile;
	}

	@Override
	public void initViews() {
		mListDir = (ListView)findViewById(R.id.pics_folder_dir_list);
		mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas, R.layout.popup_folder_list_item) {
			@Override
			public void convert(ViewHolder helper, ImageFolder item, int position) {
				helper.setText(R.id.pic_folder_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.pics_folder_dir_item_image, item.getFirstImagePath());
				helper.setText(R.id.pic_folder_dir_item_count, item.getDir());
			    ImageView choosedTv = helper.getView(R.id.folder_choosed_tv);
			    Log.i("path", "current: " + currentFile.getPath() + " dir:" + item.getDir());
			    String path1 = currentFile.getPath();
			    String path2 = item.getDir();
			    if(currentFile.getPath().equals(item.getDir())){
			    	choosedTv.setVisibility(View.VISIBLE);
			    }else{
			    	choosedTv.setVisibility(View.GONE);
			    }
			    Log.i("position", "position: " + position);
			}
		});
	}
	/**
	 * 强制要实现这个方法
	 * @author holiy
	 *
	 */
	public interface OnImageDirSelected
	{
		void selected(ImageFolder folder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mImageDirSelected != null)
				{
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
