package com.chxip.campusinfo.view.mySpinnerPopup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.chxip.campusinfo.R;

import java.util.List;


public class MyPopupWindow extends PopupWindow implements OnItemClickListener {

    private List<String> mItems;
    private MyPopupWindow mWindow;
    private MyPopupListAdapter.onItemClickListener mListener;
    public MyPopupListAdapter adapter;

    public MyPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPopupWindow(Context context, int width, List<String> items) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.myspinner_pop, null);
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置PopupWindow弹出窗体的高
        this.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);

        this.mItems = items;
        ListView listView = (ListView) contentView.findViewById(R.id.lv_list);
        mWindow = this;
        adapter = new MyPopupListAdapter(mWindow, context, mItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MyPopupWindow.this.dismiss();

    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void close() {
        this.dismiss();
    }

    public int position() {

        return 0;
    }

    public void setOnItemClickListener(MyPopupListAdapter.onItemClickListener listener) {
        this.mListener = listener;
    }

    public MyPopupListAdapter.onItemClickListener getListener() {
        //可以通过this的实例来获取设置好的listener
        return mListener;
    }

}
