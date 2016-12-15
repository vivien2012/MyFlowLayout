package com.vivien.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by vivien on 16/12/12.
 */

public class MyFlowLayout2 extends LinearLayout {

    private String TAG = MyFlowLayout2.class.getSimpleName();

    private int mScrW = 0;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mPadding = 0;

    private ViewGroup mFlowLayout;

    private ArrayList<View> mItemVies = new ArrayList<>();
    private ArrayList<String> mDatas = new ArrayList<>();

    private OnItemDelListener onItemDelListener;

    public MyFlowLayout2(Context context) {
        this(context, null);
    }

    public MyFlowLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFlowLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mScrW = context.getResources().getDisplayMetrics().widthPixels;
        this.mPadding = ScreenUtils.dip2px(8, context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScrW = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void addItems(ArrayList<String> datas) {
        View view = mLayoutInflater.inflate(R.layout.view_flowlayout, this);
        mFlowLayout = (LinearLayout) view.findViewById(R.id.view_flowlayout);
        clearViews();
        initViews(datas);
    }

    /**
     * 初始化数据view
     *
     * @param datas
     */
    private void initViews(ArrayList<String> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        mDatas = datas;
        for (int i = 0; i < datas.size(); i++) {
            View itemView = mLayoutInflater.inflate(R.layout.item_table, null);
            TextView tv = (TextView) itemView.findViewById(R.id.table_text);
            ImageView iv = (ImageView) itemView.findViewById(R.id.table_delete);
            LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.table_ll);
            ll.setPadding(mPadding, mPadding, mPadding, mPadding);
            tv.setText(datas.get(i));
            tv.setTag(i);
            tv.setOnLongClickListener(new OnTVLongClickListener());
            iv.setTag(i);
            iv.setOnClickListener(new OnIVClickListener());
            measureView(itemView);
            mItemVies.add(itemView);
        }
        addChildViews();
    }

    /**
     * 把子view添加到布局中
     */
    private void addChildViews() {
        if (mFlowLayout != null) {
            mFlowLayout.removeAllViews();
        }

        mScrW = mScrW - ScreenUtils.dip2px(40, getContext());
        // new一个新的行的线性布局，用于添加每行的条目
        LinearLayout lineView = createNewLine();
        for (int i = 0; i < mItemVies.size(); i++) {
            measureView(lineView);//测量当前的布局
            View child = mItemVies.get(i);
            measureView(child);//测量i位置当前的布局
            int lineWidth = lineView.getMeasuredWidth();
            int childWidth = child.getMeasuredWidth();

            //如果需要换行
            if (lineWidth + childWidth > mScrW) {
                ViewGroup parent = (ViewGroup) lineView.getParent();
                if (parent != null) {
                    parent.removeAllViewsInLayout();
                }
                mFlowLayout.addView(lineView);
                lineView = createNewLine();
            }

            lineView.addView(removeParentView(child));

            if (i == mItemVies.size() - 1) {
                mFlowLayout.addView(lineView);
            }
        }
    }

    /**
     * new一个新的行的线性布局，用于添加每行的条目
     *
     * @return
     */
    private LinearLayout createNewLine() {
        LinearLayout layout = new LinearLayout(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(lp);
        return layout;
    }

    /**
     * 测量一个单选按钮的长度
     */
    public void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);

        v.measure(w, h);
    }

    public View removeParentView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return view;
    }

    /**
     * 清除所有的数据
     */
    private void clearViews() {
        mItemVies.clear();
        mFlowLayout.removeAllViews();
    }

    /**
     * textview 长按事件
     */
    private class OnTVLongClickListener implements OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            TextView tv = (TextView) view;
            int pos = (int) tv.getTag();
            setShowDeleteIcon(pos);
            addChildViews();
            return false;
        }
    }

    /**
     * imageview 删除按钮的点击事件
     */
    private class OnIVClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            ImageView iv = (ImageView) view;
            int pos = (int) iv.getTag();
            mItemVies.remove(pos);
            mDatas.remove(pos);
            setTags();
            addChildViews();
            if (onItemDelListener != null) {
                onItemDelListener.onDelete(pos);
            }
        }
    }

    /**
     * 重新设置tags
     */
    private void setTags() {
        for (int i = 0; i < mItemVies.size(); i++) {
            View view = mItemVies.get(i);
            TextView tv = (TextView) view.findViewById(R.id.table_text);
            ImageView iv = (ImageView) view.findViewById(R.id.table_delete);
            tv.setTag(i);
            iv.setTag(i);
        }
    }

    /**
     * 设置对应长按某个位置显示按钮
     *
     * @param position
     */
    private void setShowDeleteIcon(int position) {
        for (View itemView : mItemVies) {
            ImageView mDelete = (ImageView) itemView.findViewById(R.id.table_delete);
            int tag = (int) mDelete.getTag();
            if (tag == position) {
                int isVisible = mDelete.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                mDelete.setVisibility(isVisible);
            }
        }
    }

    public void setOnItemDelListener(OnItemDelListener listener) {
        this.onItemDelListener = listener;
    }

    public interface OnItemDelListener {
        void onDelete(int pos);
    }

}
