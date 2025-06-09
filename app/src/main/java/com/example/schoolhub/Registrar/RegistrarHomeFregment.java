package com.example.schoolhub.Registrar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RegistrarHomeFregment extends Fragment {


    int  RegistrarID; ; //Change it to the real id :)

    TextView tvSubjectNumber,tvStudentNumbers,tvClassesNumber,tvTeachersNumber,tvEvents,tvUserName,tvToday,tvEventThree,tvEventTwo,tvEventOne;
    LinearLayout AlertsConatiner,EventsConatiner;
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

        if (getArguments() != null) {
            RegistrarID = getArguments().getInt("registrar_id", -1);
        } else {
            RegistrarID = -1; // fallback
        }
        Log.e("RegistrarID", String.valueOf(RegistrarID));


        AlertsConatiner = view.findViewById(R.id.AlertsConatiner);
        EventsConatiner =view.findViewById(R.id.EventsConatiner);



        getNumbers();
        getEvents();
        getalert();

        return view;
    }

    private void getNumbers() {
        String url = LoginActivity.baseUrl+"get_numbers.php?user_id=" + RegistrarID;


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
    private void getEvents() {
        String url = LoginActivity.baseUrl+"get_event_board.php";


        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        if (array.length() == 0) {
                            TextView tv = new TextView(requireContext());
                            tv.setText("No upcoming events");
                            tv.setTextSize(16);
                            Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inknut_antiqua_light);
                            tv.setTypeface(typeface);
                            tv.setPadding(8, 8, 8, 8);
                            tv.setTextColor(Color.DKGRAY);

                            EventsConatiner.addView(tv); // ✅ Add to layout
                            return;
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            String title = obj.getString("event_title");
                            String date = obj.getString("event_date");
                            String time = obj.getString("event_start_time");
                            String location = obj.getString("location");

                            String display = "• " + title + " on " + date + " at " + time + " (" + location + ")";


                            TextView tv = new TextView(requireContext());
                            tv.setText(display);
                            tv.setTextSize(16);
                            Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inknut_antiqua_light);
                            tv.setTypeface(typeface);
                            tv.setPadding(8, 8, 8, 8);
                            tv.setTextColor(Color.DKGRAY);

                            EventsConatiner.addView(tv); // ✅ Add to layout
                            Log.d("UpcomingEvent", title + " - " + date + " " + time + " at " + location);
                            // You can update UI here if you have TextViews
                        }

                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Event JSON error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void getalert() {
        String url = LoginActivity.baseUrl+"schedule_alert.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        // ✅ Get the array from the object
                        if (!json.has("alerts")) {
                            TextView tv = new TextView(requireContext());
                            tv.setText("No Unsheduled Subjects");
                            tv.setTextSize(16);
                            Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inknut_antiqua_light);
                            tv.setTypeface(typeface);
                            tv.setPadding(8, 8, 8, 8);
                            tv.setTextColor(Color.DKGRAY);

                            AlertsConatiner.addView(tv);
                            return;
                        }

                        JSONArray alertsArray = json.getJSONArray("alerts");

                        if (alertsArray.length() == 0) {
                            Toast.makeText(requireContext(), "No Unscheduled Subjects", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // ✅ Find the container (correct spelling)
                        AlertsConatiner.removeAllViews();

                        for (int i = 0; i < alertsArray.length(); i++) {
                            String alertMessage = alertsArray.getString(i);

                            TextView tv = new TextView(requireContext());
                            tv.setText(alertMessage);
                            tv.setTextSize(16);
                            Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inknut_antiqua_light);
                            tv.setTypeface(typeface);
                            tv.setPadding(8, 8, 8, 8);
                            tv.setTextColor(Color.DKGRAY);

                            AlertsConatiner.addView(tv);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Alerts JSON error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load alerts", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }



}