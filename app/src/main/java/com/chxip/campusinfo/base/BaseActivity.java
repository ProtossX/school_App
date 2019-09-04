package com.chxip.campusinfo.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chxip.campusinfo.R;


public class BaseActivity extends Activity implements View.OnClickListener {

    ImageView iv_left;//左边的图片
    TextView tv_title;//标题
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void initBaseViews() {

        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onLeftClick();
                break;
        }
    }

    /**
     * 设置title
     */
    protected void setTitle(String title) {
        tv_title.setText(title);
        this.title=title;
    }

    /**
     * 左边按钮的点击事件，默认是关闭Activity
     */
    protected void onLeftClick() {
        finish();
    }



}
