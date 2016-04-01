package s.android.ffmpeg.handler;

import android.content.Context;

import org.apache.http.Header;

import s.android.ffmpeg.http.AsyncHttpResponseHandler;

/**
 * @类名称 ：ResponseHandler.java
 * @类描述 ：
 * @修改记录：
 * @日期 ：2014-4-18 上午11:45:47 @作者：sein
 * @描述 ：
 * @日期 ：2014-4-18 上午11:45:47 @修改人：
 *
 **/

public class ResponseHandler extends AsyncHttpResponseHandler {
    private Context context;
    private LoadDataHandler mHandler;

    /**
     * @构造函数：ResponseHandler
     * @函数功能:
     * @参数说明：
     * @param context
     * @param mHandler
     */
    public ResponseHandler(Context context, LoadDataHandler mHandler) {
        super();
        this.context = context;
        this.mHandler = mHandler;
    }


    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mHandler.onStart();
    }

    public void onFailure(Throwable error, String content) {
        // TODO Auto-generated method stub
        super.onFailure(error, content);
        mHandler.onFailure("", "网络连接超时");
    }

    public void onFinish() {
        // TODO Auto-generated method stub
        super.onFinish();
        mHandler.onFinish();
    }

    public void onSuccess(int statusCode, Header[] headers, String content) {
        super.onSuccess(statusCode, headers, content);
        try {
            switch (statusCode) {
                case 200:
                    mHandler.onSuccess(content);
                    break;
                case 401:
                    onFailure("401", "没有登录");
                    break;
                case 403:
                    onFailure("404", "没有权限");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFailure(String error, String errorMessage) {
        if (errorMessage != null) {
            mHandler.onFailure(error, errorMessage);
        }
    }

}
