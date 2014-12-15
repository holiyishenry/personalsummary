package com.holiy.picture_handle.widget.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import com.nineoldandroids.view.ViewHelper;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class PreviewPicsViewPager extends ViewPager {

	private float mTrans;//x轴偏移
	private float mScale;//缩放
	/**
	 * 最大缩小倍数
	 */
	private static final float SCALE_MAX = 0.5f;
	/**
	 * 每个view与对应的位置
	 */
	private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<Integer, View>();
	/**
	 * 滑动时左边元素
	 */
	private View mLeft;
	/**
	 * 滑动时右边元素
	 */
	private View mRight;

	public PreviewPicsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		//滑动特别小的距离时，我们认为没有动
		float effectOffset = isSmall(positionOffsetPixels) ? 0 : positionOffset;
		//获取左边的View
		mLeft = findViewFromObject(position);
		//获取右边的View
		mRight = findViewFromObject(position + 1);
		// 添加切换动画效果
		animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	public void setObjectForPosition(View view, int position)
	{
		mChildrenViews.put(position, view);
	}
	
	/**
	 * 通过过位置获得对应的View
	 * 
	 * @param position
	 * @return
	 */
	public View findViewFromObject(int position)
	{
		return mChildrenViews.get(position);
	}
	private boolean isSmall(float positionOffset)
	{
		return Math.abs(positionOffset) < 0.0001;
	}
	
	protected void animateStack(View left, View right, float effectOffset,
			int positionOffsetPixels)
	{
		if (right != null)
		{
			/**
			 * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
			 * 如果手指从左到右的滑动（切换到前一个）：1.0~0，即从最大到一半
			 */
			mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;
			/**
			 * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
			 */
			mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
			ViewHelper.setScaleX(right, mScale);
			ViewHelper.setScaleY(right, mScale);
			ViewHelper.setTranslationX(right, mTrans);
		}
		if (left != null)
		{
			left.bringToFront();
		}
	}
	
}
