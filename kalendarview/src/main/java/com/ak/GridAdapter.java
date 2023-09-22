package com.ak;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    //customizations
    Drawable todayIndicator=null,selectedIndicator=null,eventIndicator=null;
    int dateColor=0,nonMonthDateColor=0,todayDateColor=0,selectedDateColor=0;
    Typeface dateFontFace;
    int dateTextStyle = 0;
    int calendarBackgroundColor = 0;
    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents,Date color,List<ColoredDate> colorFulDates) {
        super(context, R.layout.calendarview_cell);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
        cos=context;
        color_date=color;
        this.colorFulDates=colorFulDates;
    }

    public void setDrawables(Drawable todayIndicator,Drawable selectedIndicator,Drawable eventIndicator){
        this.todayIndicator = todayIndicator;
        this.selectedIndicator = selectedIndicator;
        this.eventIndicator = eventIndicator;
    }
    public void setTextColors(int dateColor,int nonMonthDateColor,int todayDateColor,int selectedDateColor){
        this.dateColor = dateColor;
        this.nonMonthDateColor = nonMonthDateColor;
        this.todayDateColor = todayDateColor;
        this.selectedDateColor = selectedDateColor;
    }

    public void setFontProperties(Typeface fontFace,int textStyle){
        dateFontFace = fontFace;
        dateTextStyle = textStyle;
    }

    public void setCalendarBackgroundColor(int backgroundColor){
        this.calendarBackgroundColor = backgroundColor;
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
            ConstraintLayout clRoot = view.findViewById(R.id.cl_root);
            llParent.setBackgroundColor(calendarBackgroundColor);
            clRoot.setBackgroundColor(calendarBackgroundColor);
        }
        LinearLayout llParent = view.findViewById(R.id.ll_parent);
        ConstraintLayout clRoot = view.findViewById(R.id.cl_root);
        TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
        //set font family
        if(dateTextStyle!=0)  cellNumber.setTextAppearance(getContext(),dateTextStyle);
        if(dateFontFace!=null)  cellNumber.setTypeface(dateFontFace);
        //set background
        llParent.setBackgroundColor(calendarBackgroundColor);
        clRoot.setBackgroundColor(calendarBackgroundColor);

        if(displayMonth == currentMonth && displayYear == currentYear){
            cellNumber.setTextColor(dateColor!=0?dateColor:Color.BLACK);
            cellNumber.setTag(0);

        }else{

            if(displayMonth > currentMonth && displayYear == currentYear || (displayMonth < currentMonth && displayYear > currentYear))
            {
                cellNumber.setTag(1);
            }
            else{
                cellNumber.setTag(2);
            }
            cellNumber.setTextColor(nonMonthDateColor!=0?nonMonthDateColor:Color.LTGRAY);

        }
        //Add day to calendar
        //TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
        cellNumber.setText(String.valueOf(dayValue));


        if(displayMonth == toMonth && displayYear == toYear&&dayValue==toDay){
            if(todayIndicator!=null)
                llParent.setBackground(todayIndicator);
            else
                llParent.setBackgroundResource(R.drawable.calendarview_today);
            cellNumber.setTag(-1);
            if(todayDateColor!=0)
                cellNumber.setTextColor(todayDateColor);
        }
        //set custom date color before set the selected color
        int customDateColor = getDateColor(mDate);
        if(customDateColor!=0)
            cellNumber.setTextColor(customDateColor);

        if(displayMonth == colorMonth&&colorday==dayValue)
        {
            if(selectedIndicator!=null)
                llParent.setBackground(selectedIndicator);
            else
                llParent.setBackgroundResource(R.drawable.calendarview_select_date);
            cellNumber.setTextColor(selectedDateColor!=0?selectedDateColor:Color.WHITE);
        }

        view.setTag(position);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });



        //Add events to the calendar
        TextView tvEventIndicator = (TextView)view.findViewById(R.id.event_id);
        Calendar eventCalendar = Calendar.getInstance();
        for(int i = 0; i < allEvents.size(); i++){
            eventCalendar.setTime(allEvents.get(i).getDate());
            if(dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)){
                if(eventIndicator!=null)
                    tvEventIndicator.setBackground(eventIndicator);
                else
                    tvEventIndicator.setBackgroundResource(R.drawable.calendarview_event);
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

    public int getDateColor(Date mDate){
        for(int i=0;i<colorFulDates.size();i++){
            if(sdfDmy.format(mDate).equals(sdfDmy.format(colorFulDates.get(i).date)))
            {
                return colorFulDates.get(i).color;
            }
        }
        return 0;
    }
}
