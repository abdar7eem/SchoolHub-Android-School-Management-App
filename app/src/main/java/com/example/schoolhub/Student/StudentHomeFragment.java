package com.example.schoolhub.Student;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentHomeFragment extends Fragment {

    private TextView txtGreeting, txtUpcoming, txtEvent, txtSnapshot;
    private final int studentId = 1; // replace with actual logged-in student ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        txtGreeting = view.findViewById(R.id.txtGreeting);
        txtUpcoming = view.findViewById(R.id.txtUpcoming);
        txtEvent = view.findViewById(R.id.txtEvent);
        txtSnapshot = view.findViewById(R.id.txtSnapshot);

        setGreeting();
        fetchDashboardData();

        return view;
    }




    private void setGreeting() {
        String url = "http://192.168.56.1/schoolhub/get_student_name.php?user_id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        String date = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(new Date());
                        txtGreeting.setText("Hello " + name + "!\nToday is " + date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void fetchDashboardData() {
        String url = "http://192.168.56.1/schoolhub/get_dashboard_data.php?student_id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // What's Coming Up
                        JSONArray schedule = response.getJSONArray("schedule");
                        StringBuilder sbSchedule = new StringBuilder();
                        for (int i = 0; i < schedule.length(); i++) {
                            JSONObject obj = schedule.getJSONObject(i);
                            sbSchedule.append("• ").append(obj.getString("subject"))
                                    .append(" at ").append(obj.getString("start_time")).append("\n");
                        }
                        txtUpcoming.setText(sbSchedule.toString().trim());

                        // Featured Event
                        JSONObject event = response.getJSONObject("event");
                        txtEvent.setText("• " + event.getString("title") + " - " + event.getString("date") + " at " + event.getString("location"));

                        // Snapshot
                        JSONObject snapshot = response.getJSONObject("snapshot");
                        txtSnapshot.setText("• Math Avg: " + snapshot.getString("math_avg") +
                                " | Attendance: " + snapshot.getString("attendance") + "%");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
