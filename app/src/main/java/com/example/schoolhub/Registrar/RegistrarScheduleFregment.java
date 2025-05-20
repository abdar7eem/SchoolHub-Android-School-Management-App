package com.example.schoolhub.Registrar;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.InfoClass;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RegistrarScheduleFregment extends Fragment {

    Spinner spnEndTime,spnStartTime,spnSubject,spnClasses;

    Button btnAddSchedule,btnCheckConflict;
    TextView txtSelectDays;

    private final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};

    String[] timeSlots = {"8:00(first)", "8:45(secound)", "9:30(third)", "10:40(Fourth)", "11:25(Fifth)", "12:10(Sixth)","12:55(Seventh)"};


    // Boolean array for initial selected items
    boolean[] selectedDays;
    List<String> selectedDayList = new ArrayList<>();
    // List to store selected day indices
    private final List<Integer> selectedDayIndices = new ArrayList<>();

    private TableLayout tableSchedule;

    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private final String[] lessonNumbers = {"1", "2", "3", "4", "5", "6", "7"};

    InfoClass selectedClass;
    private Map<String, Map<String, String>> scheduleData = new HashMap<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_schedule, container, false);
        spnStartTime=view.findViewById(R.id.spnStartTime);
        spnSubject=view.findViewById(R.id.spnSubject);
        spnClasses=view.findViewById(R.id.spnClasses);
        btnAddSchedule=view.findViewById(R.id.btnAddSchedule);
        btnCheckConflict=view.findViewById(R.id.btnCheckConflict);
        txtSelectDays = view.findViewById(R.id.txtSelectedDays);
        btnAddSchedule.setEnabled(false); // initially disabled
        selectedDays = new boolean[daysOfWeek.length]; // must match array size


        txtSelectDays.setOnClickListener(v -> showDaysMultiDialog());

        btnCheckConflict.setOnClickListener(v -> checkScheduleConflict());
        btnAddSchedule.setOnClickListener(v -> addSchedule());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                timeSlots
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStartTime.setAdapter(adapter);

        tableSchedule = view.findViewById(R.id.tableSchedule);


        loadClasses();



        return view;
    }

    private void loadClasses() {
        String url = "http://192.168.56.1/schoolhub/get_classes.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<InfoClass> Classes = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");

                            Classes.add(new InfoClass(id,name));
                        }

                        ArrayAdapter<InfoClass> adapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_spinner_item,
                                Classes
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnClasses.setAdapter(adapter);
                        spnClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                 selectedClass = (InfoClass) parent.getItemAtPosition(position);
                                loadScheduleForClass(selectedClass.getId());
                                loadSubjects(selectedClass.getId());
                                Log.e("SchedulePost", "Days: " );


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Optional: Handle case when nothing is selected
                            }
                        });

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void showDaysMultiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Days");

        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedDayList.contains(daysOfWeek[which])) {
                    selectedDayList.add(daysOfWeek[which]);
                }
            } else {
                selectedDayList.remove(daysOfWeek[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selectedDays.length; i++) {
                if (selectedDays[i]) {
                    sb.append(daysOfWeek[i]).append(", ");
                }
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2); // remove trailing comma
            txtSelectDays.setText(sb.toString());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void loadSubjects(int classId) {
        String url = "http://192.168.56.1/schoolhub/get_subjects_class.php?class_id=" + classId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<SubjectInfo> subjects = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("subject_id");
                            String name = obj.getString("subject_name");
                            subjects.add(new SubjectInfo(id, name));
                        }

                        ArrayAdapter<SubjectInfo> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                subjects
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnSubject.setAdapter(adapter);

                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void checkScheduleConflict() {
        InfoClass selectedClass = (InfoClass) spnClasses.getSelectedItem();
        SubjectInfo selectedSubject = (SubjectInfo) spnSubject.getSelectedItem();
        String startTime = spnStartTime.getSelectedItem().toString();
        String day = txtSelectDays.getText().toString().trim();

        if (selectedClass == null || selectedSubject == null || day.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.56.1/schoolhub/check_conflict.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if ("conflict".equals(status)) {
                            btnCheckConflict.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            btnCheckConflict.setText("Conflict Found!");
                            btnAddSchedule.setEnabled(false);
                        } else {
                            btnCheckConflict.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            btnCheckConflict.setText("No Conflict");
                            btnAddSchedule.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(selectedClass.getId()));
                params.put("day_of_week", day);
                params.put("start_time", startTime);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void addSchedule() {
        InfoClass selectedClass = (InfoClass) spnClasses.getSelectedItem();
        SubjectInfo selectedSubject = (SubjectInfo) spnSubject.getSelectedItem();
        String startTime = spnStartTime.getSelectedItem().toString().split("\\(")[0]; // clean time like "8:00"

        if (selectedClass == null || selectedSubject == null || selectedDayList.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.56.1/schoolhub/add_schedule.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("success".equals(jsonObject.getString("status"))) {
                            int insertedCount = jsonObject.optInt("inserted", selectedDayList.size());
                            Toast.makeText(getContext(), "Schedule added for " + insertedCount + " day(s)", Toast.LENGTH_SHORT).show();
                            loadScheduleForClass(selectedClass.getId());

                        } else {
                            Toast.makeText(getContext(), "Insert failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Response parsing failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    String message = "Unknown network error";
                    if (error.networkResponse != null) {
                        message = "HTTP Code: " + error.networkResponse.statusCode;
                    } else if (error.getCause() != null) {
                        message = error.getCause().getMessage();
                    }

                    Toast.makeText(getContext(), "Network error: " + message, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(selectedClass.getId()));
                params.put("subject_id", String.valueOf(selectedSubject.getId()));
                params.put("start_time", startTime);

                // Join selected days into comma-separated string
                String joinedDays = TextUtils.join(",", selectedDayList);
                params.put("day_of_week", joinedDays);

                // Debug logs
                Log.d("SchedulePost", "Class ID: " + selectedClass.getId());
                Log.d("SchedulePost", "Subject ID: " + selectedSubject.getId());
                Log.d("SchedulePost", "Start Time: " + startTime);
                Log.d("SchedulePost", "Days: " + joinedDays);

                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void buildScheduleTable() {
        tableSchedule.removeAllViews();

        // Header row
        TableRow headerRow = new TableRow(requireContext());
        headerRow.addView(createHeaderCell("Lessons")); // first empty corner

        for (String lesson : lessonNumbers) {
            headerRow.addView(createHeaderCell(lesson));
        }
        tableSchedule.addView(headerRow);

        // Day rows
        for (String day : days) {
            TableRow row = new TableRow(requireContext());
            row.addView(createDayCell(day));

            for (String lesson : lessonNumbers) {
                String subject = "";
                if (scheduleData.containsKey(day) && scheduleData.get(day).containsKey(lesson)) {
                    subject = scheduleData.get(day).get(lesson);
                }
                row.addView(createSubjectCell(subject));
            }
            tableSchedule.addView(row);
        }
    }
    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setBackgroundColor(Color.parseColor("#970D0D"));
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        return tv;
    }

    private TextView createDayCell(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setBackgroundColor(Color.parseColor("#970D0D"));
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        return tv;
    }

    private TextView createSubjectCell(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(12, 8, 12, 8);
        tv.setBackgroundColor(Color.WHITE);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundResource(R.drawable.cell_border); // ✅ border added

        return tv;
    }

//    private int getSubjectColor(String subject) {
//        switch (subject.toLowerCase()) {
//            case "math": return Color.BLUE;
//            case "english": return Color.RED;
//            case "arabic": return Color.CYAN;
//            case "sport": return Color.GREEN;
//            case "history": return Color.MAGENTA;
//            case "science": return Color.rgb(128, 0, 128); // purple
//            case "art": return Color.rgb(255, 140, 0); // orange
//            default: return Color.DKGRAY;
//        }
//    }
    private void loadScheduleForClass(int classId) {
        String url = "http://192.168.56.1/schoolhub/get_class_schedule.php?class_id=" + classId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray scheduleArray = json.getJSONArray("schedule");
                        Log.d("Schedule Post", "1");

                        scheduleData.clear();

                        for (int i = 0; i < scheduleArray.length(); i++) {
                            JSONObject obj = scheduleArray.getJSONObject(i);
                            String day = obj.getString("day_of_week"); // e.g., "Monday"
                            String time = obj.getString("start_time"); // e.g., "8:00"
                            String subject = obj.getString("name");

                            time=formatTime(time);
                            Log.d("Schedule Post", subject);
                            Log.d("Schedule time", time);
                            Log.d("Schedule day", day);


                            String lesson = convertTimeToLessonNumber(time);
                            Log.d("Schedule Post", lesson);

                            if (!scheduleData.containsKey(day)) {
                                scheduleData.put(day, new HashMap<>());
                                Log.d("Schedule Post", "4");

                            }
                            scheduleData.get(day).put(lesson, subject);
                            Log.d("Schedule Post", "5");

                        }

                        buildScheduleTable();
                        Log.d("Schedule Post", "6");

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Unknown network error";
                    if (error.networkResponse != null) {
                        message = "HTTP Code: " + error.networkResponse.statusCode;
                    } else if (error.getCause() != null) {
                        message = error.getCause().getMessage();
                    }

                    Toast.makeText(getContext(), "Network error: " + message, Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private String convertTimeToLessonNumber(String time) {
        switch (time) {
            case "8:00": return "1";
            case "8:45": return "2";
            case "9:30": return "3";
            case "10:40": return "4";
            case "11:25": return "5";
            case "12:10": return "6";
            case "12:55": return "7";
            default: return "0";
        }
    }
    private String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("H:mm", Locale.getDefault());
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return time; // fallback to original if error
        }
    }


}