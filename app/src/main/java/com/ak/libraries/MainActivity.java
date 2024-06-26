package com.ak.libraries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.ak.ColoredDate;
import com.ak.EventObjects;
import com.ak.KalendarView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KalendarView mKalendarView = findViewById(R.id.kalendar);

        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.setColoredDates(datesColors);

        List<EventObjects> events = new ArrayList<>();
        events.add(new EventObjects("meeting",new Date()));
        mKalendarView.setEvents(events);

        mKalendarView.setDateSelector(new KalendarView.DateSelector() {
            @Override
            public void onDateClicked(Date selectedDate) {
                Log.d("DateSel",selectedDate.toString());
            }
        });

        mKalendarView.setMonthChanger(changedMonth -> Log.d("Changed","month changed "+changedMonth));
        Calendar tempCal = Calendar.getInstance();
        tempCal.set(Calendar.DATE,11);
        List<EventObjects> events2 = new ArrayList<>();
        events2.add(new EventObjects("meeting",tempCal.getTime()));

        tempCal.set(Calendar.DATE,15);
        List<ColoredDate> datesColors2 = new ArrayList<>();
        datesColors2.add(new ColoredDate(tempCal.getTime(), getResources().getColor(R.color.red_holiday)));
        mKalendarView.addColoredDates(datesColors2);

        mKalendarView.addEvents(events2);

        Button btBottomDialog = findViewById(R.id.bt_bottom_dialog);
        btBottomDialog.setOnClickListener(view -> showDialog());

    }

    void showDialog(){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View bottomSheet = getLayoutInflater().inflate(R.layout.bottom_dialog, null);

        Button demo = bottomSheet.findViewById(R.id.bt_bottom_dialog);
        KalendarView kalendarView = bottomSheet.findViewById(R.id.kalendar);
        demo.setOnClickListener(
                view -> {
                    kalendarView.setInitialSelectedDate(new Date());
                }
        );

        dialog.setContentView(bottomSheet);
        dialog.show();
    }
}