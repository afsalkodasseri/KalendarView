package com.ak.libraries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ak.ColoredDate;
import com.ak.KalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KalendarView vew = findViewById(R.id.kalendar);
        List<ColoredDate> datesColors = new ArrayList<>();
        datesColors.add(new ColoredDate(new Date(),getColor(R.color.manja)));
        vew.setColoredDates(datesColors);
    }
}