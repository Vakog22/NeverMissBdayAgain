package com.example.nevermissbdayagain;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaDrm;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;

    public MyGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        super(context, R.layout.calendar_single_cell);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int DisplayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int DisplayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.calendar_single_cell, parent, false);
        }

        if(DisplayMonth == currentMonth && DisplayYear == currentYear){
            view.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        }
        else {
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }

        TextView DayNumber = view.findViewById(R.id.calendar_day);
        TextView EventAmount = view.findViewById(R.id.events_id);

        DayNumber.setText(String.valueOf(DayNo));
        Calendar eventCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < events.size();i++){
           eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));

            if (DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && DisplayMonth == eventCalendar.get(Calendar.MONTH)+1 && DisplayYear == eventCalendar.get(Calendar.YEAR)){
                arrayList.add(events.get(i).getPERSON_NAME());
                EventAmount.setText(arrayList.size() + "ДР");
            }
        }

        return view;
    }

    private Date ConvertStringToDate(String input){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try{
            date = format.parse(input);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}