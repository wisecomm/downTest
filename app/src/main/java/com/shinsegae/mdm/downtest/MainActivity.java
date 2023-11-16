package com.shinsegae.mdm.downtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shinsegae.mdm.downtest.util.UDialog;
import com.shinsegae.mdm.downtest.util.XappUpgrade;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static Context context = null;
    public static MainActivity m_MainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Log.d("MainActivity", "onCreate: hasPermissions");
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "이 앱을 실행하기 위해서는 권한이 필요합니다.",
                    100, perms);
            Log.d("MainActivity", "onCreate: not hasPermissions");
        }

        m_MainActivity = this;
        UDialog.m_ActActivity = this;
        final Button button = findViewById(R.id.btnDownload);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UDialog.show(UDialog.m_ActActivity, "앱 업데이트", "최신버전의 앱이 있습니다.\n설치하셔야합니다.", "확인", "취소",
                        (dialog, id) -> {
                            String strUrl = "https://wisecomm.github.io/iosdowntest/app-release.apk";
                            XappUpgrade.downloading(context, strUrl);
/*
                            new Thread(() -> {
                                boolean bReturn = DownLoad.downLoadInstall(context, strUrl, "app-update.apk");
                                if(bReturn) {
//                                    finish();
                                } else {    // 실패
                                    Toast.makeText(context, "다운로드 실패" + DownLoad.strErrorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }).start();
 */
                        },
                        (dialog, id) -> {
                            finish();
                        }
                );
            }
        });

    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, perms, grantResults, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(XappUpgrade.downloadCompleteReceiver, completeFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(XappUpgrade.downloadCompleteReceiver);
    }


    public void installApk(File file, int status) {
        if(status != DownloadManager.STATUS_SUCCESSFUL) {
            Toast.makeText(context, "다운로드 실패 했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

}
