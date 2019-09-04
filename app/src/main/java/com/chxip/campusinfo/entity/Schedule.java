package com.chxip.campusinfo.entity;

import com.chxip.campusinfo.util.Utils;
import com.zhuangfei.timetable.model.ScheduleEnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 陈湘平 on 2018/10/9.
 */

public class Schedule implements ScheduleEnable {

    private int scheduleId;

    private int userId;
    /**
     * 课程名
     */
    private String name="";

    /**
     * 教室
     */
    private String room="";

    /**
     * 教师
     */
    private String teacher="";

    /**
     * 第几周至第几周上
     */
    private List<Integer> weekList=new ArrayList<>();

    private String week;

    /**
     * 开始上课的节次
     */
    private int start=0;

    /**
     * 上课节数
     */
    private int step=0;

    /**
     * 周几上
     */
    private int day=0;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public List<Integer> getWeekList() {
        if(weekList.size()==0 && !week.equals("")){
            String[] weekSrr=getWeek().split("、");
            for (int i=0 ;i <weekSrr.length ; i ++){
                weekList.add(Utils.getInt(weekSrr[i]));
            }
        }
        return weekList;
    }

    public void setWeekList(List<Integer> weekList) {
        this.weekList = weekList;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @Override
    public com.zhuangfei.timetable.model.Schedule getSchedule() {
        com.zhuangfei.timetable.model.Schedule schedule=new com.zhuangfei.timetable.model.Schedule();
        schedule.setDay(getDay());
        schedule.setName(getName());
        schedule.setRoom(getRoom());
        schedule.setStart(getStart());
        schedule.setStep(getStep());
        schedule.setTeacher(getTeacher());
        schedule.setWeekList(getWeekList());
        schedule.setScheduleId(getScheduleId());
        Random random = new Random();
        int num = random.nextInt(10);
        schedule.setColorRandom(num);
        return schedule;
    }
}
