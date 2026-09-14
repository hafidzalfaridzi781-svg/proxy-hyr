package com.zyecitedz.proxyhyr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class PermissionHelper {

    /** Mulai foreground service agar app tetap hidup di background */
    public static void startBackgroundService(Context ctx) {
        try {
            Intent i = new Intent(ctx, BackgroundService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                ctx.startForegroundService(i);
            } else {
                ctx.startService(i);
            }
        } catch (Throwable t) {
            Toast.makeText(ctx, "Gagal start background: " + t.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /** Minta exemption battery optimization */
    public static void requestBatteryExemption(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT < 23) return;

            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            if (pm == null) return;

            if (pm.isIgnoringBatteryOptimizations(ctx.getPackageName())) return;

            Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            i.setData(Uri.parse("package:" + ctx.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Throwable t) {
            // fallback: buka settings battery optimization
            try {
                Intent i = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } catch (Throwable ignored) {}
        }
    }

    /** Helper cek apakah service foreground sedang jalan */
    public static boolean isBackgroundRunning(Context ctx) {
        return BackgroundService.RUNNING;
    }
}
