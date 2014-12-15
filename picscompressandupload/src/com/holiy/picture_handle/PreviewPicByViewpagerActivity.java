package com.holiy.picture_handle;

import com.holiy.picture_handle.widget.utils.ImageLoader;
import com.holiy.picture_handle.widget.utils.PreviewPicsViewPager;
import com.holiy.picture_handle.widget.utils.ImageLoader.Type;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PreviewPicByViewpagerActivity extends Activity {
	private PreviewPicsViewPager picsViewpager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview_pic_by_viewpager);
		initView();
		
	}
	
	private void initView(){
		picsViewpager = (PreviewPicsViewPager) findViewById(R.id.preview_pics_viewpager);
		picsViewpager.setAdapter(new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return ShowPicsAdaptar.mSelectedImage.size();
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object)
			{
				container.removeView((View) object);
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{
				ImageView imageView = new ImageView(PreviewPicByViewpagerActivity.this);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(params);
				ImageLoader.getInstance(3,Type.LIFO).loadImage(ShowPicsAdaptar.mSelectedImage.get(position), imageView);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				container.addView(imageView);
				picsViewpager.setObjectForPosition(imageView, position);
				return imageView;
			}
		});
	}
}
