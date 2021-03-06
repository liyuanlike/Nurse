package com.jerry.nurse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.activity.AnnouncementActivity;
import com.jerry.nurse.activity.AnnouncementDetailActivity;
import com.jerry.nurse.activity.CreditCheckActivity;
import com.jerry.nurse.activity.HtmlActivity;
import com.jerry.nurse.adapter.BannerAdapter;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Announcement;
import com.jerry.nurse.model.AnnouncementsResult;
import com.jerry.nurse.model.Banner;
import com.jerry.nurse.model.BannersResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.CircleIndicator;
import com.jerry.nurse.view.ViewPagerScroller;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/7/15.
 */

public class OfficeFragment extends BaseFragment {

    public static final int DEFAULT_PAGE = 1;

    private static final String REPORT_EVENT_URL = ServiceConstant.EVENT_REPORT_IP + "?Ruid=";

    // Banner停留时间
    private static final int BANNER_DELAYED = 5000;
    // Banner滚动持续时间
    private static final int SCROLLER_DURATION = 1000;

    @Bind(R.id.vp_banner)
    ViewPager mViewPager;

    @Bind(R.id.ci_banner)
    CircleIndicator mIndicator;

    @Bind(R.id.tv_announcement)
    TextView mAnnouncementTextView;

    @Bind(R.id.tv_announcement_more)
    TextView mMoreTextView;

    private List<View> mBannerViews;
    private List<Banner> mBanners;
    private List<Announcement> mAnnouncements;

    private Runnable mBannerRunnable;

    private LoginInfo mInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mViewPager != null) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem != mBannerViews.size() - 1) {
                    mViewPager.setCurrentItem(currentItem + 1);
                } else {
                    mViewPager.setCurrentItem(0);
                }
                postDelayed(mBannerRunnable, BANNER_DELAYED);
            }
        }
    };

    /**
     * 实例化方法
     *
     * @return
     */
    public static OfficeFragment newInstance() {
        OfficeFragment fragment = new OfficeFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_office;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mInfo = DataSupport.findFirst(LoginInfo.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);

        // 获取最新Banner
        getBanner(loginInfo.getHospitalId(), loginInfo.getDepartmentId());
        // 获取最新公告
        getAnnouncement(DEFAULT_PAGE, loginInfo.getHospitalId(), loginInfo.getDepartmentId());
    }

    /**
     * 获取广告
     *
     * @param hospitalId
     * @param departmentId
     */
    private void getBanner(String hospitalId, String departmentId) {
        // 是否认证通过并绑定好了医院、科室
        if (!checkPermission()) {
            hospitalId = "";
            departmentId = "";
        } else {
            if (hospitalId == null) {
                hospitalId = "";
            }
            if (departmentId == null) {
                departmentId = "";
            }
        }

        OkHttpUtils.get().url(ServiceConstant.GET_BANNER)
                .addParams("HospitalId", hospitalId)
                .addParams("DepartmentId", departmentId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        //从数据库中获取数据
                        mBanners = DataSupport.findAll(Banner.class);
                        if (mBanners != null) {
                            updateBanners();
                        }
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        BannersResult result = new Gson().fromJson(response, BannersResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mBanners = result.getBody();
                            if (mBanners == null) {
                                mBanners = new ArrayList<>();
                            }
                            updateBanners();
                            if (mBanners.size() > 0) {
                                //添加新数据到数据库
                                DataSupport.deleteAll(Banner.class);
                                DataSupport.saveAll(result.getBody());
                            }
                        } else {
                            L.i(result.getMsg());
                        }
                    }
                });
    }


    /**
     * 获取公告咨询
     *
     * @param page
     * @param hospitalId
     * @param officeId
     */
    private void getAnnouncement(int page, String hospitalId, String officeId) {
        if (!checkPermission()) {
            hospitalId = "";
            officeId = "";
        } else {
            if (hospitalId == null) {
                hospitalId = "";
            }
            if (officeId == null) {
                officeId = "";
            }
        }

        OkHttpUtils.get().url(ServiceConstant.GET_ANNOUNCEMENT + "1")
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        //从数据库中获取数据
                        mAnnouncements = DataSupport.findAll(Announcement.class);
                        if (mAnnouncements != null) {
                            updateAnnouncements();
                        }
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        AnnouncementsResult result = new Gson().fromJson(response, AnnouncementsResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mAnnouncements = result.getBody();
                            if (mAnnouncements == null) {
                                mAnnouncements = new ArrayList<>();
                            }
                            if (mAnnouncements.size() > 0) {
                                //添加新数据到数据库

                                DataSupport.deleteAll(Announcement.class);
                                DataSupport.saveAll(result.getBody());
                            } else {
                                mMoreTextView.setVisibility(View.INVISIBLE);
                            }
                            updateAnnouncements();
                        } else {
                            T.showShort(getActivity(), result.getMsg());
                        }
                    }
                });
    }

    /**
     * 判断权限进行消息展示
     */
    public static boolean checkPermission() {
        // 两证同时通过验证才可以进行院务内的消息展示
        LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
        // 如果是院务账号直接通过权限验证
        if (!TextUtils.isEmpty(loginInfo.getReguserId())) {
            return true;
        }
        if (!TextUtils.isEmpty(loginInfo.getXFId())) {
            return true;
        }
        if (loginInfo.getPStatus() == AUDIT_SUCCESS && loginInfo.getQStatus() == AUDIT_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新Banner显示
     */
    private void updateBanners() {
        try {
            mBannerViews = new ArrayList<>();
            for (int i = 0; i < mBanners.size(); i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(this).load(ServiceConstant.BANNER_ADDRESS + mBanners.get(i).getBannerUrl()).into(imageView);
                final int index = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = HtmlActivity.getIntent(getActivity(), mBanners.get(index).getBannerToUrl()
                                , mBanners.get(index).getTitle());
                        startActivity(intent);
                    }
                });
                if (mBanners.get(i).getIsFlag() == 1) {
                    mBannerViews.add(imageView);
                }
            }

            BannerAdapter bannerAdapter = new BannerAdapter(mBannerViews);
            mViewPager.setAdapter(bannerAdapter);

            // 设置Banner导航点
            mIndicator.setViewPager(mViewPager);

            // 设置Banner滚动速度
            ViewPagerScroller scroller = new ViewPagerScroller(getActivity());
            scroller.setScrollDuration(SCROLLER_DURATION);
            scroller.initViewPagerScroll(mViewPager);

            // 每5秒切换一条Banner
            mBannerRunnable = new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            };
            mHandler.postDelayed(mBannerRunnable, BANNER_DELAYED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新公告显示
     */
    private void updateAnnouncements() {
        try {
            Announcement announcement = DataSupport.findFirst(Announcement.class);
            if (announcement != null) {
                mAnnouncementTextView.setText(announcement.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tv_announcement)
    void onAnnouncement(View view) {
        Announcement announcement = DataSupport.findFirst(Announcement.class);
        if (announcement != null) {
            Intent intent = AnnouncementDetailActivity.getIntent(getActivity(), announcement);
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_announcement_more)
    void onAnnouncementMore(View view) {
        Intent intent = AnnouncementActivity.getIntent(getActivity());
        startActivity(intent);
    }

    /**
     * 护理不良事件
     *
     * @param view
     */
    @OnClick(R.id.ll_event_report)
    void onEventReport(View view) {
        if (TextUtils.isEmpty(mInfo.getReguserId())) {
            showBindTipDialog("不良事件");
            return;
        }
        Intent intent = HtmlActivity.getIntent(getActivity(),
                REPORT_EVENT_URL + mInfo.getReguserId(), null);
//        Intent intent = EventReportActivity.getIntent(getActivity());
        startActivity(intent);
    }

    /**
     * 显示提示框
     */
    private void showBindTipDialog(String msg) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tips)
                .setMessage("请先绑定" + msg + "账号，即可开启此功能！")
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @OnClick(R.id.ll_nurse_class)
    void onNurseClass(View view) {
//        Intent intent = HtmlActivity.getIntent(getActivity(), NURSE_CLASS_URL, null);
//        startActivity(intent);
    }

    /**
     * 学分查看
     *
     * @param view
     */
    @OnClick(R.id.ll_credit_check)
    void onCreditCheck(View view) {
        if (TextUtils.isEmpty(mInfo.getXFId())) {
            showBindTipDialog("学分");
            return;
        }
        Intent intent = CreditCheckActivity.getIntent(getActivity());
        startActivity(intent);
    }

    /**
     * 排班查看
     *
     * @param view
     */
    @OnClick(R.id.ll_schedule_check)
    void onScheduleCheck(View view) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tips)
                .setMessage("排班功能正在研发中！")
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mBannerRunnable);
    }
}
