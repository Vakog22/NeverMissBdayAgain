package com.example.nevermissbdayagain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.PersonName.setText(events.getPERSON_NAME());
        holder.PersonAge.setText(events.getPERSON_AGE());
        holder.DateText.setText(events.getDATE());
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCalendarEvent(events.getPERSON_NAME(),events.getPERSON_AGE(),events.getDATE());
                arrayList.remove(position); //f433ttbgeergerguhujjujgouuhu
                notifyDataSetChanged();
            }
        });

        if(isNotifiable(events.getDATE(), events.getPERSON_NAME())){
            holder.NotifyMe.setImageResource(R.drawable.ic_notification_on);
        }
        else{
            holder.NotifyMe.setImageResource(R.drawable.ic_notification_off);
        }
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(ConvertStringToDate(events.getDATE()));
        int alarmYear = dateCalendar.get(Calendar.YEAR);
        int alarmMonth = dateCalendar.get(Calendar.MONTH);
        int alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

        holder.NotifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNotifiable(events.getDATE(), events.getPERSON_NAME())){
                    holder.NotifyMe.setImageResource(R.drawable.ic_notification_off);
                    deleteAlarm(GetRequestCode(events.getDATE(), events.getPERSON_NAME()));
                    updateEvent(events.getDATE(), events.getPERSON_NAME(), "off");
                    notifyDataSetChanged();
                }
                else{
                    holder.NotifyMe.setImageResource(R.drawable.ic_notification_on);
                    Calendar alarmCalender = Calendar.getInstance();
                    alarmCalender.set(alarmYear, alarmMonth, alarmDay);
                    setAlarm(alarmCalender,events.getPERSON_NAME(),GetRequestCode(events.getDATE(), events.getPERSON_NAME()));
                    updateEvent(events.getDATE(), events.getPERSON_NAME(), "on");
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView DateText, PersonName, PersonAge;
        Button Delete;
        ImageButton NotifyMe;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateText = itemView.findViewById(R.id.tv_date_out);
            PersonName = itemView.findViewById(R.id.tv_person_name_out);
            PersonAge = itemView.findViewById(R.id.tv_person_age_out);
            Delete = itemView.findViewById(R.id.btn_delete_event);
            NotifyMe = itemView.findViewById(R.id.btn_notify_event);

        }
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

    private void deleteCalendarEvent(String person_name, String person_age, String date){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.DeleteEvent(person_name,person_age,date,sqLiteDatabase);
        dbOpenHelper.close();
    }

    private boolean isNotifiable(String date, String pName){
        boolean notifiable = false;
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,pName,sqLiteDatabase);
        while (cursor.moveToNext()){
            String notify = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.NOTIFY));
            if (notify.equals("on")){
                notifiable = true;
            }
            else{
                notifiable = false;
            }
        }
        cursor.close();

        return  notifiable;
    }

    private void setAlarm(Calendar calendar, String pName, int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("person_name", pName);
        intent.putExtra("id", RequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
    }

    private void deleteAlarm(int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private int GetRequestCode(String date, String pName){
        int code = 0;
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,pName,sqLiteDatabase);
        while (cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndexOrThrow(DBStructure.ID)); //kill me
        }
        cursor.close();
        return code;
    }

    private void updateEvent(String date, String pName, String notify){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.UpdateEvent(date,pName,notify,sqLiteDatabase);
        dbOpenHelper.close();
    }
}
