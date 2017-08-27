package com.jerry.nurse.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.jerry.nurse.R;

import butterknife.Bind;

public class WelcomeActivity extends BaseActivity {

    private static final int ANIMATION_DURATIONG = 1800;

    @Bind(R.id.iv_bg)
    ImageView mBgImageView;

    @Bind(R.id.iv_logo_wing)
    ImageView mWingImageView;

    @Bind(R.id.iv_logo_text)
    ImageView mTextImageView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int startAlpha = 0;
        int endAlpha = 1;

        // 创建两个图标渐显的动画
        ObjectAnimator wingAnimator = ObjectAnimator
                .ofFloat(mWingImageView, "alpha", startAlpha, endAlpha)
                .setDuration(ANIMATION_DURATIONG);
        wingAnimator.setInterpolator(new LinearInterpolator());
        mWingImageView.setVisibility(View.VISIBLE);

        ObjectAnimator textAnimator = ObjectAnimator
                .ofFloat(mTextImageView, "alpha", startAlpha, endAlpha)
                .setDuration(ANIMATION_DURATIONG);
        textAnimator.setInterpolator(new LinearInterpolator());
        mTextImageView.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set.play(wingAnimator).with(textAnimator);
        set.start();

        // 动画播放完毕后跳转到登录页面
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            Intent intent = LoginActivity.getIntent(WelcomeActivity.this);
                            startActivity(intent);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        });
    }
}