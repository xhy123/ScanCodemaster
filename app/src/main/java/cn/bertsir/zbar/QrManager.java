package cn.bertsir.zbar;

import android.app.Activity;
import android.content.Intent;

import com.hhhh.scancode_master.ScanAddCodeActivity;


/**
 * Created by Bert on 2017/9/22.
 */

public class QrManager {

    private static QrManager instance;
    private QrConfig options;

    public OnScanResultCallback resultCallback;

    public synchronized static QrManager getInstance() {
        if(instance == null)
            instance = new QrManager();
        return instance;
    }

    public OnScanResultCallback getResultCallback() {
        return resultCallback;
    }


    public QrManager init(QrConfig options) {
        this.options = options;
        return this;
    }

    public void startScan(Activity activity, OnScanResultCallback resultCall,LeadTextCallBack mClickCallBack){

        if (options == null) {
            options = new QrConfig.Builder().create();
        }
        Intent intent = new Intent(activity, QRActivity.class);
        intent.putExtra(QrConfig.EXTRA_THIS_CONFIG, options);
        activity.startActivity(intent);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
        mCallBack = mClickCallBack;
    }

 /*   public void startScanCodeActivity(Activity activity, OnScanResultCallback resultCall){

        if (options == null) {
            options = new QrConfig.Builder().create();
        }
        Intent intent = new Intent(activity, CodeScanActivity.class);
        intent.putExtra(QrConfig.EXTRA_THIS_CONFIG, options);
        activity.startActivity(intent);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
       // mCallBack = mClickCallBack;
    }*/

    public void startScanAddCodeActivity(Activity activity, OnScanResultCallback resultCall,LeadTextCallBack mClickCallBack){

        if (options == null) {
            options = new QrConfig.Builder().create();
        }
        Intent intent = new Intent(activity, ScanAddCodeActivity.class);
        intent.putExtra(QrConfig.EXTRA_THIS_CONFIG, options);
        activity.startActivity(intent);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
        mCallBack = mClickCallBack;
    }


    public interface OnScanResultCallback {
        /**
         * 处理成功
         * 多选
         *
         * @param result
         */
        void onScanSuccess(String result);

    }


    public interface LeadTextCallBack{
       void clickOk();
    }

    public LeadTextCallBack mCallBack;


    public LeadTextCallBack getCallBack() {
        return  mCallBack;
    }
}
