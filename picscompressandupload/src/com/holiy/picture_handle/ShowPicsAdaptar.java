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
 * չʾ���ͼƬ��������
 * @author holiy
 */
public class ShowPicsAdaptar extends CommonAdapter<String> {

	/**
	 * �û�ѡ���ͼƬ���洢ΪͼƬ������·��
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * �ļ���·��
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
		//����
		helper.setImageResource(R.id.folder_first_pic_item_image, R.drawable.picture_no);
		//ѡ���
		helper.setImageResource(R.id.folder_first_pic_item_select, R.drawable.pic_checked_unselected);
		final ImageView imgView = helper.getView(R.id.folder_first_pic_item_image);
		final ImageView select = helper.getView(R.id.folder_first_pic_item_select);
		
		if(item.equals("����") && position == 0){
			carmeraLayout.setVisibility(View.VISIBLE);
			picLayout.setVisibility(View.GONE);
			carmeraLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "����", 0).show();
				}
			});
		}else{
			carmeraLayout.setVisibility(View.GONE);
			picLayout.setVisibility(View.VISIBLE);
			//����ͼƬ
			helper.setImageByUrl(R.id.folder_first_pic_item_image, mDirPath + "/" + item);
		}
		imgView.setColorFilter(null);
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�Ƴ�ͼƬ
				if(mSelectedImage.contains(mDirPath + "/" + item)){
					mSelectedImage.remove(mDirPath + "/" + item);
					select.setImageResource(R.drawable.pic_checked_unselected);
					imgView.setColorFilter(null);
				}else{
					if(mSelectedImage.size() < 9){
						//���ͼƬ
						mSelectedImage.add(mDirPath + "/" + item);
						select.setImageResource(R.drawable.pic_checked_selected);
						imgView.setColorFilter(Color.parseColor("#77000000"));
					}
				}
				if(mSelectedImage.size() > 0){
					finishBtn.setClickable(true);
					finishBtn.setText("���("+ mSelectedImage.size() + "/9)");
					finishBtn.setBackgroundResource(R.drawable.blue_btn_selector);
				}else{
					finishBtn.setClickable(false);
					finishBtn.setText("���");
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
