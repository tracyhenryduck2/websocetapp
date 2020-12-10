package com.siterwell.demo.commonview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.siterwell.demo.R;

public class ParallaxListView extends ListView implements AbsListView.OnScrollListener {

	protected static final String TAG = ParallaxListView.class.getName();

	private ImageView mImageView;
	// 定义ImageView的最大可拉伸的高度
	private int mDrawableMaxHeight = -1;
	// 定义ImageView初始加载的高度
	private int mImageViewHeight = -1;
	// 定义ImageView的默认高度
	private int mDefaultImageViewHeight = 449;

	// -- 底部的View
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	// 总列表项，用于检测列表视图的底部
	private int mTotalItemCount;
	private float mLastY = -1; // save event y
	// for mScroller, 滚动页眉或者页脚
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;// 顶部
	private final static int SCROLLBACK_FOOTER = 1;// 下部

	private final static int SCROLL_DURATION = 400; // 滚动回时间
	private final static int PULL_LOAD_MORE_DELTA = 50; // 当大于50PX的时候，加载更多

	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
	// feature.
	private Scroller mScroller; // 用于回滚
	private OnScrollListener mScrollListener; // 回滚监听

	// 触发刷新和加载更多接口.
	private IXListViewListener mListViewListener;
    private Context context;

	public ParallaxListView(Context context) {
		super(context);
		init(context);
		this.context = context;
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		this.context = context;
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		this.context = context;
	}

	public void setParallaxImageView(ImageView iv) {
		mImageView = iv;
		mImageView.setScaleType(ScaleType.CENTER_CROP);
		ViewGroup.LayoutParams params = mImageView.getLayoutParams();
		mDefaultImageViewHeight = getResources().getDimensionPixelSize(R.dimen.height_battery_header);
		mDrawableMaxHeight = 3 * mDefaultImageViewHeight/2;
		params.height = mDefaultImageViewHeight;
		mImageView.setLayoutParams(params);
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);
	}

	// 设置缩放级别---控制图片的最大拉伸高度
	// 在界面加载完毕的时候调用
	public void setViewsBounds(double zoomRatio) {
		// mDrawableMaxHeight=zoomRatio*mDefaultImageViewHeight;
		//if (mDrawableMaxHeight == -1) {

			mImageViewHeight = mImageView.getHeight();
			if (mImageViewHeight < 0) {

				mImageViewHeight = mDefaultImageViewHeight;
			}

		//}
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {

		setVerticalScrollBarEnabled(false);
		// 初始化底部的View
		mFooterView = new XListViewFooter(context);
	}

	
	//内部滑动时调用
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// 监听listView的滑动
		super.onScrollChanged(l, t, oldl, oldt);

		// 如何控制图片见效的高度？---监听listView的头部滑出去的距离
		View header = (View) mImageView.getParent();

		// 得到头部划出去的距离----<0
		if (header.getTop() < 0 && mImageView.getHeight() > mImageViewHeight) {

			mImageView.getLayoutParams().height = Math.max(
					mImageView.getHeight() - Math.abs(header.getTop()), mDefaultImageViewHeight);
			header.layout(header.getLeft(), 0, header.getRight(), header.getHeight());

			// 调整ImageView所在容器的高度
			mImageView.requestLayout();

		}
	}



	//在划出屏幕时调用
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
			boolean isTouchEvent) {

		// 当listView，ScrollView滑动过头的时候回调的方法
		// 不断地控制ImageView的高度
		boolean isCollapse = resizeOverScrollby(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

		return isCollapse ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	/**
	 * 处理listView，ScrollView滑动过头
	 * @param deltaX
	 * @param deltaY
	 * @param scrollX
	 * @param scrollY
	 * @param scrollRangeX
	 * @param scrollRangeY
	 * @param maxOverScrollX
	 * @param maxOverScrollY
	 * @param isTouchEvent
	 * @return
	 */
	private boolean resizeOverScrollby(int deltaX, int deltaY, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
			boolean isTouchEvent) {

		if (deltaY <= 0 && isTouchEvent) {
            if(mDrawableMaxHeight>=(mImageView.getHeight() + Math.abs(deltaY)/3)){
				mImageView.getLayoutParams().height = Math.max(
						mImageView.getHeight() + Math.abs(deltaY)/3, mDefaultImageViewHeight);
				// 重新调整ImageView的宽高
				mImageView.requestLayout();
			}

		}

		return true;
	}


	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				//mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// 发送到用户的监听器
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	//自定义动画效果
	public class ResetAnimation extends Animation {

		private ImageView mView;
		// private int targetHeight;
		private int originalHeith;
		private int extraHeight;

		public ResetAnimation(ImageView mView, int targetHeight) {
			// 图片动画地减小高度
			this.mView = mView;
			// this.targetHeight = targetHeight;
			this.originalHeith = mView.getHeight();

			extraHeight = originalHeith - targetHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);

			//interpolatedTime的值为0.0~1.0f
			mView.getLayoutParams().height = (int) (originalHeith - extraHeight * interpolatedTime);
			mView.requestLayout();
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {

		// 确定XListViewFooter是最后底部的View, 并且只有一次
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}

		super.setAdapter(adapter);
	}

	/**
	 * 启用或禁用加载更多的功能.
	 *
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();// 隐藏
		} else {
			mPullLoading = false;
			mFooterView.show();// 显示
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "上拉" 和 "点击" 将调用加载更多.
		}
	}

	// 开始加载更多
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}








	// 改变底部视图高度
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // 高度足以调用加载更多
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	// 触发事件
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastY = ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				final float deltaY = ev.getRawY() - mLastY;
				mLastY = ev.getRawY();
				 if (getLastVisiblePosition() == mTotalItemCount - 1
						&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
					// 最后一页，已停止或者想拉起
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				}
				break;
			default:
				mLastY = -1; // 重置
				if (getFirstVisiblePosition() == 0) {
					// 调用刷新,如果头部视图高度大于设定高度。
					ResetAnimation anim = new ResetAnimation(mImageView, mDefaultImageViewHeight);

					anim.setDuration(500);
					mImageView.startAnimation(anim);
				}
				if (getLastVisiblePosition() == mTotalItemCount - 1) {
					// 调用加载更多.
					if (mEnablePullLoad
							&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
						startLoadMore();// 如果底部视图高度大于可以加载高度，那么就开始加载
					}
					resetFooterHeight();// 重置加载更多视图高度
				}
				break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}


	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	/**
	 * 你可以监听到列表视图，OnScrollListener 或者这个. 他将会被调用 , 当头部或底部触发的时候
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 实现这个接口来刷新/负载更多的事件
	 */
	public interface IXListViewListener {
		public void onLoadMore();
	}

}
