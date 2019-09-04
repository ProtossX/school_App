package com.chxip.campusinfo.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chxip.campusinfo.R;
import com.chxip.campusinfo.entity.Comment;
import com.chxip.campusinfo.entity.User;
import com.chxip.campusinfo.util.SharedTools;

import java.util.List;

/**
 * Created by 陈湘平 on 2018/10/10.
 */

public class CommentListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Comment> comments;
    MessageListViewAdpater.OnClickListener onClickListener;
    int messagePosition;
    User user;

    public CommentListViewAdapter(User user,int messagePosition,Context context, List<Comment> comments,MessageListViewAdpater.OnClickListener onClickListener) {
        this.context = context;
        this.comments = comments;
        layoutInflater = LayoutInflater.from(context);
        this.onClickListener=onClickListener;
        this.messagePosition=messagePosition;
        this.user=user;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.comment_item, null);
        TextView tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
        TextView tv_comment_delete= (TextView) convertView.findViewById(R.id.tv_comment_delete);
        final Comment comment=comments.get(position);
        String commentStr="";
        if(comment.getReplyUser()!=null){
            commentStr=commentStr+comment.getReplyUser().getRealName()+" 回复 ";
            if(comment.getCommentUser()!=null){
                commentStr=commentStr+comment.getCommentUser().getRealName()+"：";
            }
        }else{
            if(comment.getCommentUser()!=null){
                commentStr=commentStr+comment.getCommentUser().getRealName()+"：";
            }
        }
        commentStr=commentStr+comment.getCommentContent();
        tv_comment.setText(commentStr);
        if(comment.getCommentUserId()==user.getId()){
            tv_comment_delete.setVisibility(View.VISIBLE);
        }else{
            tv_comment_delete.setVisibility(View.INVISIBLE);
        }
        tv_comment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteCommentClick(comment.getCommentId(),messagePosition,position);
            }
        });

        return convertView;
    }

}
