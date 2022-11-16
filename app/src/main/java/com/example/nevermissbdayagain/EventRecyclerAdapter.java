package com.example.nevermissbdayagain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

    //Привязка данных
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Events events = arrayList.get(position);
        holder.PersonName.setText(events.getPERSON_NAME());
        holder.PersonAge.setText(events.getPERSON_AGE());
        //Ф-я кнопки удалить
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCalendarEvent(events.getPERSON_NAME(),events.getPERSON_AGE(),events.getDATE());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    //Возвращает кол-во элементов
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //Класс отдельной строчки
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView PersonName, PersonAge;
        Button Delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            PersonName = itemView.findViewById(R.id.tv_person_name_out);
            PersonAge = itemView.findViewById(R.id.tv_person_age_out);
            Delete = itemView.findViewById(R.id.btn_delete_event);
        }
    }

    //Ф-я удаления события
    private void deleteCalendarEvent(String person_name, String person_age, String date){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.DeleteEvent(person_name,person_age,date,sqLiteDatabase);
        dbOpenHelper.close();
    }
}
