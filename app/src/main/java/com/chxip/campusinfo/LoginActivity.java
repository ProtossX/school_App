package com.chxip.campusinfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.activity.RegisteredActivity;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.MLog;
import com.chxip.campusinfo.util.SharedTools;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.util.Utils;
import com.chxip.campusinfo.util.utilNet.HttpVolley;
import com.chxip.campusinfo.util.utilNet.NetCallBackVolley;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS_ERROR:
                    String msgStr = msg.obj.toString();
                    if (!msgStr.equals("")) {
                        ToastUtil.show(LoginActivity.this, msgStr);
                    }

                    break;
            }
        }
    };

    @BindView(R.id.tv_login_versionName)
    TextView tv_login_versionName;
    @BindView(R.id.iv_login_name)
    ImageView iv_login_name;
    @BindView(R.id.et_login_name)
    EditText et_login_name;
    @BindView(R.id.iv_login_nameDelete)
    ImageView iv_login_nameDelete;
    @BindView(R.id.iv_login_pwd)
    ImageView iv_login_pwd;
    @BindView(R.id.et_login_pwd)
    EditText et_login_pwd;
    @BindView(R.id.iv_login_pwdDelete)
    ImageView iv_login_pwdDelete;
    @BindView(R.id.cb_login_jzmm)
    CheckBox cb_login_jzmm;
    @BindView(R.id.btn_login)
    LinearLayout btn_login;
    @BindView(R.id.ll_login_registered)
    LinearLayout ll_login_registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();

    }


    private void initViews() {
        et_login_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.length() == 0) {
                    iv_login_nameDelete.setVisibility(View.GONE);
                } else {
                    iv_login_nameDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        et_login_name.setText(SharedTools.getString(SharedTools.UserName));

        et_login_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.length() == 0) {
                    iv_login_pwdDelete.setVisibility(View.GONE);
                } else {
                    iv_login_pwdDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        et_login_pwd.setText(SharedTools.getString(SharedTools.Password));

        tv_login_versionName.setText("当前版本：v" + Utils.getVersionName(this, this.getPackageName()));

    }

    @OnClick({R.id.ll_login_registered, R.id.iv_login_nameDelete, R.id.iv_login_pwdDelete, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_login_nameDelete:
                et_login_name.setText("");
                break;
            case R.id.iv_login_pwdDelete:
                et_login_pwd.setText("");
                break;
            case R.id.btn_login:
                loginValidation();
                break;
            case R.id.ll_login_registered:
                Intent intent = new Intent(this, RegisteredActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * 登录验证
     */
    private void loginValidation() {
        String name = et_login_name.getText().toString();
        String pwd = et_login_pwd.getText().toString();

        if (!Utils.isNull(name)) {
            if (!Utils.isNull(pwd)) {

                Login(name, pwd);
            } else {
                ToastUtil.show(this, getResources().getString(R.string.login_pwd_null));
            }
        } else {
            ToastUtil.show(this, getResources().getString(R.string.login_name_null));
        }

    }


    /**
     * 登录提示框
     */
    Dialog loginDialog;
    // login 返回成功，但是登陆不成功
    private final int LOGIN_SUCCESS_ERROR = 1;

    // 登录成功后返回的信息
    User user;

    /**
     * 登录
     */
    private void Login(final String name, final String pwd) {
        HashMap<String, String> map = new HashMap<>();
        map.put("userName", name);
        map.put("passWord", pwd);


        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                // TODO Auto-generated method stub
                MLog.i("Login登录返回:" + response);
                loginDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        if (cb_login_jzmm.isChecked()) {
                            SharedTools.saveData(SharedTools.UserName, name);
                            SharedTools.saveData(SharedTools.Password, pwd);
                        } else {
                            SharedTools.saveData(SharedTools.UserName, "");
                            SharedTools.saveData(SharedTools.Password, "");
                        }
                        String result = jsonObject.getString("Result");
                        user = new Gson().fromJson(result, User.class);
                        SharedTools.saveObject(user, SharedTools.User);


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = LOGIN_SUCCESS_ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    loginDialog.dismiss();
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                loginDialog.dismiss();
                MLog.i(error.toString());
                ToastUtil.showNetworkError(LoginActivity.this);
            }
        };

        if (Utils.isNetworkAvailable(LoginActivity.this)) {
            loginDialog = Utils.createLoadingDialog(LoginActivity.this, getString(R.string.login_ts_msg));
            loginDialog.show();
            HttpVolley.volleStringRequestPostLogin(Urls.Login, map, ncbv);
        }
    }


    // 点击空白区域 自动隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


}
