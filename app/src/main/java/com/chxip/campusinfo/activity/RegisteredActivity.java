package com.chxip.campusinfo.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.base.BaseActivity;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.MLog;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.util.Utils;
import com.chxip.campusinfo.util.utilNet.HttpVolley;
import com.chxip.campusinfo.util.utilNet.NetCallBackVolley;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.chxip.campusinfo.view.ImageTextView;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisteredActivity extends BaseActivity {

    @BindView(R.id.et_registered_name)
    EditText et_registered_name;
    @BindView(R.id.tv_login_name_detele)
    ImageTextView tv_login_name_detele;
    @BindView(R.id.et_registered_pwd)
    EditText et_registered_pwd;
    @BindView(R.id.tv_login_pwd_according)
    ImageTextView tv_login_pwd_according;
    @BindView(R.id.et_registered_qrPwd)
    EditText et_registered_qrPwd;
    @BindView(R.id.tv_login_qrPwd_according)
    ImageTextView tv_login_qrPwd_according;
    @BindView(R.id.ll_registered)
    LinearLayout ll_registered;
    @BindView(R.id.et_registered_realname)
    EditText et_registered_realname;
    @BindView(R.id.tv_login_realname_detele)
    ImageTextView tv_login_realname_detele;
    @BindView(R.id.rb_registered_men)
    RadioButton rb_registered_men;
    @BindView(R.id.rb_registered_wumen)
    RadioButton rb_registered_wumen;
    @BindView(R.id.et_registered_email)
    EditText et_registered_email;
    @BindView(R.id.tv_login_email_detele)
    ImageTextView tv_login_email_detele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);

        initBaseViews();
        setTitle("注册");

        initViews();

    }


    private void initViews() {

        et_registered_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_name_detele.setVisibility(View.VISIBLE);
                } else {
                    tv_login_name_detele.setVisibility(View.GONE);
                }

            }
        });
        et_registered_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_pwd_according.setVisibility(View.VISIBLE);
                } else {
                    tv_login_pwd_according.setVisibility(View.GONE);
                }
            }
        });
        et_registered_qrPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_qrPwd_according.setVisibility(View.VISIBLE);
                } else {
                    tv_login_qrPwd_according.setVisibility(View.GONE);
                }
            }
        });
        et_registered_realname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_realname_detele.setVisibility(View.VISIBLE);
                } else {
                    tv_login_realname_detele.setVisibility(View.GONE);
                }
            }
        });
        et_registered_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    tv_login_email_detele.setVisibility(View.VISIBLE);
                } else {
                    tv_login_email_detele.setVisibility(View.GONE);
                }
            }
        });
    }

    boolean isShowPassword = true;
    boolean isShowQrPassword = true;

    @OnClick({R.id.tv_login_email_detele,R.id.tv_login_realname_detele, R.id.tv_login_name_detele, R.id.tv_login_pwd_according, R.id.tv_login_qrPwd_according, R.id.ll_registered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login_realname_detele:
                et_registered_realname.setText("");
                break;
            case R.id.tv_login_name_detele:
                et_registered_name.setText("");
                break;
            case R.id.tv_login_pwd_according:
                if (isShowPassword) {
                    et_registered_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassword = false;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_hidden));
                    et_registered_pwd.setSelection(et_registered_pwd.getText().length());
                } else {
                    et_registered_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPassword = true;
                    tv_login_pwd_according.setText(getString(R.string.image_login_pwd_according));
                    et_registered_pwd.setSelection(et_registered_pwd.getText().length());
                }
                break;
            case R.id.tv_login_qrPwd_according:
                if (isShowQrPassword) {
                    et_registered_qrPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowQrPassword = false;
                    tv_login_qrPwd_according.setText(getString(R.string.image_login_pwd_hidden));
                    et_registered_qrPwd.setSelection(et_registered_qrPwd.getText().length());
                } else {
                    et_registered_qrPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowQrPassword = true;
                    tv_login_qrPwd_according.setText(getString(R.string.image_login_pwd_according));
                    et_registered_qrPwd.setSelection(et_registered_qrPwd.getText().length());
                }
                break;
            case R.id.tv_login_email_detele:
                et_registered_email.setText("");
                break;
            case R.id.ll_registered:
                User user = new User();
                user.setName(et_registered_name.getText().toString());
                user.setPhone(et_registered_email.getText().toString());
                user.setPassword(et_registered_pwd.getText().toString());
                user.setRealName(et_registered_realname.getText().toString());
                user.setType(1);

                if (user.getName() == null || user.getName().equals("")) {
                    ToastUtil.show(this, "请输入学号");
                    return;
                }
                if (user.getPassword() == null || user.getPassword().equals("")
                        || et_registered_qrPwd.getText().toString() == null || et_registered_qrPwd.getText().toString().equals("")
                        ) {
                    ToastUtil.show(this, "请输入密码");
                    return;
                }
                if (user.getPassword().length() < 6 || et_registered_qrPwd.getText().toString().length() < 6) {
                    ToastUtil.show(this, "密码长度必须大于6位");
                    return;
                }

                if (!user.getPassword().equals(et_registered_qrPwd.getText().toString())) {
                    ToastUtil.show(this, "两次密码不一致");
                    return;
                }
                if (user.getRealName() == null && user.getRealName().equals("")) {
                    ToastUtil.show(this, "请输入真实姓名/昵称");
                    return;
                }

                if(rb_registered_men.isChecked()){
                    user.setSex("男");
                }else{
                    user.setSex("女");
                }

                //注册
                Register(user);

                break;
        }
    }

    Dialog loginDialog;

    /**
     * 登录
     */
    private void Register(User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("pwd", user.getPassword());
        map.put("type", user.getType()+"");
        map.put("realName", user.getRealName());
        map.put("phone", user.getPhone());
        map.put("sex", user.getSex());

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
                        ToastUtil.show(RegisteredActivity.this, "注册成功");
                        finish();
                    } else {
                        String strmsg = jsonObject.getString("Message");
                        ToastUtil.show(RegisteredActivity.this,strmsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                loginDialog.dismiss();
                MLog.i(error.toString());
                ToastUtil.showNetworkError(RegisteredActivity.this);
            }
        };

        if (Utils.isNetworkAvailable(RegisteredActivity.this)) {
            loginDialog = Utils.createLoadingDialog(RegisteredActivity.this, "注册中,请稍后...");
            loginDialog.show();
            HttpVolley.volleStringRequestPostLogin(Urls.Register, map, ncbv);
        }
    }
}
