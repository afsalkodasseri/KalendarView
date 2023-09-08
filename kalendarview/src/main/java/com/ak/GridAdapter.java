package com.ak;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ak.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridAdapter extends ArrayAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    public List<EventObjects> allEvents;
    //for change the color for specific dates
    public List<ColoredDate> colorFulDates = new ArrayList<>();
    SimpleDateFormat sdfDmy = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    Date color_date;
    Context cos;
    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents,Date color) {
        super(context, R.layout.calendarview_cell);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
        cos=context;
        color_date=color;
    }
    @NonNull
    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        Calendar today_date=Calendar.getInstance();
        int toYear=today_date.get(Calendar.YEAR);
        int toMonth=today_date.get(Calendar.MONTH)+1;
        int toDay=today_date.get(Calendar.DATE);
        int colorday=color_date.getDate();
        int colorMonth=color_date.getMonth()+1;
        int coloryear=color_date.getYear();
        View view = convertView;

        if(view == null){
            view = mInflater.inflate(R.layout.calendarview_cell, parent, false);
            LinearLayout llParent = view.findViewById(R.id.ll_parent);
            llParent.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        LinearLayout llParent = view.findViewById(R.id.ll_parent);
        TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
        if(displayMonth == currentMonth && displayYear == currentYear){
            cellNumber.setTextColor(Color.BLACK);
            cellNumber.setTag(0);

        }else{

            if(displayMonth > currentMonth && displayYear == currentYear || (displayMonth < currentMonth && displayYear > currentYear))
            {
                cellNumber.setTag(1);
            }
            else{
                cellNumber.setTag(2);
            }
            cellNumber.setTextColor(Color.LTGRAY);

        }
        //Add day to calendar
        //TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
        cellNumber.setText(String.valueOf(dayValue));


        if(displayMonth == toMonth && displayYear == toYear&&dayValue==toDay){
            llParent.setBackgroundResource(R.drawable.calendarview_today);
            cellNumber.setTag(-1);
        }
        if(displayMonth == colorMonth&&colorday==dayValue)
        {
            llParent.setBackgroundResource(R.drawable.calendarview_select_date);
            cellNumber.setTextColor(Color.WHITE);
        }

        view.setTag(position);

        int customDateColor = getDateColor(mDate);
        if(customDateColor!=0)
            cellNumber.setTextColor(customDateColor);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });



        //Add events to the calendar
        TextView eventIndicator = (TextView)view.findViewById(R.id.event_id);
        Calendar eventCalendar = Calendar.getInstance();
        for(int i = 0; i < allEvents.size(); i++){
            eventCalendar.setTime(allEvents.get(i).getDate());
            if(dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)){
                eventIndicator.setBackgroundResource(R.drawable.calendarview_event);
            }
        }
        return view;
    }
    @Override
    public int getCount() {
        return monthlyDates.size();
    }
    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }
    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }

    private int getDateColor(Date mDate){
        for(int i=0;i<colorFulDates.size();i++){
            if(sdfDmy.format(mDate).equals(sdfDmy.format(colorFulDates.get(i).date)))
            {
                return colorFulDates.get(i).color;
            }
        }
        return 0;
    }
}
