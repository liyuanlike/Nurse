package com.jerry.nurse.fragment;

import android.os.Bundle;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/7/15.
 */

public class OfficeFragment extends BaseFragment {

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

    }
}
