package com.shinsegae.mdm.downtest.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.shinsegae.mdm.downtest.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

// 참조 : https://velog.io/@tempstrata/android-apk-update-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84
public class DownLoad {

    public static String strErrorMsg = "";

    public static File downLoad(String url, String title) {
        File downFile = null;

        try {
            URL u = new URL(url);
            HttpURLConnection connect = (HttpURLConnection) u.openConnection();
            connect.setDoOutput(true);
            connect.connect();
            if (connect.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.w("ERROR SERVER RETURNED HTTP", connect.getResponseCode() + "");
                strErrorMsg = "ERROR SERVER RETURNED HTTP";
                return downFile;
            }

            File path = new File(Environment.getExternalStorageDirectory() + "/download");
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists()) {
                directory.mkdir();
            }

            downFile = new File(directory, title);
            if (!downFile.exists()) {
                downFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(downFile, false);
            InputStream is = connect.getInputStream();
            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b = is.read(bytes, 0, 1024)) != -1) {
                fos.write(bytes, 0, b);
            }

            strErrorMsg = "";
        } catch (Exception e) {
            strErrorMsg = e.getMessage();
            downFile = null;
        }

        return downFile;
    }

    public static boolean downLoadInstall(Context context, String url, String title) {
        boolean bReturn = false;

        try {
            title = getFileNamSeq(title);
            File downFile = downLoad(url, title);
            if (downFile == null) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", downFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
            bReturn = true;
        } catch (Exception e) {
            strErrorMsg = e.getMessage();
        }

        return bReturn;
    }

    public static String getFileNamSeq(String title) {
        File downFile = null;
        String strFileName = title;

        try {
            for(int i=0; i < 10000; i++) {
                strFileName = i + title;
                if(getFileNameDel(strFileName)) {
                    return strFileName;
                }
            }
        } catch (Exception e) {
            strErrorMsg = e.getMessage();
        }
        return strFileName;
    }

    private static boolean getFileNameDel(String title) {
        File downFile = null;

        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists()) {
                directory.mkdir();
            }

            downFile = new File(directory, title);
            if (!downFile.exists()) {
                return true;
            } else {
                downFile.delete();
            }
            try {
                FileOutputStream fos = new FileOutputStream(downFile, false);
                fos.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            strErrorMsg = e.getMessage();
        }
        return false;
    }

    private static DownloadManager mDownloadManager;
    private static Long mDownloadQueueId;
    private static File outputFile;

    private static String outputDirectory = Environment.DIRECTORY_DOWNLOADS + File.separator + "apk_download";

    public static void URLDownloading(Context mContext, String strUrl, String title) {
        title = UUID.randomUUID().toString() + ".apk";

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
        boolean aa = outputFile.exists();
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        Uri downloadUri = Uri.parse(strUrl.trim());

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        List<String> pathSegmentList = downloadUri.getPathSegments();
        request.setTitle("다운로드 항목");
        request.setDestinationUri(Uri.fromFile(outputFile));

        request.setAllowedOverMetered(true);

        mDownloadQueueId = mDownloadManager.enqueue(request);
    }

    public static BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(mDownloadQueueId == reference){
                DownloadManager.Query query = new DownloadManager.Query();  // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference);
                Cursor cursor = mDownloadManager.query(query);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                int status = cursor.getInt(columnIndex);
                int reason = cursor.getInt(columnReason);

                cursor.close();

                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL :
//                        MainActivity.m_MainActivity.installApk(outputFile);
                        break;

                    case DownloadManager.STATUS_PAUSED :
                        Toast.makeText(context, "다운로드가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_FAILED :
                        Toast.makeText(context, "다운로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    public static boolean deleteDirectory(File directoryToBeDeleted) {
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
