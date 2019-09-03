package com.bangfruits.framework.common.http.callback.json;

import android.app.Activity;
import android.content.Intent;

import com.bangfruits.framework.common.model.ResultJson;
import com.bangfruits.framework.common.model.SimpleCodeJson;
import com.bangfruits.framework.manager.ActivityLifecycleManager;
import com.bangfruits.framework.common.constant.SPConstants;
import com.bangfruits.framework.common.http.exception.TokenException;
import com.bangfruits.framework.common.utils.PreferenceUtils;
import com.bangfruits.framework.common.utils.ToastUtils;
import com.bangfruits.framework.modules.welcome.activity.WelcomeActivity;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;

/**
 * Created by LY on 2019/4/2.
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public JsonCallback() {
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,实际使用根据需要修改
     */
    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {

        // 不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        //这里得到ResultJson<T>类型
        Type type = params[0];

        if(!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        //这里得到ResultJson
        Type rawType = ((ParameterizedType) type).getRawType();
        //这里得到TModel
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];

        ResponseBody body = response.body();
        if(body == null) return null;
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if(rawType != ResultJson.class){
            T data = gson.fromJson(jsonReader,type);
            response.close();
            return data;
        }else {
            if(typeArgument == Void.class){
                //无数据类型 new DialogCallback<ResultJson<Void>>() 这种形式传递的泛型
                SimpleCodeJson simpleCodeJson = gson.fromJson(jsonReader,SimpleCodeJson.class);
                response.close();
                return (T) simpleCodeJson.toResultJson();
            }else {
                ResultJson resultJson = gson.fromJson(jsonReader,type);
                response.close();
                int code = resultJson.getCode();

                if(code == 0){
                    return (T) resultJson;
                }else if(code == 104) {
                    throw new IllegalStateException("用户授权信息无效");
                }else if(code == 105){
                    throw new IllegalStateException("用户授权信息已过期");
                }else if(code == 106){
                    throw new IllegalStateException("用户账户被禁用");
                }else if(code == 300){
                    throw new IllegalStateException("其他异常信息");
                }else {
                    throw new IllegalStateException("错误代码："+code+"，错误信息："+resultJson.getMsg());
                }
            }
        }
    }
    @Override
    public void onError(Response<T> response) {
        super.onError(response);

        Throwable exception = response.getException();
        //如果OkGo配置中Logger日志已打开，super.onError(response);就已经将printStackTrace打印出来了
        /*if (exception != null) {
            exception.printStackTrace();
        }*/
        if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
            ToastUtils.error("网络连接失败，请连接网络！");
        } else if (exception instanceof SocketTimeoutException) {
            ToastUtils.error("网络请求超时，请稍后重试！");
        } else if (exception instanceof HttpException) {
            ToastUtils.error("暂时无法连接服务器，请稍后重试！");
        } else if (exception instanceof StorageException) {
            ToastUtils.error("SD卡不存在或者没有获取SD卡访问权限！");
        } else if (exception instanceof TokenException) {
            // 删除token，跳转到登录页面
            skipLoginActivityAndFinish(exception);
        } else if (exception instanceof IllegalStateException) {
            ToastUtils.error(exception.getMessage());
            skipLoginActivityAndFinish(exception);
        }
    }

    /**
     * 删除token，跳转到登录页面
     * */
    private void skipLoginActivityAndFinish(Throwable exception) {
        //删除本地过期的token
        PreferenceUtils.remove(SPConstants.TOKEN);
        PreferenceUtils.remove(SPConstants.USER_ID);
        Intent intent = new Intent("com.bonait.bnframework.modules.welcome.activity.LoginActivity.ACTION_START");
        // 获取当前Activity（栈中最后一个压入的）
        Activity activity = ActivityLifecycleManager.get().currentActivity();

        // 如果不是WelcomeActivity，显示Toast告知token已过期
        if (activity.getClass() != WelcomeActivity.class) {
            ToastUtils.info(exception.getMessage());
        }

        // 结束所有Activity
        ActivityLifecycleManager.get().finishAllActivity();
        // 跳转到登录页面
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
