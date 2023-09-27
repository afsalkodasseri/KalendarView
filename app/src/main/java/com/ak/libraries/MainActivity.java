package com.ak.libraries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.util.Log;

import com.ak.ColoredDate;
import com.ak.EventObjects;
import com.ak.KalendarView;

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
        tempCal.set(Calendar.DATE,1);
        tempCal.set(Calendar.MONTH,4);
        mKalendarView.setInitialSelectedDate(tempCal.getTime());

    }
}