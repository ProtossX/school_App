package com.chxip.campusinfo;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chxip.campusinfo.activity.fragment.LostFoundFragment;
import com.chxip.campusinfo.activity.fragment.MessageWallFragment;
import com.chxip.campusinfo.activity.fragment.MyFragment;
import com.chxip.campusinfo.activity.fragment.ScheduleFragment;
import com.chxip.campusinfo.util.ToastUtil;
import com.chxip.campusinfo.view.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {


    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tl_main_menu)
    TabLayout tl_main_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        tl_main_menu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            TabLayout.Tab oldTab;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.mipmap.main_workbench_selected);
                        break;
                    case 1:
                        tab.setIcon(R.mipmap.order_record_selected);
                        break;
                    case 2:
                        tab.setIcon(R.mipmap.check_record_selected);
                        break;
                    case 3:
                        tab.setIcon(R.mipmap.personal_center_selected);
                        break;
                }
                if (oldTab != null) {
                    switch (oldTab.getPosition()) {
                        case 0:
                            oldTab.setIcon(R.mipmap.main_workbench);
                            break;
                        case 1:
                            oldTab.setIcon(R.mipmap.order_record);
                            break;
                        case 2:
                            oldTab.setIcon(R.mipmap.check_record);
                            break;
                        case 3:
                            oldTab.setIcon(R.mipmap.personal_center);
                            break;
                    }
                }
                changeIconImgBottomMargin(tl_main_menu);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                oldTab = tab;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        List<Fragment> fragmentList = new ArrayList<>();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        LostFoundFragment lostFoundFragment = new LostFoundFragment();
        MessageWallFragment messageWallFragment = new MessageWallFragment();
        MyFragment myFragment = new MyFragment();

        fragmentList.add(scheduleFragment);
        fragmentList.add(lostFoundFragment);
        fragmentList.add(messageWallFragment);
        fragmentList.add(myFragment);

        viewPager.setAdapter(new MainMenuFragmentAdapter(getSupportFragmentManager(), fragmentList, this));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setSlide(false);
        changeIconImgBottomMargin(tl_main_menu);
    }

    private void changeIconImgBottomMargin(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeIconImgBottomMargin((ViewGroup) child);
            } else if (child instanceof ImageView) {
                ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                lp.bottomMargin = 4;
                child.requestLayout();
            }
        }
    }

    class MainMenuFragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> flist;
        Context mContext;


        public MainMenuFragmentAdapter(FragmentManager fm, List<Fragment> list, Context context) {
            super(fm);
            this.flist = list;
            this.mContext = context;
        }


        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub

            Fragment fragment = flist.get(arg0);
            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return flist.size();
        }

    }


    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    ToastUtil.show(this, getResources().getString(R.string.press_again_exit));
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:

                    finish();
                    System.exit(0);
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
