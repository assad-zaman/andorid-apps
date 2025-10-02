package com.assad.complaintapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static java.sql.DriverManager.getConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    static DBCredentials dbCredentials = new DBCredentials();
    private static final String DRIVER = dbCredentials.getDriver();
    private static final String URL = dbCredentials.getUrl();
    private static final String USERNAME = dbCredentials.getUsername();
    private static final String PASSWORD = dbCredentials.getPassword(); // Corrected this line
    private static Connection connection;

    Spinner spino1, spino2, spino3;

    ArrayList<ComplaintType> complaintTypeArrayList1 = new ArrayList<>();
    ArrayList<ComplaintType> complaintTypeArrayList2 = new ArrayList<>();
    ArrayList<ComplaintType> complaintTypeArrayList3 = new ArrayList<>();
    ArrayList<String> typearrayList1 = new ArrayList<>(); // Corrected initialization
    ArrayList<String> typearrayList2 = new ArrayList<>(); // Corrected initialization
    ArrayList<String> typearrayList3 = new ArrayList<>(); // Corrected initialization

    EditText editText;
    Button button;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ImageView imageMenu;

    TextView name_designation;
    String emp_name;
    String emp_name_bangla ;
    String designation_bangla ;
    String card_no;
    String user_or_admin;

    LinearLayout layout_user;
    LinearLayout layout_admin;

    ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    int counter1=0, counter2=0,counter3=0,counter4=0,counter_pending =0, counter_in_progress =0, counter_completed =0, counter_cancelled =0;

    TextView textViewProgressbar1, textViewProgressbar2, textViewProgressbar3, textViewProgressbar4;

    TextView textCancelled, textInProgress, textCompleted, textInPending;

    TextView textCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);



        spino1 = findViewById(R.id.spinner1);
        spino2 = findViewById(R.id.spinner2);
        spino3 = findViewById(R.id.spinner3);

        editText = findViewById(R.id.editText); // Moved initialization here
        button = findViewById(R.id.button); // Moved initialization here

        spinnerDataPopulator1();
        spinnerDataPopulator3();




        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imageMenu = findViewById(R.id.imageMenu);

        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.mhome) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }

                else if (itemId == R.id.mreply) {
                    Toast.makeText(MainActivity.this, "Return", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                else if (itemId == R.id.mdetail) {
                    Toast.makeText(MainActivity.this, "Detail", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ComplaintDetail.class);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        emp_name = sharedPreferences.getString("EMP_NAME", "");
        emp_name_bangla = sharedPreferences.getString("EMP_NAME_BANGLA","");
        designation_bangla = sharedPreferences.getString("DESIGNATION_BANGLA","");
        card_no = sharedPreferences.getString("CARD_NO","");
        user_or_admin = sharedPreferences.getString("USER_OR_ADMIN","");

        name_designation = findViewById(R.id.text_name_designation);
        name_designation.setText(emp_name_bangla + "  - " + card_no + " \n (" + designation_bangla + ")");
        layout_admin = findViewById(R.id.layout_admin);
        layout_user = findViewById(R.id.layout_user);
        if (user_or_admin.equals("Admin")){
            layout_user.setVisibility(View.GONE);
            layout_admin.setVisibility(View.VISIBLE);
            progressBarVisual();
        }
        else {
            layout_admin.setVisibility(View.GONE);
            layout_user.setVisibility(View.VISIBLE);

        }


        textCalendar = findViewById(R.id.textCalendar);
        Map<String, String> events = getEvents();
        CalendarView calendarView = findViewById(R.id.calendarView);

        //CalendarView calendarView = findViewById(R.id.calendarView);


        /*
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //String date = year + "-" +  (month + 1)  + "-" + dayOfMonth;
                month  = month + 1;
                String date = String.format("%04d-%02d-%02d", year, month , dayOfMonth);
                String eventText = events.get(date);
                if (eventText != null) {
                    textCalendar.setText(eventText);
                    textCalendar.setTextColor(Color.RED);
                } else {
                    textCalendar.setText("");
                }
            }
        });
        */
        /*
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(MainActivity.this, "Selected Date: " + date, Toast.LENGTH_SHORT).show();
            }
        });
        */





    }

    public Map<String, String> getEvents() {
        Map<String, String> events = new HashMap<>();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String query = "SELECT COUNT (*) COM_COUNT, TRUNC(CREATION_DATE) CREATION_DATE  FROM CP_COMPLAINT_INFO GROUP BY TRUNC(CREATION_DATE)";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String date = rs.getDate("CREATION_DATE").toString();
                String text = rs.getString("COM_COUNT");
                events.put(date, text);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    private void progressBarVisual() {
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        textViewProgressbar1 = findViewById(R.id.textViewProgressbar1);
        textViewProgressbar2 = findViewById(R.id.textViewProgressbar2);
        textViewProgressbar3 = findViewById(R.id.textViewProgressbar3);
        textViewProgressbar4 = findViewById(R.id.textViewProgressbar4);
        //progressBar.setProgress(70);

        /*
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            counter++;
            progressBar.setProgress(counter);
            textViewProgressbar.setText("" + counter );
            if (counter == 70) {
                timer.cancel();
            }
        }
        };
        timer.schedule(timerTask, 0, 100);
        */

        try {
            Class.forName(DRIVER);
            connection = getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ROUND(UNDER_PROCESS/TOTAL*100) UNDER_PROCESS_PCT,  \n" +
                    "      ROUND(CANCELLED/TOTAL*100) CANCELLED_PCT, \n" +
                    "      ROUND(SOLVED/TOTAL*100) SOLVED_PCT, \n" +
                    "      ROUND(PENDINGS/TOTAL*100) PENDINGS_PCT,\n" +
                    "      TOTAL, UNDER_PROCESS, CANCELLED, SOLVED, PENDINGS \n" +
                    "FROM (\n" +
                    "SELECT  SUM(NVL(TOTAL,0)) TOTAL, SUM(NVL(UNDER_PROCESS,0)) UNDER_PROCESS, SUM(NVL(CANCELLED,0)) CANCELLED, \n" +
                    "        SUM(NVL(SOLVED,0)) SOLVED, SUM(NVL(PENDINGS,0)) PENDINGS\n" +
                    "FROM (\n" +
                    "SELECT STATUS_CNT TOTAL, \n" +
                    "       CASE WHEN COML_STATUS ='প্রক্রিয়াধীন আছে' THEN STATUS_CNT END UNDER_PROCESS,\n" +
                    "       CASE WHEN COML_STATUS ='বাতিল করা হয়েছে' THEN STATUS_CNT END CANCELLED,\n" +
                    "       CASE WHEN COML_STATUS ='সমাধান হয়েছে' THEN STATUS_CNT END SOLVED,\n" +
                    "       CASE WHEN COML_STATUS ='অপেক্ষমান' THEN STATUS_CNT END PENDINGS\n" +
                    "     FROM(\n" +
                    "SELECT COUNT (*) STATUS_CNT, COML_STATUS \n" +
                    "  FROM CP_COMPLAINT_INFO\n" +
                    "  GROUP BY COML_STATUS )\n" +
                    "  )\n" +
                    "  )");

            while (resultSet.next()) {
                counter_pending =  resultSet.getInt(1);
                counter_cancelled =  resultSet.getInt(2);
                counter_completed =  resultSet.getInt(3);
                counter_in_progress =  resultSet.getInt(4);

            }

            final Handler handler1 = new Handler();
            final Handler handler2 = new Handler();
            final Handler handler3 = new Handler();
            final Handler handler4 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(counter1 <=counter_pending)
                    {
                        textViewProgressbar1.setText("" + counter1 + " %");
                        progressBar1.setProgress(counter1);
                        counter1++;
                        handler1.postDelayed(this,50);
                    }
                    else {
                        handler1.removeCallbacks(this);
                    }
                }

            }, 50);


            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(counter2 <=counter_cancelled)
                    {
                        textViewProgressbar2.setText("" + counter2 + " %");
                        progressBar2.setProgress(counter2);
                        counter2++;
                        handler2.postDelayed(this,50);
                    }

                    else {
                        handler2.removeCallbacks(this);
                    }
                }

            }, 50);

            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(counter3 <=counter_completed)
                    {
                        textViewProgressbar3.setText("" + counter3 + " %");
                        progressBar3.setProgress(counter3);
                        counter3++;
                        handler3.postDelayed(this,50);
                    }

                    else {
                        handler3.removeCallbacks(this);
                    }
                }

            }, 50);

            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(counter4 <=counter_in_progress)
                    {
                        textViewProgressbar4.setText("" + counter4 + " %");
                        progressBar4.setProgress(counter4);
                        counter4++;
                        handler4.postDelayed(this,50);
                    }
                    else {
                        handler4.removeCallbacks(this);
                    }
                }

            }, 50);

            connection.close();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }





    }

    public void spinnerDataPopulator1() {
        try {
            Class.forName(DRIVER);
            connection = getConnection(URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE COML_TYPE_ID = -1 \n" +
                    "UNION ALL \n" +
                    "SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE LEVEL_ = 1" +
                    "ORDER BY COML_TYPE_ID");

            //ResultSet resultSet = statement.executeQuery("SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE LEVEL_ = 1");

            while (resultSet.next()) {
                typearrayList1.add(  resultSet.getString(2));
                complaintTypeArrayList1.add(new ComplaintType(resultSet.getInt(1), resultSet.getString(2)));
            }

            //typearrayList1.add(0, "সিলেক্ট");

            ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.spinner_layout, typearrayList1);
            ad.setDropDownViewResource(R.layout.spinner_layout);
            spino1.setAdapter(ad);

            spino1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedParentItem = String.valueOf(complaintTypeArrayList1.get(position).getComplaintid());
                    spinnerDataPopulator2(selectedParentItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

            connection.close();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void spinnerDataPopulator2(String parentID) {
        try {
            Class.forName(DRIVER);
            connection = getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE PARENT_ = to_number(" + parentID + ")");
            typearrayList2.clear(); // Clear previous data
            while (resultSet.next()) {
                typearrayList2.add(resultSet.getString(2));
                complaintTypeArrayList2.add(new ComplaintType(resultSet.getInt(1), resultSet.getString(2)));
            }
            //typearrayList2.add(0, "সিলেক্ট");

            ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.spinner_layout, typearrayList2);
            ad.setDropDownViewResource(R.layout.spinner_layout);
            spino2.setAdapter(ad);
            connection.close();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void spinnerDataPopulator3() {
        try {
            Class.forName(DRIVER);
            connection = getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE COML_TYPE_ID = -1 \n" +
                    "UNION ALL \n" +
                    "SELECT COML_TYPE_ID, COML_TYPE_NAME FROM CP_COMPLAINT_TYPE WHERE LEVEL_ = 3");
            typearrayList3.clear(); // Clear previous data
            while (resultSet.next()) {
                typearrayList3.add(resultSet.getString(2));
                complaintTypeArrayList3.add(new ComplaintType(resultSet.getInt(1), resultSet.getString(2)));
            }
            //typearrayList3.add(0, "সিলেক্ট");
            ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.spinner_layout, typearrayList3); // Corrected this line
            ad.setDropDownViewResource(R.layout.spinner_layout);
            spino3.setAdapter(ad);
            connection.close();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonSaveData(View view) {
        try {
            this.connection = getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = null;
            Statement statement = connection.createStatement();
            StringBuffer stringBuffer1 = new StringBuffer();
            String sysdateStr = "";
            Timestamp sysdate = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String forDate = sdff.format(sysdate.getTime());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This is a detailed message that can be quite long.")
                    .setTitle("Alert Dialog Title");



            int selectedItemPosition1 = spino1.getSelectedItemPosition();
            int compTypeId1 = complaintTypeArrayList1.get(selectedItemPosition1).getComplaintid();
            int selectedItemPosition2 = spino2.getSelectedItemPosition();
            int compTypeId2 = complaintTypeArrayList2.get(selectedItemPosition2).getComplaintid();
            int selectedItemPosition3 = spino3.getSelectedItemPosition();
            int compTypeId3 = complaintTypeArrayList3.get(selectedItemPosition3).getComplaintid();

            if ( (compTypeId1 >0 ) && (compTypeId2 >0) && (compTypeId3 >0)){
                String insertString = "insert into CP_COMPLAINT_INFO " +
                        "( COML_GROUP_ID, COML_TYPE_ID, COML_SUBTYPE_ID, DETAIL,   CREATED_BY, CREATION_DATE, COML_STATUS) " +
                        "values (?, ?, ?,  ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertString);
                preparedStatement.setInt(1, compTypeId1);
                preparedStatement.setInt(2, compTypeId2);
                preparedStatement.setInt(3, compTypeId3);
                preparedStatement.setString(4, editText.getText().toString());
                preparedStatement.setInt(5, card_no.toString().isEmpty() ? 0 : Integer.parseInt(card_no.toString()));
                preparedStatement.setTimestamp(6, new Timestamp(sysdate.getTime()));
                preparedStatement.setString(7, "অপেক্ষমান");
                // Execute the query
                preparedStatement.executeUpdate();
                Toast.makeText(this, "তথ্য সংরক্ষণ করা হয়েছে", Toast.LENGTH_LONG).show();

                // Add an OK button
                clearAllFields();

            }
            else {
                Toast.makeText(this, "অনুগ্রহ করে সঠিক তথ্য সিলেক্ট করুন!", Toast.LENGTH_LONG).show();

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                return ;
            }
            connection.close();
        } catch (Exception e) {
            //textView.setText(e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void buttonClearData(View view) {
        clearAllFields();
    }

    private void clearAllFields() {
        // Clear text fields
        spino1.setSelection(0);
        spino2.setSelection(0);
        spino3.setSelection(0);
        editText.setText("");


    }

    public void eventCancelledClicked(View view) {
        textCancelled = findViewById(R.id.textCancelled);
        Intent intent = new Intent(MainActivity.this, ComplaintDetail.class);
        intent.putExtra("complaintStatus", textCancelled.getText().toString());
        startActivity(intent);
    }

    public void eventInProgressClicked(View view) {
        textInProgress = findViewById(R.id.textInProgress);
        Intent intent = new Intent(MainActivity.this, ComplaintDetail.class);
        intent.putExtra("complaintStatus", textInProgress.getText().toString());
        startActivity(intent);
    }

    public void eventCompletedClicked(View view) {
        textCompleted = findViewById(R.id.textCompleted);
        Intent intent = new Intent(MainActivity.this, ComplaintDetail.class);
        intent.putExtra("complaintStatus", textCompleted.getText().toString());
        startActivity(intent);
    }

    public void eventInPendingClicked(View view) {
        textInPending = findViewById(R.id.textPending);
        Intent intent = new Intent(MainActivity.this, ComplaintDetail.class);
        intent.putExtra("complaintStatus", textInPending.getText().toString());
        startActivity(intent);
    }
}

