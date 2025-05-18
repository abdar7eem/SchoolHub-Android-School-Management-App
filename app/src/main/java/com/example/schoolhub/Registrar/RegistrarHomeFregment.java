package com.example.schoolhub.Registrar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RegistrarHomeFregment extends Fragment {


    int userid=3;
    TextView tvSubjectNumber,tvStudentNumbers,tvClassesNumber,tvTeachersNumber,tvEvents,tvUserName,tvToday;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_home, container, false);
        tvSubjectNumber=view.findViewById(R.id.tvSubjectNumber);
        tvStudentNumbers=view.findViewById(R.id.tvStudentNumbers);
        tvClassesNumber=view.findViewById(R.id.tvClassesNumber);
        tvTeachersNumber=view.findViewById(R.id.tvTeachersNumber);
        tvEvents=view.findViewById(R.id.tvEvents);
        tvUserName=view.findViewById(R.id.tvUserName);
        tvToday=view.findViewById(R.id.tvToday);


        getNumbers();
        return view;
    }

    private void getNumbers() {
        String url = "http://192.168.56.1/schoolhub/get_numbers.php?user_id=" + userid;


        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        int teachers = json.getInt("teachers");
                        int students = json.getInt("students");
                        int classes = json.getInt("classes");
                        int subjects = json.getInt("subjects");
                        int events = json.getInt("events");
                        String name = json.getString("user_name");
                        String currentDate = json.getString("current_date");

                        Toast.makeText(requireContext(), name, Toast.LENGTH_SHORT).show();


                        tvTeachersNumber.setText(String.valueOf(teachers)+"    ");
                        tvStudentNumbers.setText(String.valueOf(students)+"    ");
                        tvClassesNumber.setText(String.valueOf(classes)+"    ");
                        tvSubjectNumber.setText(String.valueOf(subjects)+"    ");
                        tvEvents.setText("•Events :"+"    " + String.valueOf(events));
                        tvUserName.setText(name);
                        tvToday.setText(currentDate);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load system overview", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}