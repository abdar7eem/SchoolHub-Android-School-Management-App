package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TeacherHomeFragment extends Fragment {

    private TextView tvUserName, tvToday, tvSchedule, tvNotification, tvSubmissions;
    private MaterialCardView cardSchedule, cardNotification, cardSubmissions;

    private final int teacherId = 1;
    private final String baseUrl = "http://192.168.3.246/SchoolHub/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvToday = view.findViewById(R.id.tvToday);
        tvSchedule = view.findViewById(R.id.tvSchedule);
        tvNotification = view.findViewById(R.id.tvNotification);
        tvSubmissions = view.findViewById(R.id.tvSubmissions);

        cardSchedule = view.findViewById(R.id.cardSchedule);
        cardNotification = view.findViewById(R.id.cardNotification);
        cardSubmissions = view.findViewById(R.id.cardSubmissions);

        cardSchedule.setOnClickListener(v ->
                loadFragment(new TeacherScheduleFragment()));
        cardNotification.setOnClickListener(v ->
                loadFragment(new TeacherNotificationFragment()));
        cardSubmissions.setOnClickListener(v ->
                loadFragment(new TeacherViewSubmissionsFragment()));

        setGreeting();
        fetchDashboardData();

        return view;
    }

    private void loadFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.teacherFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void showToast(String msg) {
        android.widget.Toast.makeText(getContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void setGreeting() {
        String url = baseUrl + "get_teacher_name.php?teacher_id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        tvUserName.setText(" " + name + "!");
                        String date = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(new Date());
                        tvToday.setText(date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchDashboardData() {
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date()); // e.g., Monday
        String url = baseUrl + "get_teacher_dashboard.php?teacher_id=" + teacherId + "&day=" + currentDay;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Schedule
                        JSONArray schedule = response.getJSONArray("schedule");
                        StringBuilder sbSchedule = new StringBuilder();
                        for (int i = 0; i < schedule.length(); i++) {
                            JSONObject obj = schedule.getJSONObject(i);
                            sbSchedule.append("• ").append(obj.getString("subject"))
                                    .append(" at ").append(obj.getString("start_time"))
                                    .append(" - ").append(obj.getString("class")).append("\n");
                        }
                        tvSchedule.setText(sbSchedule.toString().trim());

                        // Notifications
                        JSONArray systemNotifs = response.getJSONArray("system_notifications");
                        StringBuilder sbSystemNotifs = new StringBuilder();
                        for (int i = 0; i < systemNotifs.length(); i++) {
                            JSONObject obj = systemNotifs.getJSONObject(i);
                            sbSystemNotifs.append("• ").append(obj.getString("title"))
                                    .append(" - ").append(obj.getString("created_at")).append("\n");
                        }
                        tvNotification.setText(sbSystemNotifs.toString().trim());

                        // Submissions
                        JSONArray subs = response.getJSONArray("submissions");
                        StringBuilder sbSub = new StringBuilder();
                        for (int i = 0; i < subs.length(); i++) {
                            JSONObject obj = subs.getJSONObject(i);
                            sbSub.append("• ").append(obj.getString("student_name"))
                                    .append(" – “").append(obj.getString("title"))
                                    .append("”  -").append(obj.getString("time_ago")).append("\n");
                        }
                        tvSubmissions.setText(sbSub.toString().trim());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

}
