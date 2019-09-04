package com.chxip.campusinfo.activity.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chxip.campusinfo.LoginActivity;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.activity.ClipImageActivity;
import com.chxip.campusinfo.activity.MyCommentActivity;
import com.chxip.campusinfo.activity.MyMessageActivity;
import com.chxip.campusinfo.activity.SystemMessageActivity;
import com.chxip.campusinfo.activity.UpdatePassWordActivity;
import com.chxip.campusinfo.application.MyApplication;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.MLog;
import com.chxip.campusinfo.util.SharedTools;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.chxip.campusinfo.view.ImageTextView;
import com.chxip.campusinfo.view.cameraView.CircleImageView;
import com.yanzhenjie.permission.AndPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by 陈湘平 on 2018/10/8.
 */

public class MyFragment extends Fragment {

    View view;
    @BindView(R.id.personal_center_avatar)
    CircleImageView personal_center_avatar;
    @BindView(R.id.tv_persion_center_name)
    TextView tv_persion_center_name;
    @BindView(R.id.tv_personal_center_position)
    TextView tv_personal_center_position;
    @BindView(R.id.ll_personal_center)
    LinearLayout ll_personal_center;
    @BindView(R.id.iv_personal_center_checkCar_statistics)
    ImageView iv_personal_center_checkCar_statistics;
    @BindView(R.id.rl_my_lost_found)
    RelativeLayout rl_my_lost_found;
    @BindView(R.id.iv_personal_center_course)
    ImageView iv_personal_center_course;
    @BindView(R.id.rl_my_message_wall)
    RelativeLayout rl_my_message_wall;
    @BindView(R.id.iv_personal_center_system_set)
    ImageTextView iv_personal_center_system_set;
    @BindView(R.id.rl_personal_center_system_set)
    RelativeLayout rl_personal_center_system_set;
    @BindView(R.id.rl_personal_center_out_login)
            RelativeLayout rl_personal_center_out_login;
    @BindView(R.id.rl_my_comment)
            RelativeLayout rl_my_comment;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        user = (User) SharedTools.readObject(SharedTools.User);
        createCameraTempFile(savedInstanceState);
        initViews();

        return view;
    }

    User user;

    private void initViews() {

        tv_persion_center_name.setText(user.getRealName());

        Glide.with(this)
                .load(Urls.BASE_IMG+"/"+user.getImageUrl())
                .error(R.mipmap.default_avatar)
                .into(personal_center_avatar);
    }



    @OnClick({R.id.iv_system_message,R.id.rl_my_comment,R.id.rl_personal_center_out_login,R.id.personal_center_avatar, R.id.ll_personal_center, R.id.rl_my_lost_found, R.id.rl_my_message_wall, R.id.rl_personal_center_system_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_center_avatar:
            case R.id.ll_personal_center:
                // 先判断是否有权限。
                if (AndPermission.hasPermission(getActivity(), Manifest.permission.CAMERA) && AndPermission.hasPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPop();
                } else {
                    // 申请权限。
                    AndPermission.with(getActivity())
                            .requestCode(100)
                            .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .send();
                }
                break;
            case R.id.rl_my_lost_found:
                Intent myLostFound=new Intent(getActivity(), MyMessageActivity.class);
                myLostFound.putExtra("type",1);
                startActivity(myLostFound);
                break;
            case R.id.rl_my_message_wall:
                Intent myMessageWall=new Intent(getActivity(), MyMessageActivity.class);
                myMessageWall.putExtra("type",2);
                startActivity(myMessageWall);
                break;
            case R.id.rl_personal_center_system_set:
                Intent intent=new Intent(getActivity(), UpdatePassWordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_personal_center_out_login:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定退出登陆吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedTools.deleteDate(SharedTools.Password);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
            case R.id.rl_my_comment:
                Intent myComment=new Intent(getActivity(), MyCommentActivity.class);
                startActivity(myComment);
                break;
            case R.id.iv_system_message:
                Intent systemMessage=new Intent(getActivity(), SystemMessageActivity.class);
                startActivity(systemMessage);
                break;
        }
    }



    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //调用照相机返回图片临时文件
    private File tempFile;


    PopupWindow settlementPop;

    private void showPop() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_information_pop, null);
        RelativeLayout rl_mip_xc = (RelativeLayout) view.findViewById(R.id.rl_mip_xc);
        RelativeLayout rl_mip_pz = (RelativeLayout) view.findViewById(R.id.rl_mip_pz);
        RelativeLayout rl_mip_qx = (RelativeLayout) view.findViewById(R.id.rl_mip_qx);


        settlementPop = new PopupWindow(view, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        settlementPop.setOutsideTouchable(true);
        settlementPop.setBackgroundDrawable(new BitmapDrawable());
        //防止PopupWindow被软件盘挡住
        settlementPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        settlementPop.setTouchable(true);
        //更改透明度

        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);

        // 设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        settlementPop.setFocusable(true);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        settlementPop.setAnimationStyle(R.style.mypopwindow_anim_style);

        settlementPop.showAtLocation(ll_personal_center, Gravity.BOTTOM, 0, 0);

        settlementPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        rl_mip_xc.setOnClickListener(new View.OnClickListener() {//相册选择
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_PICK);

            }
        });

        rl_mip_pz.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             /**
                                              * 跳转到照相机
                                              */
                                             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                             Uri photoOutputUri;
                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                 photoOutputUri = FileProvider.getUriForFile(getActivity(),getActivity().getPackageName() + ".fileprovider", tempFile);
                                             } else {
                                                 photoOutputUri = Uri.fromFile(tempFile);
                                             }
                                             intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutputUri);
                                             startActivityForResult(intent, REQUEST_CAPTURE);
                                         }
                                     }

        );

        rl_mip_qx.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             settlementPop.dismiss();
                                         }
                                     }

        );

    }

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
        MLog.i("tempFile:" + tempFile.getPath());
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (settlementPop != null) {
            settlementPop.dismiss();
        }
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == getActivity().RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == getActivity().RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }


                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == getActivity().RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    final String cropImagePath = getRealFilePathFromUri(getActivity().getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    personal_center_avatar.setImageBitmap(bitMap);
                    MLog.i("URI:" + cropImagePath);
                    compress(cropImagePath);

                }
                break;
        }
    }



    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    private void saveImage(File file) {

        // mImgUrl为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));

        //构建请求体
        RequestBody requestBody = builder.build();

        //构建请求
        Request request = new Request.Builder()
                .url(Urls.ChangeHeadPortrait)//地址
                .addHeader("userId", user.getId()+"")
                .post(requestBody)//添加请求体
                .build();

        MyApplication.getOkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        MLog.i("图片提交失败，重新提交");
                        saveDateHandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        MLog.i("修改头像："+result);
                        if (result != null && !result.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                user.setImageUrl(jsonObject.getString("Result"));
                                SharedTools.saveObject(user, SharedTools.User);
                                saveDateHandler.sendEmptyMessage(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    //压缩图片
    private void compress(String path) {
        File file = null;
        try {
            file = new File(path);
        } catch (Exception e) {
            saveDateHandler.sendEmptyMessage(2);
            e.printStackTrace();
        }
        if (file != null && file.exists()) {
            Luban.get(getActivity())
                    .load(file)                     //传人要压缩的图片
                    .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI

                        }

                        @Override
                        public void onSuccess(final File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            saveImage(file);
                           /* Thread thread=new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    postImage(file,Urls.ChangeHeadPortrait);
                                }
                            };
                            thread.start();*/
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过去出现问题时调用
                            e.printStackTrace();
                        }
                    }).launch();    //启动压缩
        } else {
            saveDateHandler.sendEmptyMessage(2);

        }
    }


    Handler saveDateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtil.show(getActivity(),"更换头像成功");
                    break;
                case 2:
                    ToastUtil.show(getActivity(),"更换头像失败，请重试!");
                    break;
            }

        }
    };

    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClipImageActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
