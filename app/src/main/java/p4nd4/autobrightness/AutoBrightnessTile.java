package p4nd4.autobrightness;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.provider.Settings.System;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AutoBrightnessTile extends TileService {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1) {
                tile.setIcon(Icon.createWithResource(this, R.drawable.ic_brightness_auto));
                tile.setLabel("Auto Brightness");
            } else {
                tile.setIcon(Icon.createWithResource(this, R.drawable.ic_brightness_set));
                tile.setLabel("Set Brightness");
            }
            tile.updateTile();
        } catch (Exception e) {}
    }

    Handler collapseNotificationHandler;
    @Override
    public void onClick() {

        super.onClick();


            Intent svc;
            SharedPreferences AppData;
            SharedPreferences.Editor editor;

            AppData = PreferenceManager.getDefaultSharedPreferences(this);
            if (AppData.getBoolean("autoHideTile", true)) collapseNow();


                if (!Settings.System.canWrite(this)) {
                    Intent startSetup = new Intent(this, Setup.class);
                    startSetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startSetup);
                } else {


                    if (AppData.getBoolean("BrightnessMenuOnTap", false)) {
                        Intent startSetup = new Intent(this, AdjustBrightness.class);
                        startSetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startSetup);
                    } else {


                        Tile tile = getQsTile();
                    try {
                        if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 0) {
                            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_brightness_auto));
                            tile.setLabel("Auto Brightness");
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                            //chk and STORE
                            if (AppData.getBoolean("storeManualBrightness", false)) {
                                editor = AppData.edit();
                                editor.putInt("ManualBrightness", Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
                                editor.commit();
                            }
                        } else {
                            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_brightness_set));
                            tile.setLabel("Set Brightness");
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                            //chk and RESTORE
                            if (AppData.getBoolean("storeManualBrightness", false)) {
                                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, AppData.getInt("ManualBrightness", 255));
                            }
                        }
                        tile.updateTile();
                    } catch (Exception e) {
                    }
                }
                }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void collapseNow() {

        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }
        collapseNotificationHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                Object statusBarService = getSystemService("statusbar");
                Class<?> statusBarManager = null;

                try {
                    statusBarManager = Class.forName("android.app.StatusBarManager");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Method collapseStatusBar = null;

                try {
                    if (Build.VERSION.SDK_INT > 16) {
                        collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                    } else {
                        collapseStatusBar = statusBarManager .getMethod("collapse");
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                collapseStatusBar.setAccessible(true);

                try {
                    collapseStatusBar.invoke(statusBarService);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }, 300L);

    }
}
