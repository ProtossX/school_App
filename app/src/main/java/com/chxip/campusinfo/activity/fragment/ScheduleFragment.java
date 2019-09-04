package com.chxip.campusinfo.activity.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.activity.AddScheduleActivity;
import com.chxip.campusinfo.activity.SystemMessageActivity;
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
import com.google.gson.reflect.TypeToken;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 陈湘平 on 2018/10/8.
 */

public class ScheduleFragment extends Fragment {

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
                        ToastUtil.showNetworkError(getActivity());
                    } else {
                        ToastUtil.show(getActivity(), msgStr);
                    }
                    break;
            }
        }
    };

    View view;
    @BindView(R.id.id_title)
    TextView titleTextView;
    @BindView(R.id.id_layout)
    LinearLayout id_layout;
    @BindView(R.id.id_weekview)
    WeekView mWeekView;
    @BindView(R.id.id_timetableView)
    TimetableView mTimetableView;
    @BindView(R.id.iv_system_message)
    ImageView iv_system_message;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        user= (User) SharedTools.readObject(SharedTools.User);
        gson=new Gson();
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetScheduleByUserId();
    }

    User user;
    Gson gson;

    private void initViews(){
        scheduleList=new ArrayList<>();
        iv_system_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SystemMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    Dialog dialog;

    private void GetScheduleByUserId(){
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("获取课程:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        String result = jsonObject.getString("Result");
                        List<com.chxip.campusinfo.entity.Schedule> newList = gson.fromJson(result, new TypeToken<List<com.chxip.campusinfo.entity.Schedule>>() {
                        }.getType());
                        scheduleList.clear();
                        scheduleList.addAll(newList);
                        initTimetableView();
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
        if (Utils.isNetworkAvailable(getActivity())) {
            dialog = Utils.createLoadingDialog(getActivity(), getString(R.string.app_dialog_getData));
            dialog.show();
            String path = Urls.GetScheduleByUserId;
            path = path + "?userId=" + user.getId();
            MLog.i("获取课程：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }

    List<com.chxip.campusinfo.entity.Schedule> scheduleList;

    //记录切换的周次，不一定是当前周
    int target = -1;

    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        mTimetableView.isShowWeekends(true).updateView();
        mTimetableView.isShowNotCurWeek(false).updateView();

        mWeekView.itemCount(25);//设置25周
        //设置周次选择属性
        mWeekView.source(scheduleList)
                .curWeek(1)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                }).callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(scheduleList)
                .curWeek(1)
                .curTerm("")
                .maxSlideItem(12)
                .monthWidthDp(30)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
//                .alpha(0.1f, 0.1f, 0.6f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start, final Schedule subject) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("提示");
                        builder.setMessage("确定删除此课程吗？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteScheduleById(subject);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.create().show();
                    }
                })
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        Intent intent=new Intent(getActivity(),AddScheduleActivity.class);
                        intent.putExtra("schedule",scheduleList.get(0));
                        intent.putExtra("type",2);
                        startActivity(intent);
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Intent intent=new Intent(getActivity(),AddScheduleActivity.class);
                        intent.putExtra("type",1);
                        startActivity(intent);
                    }
                })
                .showView();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    public void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[25];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += bean.getName() + ","+bean.getWeekList().toString()+","+bean.getStart()+","+bean.getStep()+"\n";
        }
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.id_title, R.id.id_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;

        }
    }

    private void DeleteScheduleById(final Schedule schedule){
        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("删除课程:" + response);
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                        ToastUtil.show(getContext(),"删除成功");
                        mTimetableView.dataSource().remove(schedule);
                        mTimetableView.updateView();
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
        if (Utils.isNetworkAvailable(getActivity())) {
            dialog = Utils.createLoadingDialog(getActivity(), getString(R.string.app_dialog_delete));
            dialog.show();
            String path = Urls.DeleteScheduleById;
            path = path + "?scheduleId=" + schedule.getScheduleId();
            MLog.i("删除课程：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
