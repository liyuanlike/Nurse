package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by Jerry on 2017/7/16.
 */

public abstract class BaseFragment extends Fragment {

    private View mRootView;

    /**
     * 获取页面布局文件
     *
     * @return
     */
    public abstract int getLayoutResId();

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    public abstract void init(Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, mRootView);
        init(savedInstanceState);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 友盟统计页面
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
