package com.chxip.campusinfo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chxip.campusinfo.R;


public class CustomDialog extends Dialog implements
		View.OnClickListener {

	/** 布局文件 **/
	int layoutRes;
	/** 上下文对象 **/
	Context context;
	/** 确定按钮 **/
	private Button confirmBtn;
	/** 取消按钮 **/
	private Button cancelBtn;
	/** 点击次数 **/
	private int check_count = 0;
	/** Toast时间 **/
	public static final int TOAST_TIME = 1000;

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}

	private String title = "";
	private String content = "";
	TextView title_tv, content_tv;

	/**
	* 自定义布局的构造方法
	* 
	* @param context
	* @param resLayout
	*/
	public CustomDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	* 自定义主题及布局的构造方法
	* 
	* @param context
	* @param theme
	* @param resLayout
	*/
	/*public CustomDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
	}*/
	/**
	 * 自定义主题及布局的构造方法
	 * @param context
	 * @param theme
	 * @param resLayout
	 * @param title
	 * @param content
	 */
	public CustomDialog(Context context, int theme, int resLayout,
						String title, String content) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.title = title;
		this.content = content;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 指定布局
		this.setContentView(layoutRes);

		// 根据id在布局中找到控件对象
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		title_tv = (TextView) findViewById(R.id.tv1);
		title_tv.setText(title);
		content_tv = (TextView) findViewById(R.id.tv2);
		content_tv.setText(content);

		// 设置按钮的文本颜色
		confirmBtn.setTextColor(0xff1E90FF);
		cancelBtn.setTextColor(0xff1E90FF);

		// 为按钮绑定点击事件监听器
		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {

	}

}
