package com.bangfruits.framework.test;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bangfruits.framework.R;
import com.bangfruits.framework.common.model.ResultJson;
import com.bangfruits.framework.manager.ActivityLifecycleManager;
import com.bangfruits.framework.common.base.BaseActivity;
import com.bangfruits.framework.common.constant.Constants;
import com.bangfruits.framework.common.http.callback.files.FileProgressDialogCallBack;
import com.bangfruits.framework.common.http.callback.files2.FileProgressDialogCallBack2;
import com.bangfruits.framework.common.http.callback.json.JsonDialogCallback;
import com.bangfruits.framework.common.utils.AppUtils;
import com.bangfruits.framework.common.utils.ToastUtils;
import com.bangfruits.framework.modules.welcome.model.AppLoginPo;
import com.bangfruits.framework.modules.mine.model.UpdateAppPo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TestActivity extends BaseActivity {

    @BindView(R.id.button)
    QMUIRoundButton button;
    @BindView(R.id.button2)
    QMUIRoundButton button2;

    private String token = "";
    private Context context;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        context = this;
    }

    @OnClick({R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                //ToastUtils.info("提交成功！");
                //SmartToast.info("提交成功！");
                login();
                break;
            case R.id.button2:
                //SmartToast.warning("保存失败！");
                //ToastUtils.warning("保存失败！");
                // token未过期，跳转到主界面
                /*Intent intent = new Intent(TestActivity.this, BottomNavigation2Activity.class);
                startActivity(intent);*/
                //checkToken();
                checkPermission();
                break;
        }
    }


    private void checkToken() {
        String url = Constants.SERVICE_IP + "/checkToken.do";

        OkGo.<ResultJson<Void>>post(url)
                .tag(this)
                .params("token", token)
                .execute(new JsonDialogCallback<ResultJson<Void>>(this) {
                    @Override
                    public void onSuccess(Response<ResultJson<Void>> response) {
                        ResultJson<Void> baseCodeJson = response.body();
                        ToastUtils.info(baseCodeJson.getMsg());
                    }
                });
    }


    private void login() {
        String userName = "admin";
        String passWord = AppUtils.encryptSha256("1");
        String url = Constants.SERVICE_IP + "/auth/login";
        OkGo.<ResultJson<AppLoginPo>>get(url)
                .tag(this)
                .params("username",userName)
                .params("password",passWord)
                .headers("content-type","application/x-www-form-urlencoded")
                .execute(new JsonDialogCallback<ResultJson<AppLoginPo>>(this) {
                    @Override
                    public void onSuccess(Response<ResultJson<AppLoginPo>> response) {
                        ResultJson<AppLoginPo> loginPo = response.body();
                        token = loginPo.getResult().getToken();
                        //Log.d(token);
                    }

                    @Override
                    public void onError(Response<ResultJson<AppLoginPo>> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 检查权限是否授权
     */
    @AfterPermissionGranted(Constants.ALL_PERMISSION)
    @Override
    public void checkPermission() {

        //String[] params = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        String[] params = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //判断是否获取权限
        if (EasyPermissions.hasPermissions(this, params)) {
            // 全部权限申请成功后
            doDownload();
        } else {
            //未获取权限或拒绝权限时
            EasyPermissions.requestPermissions(this,
                    "xxx需要用到以下权限：\n\n1. 录制音频权限\n\n2. 录制视频权限\n\n3. 文件读取存储权限",
                    Constants.ALL_PERMISSION, params);
        }
    }

    private String downloadUrl;
    private long totalSize;

    private void getAppId() {
        String url = Constants.SERVICE_IP+"/iandroid/appVersionAction!getNewVersion.do";
        OkGo.<UpdateAppPo>post(url)
                .tag(this)
                .execute(new JsonDialogCallback<UpdateAppPo>(this) {
                    @Override
                    public void onSuccess(Response<UpdateAppPo> response) {
                        UpdateAppPo updateAppPo = response.body();
                        String appId = updateAppPo.getApkId();
                        //获取apk下载地址
                        downloadUrl = Constants.SERVICE_IP+ "/file-download?fileId=" + appId;
                        totalSize = Long.parseLong(updateAppPo.getFileSize());
                        //toDownload();
                        doDownload();
                    }
                });
    }

    private void toDownload() {
        OkGo.<File>get(downloadUrl)
                .tag(this)
                .execute(new FileProgressDialogCallBack2(this,totalSize) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        ToastUtils.success("下载完成！");
                    }
                });
    }

    private void doDownload() {
        downloadUrl = Constants.SERVICE_IP+ "/file-download?fileId=1509";
        OkGo.<File>get(downloadUrl)
                .tag(this)
                .execute(new FileProgressDialogCallBack(this) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        ToastUtils.success("下载完成！");
                    }
                });
    }

    /**
     * 重写返回键，实现双击退出程序效果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.normal("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                OkGo.getInstance().cancelAll();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                ActivityLifecycleManager.get().appExit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
