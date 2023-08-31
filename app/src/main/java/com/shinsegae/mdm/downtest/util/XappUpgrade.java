package com.shinsegae.mdm.downtest.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.shinsegae.mdm.downtest.MainActivity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class XappUpgrade {
    private XappUpgrade() {
    }

    private static DownloadManager mDownloadManager;
    private static Long mDownloadQueueId;
    private static File outputFile;

    private static String outputDirectory = Environment.DIRECTORY_DOWNLOADS + File.separator + "apk_download";


    public static void downloading(Context mContext, String strUrl) {
        String title = UUID.randomUUID().toString() + ".apk";

        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        // 다운로드 디렉토리 삭제
        File directoryDel = Environment.getExternalStoragePublicDirectory(outputDirectory);
        deleteDirectory(directoryDel);

        File directory = Environment.getExternalStoragePublicDirectory(outputDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }
        outputFile = new File(directory, title);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        Uri downloadUri = Uri.parse(strUrl.trim());

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        List<String> pathSegmentList = downloadUri.getPathSegments();
        request.setTitle("APK 버전업");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(outputFile));

        request.setAllowedOverMetered(true);

        mDownloadQueueId = mDownloadManager.enqueue(request);
    }


    public static BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            int mProgress = 0;

            if(mDownloadQueueId == reference){
                DownloadManager.Query query = new DownloadManager.Query();  // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference);
                Cursor cursor = mDownloadManager.query(query);

                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int status = cursor.getInt(columnIndex);
//                int reason = cursor.getInt(columnReason);
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        MainActivity.m_MainActivity.installApk(outputFile, status);
                        break;
                    default:
                        MainActivity.m_MainActivity.installApk(outputFile, status);
                        break;

/*
                    case DownloadManager.STATUS_PAUSED :
                        Toast.makeText(context, "다운로드가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_FAILED :
                        Toast.makeText(context, "다운로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
 */
                }
                cursor.close();
            }
        }
    };

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        try {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
            return directoryToBeDeleted.delete();
        } catch (Exception e) {
            return false;
        }
    }

}
