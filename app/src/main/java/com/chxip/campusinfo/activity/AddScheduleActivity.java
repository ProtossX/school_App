package com.chxip.campusinfo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chxip.campusinfo.R;
import com.chxip.campusinfo.base.BaseActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScheduleActivity extends BaseActivity {

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
                        ToastUtil.showNetworkError(AddScheduleActivity.this);
                    } else {
                        ToastUtil.show(AddScheduleActivity.this, msgStr);
                    }
                    break;
            }
        }
    };


    @BindView(R.id.et_schedule_name)
    EditText et_schedule_name;
    @BindView(R.id.et_schedule_room)
    EditText et_schedule_room;
    @BindView(R.id.et_schedule_teacher)
    EditText et_schedule_teacher;
    @BindView(R.id.tv_schedule_week)
    TextView tv_schedule_week;
    @BindView(R.id.ll_schedule_week)
    LinearLayout ll_schedule_week;
    @BindView(R.id.tv_schedule_day)
    TextView tv_schedule_day;
    @BindView(R.id.ll_schedule_day)
    LinearLayout ll_schedule_day;
    @BindView(R.id.et_schedule_start)
    EditText et_schedule_start;
    @BindView(R.id.et_schedule_step)
    EditText et_schedule_step;
    @BindView(R.id.ll_schedule_save)
    LinearLayout ll_schedule_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        ButterKnife.bind(this);
        initBaseViews();
        setTitle("添加课程");
        type=getIntent().getIntExtra("type",0);
        if(type==2){
            schedule= (com.zhuangfei.timetable.model.Schedule) getIntent().getSerializableExtra("schedule");
            setTitle("课程明细");
        }
        initViews();
    }

    int type;
    com.zhuangfei.timetable.model.Schedule schedule;

    private void initViews(){
        weekList=new ArrayList<>();
        user= (User) SharedTools.readObject(SharedTools.User);
        gson=new Gson();
        if(type==2){
            et_schedule_name.setText(schedule.getName());
            et_schedule_room.setText(schedule.getRoom());
            et_schedule_teacher.setText(schedule.getTeacher());
            String week="";
            if(schedule.getWeekList()!=null){
                for (int i=0 ;i < schedule.getWeekList().size() ; i ++){
                    weekList.add(schedule.getWeekList().get(i));
                    if(week.equals("")){
                        week="第" + schedule.getWeekList().get(i) + "周";
                    }else{
                        week=week+"、第" + schedule.getWeekList().get(i) + "周";
                    }
                }
            }

            tv_schedule_week.setText(week);
            day=schedule.getDay();
            String day="";
            switch (schedule.getDay()){
                case 1:
                    day="星期一";
                    break;
                case 2:
                    day="星期二";
                    break;
                case 3:
                    day="星期三";
                    break;
                case 4:
                    day="星期四";
                    break;
                case 5:
                    day="星期五";
                    break;
                case 6:
                    day="星期六";
                    break;
                case 7:
                    day="星期日";
                    break;
            }
            tv_schedule_day.setText(day);
            et_schedule_start.setText(schedule.getStart()+"");
            et_schedule_step.setText(schedule.getStep()+"");
        }
    }

    User user;
    Dialog dialog;
    List<Integer> weekList;
    int day;
    Gson gson;


    @OnClick({R.id.ll_schedule_week, R.id.ll_schedule_day, R.id.ll_schedule_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_schedule_week:
                String[] areas = new String[25];
                for (int i = 0; i < 25; i++) {
                    areas[i] = "第" + (i + 1) + "周";
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("请选择上课周");
                alertBuilder.setMultiChoiceItems(areas, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        if (isChecked){
                            weekList.add(i+1);
                        }else {
                            weekList.remove((Integer)(i+1));
                        }
                    }
                });
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String week="";
                        for (int j =0 ;j < weekList.size() ; j ++){
                            if(week.equals("")){
                                week="第" + weekList.get(j) + "周";
                            }else{
                                week=week+"、第" + weekList.get(j) + "周";
                            }
                        }
                        tv_schedule_week.setText(week);
                        dialog.dismiss();
                    }
                });
                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = alertBuilder.create();
                dialog.show();
                break;
            case R.id.ll_schedule_day:
                final String[] items = new String[]{"星期1","星期二","星期三","星期四","星期五","星期六","星期日"};
                AlertDialog.Builder alertBuildera = new AlertDialog.Builder(this);
                alertBuildera.setTitle("这是多选框");
                alertBuildera.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        day=i+1;
                    }
                });

                alertBuildera.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_schedule_day.setText(items[day-1]);
                        dialog.dismiss();
                    }
                });
                alertBuildera.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog = alertBuildera.create();
                dialog.show();
                break;
            case R.id.ll_schedule_save:
                saveData();
                break;
        }
    }

    private void saveData(){
        Schedule  newSchedule=new Schedule();
        if(schedule!=null){
            newSchedule.setScheduleId(schedule.getScheduleId());
        }
        newSchedule.setName(et_schedule_name.getText().toString());
        newSchedule.setRoom(et_schedule_room.getText().toString());
        newSchedule.setTeacher(et_schedule_teacher.getText().toString());
        String week="";
        for (int i =0 ; i < weekList.size() ; i++){
            if(week.equals("")){
                week=weekList.get(i)+"";
            }else{
                week=week+"、"+weekList.get(i)+"";
            }
        }
        newSchedule.setWeek(week);
        newSchedule.setDay(day);
        newSchedule.setStart(Utils.getInt(et_schedule_start.getText().toString()));
        newSchedule.setStep(Utils.getInt(et_schedule_step.getText().toString()));

        if(newSchedule.getName().equals("")){
            ToastUtil.show(this,"请输入课程名称");
            return;
        }
        if(week.equals("")){
            ToastUtil.show(this,"请选择上课周");
            return;
        }
        if(day==0){
            ToastUtil.show(this,"请选择上课时间");
            return;
        }
        if(newSchedule.getStart()==0){
            ToastUtil.show(this,"请输入开始上课的节次");
            return;
        }
        if(newSchedule.getStep()==0){
            ToastUtil.show(this,"请输入上课节数");
            return;
        }
        newSchedule.setUserId(user.getId());


        NetCallBackVolley ncbv = new NetCallBackVolley() {
            @Override
            public void onMyRespone(String response) {
                MLog.i("添加课程:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("Success");
                    if (status) {
                       //添加成功
                        ToastUtil.show(AddScheduleActivity.this,"保存成功");
                        finish();

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
        if (Utils.isNetworkAvailable(this)) {
            String path = Urls.AddSchedule;
            path = path + "?schedule=" + gson.toJson(newSchedule);
            MLog.i("添加课程：" + path);
            HttpVolley.volleStringRequestGetString(path, ncbv, handler);
        }

    }
}
