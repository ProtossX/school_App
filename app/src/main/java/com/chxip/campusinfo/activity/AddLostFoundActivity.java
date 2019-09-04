package com.chxip.campusinfo.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.activity.adapter.PhotoAdapter;
import com.chxip.campusinfo.activity.adapter.RecyclerItemClickListener;
import com.chxip.campusinfo.application.MyApplication;
import com.chxip.campusinfo.base.BaseActivity;
import com.chxip.campusinfo.entity.Image;
import com.chxip.campusinfo.entity.MessageContent;
import com.chxip.campusinfo.entity.Schedule;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.Constants;
import com.chxip.campusinfo.util.MLog;
import com.chxip.campusinfo.util.SharedTools;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.util.Utils;
import com.chxip.campusinfo.util.utilNet.HttpVolley;
import com.chxip.campusinfo.util.utilNet.NetCallBackVolley;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class AddLostFoundActivity extends BaseActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:

                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(AddLostFoundActivity.this);
                    } else {
                        ToastUtil.show(AddLostFoundActivity.this, msgStr);
                    }
                    break;
            }
        }
    };


    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.recyclerView_image)
    RecyclerView recyclerView_image;
    @BindView(R.id.ll_save)
    LinearLayout ll_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost_found);
        ButterKnife.bind(this);

        initBaseViews();
        setTitle("发表信息");
        type=getIntent().getIntExtra("type",0);
        if(type==3){
            messageContent= (MessageContent) getIntent().getSerializableExtra("messageContent");
        }
        user= (User) SharedTools.readObject(SharedTools.User);
        gson=new Gson();
        initViews();

    }

    int type;
    MessageContent messageContent;
    User user;
    Gson gson;
    PhotoAdapter photoAdapter;
    ArrayList<String> photoLists;

    private void initViews(){
        photoLists = new ArrayList<>();
        if(type==3){
            et_title.setText(messageContent.getMessageTitle());
            et_content.setText(messageContent.getMessageContent());
            if(messageContent.getImages()!=null){
                for (int i=0 ;i < messageContent.getImages().size() ; i ++){
                    photoLists.add(Urls.BASE_IMG+messageContent.getImages().get(i).getImageUrl());
                }
            }
        }


        photoAdapter = new PhotoAdapter(AddLostFoundActivity.this, photoLists);
        recyclerView_image.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        photoAdapter.setMAX(6);
        recyclerView_image.setAdapter(photoAdapter);
        recyclerView_image.addOnItemTouchListener(new RecyclerItemClickListener(AddLostFoundActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            // 先判断是否有权限。
                            if (AndPermission.hasPermission(AddLostFoundActivity.this, Manifest.permission.CAMERA)
                                    && AndPermission.hasPermission(AddLostFoundActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                showPhotoPop();
                            } else {
                                // 申请权限。
                                AndPermission.with(AddLostFoundActivity.this)
                                        .requestCode(101)
                                        .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .send();
                            }

                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(photoLists)
                                    .setCurrentItem(position)
                                    .start(AddLostFoundActivity.this);
                        }
                    }
                }));
    }


    @OnClick({ R.id.ll_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_save:
                saveData();
                break;
        }
    }

    Dialog dialog;
    int messageId;

    private void saveData(){
        if(photoLists.size()==0){
            ToastUtil.show(this,"请选择图片");
            return;
        }
        MessageContent newMessageContent=new MessageContent();
        if(type==1){
            newMessageContent.setMessageType(1);
        }else if(type==2){
            newMessageContent.setMessageType(2);
        }

        if(messageContent!=null){
            newMessageContent.setMessageId(messageContent.getMessageId());
            newMessageContent.setDeleteiImages(messageContent.getDeleteiImages());
        }
        newMessageContent.setMessageTitle(et_title.getText().toString());
        newMessageContent.setMessageContent(et_content.getText().toString());
        if(newMessageContent.getMessageTitle().equals("")){
            ToastUtil.show(this,"请输入标题");
            return;
        }
        if(newMessageContent.getMessageContent().equals("")){
            ToastUtil.show(this,"请输入内容");
            return;
        }
        newMessageContent.setUserId(user.getId());


        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("添加信息:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        messageId=jsonObject.getInt("Result");
                        //添加成功

                        saveImage();
                    } else {
                        dialog.dismiss();
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
                dialog.dismiss();
            }
        };
        if (Utils.isNetworkAvailable(this)) {
            dialog=Utils.createLoadingDialog(this,getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.AddMessage;
            path = path + "?message=" + gson.toJson(newMessageContent);
            MLog.i("添加信息：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }

    }

    int index = 0;//正在上传图片的下标
    List<String> saveImageUrlList;
    /**
     * 保存图片
     */
    private void saveImage( ) {
        saveImageUrlList=new ArrayList<>();
        if (photoLists != null && photoLists.size() > 0) {
            for (int i = 0; i < photoLists.size(); i++) {
                if (photoLists.get(i).contains("http")) {//网络图片，不需要上传

                }else{
                    saveImageUrlList.add(photoLists.get(i));
                }
            }
        }

        if (saveImageUrlList.size() > 0) {
            if(Utils.isNetworkAvailable(this)){
                compress(saveImageUrlList.get(index));
            }
        }else{
            dialog.dismiss();
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
                .url(Urls.AddImage)//地址
                .addHeader("contentId", messageId+"")
                .post(requestBody)//添加请求体
                .build();

        MyApplication.getOkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage());
                        //重新上传
                        compress(saveImageUrlList.get(index));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        MLog.i("上传图片:" + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            boolean resutl = jsonObject.getBoolean("Success");
                            if (resutl) {
                                if (index < saveImageUrlList.size()-1) {
                                    index++;
                                    compress(saveImageUrlList.get(index));
                                } else {
                                    //图片全部上传成功
                                    saveDateHandler.sendEmptyMessage(1);
                                }
                            } else {
                                String strmsg = jsonObject.getString("Message");
                                MLog.i("上传第" + index + "张图片的errorMsg:" + strmsg);
                                //重新上传
                                compress(saveImageUrlList.get(index));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    Handler saveDateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    ToastUtil.show(AddLostFoundActivity.this,"保存成功");
                    finish();
                    break;
                case 2:
                    if (index < saveImageUrlList.size()-1) {
                        index++;
                        compress(saveImageUrlList.get(index));
                    } else {
                        //图片全部上传成功
                        saveDateHandler.sendEmptyMessage(1);
                    }
                    break;
            }

        }
    };

    private void compress(String path) {
        File file = new File(path);
        if(!file.exists()){//文件不存在
            saveDateHandler.sendEmptyMessage(2);
        }
        Luban.get(this)
                .load(file)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI

                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        saveImage(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过去出现问题时调用
                        e.printStackTrace();
                    }
                }).launch();    //启动压缩
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddLostFoundActivity.this.RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if(messageContent!=null && messageContent.getImages()!=null && messageContent.getImages().size()>0){
                for (int i =0 ;i < photoLists.size() ; i++){
                    boolean isExists=false;
                    if(photoLists.get(i).contains("http")) {
                        for (int j = 0; j < photos.size(); j++) {
                            if (photoLists.get(i).equals(photos.get(j))) {
                                isExists = true;
                                break;
                            }
                        }
                    }
                    if(!isExists){
                        for (int j=0 ;j < messageContent.getImages().size() ; j++){
                            if(photoLists.get(i).contains(messageContent.getImages().get(j).getImageUrl())){
                                messageContent.getDeleteiImages().add(messageContent.getImages().get(j));
                            }
                        }
                    }
                }
            }

            photoLists.clear();

            if (photos != null) {
                photoLists.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CAPTURE) {
            if (resultCode != 0) {
                photoLists.add(path);
                photoAdapter.notifyDataSetChanged();
            } else {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                    MLog.i("删除图片");
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            showPhotoPop();
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(AddLostFoundActivity.this, deniedPermissions)) {

                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(AddLostFoundActivity.this, 300)
                        .setTitle(getString(R.string.permission_fail_apply))
                        .setMessage(getString(R.string.permission_fail_apply_message_one))
                        .setPositiveButton(getString(R.string.permission_set))
                        .show();
            }
        }
    };


    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    String path = "";//系统相机拍照照片存储路径

    private void showPhotoPop() {

        View photoPopView = LayoutInflater.from(AddLostFoundActivity.this).inflate(R.layout.my_information_pop, null);

        RelativeLayout rl_mip_xc = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_xc);
        RelativeLayout rl_mip_pz = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_pz);
        RelativeLayout rl_mip_qx = (RelativeLayout) photoPopView.findViewById(R.id.rl_mip_qx);

        final PopupWindow photpPop = new PopupWindow(photoPopView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        photpPop.setOutsideTouchable(true);
        photpPop.setBackgroundDrawable(new BitmapDrawable());

        photpPop.setTouchable(true);
        // 设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        photpPop.setFocusable(true);
        //更改透明度
        WindowManager.LayoutParams params = AddLostFoundActivity.this.getWindow().getAttributes();
        params.alpha = 0.5f;
        AddLostFoundActivity.this.getWindow().setAttributes(params);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        photpPop.setAnimationStyle(R.style.mypopwindow_anim_style);
        photpPop.showAtLocation(recyclerView_image, Gravity.BOTTOM, 0, 0);

        photpPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = AddLostFoundActivity.this.getWindow().getAttributes();
                params.alpha = 1f;
                AddLostFoundActivity.this.getWindow().setAttributes(params);

            }
        });


        rl_mip_xc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(6)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .setSelected(photoLists)
                        .start(AddLostFoundActivity.this);

                photpPop.dismiss();
            }
        });

        rl_mip_pz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photpPop.dismiss();
                path = Environment.getExternalStorageDirectory().getPath() + "/image/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                path = path + Utils.getNowTimeYYYYMMDDHHmmssSSS() + ".jpg";
                File file = new File(path);
                if (!file.exists()) {
                    //在指定的文件夹中创建文件
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                File tempFile = new File(path);
                /**
                 * 跳转到照相机
                 */
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                Uri photoOutputUri;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoOutputUri = FileProvider.getUriForFile(AddLostFoundActivity.this,
                            AddLostFoundActivity.this.getPackageName() + ".fileprovider", tempFile);
                } else {
                    photoOutputUri = Uri.fromFile(file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoOutputUri);
                startActivityForResult(intent, REQUEST_CAPTURE);

                MLog.i("拍照图片保存路径:" + path);

            }
        });

        rl_mip_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photpPop.dismiss();
            }
        });

    }
}
