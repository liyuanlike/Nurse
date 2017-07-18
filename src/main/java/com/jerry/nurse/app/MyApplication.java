package com.jerry.nurse.app;

import android.app.ActivityManager;
import android.content.pm.PackageManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.jerry.nurse.util.L;
import com.umeng.analytics.MobclickAgent;

import org.litepal.LitePalApplication;

import java.util.Iterator;
import java.util.List;


/**
 * Created by Jerry on 2017/7/17.
 */

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化友盟
        initMobclickAgent();
        // 初始化环信
        initEaseMob();
    }

    /**
     * 初始化友盟
     */
    private void initMobclickAgent() {
        // 禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
    }

    /**
     * 初始化环信
     */
    private void initEaseMob() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

        // 初始化前要验证
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            L.e("enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

    /**
     * 获取app名称
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}