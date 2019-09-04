package com.chxip.campusinfo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.activity.adapter.MessageListViewAdpater;
import com.chxip.campusinfo.base.BaseActivity;
import com.chxip.campusinfo.entity.Comment;
import com.chxip.campusinfo.entity.MessageContent;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.Constants;
import com.chxip.campusinfo.util.MLog;
import com.chxip.campusinfo.util.SharedTools;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.util.Utils;
import com.chxip.campusinfo.util.utilNet.HttpVolley;
import com.chxip.campusinfo.util.utilNet.NetCallBackVolley;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.chxip.campusinfo.view.AutoListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCommentActivity extends BaseActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SUCCESS:
                    String Data = msg.obj.toString();
                    int htype = msg.arg1;

                    List<MessageContent> newList = gson.fromJson(Data, new TypeToken<List<MessageContent>>() {
                    }.getType());
                    if (htype == 0) {//进来初始化 和刷新
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        //下拉刷新结束
                        alv_my_comment.onRefreshComplete();
                        if(messageContentList.size()<=0){
                            alv_my_comment.setResultSize(0);
                        }

                        alv_my_comment.setResultSize(messageContentList.size());

                    } else if (htype == 2) {//加载更多
                        alv_my_comment.onLoadComplete();
                        messageContentList.addAll(newList);
                        alv_my_comment.setResultSize(newList.size());
                    } else {//条件查询
                        alv_my_comment.onRefreshComplete();
                        messageContentList.clear();
                        messageContentList.addAll(newList);
                        alv_my_comment.setResultSize(messageContentList.size());

                    }
                    if (null != messageListViewAdpater) {
                        messageListViewAdpater.notifyDataSetChanged();
                    }

                    break;
                case Constants.ERROR:
                    String msgStr = msg.obj.toString();
                    if (msgStr == null || msgStr.equals("")) {
                        ToastUtil.showNetworkError(MyCommentActivity.this);
                    } else {
                        ToastUtil.show(MyCommentActivity.this, msgStr);
                    }

                    break;
            }
        }
    };
    
    @BindView(R.id.et_my_comment_keyword)
    EditText et_my_comment_keyword;
    @BindView(R.id.alv_my_comment)
    AutoListView alv_my_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
        ButterKnife.bind(this);

        initBaseViews();
        setTitle("我的评论");

        gson=new Gson();
        user= (User) SharedTools.readObject(SharedTools.User);
        initViews();
        
    }

    User user;
    List<MessageContent> messageContentList;
    MessageListViewAdpater messageListViewAdpater;
    Gson gson;
    int page = 1;
    int size = 10;
    int httpType = 1;
    String keyWord = "";


    @Override
    public void onResume() {
        super.onResume();
        page=1;
        httpType=1;
        GetMessageByCommentUserId();
    }

    private void initViews(){
        messageContentList=new ArrayList<>();
        messageListViewAdpater=new MessageListViewAdpater(MyCommentActivity.this,messageContentList);
        alv_my_comment.setAdapter(messageListViewAdpater);

        alv_my_comment.setOnRefreshListener(new AutoListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpType = 1;
                GetMessageByCommentUserId();
            }
        });

        alv_my_comment.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                page++;
                httpType = 2;
                GetMessageByCommentUserId();
            }
        });

        et_my_comment_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyWord = s.toString();
                httpType = 1;
                page = 1;
                GetMessageByCommentUserId();
            }
        });

        messageListViewAdpater.setOnClickListener(new MessageListViewAdpater.OnClickListener() {
            @Override
            public void onDeleteMessage(final int messageId, final int messagePosition) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommentActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除此信息吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessageById(messageId,messagePosition);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }

            @Override
            public void onCommentClick(int messageId,int messagePosition) {
                showComment(messageId,messagePosition);
            }

            @Override
            public void onReplyClick(User commentUser,int messageId,int messagePosition) {
                showReply(commentUser,messageId,messagePosition);
            }

            @Override
            public void onDeleteCommentClick(final int commentId, final int messagePosition, final int commentPosition) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommentActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除此评论吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteCommentById(commentId,messagePosition,commentPosition);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }

            @Override
            public void onUpdateMessage(MessageContent messageContent) {

            }
        });
    }

    Dialog commentDialog;

    private void showComment(final int messageId, final int messagePosition){
        commentDialog = new Dialog(MyCommentActivity.this, R.style.mystyle);// 创建自定义样式dialog
        commentDialog.setContentView(R.layout.message_comment_item);// 设置布局
        final EditText et_message_comment= (EditText) commentDialog.findViewById(R.id.et_message_comment);
        LinearLayout ll_message_commnet_save= (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_message_comment.getText().toString().equals("")){
                    ToastUtil.show(MyCommentActivity.this,"请输入信息");
                    return;
                }
                Comment comment=new Comment();
                comment.setMessageId(messageId);
                comment.setCommentContent(et_message_comment.getText().toString());
                comment.setCommentUserId(user.getId());
                saveData(comment, messagePosition);
            }
        });

        commentDialog.show();
    }


    private void showReply(final User commentUser, final int messageId, final int messagePosition){


        commentDialog = new Dialog(MyCommentActivity.this, R.style.loading_dialog);// 创建自定义样式dialog
        commentDialog.setContentView(R.layout.message_comment_item);// 设置布局
        final EditText et_message_comment= (EditText) commentDialog.findViewById(R.id.et_message_comment);
        et_message_comment.setHint("回复 "+commentUser.getRealName());
        LinearLayout ll_message_commnet_save= (LinearLayout) commentDialog.findViewById(R.id.ll_message_commnet_save);
        ll_message_commnet_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_message_comment.getText().toString().equals("")){
                    ToastUtil.show(MyCommentActivity.this,"请输入信息");
                    return;
                }
                Comment comment=new Comment();
                comment.setMessageId(messageId);
                comment.setReplyUserId(user.getId());
                comment.setCommentContent(et_message_comment.getText().toString());
                comment.setCommentUserId(commentUser.getId());
                saveData(comment, messagePosition);
            }
        });
        commentDialog.show();
    }


    private void DeleteCommentById(int commentId, final int messagePosition, final int commentPosition){
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("删除评论:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(MyCommentActivity.this,"删除成功");
                        messageContentList.get(messagePosition).getComments().remove(commentPosition);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                dialog.dismiss();
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog = Utils.createLoadingDialog(MyCommentActivity.this, getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteCommentById;
            path = path + "?commentId=" + commentId;
            MLog.i("删除评论：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    private void DeleteMessageById(int messageId, final int messagePosition){
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("删除信息:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(MyCommentActivity.this,"删除成功");
                        messageContentList.remove(messagePosition);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
                        String strmsg = jsonObject.getString("Message");
                        Message msg = new Message();
                        msg.obj = strmsg;
                        msg.what = Constants.ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyErrorResponse(VolleyError error) {
                MLog.i(error.toString());
                dialog.dismiss();
                //ToastUtil.showNetworkError(CustomerQueryActivity.this);
            }
        };
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog = Utils.createLoadingDialog(MyCommentActivity.this, getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteMessageById;
            path = path + "?messageId=" + messageId;
            MLog.i("删除信息：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    Dialog dialog;
    private void saveData(Comment comment, final int messagePosition){

        MLog.i("添加评论："+gson.toJson(comment));

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("添加评论:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        String result=jsonObject.getString("Result");
                        Comment comment1=gson.fromJson(result,Comment.class);
                        commentDialog.dismiss();
                        messageContentList.get(messagePosition).getComments().add(comment1);
                        messageListViewAdpater.notifyDataSetChanged();
                    } else {
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
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            dialog=Utils.createLoadingDialog(MyCommentActivity.this,getString(R.string.app_dialog_save));
            dialog.show();
            String path = Urls.AddComment;
            path = path + "?comment=" + gson.toJson(comment);
            MLog.i("添加评论：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }

    }

    /**
     * 获取我的评论
     */
    private void GetMessageByCommentUserId() {

        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("获取我的评论:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        String str = jsonObject.getString("Result");
                        Message msg = new Message();
                        msg.obj = str;
                        msg.what = Constants.SUCCESS;
                        handler.sendMessage(msg);
                    } else {
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
            }
        };
        if (Utils.isNetworkAvailable(MyCommentActivity.this)) {
            String path = Urls.GetMessageByCommentUserId;
            path = path + "?keyWord=" + keyWord;
            path = path + "&userId="+user.getId();
            path = path + "&page=" + page;
            path = path + "&size=" + size;
            MLog.i("获取我的评论：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }
}
