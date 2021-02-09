package p4nd4.autobrightness;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;



public class Setup extends Activity {
    Button btn2;
    Button btn3;
    boolean autoHideTile;
    boolean BrightnessMenuOnTap;
    boolean storeManualBrightness;
    Switch sw1;
    Switch sw2;
    Switch sw3;
    SharedPreferences AppData;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);
        WebView webView = (WebView) findViewById(R.id.gif1);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl("file:///android_asset/howto.htm");


        AppData = PreferenceManager.getDefaultSharedPreferences(this);

        sw1 = (Switch) findViewById(R.id.switch1);

        BrightnessMenuOnTap = AppData.getBoolean("BrightnessMenuOnTap", false);


        sw1.setChecked(BrightnessMenuOnTap);
        sw1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BrightnessMenuOnTap=!BrightnessMenuOnTap;
                editor = AppData.edit();
                editor.putBoolean("BrightnessMenuOnTap", BrightnessMenuOnTap);
                editor.commit();

                if (!BrightnessMenuOnTap) Toast.makeText(getApplicationContext(), "Tapping tile will toggle Auto Brightness.", Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), "Tapping tile will open brightness menu.", Toast.LENGTH_LONG).show();


            }
        });

        sw2 = (Switch) findViewById(R.id.switch2);

        autoHideTile = AppData.getBoolean("autoHideTile", true);


        sw2.setChecked(autoHideTile);
        sw2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                autoHideTile=!autoHideTile;
                editor = AppData.edit();
                editor.putBoolean("autoHideTile", autoHideTile);
                editor.commit();

                if (!autoHideTile) Toast.makeText(getApplicationContext(), "Quick Settings will remain open after tapping this tile.", Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), "Quick Settings will hide after tapping this tile.", Toast.LENGTH_LONG).show();


            }
        });

        sw3 = (Switch) findViewById(R.id.switch3);

        storeManualBrightness = AppData.getBoolean("storeManualBrightness", false);


        sw3.setChecked(storeManualBrightness);
        sw3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                storeManualBrightness=!storeManualBrightness;
                editor = AppData.edit();
                editor.putBoolean("storeManualBrightness", storeManualBrightness);
                try {
                    editor.putInt("ManualBrightness", Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
                } catch (Exception e) {}
                editor.commit();

                if (!storeManualBrightness) Toast.makeText(getApplicationContext(), "App won't try to restore previous manual brightness level.", Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), "App will store manual brightness level and do a restore once you go back to manual.\n(Pixel AI brightness fix)", Toast.LENGTH_LONG).show();


            }
        });


        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                //System.exit(0);
            }
        });

        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5847423621940926942"));
                startActivity(i);
            }
        });

        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/Panda-Soft");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        TextView PermissionsText;
        Intent intentSettings;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (!Settings.System.canWrite(this))) {
            Toast.makeText(this, "Please select " + getString(R.string.app_name) + " and allow permissions first!", Toast.LENGTH_LONG).show();
            if (!Settings.System.canWrite(Setup.this)) {
                intentSettings = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSettings);
            }

            PermissionsText = (TextView) findViewById(R.id.permissions);
            PermissionsText.setText(R.string.perm);
            PermissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

        } else {
            PermissionsText = (TextView) findViewById(R.id.permissions);
            PermissionsText.setText("");
            PermissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP,0);
        }

    }

}
