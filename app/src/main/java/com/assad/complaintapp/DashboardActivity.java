package com.assad.complaintapp;

import static java.sql.DriverManager.getConnection;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class DashboardActivity extends AppCompatActivity {

    static DBCredentials dbCredentials = new DBCredentials();

    private static final String DRIVER = dbCredentials.getDriver();
    private static final String URL = dbCredentials.getUrl();
    private static final String USERNAME = dbCredentials.getUsername();
    private static final String PASSWORD = dbCredentials.getPassword(); // Corrected this line
    private static Connection connection;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button login_button;
    private CheckBox admin_check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        //usernameEditText = findViewById(R.id.username);
        //passwordEditText = findViewById(R.id.password);
        //login_button = findViewById(R.id.login_button);
        //admin_check = findViewById(R.id.admin_check);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy (threadPolicy);

        admin_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (admin_check.isChecked()) {
                    passwordEditText.setVisibility(View.VISIBLE);
                }
                else {
                    passwordEditText.setVisibility(View.GONE);
                }
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                try {
                    Class.forName(DRIVER);
                    connection = getConnection(URL, USERNAME, PASSWORD);
                    PreparedStatement preparedStatement = null;
                    Statement statement = connection.createStatement();

                    String query ="SELECT 'Y' LOGIN, NAME, NAME_BANGLA, DESIGNATION_BANGLA,CARD_NO, 'Users' USER_OR_ADMIN from HRMS.VIEW_EMPLOYEE\n" +
                            "WHERE (ACC_NO) = UPPER ('" + username.toString() + "') " +
                            "and active_status='Y'";
                    String results ="N";
                    ResultSet resultSet;
                    String emp_name ="", emp_name_bangla="", designation_bangla="", card_no="", user_or_admin="";

                    if (admin_check.isChecked()){

                        String password = passwordEditText.getText().toString();

                        query = "SELECT 'Y' LOGIN , NAME, NAME_BANGLA, DESIGNATION_BANGLA,E.CARD_NO,  'Admin' USER_OR_ADMIN " +
                                "  FROM HRMS.SC_LOGIN_USER L, HRMS.VIEW_EMPLOYEE E " +
                                " WHERE (LOGIN_NAME) = UPPER ('" + username.toString() + "') AND ENC_DEC.DECRYPT(PASS_WORD) ='" + password.toString()+"'" +
                                "   AND L.ACTIVE_STATUS='Y'" +
                                "   AND L.CARD_NO = E.CARD_NO" +
                                "   AND E.ACTIVE_STATUS='Y'";

                        results ="N";
                        resultSet = statement.executeQuery(query);
                        while (resultSet.next()) {
                            results = resultSet.getString(1);
                            emp_name = resultSet.getString(2);
                            emp_name_bangla = resultSet.getString(3);
                            designation_bangla = resultSet.getString(4);
                            card_no = resultSet.getString(5);
                            user_or_admin = resultSet.getString(6);
                        }
                    }
                    else {

                        resultSet = statement.executeQuery(query);

                        int grade_id =0;
                        while (resultSet.next()) {
                            results = resultSet.getString(1);
                            emp_name = resultSet.getString(2);
                            emp_name_bangla = resultSet.getString(3);
                            designation_bangla = resultSet.getString(4);
                            card_no = resultSet.getString(5);
                            user_or_admin = resultSet.getString(6);
                        }

                    }


                    //if (username.equals("admin") && password.equals("admin")) {
                    if (results.equals("Y")) {
                        //Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("EMP_NAME", emp_name);
                        //intent.putExtra("EMP_NAME_BANGLA", emp_name_bangla);
                        //intent.putExtra("DESIGNATION_BANGLA", designation_bangla);
                        //intent.putExtra("CARD_NO", card_no);

                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("EMP_NAME", emp_name);
                        editor.putString("EMP_NAME_BANGLA", emp_name_bangla);
                        editor.putString("DESIGNATION_BANGLA", designation_bangla);
                        editor.putString("CARD_NO", card_no);
                        editor.putString("USER_OR_ADMIN", user_or_admin);
                        editor.apply();
                        //startActivity(intent);
                        finish(); // Optional: Close the login activity
                    }
                    else {
                        //Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                    }

                }
                catch (Exception e) {
                    //Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }





}
