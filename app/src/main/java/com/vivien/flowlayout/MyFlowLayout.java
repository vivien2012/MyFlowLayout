package com.vivien.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivien on 16/12/12.
 */

public class MyFlowLayout extends LinearLayout {

    private String TAG = MyFlowLayout.class.getSimpleName();

    private OnItemClickListener onItemClickListener;

    public MyFlowLayout(Context context) {
        super(context);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new MarginLayoutParams(context, attrs));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeigh = MeasureSpec.getMode(heightMeasureSpec);

        Log.e(TAG, sizeWidth + " " + sizeHeight);

        //如果是wrap_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        //遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            child.setTag(String.valueOf(i));
            //测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            /**
             * 如果加入当前child，则超出最大宽度，则得到的最大宽度给width， 类加height， 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth); //取最大的
                lineWidth = childWidth; //重新开启新行，开始记录
                //叠加当前高度
                height += lineHeight;
                //开启记录下一行的高度
                lineHeight = childHeight;
            } else {
                //否则累加值lineWidth, lineHeight取最大高度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeigh == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    /**
     * 存储所有的view，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<>();

    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, final int t, final int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                // 记录这一行所有的view以及最大高度
                mLineHeight.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<>();
            }

            /**
             * 如果不换行，则累加
             */
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }

        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);

            Log.e(TAG, "第" + i + "行：" + lineViews.size() + ", " + lineViews);
            Log.e(TAG, "第" + i + "行：" + lineHeight);

            //遍励当前行所有的view
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                // 计算childView的left, top, right, bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                Log.e(TAG, child + " ,  l = " + lc + " , t = " + tc + " , r = " + rc + " , bc = " + bc);

                child.layout(lc, tc, rc, bc);
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tag = ((String) view.getTag());
                        onItemClickListener.onItemClick(Integer.valueOf(tag));
                    }
                });

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}
