package com.shinsegae.mdm.downtest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UDialog {
    public static Activity m_ActActivity = null;
    public static AlertDialog m_alertDialog = null;

        public static void show(final Activity actActivity, int strTitle, int strContent, int strOk) {
            show(actActivity, actActivity.getString(strTitle), actActivity.getString(strContent), actActivity.getString(strOk), null);
        }

        public static void show(final Activity actActivity, String strTitle, String strContent, String strOk) {
            show(actActivity, strTitle, strContent, strOk, null);
        }

        public static void show(final Activity actActivity, int strTitle, int strContent, int strOk, DialogInterface.OnClickListener OnOkClick) {
            show(actActivity, actActivity.getString(strTitle), actActivity.getString(strContent), actActivity.getString(strOk), OnOkClick);
        }

        public static void show(final Activity actActivity, String strTitle, String strContent, String strOk, DialogInterface.OnClickListener OnOkClick) {
            AlertDialog.Builder ad = new AlertDialog.Builder(actActivity);

            ad.setTitle(strTitle);
            ad.setMessage(strContent);
            ad.setCancelable(false);

            if (OnOkClick != null) {
                ad.setPositiveButton(strOk, OnOkClick);
            } else {
                ad.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
            }

            m_alertDialog = ad.create();
            m_alertDialog.show();
        }


        public static void show(final Activity actActivity, int strTitle, int strContent, int strOk, int strCencel
                , DialogInterface.OnClickListener OnOkClick, DialogInterface.OnClickListener OnCencelClick) {
            show(actActivity, actActivity.getString(strTitle), actActivity.getString(strContent), actActivity.getString(strOk), actActivity.getString(strCencel), OnOkClick, OnCencelClick);
        }

        public static void show(final Activity actActivity, String strTitle, String strContent, String strOk, String strCencel
                , DialogInterface.OnClickListener OnOkClick, DialogInterface.OnClickListener OnCencelClick) {


            AlertDialog.Builder ad = new AlertDialog.Builder(actActivity);

            ad.setTitle(strTitle);
            ad.setMessage(strContent);
            ad.setCancelable(false);

            if (OnOkClick != null) {
                ad.setPositiveButton(strOk, OnOkClick);
            }

            if (OnCencelClick != null) {
                ad.setNeutralButton(strCencel, OnCencelClick);
            } else {
                ad.setNeutralButton(strCencel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();

                    }
                });
            }


            m_alertDialog = ad.create();
            m_alertDialog.show();

        }

        public static void show(final Activity actActivity, LinearLayout linear, int iTextViewId, int strTitle, int strContent, int strOk, int strCencel
                , DialogInterface.OnClickListener OnOkClick, DialogInterface.OnClickListener OnCencelClick) {
            show(actActivity, linear, iTextViewId, actActivity.getString(strTitle), actActivity.getString(strContent), actActivity.getString(strOk), actActivity.getString(strCencel), OnOkClick, OnCencelClick);

        }

        public static void show(final Activity actActivity, LinearLayout linear, int iTextViewId, String strTitle, String strContent, String strOk, String strCencel
                , DialogInterface.OnClickListener OnOkClick, DialogInterface.OnClickListener OnCencelClick) {


            AlertDialog.Builder ad = new AlertDialog.Builder(actActivity);

            ad.setView(linear);
            if (iTextViewId != -1)
                ((TextView) linear.findViewById(iTextViewId)).setText(" " + strContent);

            ad.setTitle(strTitle);
            ad.setCancelable(false);

            if (OnOkClick != null) {
                ad.setPositiveButton(strOk, OnOkClick);
            }

            if (OnOkClick != null) {
                ad.setNeutralButton(strCencel, OnCencelClick);
            } else {
                ad.setNeutralButton(strCencel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();

                    }
                });
            }
            m_alertDialog = ad.create();
            m_alertDialog.show();
    }
}
