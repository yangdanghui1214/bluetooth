package com.tsign.bluetooth.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 万能的RecyclerView的ViewHolder
 *
 * @author 南尘
 * @date 16-7-30
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private Context context;

    public BaseRecyclerHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        //指定一个初始为8
        views = new SparseArray<>(8);
    }

    /**
     * 取得一个RecyclerHolder对象
     *
     * @param context  上下文
     * @param itemView 子项
     * @return 返回一个RecyclerHolder对象
     */
    public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
        return new BaseRecyclerHolder(context, itemView);
    }

    public SparseArray<View> getViews() {
        return this.views;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 通过view的id获取对应的控件，如果没有则加入views中
     *
     * @param viewId 控件的id
     * @return 返回一个控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置字符串
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
        return this;
    }

    /**
     * 设置富文本字符串
     */
    public BaseRecyclerHolder setText(int viewId, SpannableString text) {
        TextView tv = getView(viewId);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
        return this;
    }


    /**
     * 设置字符串
     * 设置textview的高度
     */
    public BaseRecyclerHolder setText(int viewId, String text, int h) {
        TextView tv = getView(viewId);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
        tv.setHeight(h);
        return this;
    }


    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @param base
     * @return
     */
    public void setTextByPercent(Context mCxt, double base, int viewId, String text) {

        WindowManager wm = (WindowManager) mCxt.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        int width = wm.getDefaultDisplay().getWidth();
        TextView view = getView(viewId);
        view.setText(text);
        float textSize;
        if (width == 1366) {
            textSize = (float) (base * 30 * 0.7);
            view.setPadding(3, 3, 3, 3);
        } else {
            textSize = (float) (base * 30);
        }
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView iv = getView(viewId);
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(drawableId);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setVisibility(View.VISIBLE);
        iv.setImageBitmap(bitmap);
        return this;
    }

}
