package com.hhhh.scancode_master;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.hhhh.scancode_master.util.PermissionsUtils;

import java.util.ArrayList;

import cn.bertsir.zbar.CameraPreview;
import cn.bertsir.zbar.QRUtils;
import cn.bertsir.zbar.Qr.Symbol;
import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.ScanCallback;
import cn.bertsir.zbar.view.ScanView;


/**
 * Created by Administrator on 2018/3/26 0026.
 */

public class ScanAddCodeActivity extends AppCompatActivity implements PermissionsUtils.OnPermissionListener, View.OnClickListener {

    private CameraPreview code_scan_cp;
    private ScanView codeScanSv;
    private SoundPool soundPool;
 //   private QrConfig options;
    private RelativeLayout mBackLinerLayout;
    private TextView codeWhere;
    private TextView photoLinerLayout;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_CODE_SCAN_GALLERY = 112;
    public static final String RESULT_TYPE = "result_type";
    public static final String RESULT_STRING = "result_string";
    public static final int RESULT_SUCCESS = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Symbol.scanType = QrConfig.TYPE_QRCODE;
        Symbol.scanFormat = QrConfig.BARCODE_EAN13;
        Symbol.is_only_scan_center = true;
        setContentView(R.layout.activity_add_scan_code);
        // 需要请求的权限
        String permissions[] = {Manifest.permission.CAMERA, Manifest.permission.WAKE_LOCK};
        // 获取权限
        PermissionsUtils.getInstance().requestPermission(this, permissions,
                0, this);

        mBackLinerLayout = (RelativeLayout) findViewById(R.id.layout_back);
        codeWhere = (TextView) findViewById(R.id.code_where);
        photoLinerLayout = (TextView)findViewById(R.id.tv_add_photo);
        code_scan_cp = (CameraPreview)findViewById(R.id.code_add_scan_cp);
        codeScanSv = (ScanView)findViewById(R.id.code_add_scan_sv);
        codeWhere.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(this, true ? R.raw.test : R.raw.qrcode, 1);
        codeScanSv.setType(QrConfig.TYPE_QRCODE);
        codeScanSv.startScan();
        mBackLinerLayout.setOnClickListener(this);
        codeWhere.setOnClickListener(this);
        photoLinerLayout.setOnClickListener(this);

    }


    private ScanCallback resultCallback = new ScanCallback() {
        @Override
        public void onScanResult(String result) {
            if (true) {
                soundPool.play(1, 1, 1, 0, 0, 1);
            }
            if (code_scan_cp != null) {
                code_scan_cp.setFlash(false);
            }

//            QrManager.getInstance().getResultCallback().onScanSuccess(result);
//            finish();

            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(RESULT_TYPE, RESULT_SUCCESS);
            bundle.putString(RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanAddCodeActivity.this.setResult(RESULT_OK, resultIntent);
            ScanAddCodeActivity.this.finish();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:
                finish();
                break;
            case R.id.code_where:

                break;
            case R.id.tv_add_photo:
                Intent innerIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(innerIntent, REQUEST_CODE_SCAN_GALLERY);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (code_scan_cp != null) {
            code_scan_cp.setScanCallback(resultCallback);
            code_scan_cp.start();
        }
        codeScanSv.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (code_scan_cp != null) {
            code_scan_cp.setFlash(false);
            code_scan_cp.stop();
        }
        soundPool.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (code_scan_cp != null) {
            code_scan_cp.stop();
        }
        codeScanSv.onPause();
    }

    /**
     * 从相册选择
     */
    private void fromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private TextView textDialog;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_GALLERY) {
            if (data != null) {
                final Uri uri = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(uri, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                final String imagePath = c.getString(columnIndex);
                c.close();

                textDialog = showProgressDialog();
                textDialog.setText("请稍后...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap Qrbitmap = BitmapFactory.decodeFile(imagePath);
                          //  Bitmap Qrbitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            final String qrcontent = QRUtils.getInstance().decodeQRcode(Qrbitmap);
                            Qrbitmap.recycle();
                            Qrbitmap = null;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!TextUtils.isEmpty(qrcontent)){

                                        Toast.makeText(ScanAddCodeActivity.this, "解析结果:" + qrcontent, Toast.LENGTH_LONG).show();
                                        Log.e("","扫码   解析结果:      " + qrcontent);
                                        closeProgressDialog();

                                        //执行扫码成功后的一系列操作


                                        finish();

                                    }else {
                                        Toast.makeText(getApplicationContext(), "识别失败！", Toast.LENGTH_SHORT).show();
                                        closeProgressDialog();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage(), e);
                        }
                    }
                }).start();

            }
        }
    }

    @Override
    public void onRequestPermissionSuccess(int requestCode) {

    }

    @Override
    public void onRequestPermissionFailure(int requestCode) {

    }

    private AlertDialog progressDialog;

    public TextView showProgressDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setCancelable(false);
        View view = View.inflate(this, R.layout.dialog_loading, null);
        builder.setView(view);
        ProgressBar pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pb_loading.setIndeterminateTintList(ContextCompat.getColorStateList(this, R.color.dialog_pro_color));
        }
        progressDialog = builder.create();
        progressDialog.show();

        return tv_hint;
    }

    public void closeProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
