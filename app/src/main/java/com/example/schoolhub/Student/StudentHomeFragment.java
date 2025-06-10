package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentHomeFragment extends Fragment {

    private TextView txtGreeting, txtUpcoming, txtEvent, txtSnapshot;
    private  int studentId ;
    private int userId;


    private LinearLayout llUpcoming, llEvent, llSnapshot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);
        if (getArguments() != null) {
            studentId = getArguments().getInt("student_id", -1);
            userId = getArguments().getInt("user_id", -1);

        } else {
            studentId = -1;
        }
        txtGreeting = view.findViewById(R.id.txtGreeting);
        txtUpcoming = view.findViewById(R.id.txtUpcoming);
        txtEvent = view.findViewById(R.id.txtEvent);
        txtSnapshot = view.findViewById(R.id.txtSnapshot);

        llUpcoming = view.findViewById(R.id.llUpcoming);
        llEvent = view.findViewById(R.id.llFeaturedEvent);
        llSnapshot = view.findViewById(R.id.llClassSnapshot);

        setGreeting();
        fetchDashboardData();

        llUpcoming.setOnClickListener(v -> openFragment(new StudentScheduleFragment()));
        llEvent.setOnClickListener(v -> openFragment(new StudentEventFragment()));
        llSnapshot.setOnClickListener(v -> openFragment(new StudentMarksFragment()));

        return view;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("student_id", studentId);
        fragment.setArguments(bundle);

        transaction.replace(R.id.studentFragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setGreeting() {
        String url = LoginActivity.baseUrl + "get_student_name.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded()) return;

                    try {
                        String name = response.getString("name");
                        String date = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(new Date());
                        txtGreeting.setText("Hello " + name + "!\nToday is " + date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_LONG).show();
                    }
                }
        );
        request.setTag("SCHEDULE_REQUEST"); // Unique tag

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchDashboardData() {
        String url = LoginActivity.baseUrl+"get_dashboard_data.php?student_id=" + studentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (!isAdded()) return;

                        if (response.has("schedule")) {
                            JSONArray schedule = response.getJSONArray("schedule");
                            StringBuilder sbSchedule = new StringBuilder();
                            for (int i = 0; i < schedule.length(); i++) {
                                JSONObject obj = schedule.getJSONObject(i);
                                sbSchedule.append("• ").append(obj.getString("subject"))
                                        .append(" at ").append(obj.getString("start_time")).append("\n");
                            }
                            txtUpcoming.setText(sbSchedule.toString().trim());
                        } else {
                            txtUpcoming.setText("No upcoming schedule.");
                        }

                        if (response.has("event")) {
                            JSONObject event = response.getJSONObject("event");
                            txtEvent.setText("• " + event.getString("title") + " - " +
                                    event.getString("date") + " at " +
                                    event.optString("location", "TBA"));
                        } else {
                            txtEvent.setText("No event today.");
                        }

                        if (response.has("snapshot")) {
                            JSONObject snapshot = response.getJSONObject("snapshot");
                            txtSnapshot.setText("• Math Avg: " + snapshot.getString("math_avg") +
                                    " | Attendance: " + snapshot.getString("attendance") + "%");
                        } else {
                            txtSnapshot.setText("No snapshot available.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_LONG).show();
                    }
                }        );
        request.setTag("SCHEDULE_REQUEST"); // Unique tag

        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onStop() {
        super.onStop();
        Volley.newRequestQueue(requireContext()).cancelAll("SCHEDULE_REQUEST");
    }

}
