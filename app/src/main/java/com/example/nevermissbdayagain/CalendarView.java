package com.example.nevermissbdayagain;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarView extends LinearLayout {

    ImageButton btn_next;
    ImageButton btn_prev;
    TextView tv_currentDate;
    GridView gv_gridview;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    private static final int MAX_CALENDAR_DAYS = 49;

    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

    AlertDialog alertDialog;
    MyGridAdapter myGridAdapter;



    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetUpCalendar();

        btn_prev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH,-1);
                SetUpCalendar();
            }
        });

        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH,1);
                SetUpCalendar();
            }
        });

        gv_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events, null);
                RecyclerView recyclerView = showView.findViewById(R.id.rv_events);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),CollectEventByDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();


                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        SetUpCalendar();
                    }
                });
            }
        });

        gv_gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_event_layout,null);
                Button btn_add_event = addView.findViewById(R.id.btn_add_event);
                EditText et_person_name = addView.findViewById(R.id.tv_person_name);
                EditText et_person_age = addView.findViewById(R.id.tv_person_age);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(30);
                et_person_name.setFilters(filterArray);

                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));


                btn_add_event.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String PersonName = et_person_name.getText().toString();

                        if (PersonName != null && !PersonName.trim().isEmpty()){
                            if (et_person_age.getText().toString() != null && !et_person_age.getText().toString().trim().isEmpty()){
                                int PersonAge = Integer.parseInt(et_person_age.getText().toString());
                                if (PersonAge > 0 && PersonAge <= 150){
                                    SaveEvent(et_person_name.getText().toString(), et_person_age.getText().toString(),date,month,year);
                                    SetUpCalendar();
                                    alertDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(context, "Возраст слишком мал или велик", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(context, "Поле возраста пустое", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(context, "Поле имени пустое", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    private ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();

        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date,sqLiteDatabase);
        while (cursor.moveToNext()){
            String pName = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.PERSON_NAME));
            String pAge = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.PERSON_AGE));
            String Date = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.YEAR));
            Events events = new Events(pName, pAge, Date, month, year);
            arrayList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
        return arrayList;
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void SaveEvent(String person_name, String person_age, String date, String month, String year){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(person_name, person_age, date, month, year, database);
        dbOpenHelper.close();
        Toast.makeText(context, "ДР сохранено", Toast.LENGTH_SHORT).show();

    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout,this);
        btn_next = view.findViewById(R.id.btn_next);
        btn_prev = view.findViewById(R.id.btn_prev);
        tv_currentDate = view.findViewById(R.id.tv_current_date);
        gv_gridview = view.findViewById(R.id.gridview);
    }

    private void SetUpCalendar(){
        switch (calendar.get(Calendar.MONTH)) {
            case  (1):
                tv_currentDate.setText("Февраль " + calendar.get(Calendar.YEAR));
                break;
            case  (2):
                tv_currentDate.setText("Март " + calendar.get(Calendar.YEAR));
                break;
            case  (3):
                tv_currentDate.setText("Апрель " + calendar.get(Calendar.YEAR));
                break;
            case  (4):
                tv_currentDate.setText("Май " + calendar.get(Calendar.YEAR));
                break;
            case  (5):
                tv_currentDate.setText("Июнь " + calendar.get(Calendar.YEAR));
                break;
            case  (6):
                tv_currentDate.setText("Июль " + calendar.get(Calendar.YEAR));
                break;
            case  (7):
                tv_currentDate.setText("Август " + calendar.get(Calendar.YEAR));
                break;
            case  (8):
                tv_currentDate.setText("Сентябрь " + calendar.get(Calendar.YEAR));
                break;
            case  (9):
                tv_currentDate.setText("Октябрь " + calendar.get(Calendar.YEAR));
                break;
            case  (10):
                tv_currentDate.setText("Ноябрь " + calendar.get(Calendar.YEAR));
                break;
            case  (11):
                tv_currentDate.setText("Декабрь " + calendar.get(Calendar.YEAR));
                break;
            case  (0):
                tv_currentDate.setText("Январь " + calendar.get(Calendar.YEAR));
                break;
        }
        dates.clear();
        Calendar monthCalender = (Calendar) calendar.clone();
        monthCalender.set(Calendar.DAY_OF_MONTH,1);
        int FirstDayOfMonth = monthCalender.get(Calendar.DAY_OF_WEEK)+5;
        monthCalender.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);
        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalender.getTime());
            monthCalender.add(Calendar.DAY_OF_MONTH,1);
        }

        myGridAdapter = new MyGridAdapter(context,dates,calendar,eventsList);
        gv_gridview.setAdapter(myGridAdapter);
    }

    private void CollectEventsPerMonth(String Month, String Year){
        eventsList.clear();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsMonth(Month, Year, database);

        while (cursor.moveToNext()){
            String pName = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.PERSON_NAME));
            String pAge = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.PERSON_AGE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.YEAR));
            Events events = new Events(pName, pAge, date, month, year);
            eventsList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
    }
}
