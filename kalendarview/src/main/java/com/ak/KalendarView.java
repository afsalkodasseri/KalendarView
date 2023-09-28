package com.ak;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import com.ak.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KalendarView extends LinearLayout{
    private static final String TAG = KalendarView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    public GridView calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    int prev=-1,pos,cr_pos=-2;
    List<Date> dayValueInCells;
    List<EventObjects> mEvents = new ArrayList<>();
    Date selected;
    Calendar today_date=Calendar.getInstance();
    Date color_date;
    DateSelector mDateSelector;
    MonthChanger mMonthChanger;
    List<ColoredDate> colorFulDates = new ArrayList<>();

    //customizations
    Drawable todayIndicator=null,selectedIndicator=null,eventIndicator=null;
    int dateColor=0,nonMonthDateColor=0,todayDateColor=0,selectedDateColor=0;
    Typeface monthFontFace=null,weekFontFace=null,dateFontFace=null;
    int monthTextStyle=0,weekTextStyle=0,dateTextStyle=0;
    Drawable nextIcon=null,prevIcon=null;
    int calendarBackgroundColor=0;

    public KalendarView(Context context) {
        super(context);
    }
    public KalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        cal = getZeroTime(cal);
        today_date = getZeroTime(today_date);
        color_date=today_date.getTime();
        Calendar tempTomorrow = Calendar.getInstance();
        tempTomorrow.setTime(today_date.getTime());
        tempTomorrow.add(Calendar.DATE,1);
        color_date=tempTomorrow.getTime();

        todayIndicator = AppCompatResources.getDrawable(context,R.drawable.calendarview_today);
        selectedIndicator = AppCompatResources.getDrawable(context,R.drawable.calendarview_select_date);
        eventIndicator = AppCompatResources.getDrawable(context,R.drawable.calendarview_event);
        dateColor = Color.BLACK;
        nonMonthDateColor = Color.LTGRAY;
        todayDateColor = Color.BLACK;
        selectedDateColor = Color.WHITE;
        calendarBackgroundColor = Color.WHITE;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.KalendarView, 0, 0);

        int drwTodayId = typedArray.getResourceId(R.styleable.KalendarView_todayIndicator,0);
        if(drwTodayId!=0)
            todayIndicator = AppCompatResources.getDrawable(context,drwTodayId);

        int drwSelectedId = typedArray.getResourceId(R.styleable.KalendarView_selectedIndicator,0);
        if(drwSelectedId!=0)
            selectedIndicator = AppCompatResources.getDrawable(context,drwSelectedId);

        int drwEventId = typedArray.getResourceId(R.styleable.KalendarView_eventIndicator,0);
        if(drwEventId!=0)
            eventIndicator = AppCompatResources.getDrawable(context,drwEventId);

        int colorDate = typedArray.getColor(R.styleable.KalendarView_dateColor,0);
        if(colorDate!=0)
            dateColor = colorDate;
        int colorNonMonth = typedArray.getColor(R.styleable.KalendarView_nonMonthDateColor,0);
        if(colorNonMonth!=0)
            nonMonthDateColor = colorNonMonth;
        int colorToday = typedArray.getColor(R.styleable.KalendarView_todayDateColor,0);
        if(colorToday!=0)
            todayDateColor = colorToday;
        int colorSelected = typedArray.getColor(R.styleable.KalendarView_selectedDateColor,0);
        if(colorSelected!=0)
            selectedDateColor = colorSelected;

        // Set a custom font family via its reference
        int monthFontId = typedArray.getResourceId(R.styleable.KalendarView_monthFontFamily,0);
        if(monthFontId!=0)
            monthFontFace = ResourcesCompat.getFont(context, monthFontId);
        int weekFontId = typedArray.getResourceId(R.styleable.KalendarView_weekFontFamily,0);
        if(weekFontId!=0)
            weekFontFace = ResourcesCompat.getFont(context, weekFontId);
        int dateFontId = typedArray.getResourceId(R.styleable.KalendarView_dateFontFamily,0);
        if(dateFontId!=0)
            dateFontFace = ResourcesCompat.getFont(context, dateFontId);
        //for text styles
        monthTextStyle = typedArray.getResourceId(R.styleable.KalendarView_monthTextStyle,0);
        weekTextStyle = typedArray.getResourceId(R.styleable.KalendarView_weekTextStyle,0);
        dateTextStyle = typedArray.getResourceId(R.styleable.KalendarView_dateTextStyle,0);

        //for next icon
        int tempNextIcon = typedArray.getResourceId(R.styleable.KalendarView_nextIcon,0);
        if(tempNextIcon!=0)
            nextIcon = AppCompatResources.getDrawable(context,tempNextIcon);
        //for prev icon
        int tempPrevIcon = typedArray.getResourceId(R.styleable.KalendarView_prevIcon,0);
        if(tempPrevIcon!=0)
            prevIcon = AppCompatResources.getDrawable(context,tempPrevIcon);

        //for calendarbackground
        int colorBg = typedArray.getColor(R.styleable.KalendarView_calendarBackground,0);
        if(colorBg!=0)
            calendarBackgroundColor = colorBg;

        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();


        Log.d(TAG, "I need to call this method");
    }
    public KalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initializeUILayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendarview, this);
        previousButton = view.findViewById(R.id.previous_month);
        nextButton = view.findViewById(R.id.next_month);
        currentDate = view.findViewById(R.id.display_current_date);
        calendarGridView = view.findViewById(R.id.calendar_grid);
        LinearLayout llRoot = view.findViewById(R.id.ll_root);
        LinearLayout llCalendarHead = view.findViewById(R.id.ll_calendar_head);
        LinearLayout llCalendarWeek = view.findViewById(R.id.ll_calendar_week);
        //apply textstyle
        if(monthTextStyle!=0) currentDate.setTextAppearance(context,monthTextStyle);
        if(weekTextStyle!=0){
            ((TextView)view.findViewById(R.id.sun)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.mon)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.tue)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.wed)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.thu)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.fri)).setTextAppearance(context,weekTextStyle);
            ((TextView)view.findViewById(R.id.sat)).setTextAppearance(context,weekTextStyle);
        }
        //set font family
        if(monthFontFace!=null) currentDate.setTypeface(monthFontFace);
        if(weekFontFace!=null){
            ((TextView)view.findViewById(R.id.sun)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.mon)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.tue)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.wed)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.thu)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.fri)).setTypeface(weekFontFace);
            ((TextView)view.findViewById(R.id.sat)).setTypeface(weekFontFace);
        }

        //for next, prev icons
        if(nextIcon!=null)
            nextButton.setImageDrawable(nextIcon);
        if(prevIcon!=null)
            previousButton.setImageDrawable(prevIcon);

        //set calendar background
        llRoot.setBackgroundColor(calendarBackgroundColor);
        llCalendarHead.setBackgroundColor(calendarBackgroundColor);
        llCalendarWeek.setBackgroundColor(calendarBackgroundColor);
        calendarGridView.setBackgroundColor(calendarBackgroundColor);
    }
    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(v -> {
            cal.add(Calendar.MONTH, -1);
            setUpCalendarAdapter();
            if(mMonthChanger!=null)
                mMonthChanger.onMonthChanged(cal.getTime());
        });
    }
    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(v -> {
            cal.add(Calendar.MONTH, 1);
            setUpCalendarAdapter();
            if(mMonthChanger!=null)
                mMonthChanger.onMonthChanged(cal.getTime());
        });
    }
    public void setGridCellClickEvents(){
        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            pos=(int)view.getTag();
            LinearLayout llParent = view.findViewById(R.id.ll_parent);
            llParent.setBackground(selectedIndicator);
            TextView txt=view.findViewById(R.id.calendar_date_id);
            txt.setTextColor(selectedDateColor);
            color_date=dayValueInCells.get(pos);

            if((prev!=-1)&&prev!=cr_pos) {
                LinearLayout prevParent = parent.getChildAt(prev).findViewById(R.id.ll_parent);
                prevParent.setBackgroundColor(calendarBackgroundColor);
                TextView txtd=parent.getChildAt(prev).findViewById(R.id.calendar_date_id);
                txtd.setTextColor((int)(txtd.getTag())==0?dateColor:nonMonthDateColor);
                int customDateColor = mAdapter.getDateColor(dayValueInCells.get(prev));
                if(customDateColor!=0 && (int)txtd.getTag()==0)
                    txtd.setTextColor(customDateColor);
            }
            if(prev==cr_pos) {
                View childView = parent.getChildAt(prev);
                if(childView!=null){
                    LinearLayout todayParent = childView.findViewById(R.id.ll_parent);
                    todayParent.setBackground(todayIndicator);
                    TextView txtd=childView.findViewById(R.id.calendar_date_id);
                    txtd.setTextColor(todayDateColor);
                    int customDateColor = mAdapter.getDateColor(dayValueInCells.get(prev));
                    if(customDateColor!=0)
                        txtd.setTextColor(customDateColor);
                }
            }
            prev=pos;

            int month_id=(int)txt.getTag();
            if(month_id==-1) {
               cr_pos=pos;
            }
            if(month_id==1) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
                cr_pos=-2;
                //to inform month changed
                if(mMonthChanger!=null)
                    mMonthChanger.onMonthChanged(cal.getTime());
            }
            if(month_id==2) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
                cr_pos=-2;
                //to inform month changed
                if(mMonthChanger!=null)
                    mMonthChanger.onMonthChanged(cal.getTime());
            }
            if(mDateSelector!=null)
                mDateSelector.onDateClicked(color_date);
        });
    }
    private void setUpCalendarAdapter(){
        dayValueInCells = new ArrayList<Date>();
        String sDate1="27/02/2020";
        try {
            Date date1=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse(sDate1);
            Date date2=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse("08/09/2023");

        EventObjects evd=new EventObjects(10,"hello",date1);
        EventObjects evde=new EventObjects(11,"hi",date2);

//        mEvents.add(evd);
//        mEvents.add(evde);
        } catch (ParseException e) {
            Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

        Calendar mCal = (Calendar)cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
        prev=dayValueInCells.indexOf(color_date);
        cr_pos=dayValueInCells.indexOf(today_date.getTime());
        mAdapter = new GridAdapter(context, dayValueInCells, cal, mEvents,color_date,colorFulDates);
        mAdapter.setDrawables(todayIndicator,selectedIndicator,eventIndicator);
        mAdapter.setTextColors(dateColor,nonMonthDateColor,todayDateColor,selectedDateColor);
        mAdapter.setFontProperties(dateFontFace,dateTextStyle);
        mAdapter.setCalendarBackgroundColor(calendarBackgroundColor);
        calendarGridView.setAdapter(mAdapter);

//        //for animated change
//        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.grid_anim);
//        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
//        calendarGridView.setLayoutAnimation(controller);

    }


    public interface DateSelector {
        void onDateClicked(Date selectedDate);
    }

    public interface MonthChanger {
        void onMonthChanged(Date changedMonth);
    }
    /**
     * set events with the date cell<br>
     * in the KalendarView <br>
     *
     * @param mEvents List of {@link com.ak.EventObjects} object
     *
     */
    public void setEvents(List<EventObjects> mEvents){
        if(mAdapter!=null){
            mAdapter.allEvents.clear();
            this.mEvents.clear();
            mAdapter.allEvents = mEvents;
            this.mEvents = mEvents;
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     * To add the events without losing the older<br>
     * events in the KalendarView <br>
     *
     * @param mEvents List of {@link com.ak.EventObjects} object
     *
     */
    public void addEvents(List<EventObjects> mEvents){
        if(mAdapter!=null){
            mAdapter.allEvents.addAll(mEvents);
            this.mEvents.addAll(mEvents);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * set colored dates with special colors<br>
     * in the KalendarView. <br>it will overwrite the existing colored dates <br>
     *
     * @param colorDates List of {@link com.ak.ColoredDate} object
     *
     */
    public void setColoredDates(List<ColoredDate> colorDates){
        if(mAdapter!=null){
            mAdapter.colorFulDates = colorDates;
            colorFulDates = colorDates;
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     * To add the colored dates without losing the older<br>
     * colored dates in the KalendarView <br>
     *
     * @param colorDates List of {@link com.ak.ColoredDate} object
     *
     */
    public void addColoredDates(List<ColoredDate> colorDates){
        if(mAdapter!=null){
            mAdapter.colorFulDates.addAll(colorDates);
            colorFulDates.addAll(colorDates);
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     * Listener for the date click events in the KalendarView <br>
     * it gives the new clicked date
     *
     */
    public void setDateSelector(DateSelector mSelector){
        this.mDateSelector = mSelector;
    }
    /**
     * Listener for the month changes in the KalendarView <br>
     * it gives the new month changed date
     *
     */
    public void setMonthChanger(MonthChanger mChanger){
        this.mMonthChanger = mChanger;
    }

    /**
     * The function set the first selected
     * date as initial date
     *
     * @param initialDate date object
     */
    public void setInitialSelectedDate(Date initialDate){
        color_date = getZeroTime(initialDate);
        cal.setTime(color_date);
        setUpCalendarAdapter();
    }

    private Date getZeroTime(Date date){
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(date);
        tempCal.set(Calendar.HOUR,0);
        tempCal.set(Calendar.MINUTE,0);
        tempCal.set(Calendar.SECOND,0);
        tempCal.set(Calendar.MILLISECOND,0);
        return tempCal.getTime();
    }
    private Calendar getZeroTime(Calendar mCalendar){
        Calendar tempCal = (Calendar) mCalendar.clone();
        tempCal.set(Calendar.HOUR,0);
        tempCal.set(Calendar.MINUTE,0);
        tempCal.set(Calendar.SECOND,0);
        tempCal.set(Calendar.MILLISECOND,0);
        return tempCal;
    }
}
