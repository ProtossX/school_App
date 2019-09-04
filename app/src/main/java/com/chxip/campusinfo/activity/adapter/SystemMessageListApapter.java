package com.chxip.campusinfo.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.chxip.campusinfo.R;
import com.chxip.campusinfo.entity.Notification;
import com.chxip.campusinfo.util.Utils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
public class SystemMessageListApapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView

    //上拉加载更多状态-默认为0
    private int load_more_status=0;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //数据全部加载完毕
    public static final int  LOADING_END=2;

    Context context;
    List<Notification> announcementList;
    LayoutInflater layoutInflater;
    OnClickListener onClickListener;
    private boolean isShowSelect=false;

    public boolean getIsShowSelect() {
        return isShowSelect;
    }

    public void setIsShowSelect(boolean showSelect) {
        isShowSelect = showSelect;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SystemMessageListApapter(Context context, List<Notification> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
        layoutInflater=LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==TYPE_ITEM){
            View view=layoutInflater.inflate(R.layout.announcement_list_item,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            ItemViewHolder footViewHolder=new ItemViewHolder(view);
            return footViewHolder;
        }else if(viewType==TYPE_FOOTER){
            View foot_view=layoutInflater.inflate(R.layout.recycler_load_more_layout,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            FootViewHolder footViewHolder=new FootViewHolder(foot_view);
            return footViewHolder;
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder= (ItemViewHolder) holder;
            itemViewHolder.ll_announcement_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.setOnItemClickListener(v,position);
                }
            });
            Notification announcement=announcementList.get(position);
            itemViewHolder.tv_announcement_time.setText(announcement.getCreateTime());
            itemViewHolder.tv_announcement_title.setText(announcement.getNotificationTitle());
            itemViewHolder.tv_announcement_content.setText(announcement.getNotificationContent());

        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    footViewHolder.loading.setVisibility(View.VISIBLE);
                    break;
                case LOADING_END:
                    footViewHolder.foot_view_item_tv.setText("已经没有更多数据了~");
                    footViewHolder.loading.setVisibility(View.GONE);
                    break;
            }
        }


    }

    @Override
    public int getItemCount() {
        return announcementList.size()+1;
    }
    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }
    /**
     * 进行判断是普通Item视图还是FootView视图
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_announcement_time;
        TextView tv_announcement_content;
        TextView tv_announcement_title;
        LinearLayout ll_announcement_item;
        public ItemViewHolder(View view){
            super(view);
            tv_announcement_time = (TextView)view.findViewById(R.id.tv_announcement_time);
            tv_announcement_content = (TextView)view.findViewById(R.id.tv_announcement_content);
            tv_announcement_title = (TextView)view.findViewById(R.id.tv_announcement_title);
            ll_announcement_item= (LinearLayout) view.findViewById(R.id.ll_announcement_item);
        }
    }

    /**
     * 底部FootView布局
     */
    public class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;

        ProgressBar loading;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.tv_more);
            loading= (ProgressBar) view.findViewById(R.id.loading);
        }
    }

    public interface OnClickListener{
        void setOnItemClickListener(View view, int position);
    }
}
