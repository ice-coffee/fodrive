package com.jinglingtec.ijiazu.util.ViewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollView extends ViewGroup
{

    private Context context;


    /**
     * 手势识别的，工具类
     */
    private GestureDetector detector;

    /**
     * 计算位移的工具类
     */

    private Scroller scroller;


    public MyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        initView();

    }

    private void initView()
    {

        //		scroller = new MyScroller(context);
        scroller = new Scroller(context);

        detector = new GestureDetector(context, new OnGestureListener()
        {

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            /**
             * 当手指在屏幕上滑动的时候，的回调方法
             */
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {

                //让view 移动一段位置

                //System.out.println("distanceX::"+distanceX);

				/*
                 * 将view中的内容位置一段距离
				 * x,y  移动的距离
				 */
                scrollBy((int) distanceX, 0);

				/*
                 * 让view的内容，移动到某个点上
				 * x 水平坐标
				 * y 竖直坐标
				 * scrollTo(int x,int y);
				 *
				 */


                return false;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            /**
             * 快速滑动时的回调方法
             */
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                isFling = true;


                //				System.out.println("velocityX:"+velocityX);
                if (velocityX > 0 && curId > 0)
                {
                    curId--;
                }
                else if (velocityX < 0 && curId < getChildCount() - 1)
                {
                    curId++;
                }
                moveToDest(curId);

                return false;
            }

            @Override
            public boolean onDown(MotionEvent e)
            {
                // TODO Auto-generated method stub
                return false;
            }
        });

    }

    /**
     * 判断是否发生了快速滑动
     */
    private boolean isFling = false;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //		System.out.println("widthMeasureSpec"+widthMeasureSpec);
        //		System.out.println("heightMeasureSpec"+heightMeasureSpec);

        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        for (int i = 0; i < getChildCount(); i++)
        {
            View view = getChildAt(i);
            /*
			 * 测量子view的大小
			 */
            view.measure(widthMeasureSpec, heightMeasureSpec);

        }

        //		getChildAt(2).measure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    /**
     * 为子viev指定位置
     * changed 当前布局、位置是否发生改变
     * l	当前View (就是我们的MyScrollView) 在他的父view中的位置
     * t
     * r
     * b
     */
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
		/*
		 * getChildCount() 获得viewGroup的子view的个数
		 *
		 * getChildAt(i) 获得指定下标的子view
		 *
		 * layout(...) 为子view指定位置，相对于我自己
		 */

        for (int i = 0; i < getChildCount(); i++)
        {
            View view = getChildAt(i);

            view.layout(0 + i * getWidth(), 0, getWidth() + i * getWidth(), getHeight());

        }

        //			View view = getChildAt(0);
        //
        //			view.layout(30, 20, 280, 400);

    }


    private int downX = 0;
    private int lastX = 0;

    /**
     * 显示在屏幕上的子view的下标
     */
    private int curId = 0;

    private int onInter_down_x;
    private int onInter_down_y;


    @Override
    /**
     * 分发事件的方法
     */
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    /**
     * 判断是否中断事件的传递  ,会在onTouchEvent方法之前执行
     * 返回 true 中断
     * false 不中断(默认)
     */
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        boolean result = false;

        //获得水平方向移动的距离
        // 竖直方向移动的距离
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                onInter_down_x = (int) event.getX();
                onInter_down_y = (int) event.getY();

                detector.onTouchEvent(event);

                break;
            case MotionEvent.ACTION_MOVE:

                //判断 水平方向 ，和垂直方向移动的距离
                int distanceX = (int) Math.abs(event.getX() - onInter_down_x);
                int distanceY = (int) Math.abs(event.getY() - onInter_down_y);

                if (distanceX > distanceY && distanceX > 5)
                {
                    result = true;
                }
                else
                {
                    result = false;
                }


                break;
            case MotionEvent.ACTION_UP:

                break;
        }


        return result;
    }


    @Override
    /**
     * 当事件先传递给子view,而后中断的时候，onTouchEvent收到的第一个事件，是move事件。，
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        super.onTouchEvent(event);

        /**
         * 如果没有down事件，detector解析事件时，位移会变大
         */
        detector.onTouchEvent(event);


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                downX = lastX = (int) event.getX();

                break;
            case MotionEvent.ACTION_MOVE:


                break;
            case MotionEvent.ACTION_UP:

                if (!isFling)
                {

                    // scrollTo(0, 0);
                    // scrollTo(getWidth(), 0);

                    if (downX - event.getX() > getWidth() / 2)
                    { // 向右滑动到下一张
                        curId++;
                    }
                    else if (event.getX() - downX > getWidth() / 2)
                    { // 向左滑动，上一张
                        curId--;
                    }

                    moveToDest(curId);
                }
                isFling = false;

			/*
			 * 触发事件监听
			 */
                if (myPagerChangedListener != null)
                {
                    if (curId >= 0 && curId <= 9)
                    {
                        myPagerChangedListener.selectedPage(curId);
                    }
                }

                break;

            default:
                break;
        }


        return true;
    }

    /**
     * 将view移动到指定的下标
     */
    public void moveToDest(int index)
    {

        if (index < 0)
        {
            index = 0;
        }

        curId = index;

        //要移动的距离 = 终点坐标 - 现在的坐标
        int distance = curId * getWidth() - getScrollX();

        //此句的效果是瞬间移动，我们希望有一些过程
        //		scrollTo(curId*getWidth(), 0);

        scroller.startScroll(getScrollX(), 0, distance, 0, Math.abs(distance));

        /**
         * 刷新页面，
         */
        invalidate();
    }

    @Override
    /**
     * invalidate 方法会导致此方法的执行
     */
    public void computeScroll()
    {
        //		super.computeScroll();
        if (scroller.computeScrollOffset())
        {

            long currX = scroller.getCurrX();
            //System.out.println("currX::"+currX);

            scrollTo((int) currX, 0);

            //再次刷新
            invalidate();
        }
    }

    public IMyPagerChangedListener getMyPagerChangedListener()
    {
        return myPagerChangedListener;
    }

    public void setMyPagerChangedListener(IMyPagerChangedListener myPagerChangedListener)
    {
        this.myPagerChangedListener = myPagerChangedListener;
    }

    private IMyPagerChangedListener myPagerChangedListener;


    public interface IMyPagerChangedListener
    {
        void selectedPage(int curId);
    }


}
