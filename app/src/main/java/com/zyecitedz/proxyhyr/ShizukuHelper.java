package com.zyecitedz.proxyhyr;

import android.app.Activity;

import rikka.shizuku.Shizuku;

public class ShizukuHelper {

    public interface Callback {
        void onResult(boolean granted);
    }

    private static final int REQ_CODE = 2001;

    private static Shizuku.OnRequestPermissionResultListener listener;

    /** Cek apakah Shizuku service aktif (Shizuku Manager terinstall & jalan) */
    public static boolean isAvailable() {
        try {
            return Shizuku.pingBinder();
        } catch (Throwable t) {
            return false;
        }
    }

    /** Cek apakah permission Shizuku sudah diberikan */
    public static boolean isGranted() {
        try {
            return Shizuku.checkSelfPermission() == 0; // PERMISSION_GRANTED
        } catch (Throwable t) {
            return false;
        }
    }

    /** Minta permission Shizuku */
    public static void requestPermission(Activity activity, Callback cb) {
        try {
            if (!Shizuku.pingBinder()) {
                cb.onResult(false);
                return;
            }

            if (Shizuku.isPreV11() || Shizuku.checkSelfPermission() == 0) {
                cb.onResult(Shizuku.checkSelfPermission() == 0);
                return;
            }

            // register listener
            if (listener != null) {
                Shizuku.removeRequestPermissionResultListener(listener);
            }
            listener = (requestCode, grantResult) -> {
                if (requestCode == REQ_CODE) {
                    cb.onResult(grantResult == 0);
                }
            };
            Shizuku.addRequestPermissionResultListener(listener);

            // request
            Shizuku.requestPermission(REQ_CODE);

        } catch (Throwable t) {
            cb.onResult(false);
        }
    }
}
