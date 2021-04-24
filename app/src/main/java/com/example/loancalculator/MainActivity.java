package com.example.loancalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.DatePicker;
import java.util.Calendar;
import android.app.AlarmManager ;
import android.app.DatePickerDialog ;
import android.app.Notification ;
import android.app.PendingIntent ;
import android.content.Context ;
import android.content.Intent ;
import android.os.Bundle ;
import android.view.View ;
import android.widget.Button ;
import android.widget.DatePicker ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;



public class MainActivity extends AppCompatActivity {

    EditText amount, interest, years;
    Button calculate;
    TextView emi;
    TextView finalamount;
    TextView interestamount;
    private static final String TAG = "MainActivity";
    SharedPreferences sharedpreferences;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    Button btnDate ;
    final Calendar myCalendar = Calendar. getInstance () ;
    public static AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        btnDate = findViewById(R.id. btnDate ) ;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                Date dates = myCalendar .getTime() ;
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
                scheduleNotification(getNotification( btnDate .getText().toString()) , dates.getTime()) ;

                Log.e("test", String.valueOf(dates.getTime()));
            }
        };

        // for text views
        amount = findViewById(R.id.editTextNumber1);
        interest = findViewById(R.id.editTextNumber2);
        years = findViewById(R.id.editTextNumber3);

        // for button with operations
        calculate = findViewById(R.id.button);

        // for answer field
        emi = findViewById(R.id.emi);
        interestamount = findViewById(R.id.interestamount);
        finalamount = findViewById(R.id.total);

        //storing months in shared preference
        SharedPreferences.Editor editor = sharedpreferences.edit();

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st1 = amount.getText().toString();
                String st2 = interest.getText().toString();
                String st3 = years.getText().toString();
                if (TextUtils.isEmpty(st1)) {
                    amount.setError("Enter Prncipal Amount");
                    amount.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(st2)) {
                    interest.setError("Enter Interest Rate");
                    interest.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(st3)) {
                    years.setError("Enter Years");
                    years.requestFocus();
                    return;
                }

                float p = Float.parseFloat(st1);
                float i = Float.parseFloat(st2);
                float y = Float.parseFloat(st3);
                float Principal = calPric(p);
                float Rate = calInt(i);
                float Months = calMonth(y);
                float Dvdnt = calDvdnt(Rate, Months);
                float FD = calFinalDvdnt(Principal, Rate, Dvdnt);
                float D = calDivider(Dvdnt);
                float e= calEmi(FD, D);
                float TA = calTa(e, Months);
                float ti = calTotalInt(TA, Principal);
                emi.setText(String.valueOf(e));
                interestamount.setText(String.valueOf(ti));
                finalamount.setText(String.valueOf(TA));
                String st= String.valueOf(Integer.parseInt(years.getText().toString())*12);
                st= String.valueOf(2);
                editor.putString("months",st );
                editor.commit();
               // SharedPreferences shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                String channel = (sharedpreferences.getString("months",""));
                Log.e("test",channel);



            }
        });
    }


    public void scheduleNotification (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        Log.e("test","ScheduleNotificatoin");
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+1800,
                1800, //write month in  millisec
                pendingIntent);


        //Code to cancel notification
        String mon1 = (sharedpreferences.getString("month-cur",""));
        String mon2 = (sharedpreferences.getString("months",""));
        Log.e("test","Mon1 "+mon1);
        Log.e("test","Mon2 "+mon2);
        if(mon1.equals(mon2)){
            Log.e("test1","CANCEL NOTIFICATION");
            // alarmManager.cancel(pendingIntent);
          //  MainActivity.cancel();
            alarmManager.cancel(pendingIntent);
            Log.e("test","CANCEL DONE");
        }
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        Log.e("test","Get Notication");
        builder.setContentTitle( "Please Pay Your Home Loan EMI" ) ;
        content="Time For EMI";
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view , int year , int monthOfYear , int dayOfMonth) {
            myCalendar .set(Calendar. YEAR , year) ;
            myCalendar .set(Calendar. MONTH , monthOfYear) ;
            myCalendar .set(Calendar. DAY_OF_MONTH , dayOfMonth) ;
            Log.e("test","ONDatesetListener");
            updateLabel() ;
        }
    } ;
    public void setDate (View view) {
        new DatePickerDialog(
                MainActivity. this, date ,
                myCalendar .get(Calendar. YEAR ) ,
                myCalendar .get(Calendar. MONTH ) ,
                myCalendar .get(Calendar. DAY_OF_MONTH )
        ).show() ;
    }
    private void updateLabel () {
        String myFormat = "dd/MM/yy" ; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat , Locale. getDefault ()) ;
        Date date = myCalendar .getTime() ;
        Log.e("test", String.valueOf(date));
        btnDate .setText(sdf.format(date)) ;
        scheduleNotification(getNotification( btnDate .getText().toString()) , date.getTime()) ;
    }

    public float calPric(float p) {
        return (float)(p);
    }
    public float calInt(float i) {
        return (float)(i /12/ 100);
    }
    public float calMonth(float y) {
        return (float)(y * 12);
    }
    public float calDvdnt(float Rate, float Months) {
        return (float)(Math.pow(1 + Rate, Months));
    }
    public float calFinalDvdnt(float Principal, float Rate, float Dvdnt) {
        return (float)(Principal * Rate * Dvdnt);
    }
    public float calDivider(float Dvdnt) {
        return (float)(Dvdnt - 1);
    }
    public float calEmi(float FD, Float D) {
        return (float)(FD / D);
    }
    public float calTa(float emi, Float Months) {
        return (float)(emi * Months);
    }
    public float calTotalInt(float TA, float Principal) {
        return (float)(TA - Principal);
    }
}

