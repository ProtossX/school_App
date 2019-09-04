package com.chxip.campusinfo.activity.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.activity.AddLostFoundActivity;
import com.chxip.campusinfo.entity.MessageContent;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.SharedTools;
import com.chxip.campusinfo.util.utilNet.Urls;
import com.chxip.campusinfo.view.MyListView;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by 陈湘平 on 2018/10/10.
 */

public class MessageListViewAdpater extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<MessageContent> messageContents;
    User user;

    int type;

    public void setType(int type) {
        this.type = type;
    }

    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MessageListViewAdpater(Context context, List<MessageContent> messageContents) {
        this.context = context;
        this.messageContents = messageContents;
        layoutInflater = LayoutInflater.from(context);
        user= (User) SharedTools.readObject(SharedTools.User);
    }

    @Override
    public int getCount() {
        return messageContents.size();
    }

    @Override
    public Object getItem(int position) {
        return messageContents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int messagePosition, View convertView, ViewGroup parent) {

        H h;

        convertView = layoutInflater.inflate(R.layout.message_item, null);
        h = new H();
        h.iv_user_avatar = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
        h.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
        h.tv_createTime = (TextView) convertView.findViewById(R.id.tv_createTime);
        h.tv_message_title = (TextView) convertView.findViewById(R.id.tv_message_title);
        h.tv_message_content = (TextView) convertView.findViewById(R.id.tv_message_content);
        h.recyclerView_image = (RecyclerView) convertView.findViewById(R.id.recyclerView_image);
        h.mlv_message_comment= (MyListView) convertView.findViewById(R.id.mlv_message_comment);
        h.iv_comment= (ImageView) convertView.findViewById(R.id.iv_comment);
        h.tv_message_type= (TextView) convertView.findViewById(R.id.tv_message_type);
        h.tv_delete= (TextView) convertView.findViewById(R.id.tv_delete);
        h.tv_update= (TextView) convertView.findViewById(R.id.tv_update);
        convertView.setTag(h);


        final MessageContent messageContent = messageContents.get(messagePosition);

        Glide.with(context)
                .load(Urls.BASE_IMG + "/" + messageContent.getUser().getImageUrl())
                .error(R.mipmap.default_avatar)
                .into(h.iv_user_avatar);
        h.tv_user_name.setText(messageContent.getUser().getRealName());
        h.tv_createTime.setText(messageContent.getCreateTime());
        h.tv_message_title.setText(messageContent.getMessageTitle());
        h.tv_message_content.setText(messageContent.getMessageContent());


        final ArrayList<String> photoLists = new ArrayList<>();
        for (int i = 0; i < messageContent.getImages().size(); i++) {
            photoLists.add(Urls.BASE_IMG + messageContent.getImages().get(i).getImageUrl());
        }
        PhotoTwoAdapter photoTwoAdapter = new PhotoTwoAdapter(context, photoLists);
        h.recyclerView_image.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        photoTwoAdapter.setMAX(6);
        h.recyclerView_image.setAdapter(photoTwoAdapter);
        h.recyclerView_image.addOnItemTouchListener(new RecyclerItemClickListener(context,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        PhotoPreview.builder()
                                .setPhotos(photoLists)
                                .setShowDeleteButton(false)
                                .setCurrentItem(position)
                                .start((Activity) context);

                    }
                }));

        CommentListViewAdapter commentListViewAdapter=new CommentListViewAdapter(user,messagePosition,context,messageContent.getComments(),onClickListener);
        h.mlv_message_comment.setAdapter(commentListViewAdapter);
        h.mlv_message_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickListener.onReplyClick(messageContent.getComments().get(position).getCommentUser(),
                        messageContent.getMessageId(),messagePosition);
            }
        });
        h.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCommentClick(messageContent.getMessageId(),messagePosition);
            }
        });

        if(messageContent.getMessageType()==1){
            h.tv_message_type.setText("失");
        }else{
            h.tv_message_type.setText("信");
        }

        if(messageContent.getUserId()==user.getId()){
            h.tv_delete.setVisibility(View.VISIBLE);
        }else{
            h.tv_delete.setVisibility(View.INVISIBLE);
        }
        h.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteMessage(messageContent.getMessageId(),messagePosition);
            }
        });

        if(type==3){
            h.tv_update.setVisibility(View.VISIBLE);
            h.tv_message_type.setVisibility(View.GONE);
        }else{
            h.tv_update.setVisibility(View.GONE);
            h.tv_message_type.setVisibility(View.VISIBLE);
        }
        h.tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onUpdateMessage(messageContent);
            }
        });


        return convertView;
    }

    public interface OnClickListener{
        void onDeleteMessage(int messageId,int messagePosition);
        void onCommentClick(int messageId,int messagePosition);
        void onReplyClick(User commentUser,int messageId,int messagePosition);
        void onDeleteCommentClick(int commentId,int messagePosition,int commentPosition);
        void onUpdateMessage(MessageContent messageContent);
    }

    class H {
        ImageView iv_user_avatar;
        TextView tv_user_name;
        TextView tv_createTime;
        TextView tv_message_title;
        TextView tv_message_content;
        RecyclerView recyclerView_image;
        MyListView mlv_message_comment;
        ImageView iv_comment;
        TextView tv_delete;
        TextView tv_message_type;
        TextView tv_update;
    }
}
