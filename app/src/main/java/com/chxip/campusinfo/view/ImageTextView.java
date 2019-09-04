package com.chxip.campusinfo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 陈湘平 on 2017/11/29.
 */
public class ImageTextView extends AppCompatTextView {



    public ImageTextView(Context context) {
        super(context);
        init(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init(Context context){
        if(!isInEditMode()) {
            Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
            this.setTypeface(iconfont);
        }
    }
}
