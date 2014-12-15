package com.holiy.picture_handle;

import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.holiy.picture_handle.widget.utils.CommonAdapter;
import com.holiy.picture_handle.widget.utils.ViewHolder;
/**
 * 展示多个图片的适配器
 * @author holiy
 */
public class ShowPicsAdaptar extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	
	private Context context;
	private Button finishBtn;
	public ShowPicsAdaptar(Context context, List<String> mDatas,
			int itemLayoutId, String dirPath , Button finishBtn) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.context = context;
		this.finishBtn = finishBtn;
	}

	@Override
	public void convert(ViewHolder helper, final String item, int position){
		final LinearLayout carmeraLayout = helper.getView(R.id.tack_pic_layout);
		RelativeLayout picLayout = helper.getView(R.id.pick_pic_layout);
		//背景
		helper.setImageResource(R.id.folder_first_pic_item_image, R.drawable.picture_no);
		//选项框
		helper.setImageResource(R.id.folder_first_pic_item_select, R.drawable.pic_checked_unselected);
		final ImageView imgView = helper.getView(R.id.folder_first_pic_item_image);
		final ImageView select = helper.getView(R.id.folder_first_pic_item_select);
		
		if(item.equals("拍照") && position == 0){
			carmeraLayout.setVisibility(View.VISIBLE);
			picLayout.setVisibility(View.GONE);
			carmeraLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "拍照", 0).show();
				}
			});
		}else{
			carmeraLayout.setVisibility(View.GONE);
			picLayout.setVisibility(View.VISIBLE);
			//设置图片
			helper.setImageByUrl(R.id.folder_first_pic_item_image, mDirPath + "/" + item);
		}
		imgView.setColorFilter(null);
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//移除图片
				if(mSelectedImage.contains(mDirPath + "/" + item)){
					mSelectedImage.remove(mDirPath + "/" + item);
					select.setImageResource(R.drawable.pic_checked_unselected);
					imgView.setColorFilter(null);
				}else{
					if(mSelectedImage.size() < 9){
						//添加图片
						mSelectedImage.add(mDirPath + "/" + item);
						select.setImageResource(R.drawable.pic_checked_selected);
						imgView.setColorFilter(Color.parseColor("#77000000"));
					}
				}
				if(mSelectedImage.size() > 0){
					finishBtn.setClickable(true);
					finishBtn.setText("完成("+ mSelectedImage.size() + "/9)");
					finishBtn.setBackgroundResource(R.drawable.blue_btn_selector);
				}else{
					finishBtn.setClickable(false);
					finishBtn.setText("完成");
					finishBtn.setBackgroundResource(R.drawable.common_shape_blue_btn);
				}
				
			}
		});
		
		if(mSelectedImage.contains(mDirPath + "/" + item)){
			select.setImageResource(R.drawable.pic_checked_selected);
			imgView.setColorFilter(Color.parseColor("#77000000"));
		}
	}
}
