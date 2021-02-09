package p4nd4.autobrightness;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

public class AdjustBrightness extends Activity {

    ImageButton brightnessMode;
    SeekBar brightness;
    TextView brightnessValue;
    int SystemBrightness;
    float SystemAutoBrightness;
    RotateAnimation buttonClickSpin;
    ScaleAnimation buttonClickAnim;
    AnimationSet as;
    SharedPreferences AppData;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        AppData = PreferenceManager.getDefaultSharedPreferences(this);
        buttonClickSpin = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        buttonClickSpin.setDuration(300);
        buttonClickAnim = new ScaleAnimation(0.25f,1f,0.25f,1f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        buttonClickAnim.setDuration(250);


        as = new AnimationSet(true);
        as.setFillEnabled(true);
        as.setInterpolator(new BounceInterpolator());
        as.addAnimation(buttonClickAnim);
        as.addAnimation(buttonClickSpin);

        setContentView(R.layout.activity_adjust_brightness);
        brightnessMode = (ImageButton) findViewById(R.id.brightnessMode);
        brightness = (SeekBar) findViewById(R.id.brightness);
        brightnessValue = (TextView) findViewById(R.id.brightnessValue);
        brightness.setMax(255);

        if (Settings.System.canWrite(this)){
            SystemAutoBrightness=-2F;
            try {
                SystemAutoBrightness=Settings.System.getFloat(getContentResolver(), "screen_auto_brightness_adj", -2F);
            } catch (Exception e){}

            if (SystemAutoBrightness!=-2F) {
                try {
                    Settings.System.putFloat(getContentResolver(), "screen_auto_brightness_adj", SystemAutoBrightness);
                } catch (Exception e) {SystemAutoBrightness=-2F;}

            }

            try {
                SystemBrightness=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);


                if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    brightnessMode.setImageResource(R.drawable.ic_brightness_auto);
                    if (SystemAutoBrightness==-2F) {
                        brightness.setVisibility(View.INVISIBLE);
                        brightnessValue.setText("Auto");
                    } else {
                        brightnessValue.setText(Math.round((SystemAutoBrightness+1)*50) + "%");
                        brightness.setProgress(Math.round(((SystemAutoBrightness+1)*255)/2),true);
                    }
                } else {
                    brightnessMode.setImageResource(R.drawable.ic_brightness_set);
                    brightness.setVisibility(View.VISIBLE);
                    brightnessValue.setText(Math.round(SystemBrightness*100/255)+"%");
                    brightness.setProgress(SystemBrightness,true);
                }



            } catch (Exception e) {}

            brightnessMode.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.startAnimation(as);

                    try {

                        if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {

                            //chk and RESTORE
                            if (AppData.getBoolean("storeManualBrightness", false)) {
                                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, AppData.getInt("ManualBrightness", 255));
                            }

                            brightnessMode.setImageResource(R.drawable.ic_brightness_set);
                            brightness.setVisibility(View.VISIBLE);
                            SystemBrightness=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

                            brightnessValue.setText(Math.round(SystemBrightness*100/255)+"%");
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                            brightness.setProgress(SystemBrightness,true);


                        } else {

                            //chk and STORE
                            if (AppData.getBoolean("storeManualBrightness", false)) {
                                editor = AppData.edit();
                                editor.putInt("ManualBrightness", Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
                                editor.commit();
                            }

                            brightnessMode.setImageResource(R.drawable.ic_brightness_auto);
                            if (SystemAutoBrightness==-2F) {
                                brightness.setVisibility(View.INVISIBLE);
                                brightnessValue.setText("Auto");
                            } else {
                                SystemAutoBrightness=Settings.System.getFloat(getContentResolver(), "screen_auto_brightness_adj", 0F);
                                brightnessValue.setText(Math.round((SystemAutoBrightness+1)*50) + "%");
                                brightness.setProgress(Math.round(((SystemAutoBrightness+1)*255)/2),true);
                            }
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);


                        }
                    } catch (Exception e) {}

                }
            });
            brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                          if (fromUser) {

                                                              try {
                                                                  if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                                                                      Settings.System.putFloat(getContentResolver(), "screen_auto_brightness_adj", (float) ((progress - 127.5) / 127.5));
                                                                  } else {
                                                                      Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                                                                  }
                                                              } catch (Exception e) {
                                                              }
                                                              brightnessValue.setText(Math.round(progress * 100 / 255) + "%");
                                                          }
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {

                                                      }
                                                  }

            );


    } else  {
            Intent startSetup = new Intent(this, Setup.class);
            startSetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startSetup);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }


        return super.onTouchEvent(event);
    }
}
