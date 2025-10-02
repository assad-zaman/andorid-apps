package com.assad.complaintapp;

import static java.sql.DriverManager.getConnection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ComplaintSingleActivity extends AppCompatActivity {

    static DBCredentials dbCredentials = new DBCredentials();
    private static final String DRIVER = dbCredentials.getDriver();
    private static final String URL = dbCredentials.getUrl();
    private static final String USERNAME = dbCredentials.getUsername();
    private static final String PASSWORD = dbCredentials.getPassword(); // Corrected this line
    private static Connection connection;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ImageView imageMenu;

    ImageView imageViewDetail;
    TextView textViewDate;
    TextView textViewEmpno;
    TextView textViewName;
    TextView textViewDesignation;
    TextView textViewUnitname;
    TextView textViewTypename;
    TextView textViewSubtypename;
    TextView textViewDetail;
    TextView textViewStatus;

    Button btnCompleted;
    Button btnCancelled;
    Button btnProcessing;

    String taskStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_complaint);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_complaint_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        textViewDate = findViewById(R.id.textViewDate);
        textViewEmpno = findViewById(R.id.textViewEmpNo);
        textViewName = findViewById(R.id.textViewName);
        textViewDesignation = findViewById(R.id.textViewDesignation);
        textViewUnitname = findViewById(R.id.textViewUnitname);
        textViewTypename = findViewById(R.id.textViewTypename);
        textViewSubtypename = findViewById(R.id.textViewSubtypename);
        textViewDetail = findViewById(R.id.textViewDetail);
        textViewStatus = findViewById(R.id.textViewStatus);

        btnCompleted = findViewById(R.id.btnCompleted);
        btnCancelled = findViewById(R.id.btnCancelled);
        btnProcessing = findViewById(R.id.btnProcessing);

        TextView name_designation;

        String emp_name;
        String emp_name_bangla ;
        String designation_bangla ;
        String card_no;
        String user_or_admin;




        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy (threadPolicy);

        drawerLayout = findViewById(R.id.drawer_complaint_detail);
        navigationView = findViewById(R.id.detail_nav_view);
        imageMenu = findViewById(R.id.imageMenu);

        toggle = new ActionBarDrawerToggle(ComplaintSingleActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.mhome) {
                    Toast.makeText(ComplaintSingleActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ComplaintSingleActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.mreply) {
                    //Toast.makeText(MainActivity.this, "Return", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ComplaintSingleActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.mdetail) {
                    Toast.makeText(ComplaintSingleActivity.this, "Detail", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ExpenseDetail.this, LoginActivity.class);
                    Intent intent = new Intent(ComplaintSingleActivity.this, ComplaintDetail.class);
                    startActivity(intent);
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



        if (user_or_admin.equals("Admin")){
            btnCompleted.setVisibility(View.VISIBLE);
            btnCancelled.setVisibility(View.VISIBLE);
            btnProcessing.setVisibility(View.VISIBLE);
        }
        else {
            btnCompleted.setVisibility(View.GONE);
            btnCancelled.setVisibility(View.GONE);
            btnProcessing.setVisibility(View.GONE);
        }



        name_designation = findViewById(R.id.text_name_designation);
        name_designation.setText(emp_name_bangla + "  - " + card_no + " \n (" + designation_bangla + ")");

        try {


            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(DRIVER);
            this.connection = getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();

            Intent intent = getIntent();

            if(intent != null) {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT " + "CREATION_DATE, \n" +
                                "CARD_NO,\n" +
                                "NAME,\n" +
                                "DESIGNATION_NAME,\n" +
                                "FACTORY_NAME,\n" +
                                "TYPE_NAME, \n" +
                                "SUBTYPE_NAME, \n" +
                                "DETAIL, \n" +
                                "COML_STATUS\n" +
                                "FROM CP_COMPLAINT_INFO_V\n" +
                                "WHERE COML_INFO_ID = "
                            + intent.getIntExtra("complaintsId", -1));
                while (resultSet.next()) {

                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

                        String formattedDate = formatter.format(resultSet.getDate(1));
                        textViewDate.setText(formattedDate);
                        textViewEmpno.setText(resultSet.getString(2));
                        textViewName.setText(resultSet.getString(3) );
                        textViewDesignation.setText(resultSet.getString(4));
                        textViewUnitname.setText(resultSet.getString(5));
                        textViewTypename.setText(resultSet.getString(6));
                        textViewSubtypename.setText(resultSet.getString(7));
                        textViewDetail.setText(resultSet.getString(8));
                        textViewStatus.setText(resultSet.getString(9));



                }
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }



    }

    public void btnCompletedClick(View view) {
        taskStatus =  btnCompleted.getText().toString();
        textViewStatus.setText(taskStatus);
        saveData();
    }
    public void btnCancelledClick(View view) {
        taskStatus =  btnCancelled.getText().toString();
        textViewStatus.setText(taskStatus);
        saveData();
    }
    public void btnProcessingClick(View view) {
        taskStatus =  btnProcessing.getText().toString();
        textViewStatus.setText(taskStatus);
        saveData();
    }

    public void saveData(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(DRIVER);
            this.connection = getConnection(URL, USERNAME, PASSWORD);

            PreparedStatement preparedStatement = null;
            Statement statement = connection.createStatement();

            String sysdateStr = "";
            Timestamp sysdate = new Timestamp(System.currentTimeMillis());

            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String forDate = sdff.format(sysdate.getTime());

            ResultSet resultSet = statement.executeQuery("SELECT SYSDATE, TO_CHAR(SYSDATE,'RRMMDDHHMISS') ||'.jpg' FILENAME FROM DUAL  ");
            while (resultSet.next()) {
                sysdate = resultSet.getTimestamp(1) ;
                sysdateStr = resultSet.getString(2);
            }

            Intent intent = getIntent();

            String insertString = "update CP_COMPLAINT_INFO" +
                    "   set COML_STATUS = ?" +
                    " where COML_INFO_ID = ?";
            preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setString(1, taskStatus);
            preparedStatement.setInt(   2, intent.getIntExtra("complaintsId", -1));
            // Execute the query
            preparedStatement.executeUpdate();

            Toast.makeText(this, "Information saved successfully!", Toast.LENGTH_LONG).show();

            connection.close();



        } catch (Exception e) {
            //textView.setText(e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }






}

