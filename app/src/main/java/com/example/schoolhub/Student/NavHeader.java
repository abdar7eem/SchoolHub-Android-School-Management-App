package com.example.schoolhub.Student;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;

import org.json.JSONException;

public class NavHeader extends AppCompatActivity {

    private TextView txtName, txtEmail;
    private int studentId = 1; // Replace with actual ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header); // Set to your XML file

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        fetchProfile();
    }

    private void fetchProfile() {
        String url = "http://192.168.1.13/SchoolHub/get_profile.php?id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        String email = response.getString("email");

                        txtName.setText(name);
                        txtEmail.setText(email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                });

        Volley.newRequestQueue(this).add(request);
    }

}
