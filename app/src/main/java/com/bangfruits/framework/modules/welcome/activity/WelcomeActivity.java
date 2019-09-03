package com.bangfruits.framework.modules.welcome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bangfruits.framework.R;
import com.bangfruits.framework.common.model.ResultJson;
import com.bangfruits.framework.common.utils.ToastUtils;
import com.bangfruits.framework.manager.ActivityLifecycleManager;
import com.bangfruits.framework.common.constant.Constants;
import com.bangfruits.framework.common.constant.SPConstants;
import com.bangfruits.framework.common.http.callback.json.JsonCallback;
import com.bangfruits.framework.common.utils.PreferenceUtils;
import com.bangfruits.framework.modules.home.activity.BottomNavigation2Activity;
import com.bangfruits.framework.test.TestActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;
import okhttp3.OkHttpClient;

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    /*
    * 启动模式：
    * 1：启动界面时间与App加载时间相等
    * 2：设置启动界面2秒后跳转
    * */
    private final static int SELECT_MODE = 1;
    private OkHttpClient.Builder builder;

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private BGABanner mBackgroundBanner;
    private BGABanner mForegroundBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListener();
        processLogic();
    }

    private void initViews() {
        setContentView(R.layout.activity_welcome);
        mBackgroundBanner = findViewById(R.id.banner_guide_background);
        mForegroundBanner = findViewById(R.id.banner_guide_foreground);
    }

    private void setListener(){
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                initWelcome();
            }
        });

        /**
         * 设置广告图片点击跳转
         */
        mForegroundBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                Toast.makeText(banner.getContext(), "点击了第" + (position + 1) + "页", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void processLogic() {
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        mBackgroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.ic_guide_b1,
                R.drawable.ic_guide_b2,
                R.drawable.ic_guide_b3);

        mForegroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.ic_guide_1,
                R.drawable.ic_guide_2,
                R.drawable.ic_guide_3);

//        String picUrl1 = "https://b-ssl.duitang.com/uploads/item/201606/15/20160615081604_jeXSz.thumb.700_0.jpeg";
//        String picUrl2 = "https://a-ssl.duitang.com/uploads/item/201601/08/20160108214948_2Vh4f.jpeg";
//        String picUrl3 = "https://b-ssl.duitang.com/uploads/item/201509/03/20150903204626_Yi8hC.jpeg";
//        mForegroundBanner.setData(Arrays.asList(picUrl1,picUrl2,picUrl3), Arrays.asList("提示文字1", "提示文字2", "提示文字3"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }

    private void initWelcome() {
        switch (WelcomeActivity.SELECT_MODE) {
            case 1:
                fastWelcome();
                break;
            case 2:
                slowWelcome();
                break;
        }
    }

    /*方法1：启动界面时间与App加载时间相等*/
    private void fastWelcome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //判断token
                        doPost();
                    }
                });
            }
        }).start();
    }

    /*方法2：设置启动界面2秒后跳转*/
    private void slowWelcome() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断token
                doPost();
            }
        }, 5000);
    }


    /**
     * 请求服务器判断token是否过期
     */
    private void doPost() {
        //判断是否用户修改过密码
        boolean isChangePwd = PreferenceUtils.getBoolean(SPConstants.CHANGE_PWD, false);
        if (isChangePwd) {
            //若修改过密码，则使token失效
            PreferenceUtils.setString(SPConstants.TOKEN, "0123456789");
        }
        String token = PreferenceUtils.getString(SPConstants.TOKEN, "0123456789");
        // 请求后台判断token
        String url = Constants.SERVICE_IP + "/auth/checkToken";

        // 修改请求超时时间为6s，与全局超时时间分开
        builder = new OkHttpClient.Builder();
        builder.readTimeout(2000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(2000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(2000, TimeUnit.MILLISECONDS);

        OkGo.<ResultJson<Void>>post(url)
                .tag(this)
                .client(builder.build())
                .params("token", token)
                .execute(new JsonCallback<ResultJson<Void>>() {
                    @Override
                    public void onSuccess(Response<ResultJson<Void>> response) {
                        ToastUtils.normal("调用成功");
                        // 判断是否开启跳转到测试界面
                        if (Constants.SKIP_TO_TEST_ACTIVITY) {
                            skipToTestActivity();
                        } else {
                            skipToMainActivity();
                        }
                    }

                    @Override
                    public void onError(Response<ResultJson<Void>> response) {
                        super.onError(response);
                        skipToLoginActivity();
                    }
                });
    }


    /**
     * 跳转到测试页面
     * */
    private void skipToTestActivity() {
        // token未过期，跳转到主界面
        Intent intent = new Intent(WelcomeActivity.this, TestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }


    /**
     * 跳转到主界面
     * */
    private void skipToMainActivity() {
        // token未过期，跳转到主界面
        Intent intent = new Intent(WelcomeActivity.this, BottomNavigation2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 跳转到登录页面
     * */
    private void skipToLoginActivity() {
        // 跳转到登录页面
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
    }

    /**
     * 屏蔽物理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        OkGo.cancelAll(builder.build());

        if (handler != null) {
            //If token is null, all callbacks and messages will be removed.
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

}
