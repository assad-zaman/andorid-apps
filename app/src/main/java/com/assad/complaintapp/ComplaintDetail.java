package com.assad.complaintapp;

import static java.sql.DriverManager.getConnection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDetail extends AppCompatActivity {

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
    ComplaintAdapter complaintAdapter;

    TextView name_designation;

    String emp_name;
    String emp_name_bangla ;
    String designation_bangla ;
    String card_no;
    String user_or_admin;

    String complaintStatus = ""; ;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complaint_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_expense_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy (threadPolicy);

        drawerLayout = findViewById(R.id.drawer_expense_detail);
        navigationView = findViewById(R.id.detail_nav_view);
        imageMenu = findViewById(R.id.imageMenu);

        toggle = new ActionBarDrawerToggle(ComplaintDetail.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //spinnerStatus = findViewById(R.id.textViewStatus);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.mhome) {
                    Toast.makeText(ComplaintDetail.this, "Home", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ComplaintDetail.this, MainActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.mreply) {
                    //Toast.makeText(MainActivity.this, "Return", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ComplaintDetail.this, LoginActivity.class);
                    startActivity(intent);
                }
                else if (itemId == R.id.mdetail) {
                    Toast.makeText(ComplaintDetail.this, "Detail", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ExpenseDetail.this, LoginActivity.class);
                    //Intent intent = new Intent(ExpenseDetail.this, LoginActivity.class);
                    //startActivity(intent);
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

        Intent intent = getIntent();
        complaintStatus = intent.getStringExtra("complaintStatus");


        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        emp_name = sharedPreferences.getString("EMP_NAME", "");
        emp_name_bangla = sharedPreferences.getString("EMP_NAME_BANGLA","");
        designation_bangla = sharedPreferences.getString("DESIGNATION_BANGLA","");
        card_no = sharedPreferences.getString("CARD_NO","");
        user_or_admin = sharedPreferences.getString("USER_OR_ADMIN","");

        name_designation = findViewById(R.id.text_name_designation);
        name_designation.setText(emp_name_bangla + "  - " + card_no + " \n (" + designation_bangla + ")");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        List<Complaints> items = new ArrayList<Complaints>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ComplaintAdapter(items, getApplicationContext()));

        complaintAdapter = new ComplaintAdapter(items, getApplicationContext());
        recyclerView.setAdapter(complaintAdapter);

        loadData(items);
        complaintAdapter.setOnItemClickListener(new ComplaintAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Complaints clickedComplaints = items.get(position);
                int complaintsId = clickedComplaints.getId(); // Assuming you have an ID field in your Expenses class

                Intent intent = new Intent(ComplaintDetail.this, ComplaintSingleActivity.class);
                intent.putExtra("complaintsId", complaintsId);
                startActivity(intent);

        }
    });

}
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the dataset when returning to this activity
        refreshDataset();
    }

    private void refreshDataset() {
        List<Complaints> items = new ArrayList<>();
        loadData(items);
        complaintAdapter.updateData(items);
    }

    private void loadData(List<Complaints> items) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(DRIVER);
            this.connection = getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(
                    "SELECT COML_INFO_ID," +
                            "         CREATION_DATE," +
                            "         FACTORY_NAME," +
                            "         GROUP_NAME," +
                            "         TYPE_NAME," +
                            "         SUBTYPE_NAME," +
                            "         DETAIL," +
                            "         REMARKS," +
                            "         CARD_NO," +
                            "         NAME," +
                            "         DESIGNATION_NAME," +
                            "         COML_STATUS" +
                            "    FROM CP_COMPLAINT_INFO_V" +
                            "   WHERE CARD_NO = NVL ( CASE WHEN '"+ user_or_admin +"' = 'Admin' THEN NULL ELSE '"+ card_no +"' END, CARD_NO)" +
                            "     AND COML_STATUS = NVL ( CASE WHEN '"+ complaintStatus +"' = 'null' THEN NULL ELSE '"+ complaintStatus +"' END , COML_STATUS)" +
                            "    ORDER BY CREATION_DATE DESC ");
            while (resultSet.next()) {
                items.add(new Complaints(   resultSet.getInt(1),
                        resultSet.getDate(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9),
                        resultSet.getString(10),
                        resultSet.getString(11),
                        resultSet.getString(12)
                ));

            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}