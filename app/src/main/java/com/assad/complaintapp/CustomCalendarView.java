package com.assad.complaintapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomCalendarView extends CalendarView {

    private HashMap<Date, String> dateTextMap;

    int xPosition, yPosition;

    CalendarView calendarView = findViewById(R.id.calendarView);

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dateTextMap = new HashMap<>();
        // ... fetch data from database and populate dateTextMap ...
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        for (Map.Entry<Date, String> entry : dateTextMap.entrySet()) {
            Date date = entry.getKey();
            String text = entry.getValue();

            // ... calculate position of date on calendar grid ...

            calendarView.setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float touchX = event.getX();
                    float touchY = event.getY();

                    // Assuming 7 columns for days of the week
                    int calendarWidth = calendarView.getWidth();
                    int cellWidth = calendarWidth / 7;

                    int column = (int) (touchX / cellWidth);

                    // Assume the calendar has a fixed grid height (you may need to adjust)
                    int calendarHeight = calendarView.getHeight();
                    int numberOfWeeks = 6;  // Typically 6 weeks displayed in calendar
                    int cellHeight = calendarHeight / numberOfWeeks;

                    int row = (int) (touchY / cellHeight);

                    Log.d("CalendarTouch", "Touched Column: " + column + ", Row: " + row);
                    calculateDateFromGrid(column, row);
                }
                return true;
            });

            TextView textView = new TextView(getContext());
            textView.setText(text);
            // ... set text appearance and other properties ...

            textView.setX(xPosition);
            textView.setY(yPosition);
            textView.draw(canvas);
        }
    }

    private void calculateDateFromGrid(int column, int row) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarView.getDate());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-based index for Sunday
        int firstRowOffset = (firstDayOfWeek + 6) % 7; // Align Sunday as 0

        int gridPosition = row * 7 + column - firstRowOffset;

        if (gridPosition >= 0 && gridPosition < calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.add(Calendar.DAY_OF_MONTH, gridPosition);
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            Log.d("SelectedDate", "Calculated Date: " + selectedDate);
        } else {
            Log.d("SelectedDate", "Invalid touch outside month");
        }
    }

}