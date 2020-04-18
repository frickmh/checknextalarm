package com.fric.checknextalarm;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();


                    TextView textViewPackageName = findViewById(R.id.textViewPackageNameData);
                    TextView textViewTriggerTime = findViewById(R.id.textViewTriggerTimeData);
                    TextView textViewMoreInfo = findViewById(R.id.textViewMoreInfoData);


                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(info.getTriggerTime());
                        if (BuildConfig.DEBUG)
                            Log.i("test", "Next Alarm Created by: " + info.getShowIntent().getCreatorPackage() + " at " + calendar.getTime().toString());

                        textViewPackageName.setText(info.getShowIntent().getCreatorPackage());
                        textViewTriggerTime.setText(calendar.getTime() + "");
                        textViewMoreInfo.setText(info.getShowIntent().getIntentSender().getCreatorPackage());
                    } catch (Exception e) {
                        textViewPackageName.setText(R.string.None_Found);
                        textViewTriggerTime.setText(R.string.None_Found);
                        textViewMoreInfo.setText(R.string.None_Found);
                    }



                }



            }
        });

        findViewById(R.id.buttonLaunchBilling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("launchBillingActivity", "Clicked!");
                launchBillingActivity(view);
            }
        });
    }

    public void launchBillingActivity(View view) {
        Log.i("launchBillingActivity", "Clicked!");
        Intent i = new Intent(this, BillingActivity.class);
        startActivity(i);
    }
}
