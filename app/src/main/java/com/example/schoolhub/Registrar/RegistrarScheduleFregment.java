package com.example.schoolhub.Registrar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;


public class RegistrarScheduleFregment extends Fragment {

    Spinner spnEndTime,spnStartTime,spnSubject,spnClasses;

    Button btnAddSchedule;
    TextView txtSelectDays;

    private final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};

    String[] timeSlots = {"8:00", "8:45", "9:30", "11:00", "11:45", "12:40"};


    // Boolean array for initial selected items
    private final boolean[] selectedDays = new boolean[daysOfWeek.length];
    // List to store selected day indices
    private final List<Integer> selectedDayIndices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_schedule, container, false);
        spnEndTime=view.findViewById(R.id.spnEndTime);
        spnStartTime=view.findViewById(R.id.spnStartTime);
        spnSubject=view.findViewById(R.id.spnSubject);
        spnClasses=view.findViewById(R.id.spnClasses);
        btnAddSchedule=view.findViewById(R.id.btnAddSchedule);
         txtSelectDays = view.findViewById(R.id.txtSelectedDays);
        txtSelectDays.setOnClickListener(v -> showDaysMultiChoiceDialog());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                timeSlots
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStartTime.setAdapter(adapter);

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
                                InfoClass selectedClass = (InfoClass) parent.getItemAtPosition(position);
                                loadSubjects(selectedClass.getId());
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

    private void showDaysMultiChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Days");

        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                // Add the selected day index if not already present
                if (!selectedDayIndices.contains(which)) {
                    selectedDayIndices.add(which);
                }
            } else {
                // Remove the unselected day index
                selectedDayIndices.remove(Integer.valueOf(which));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Build the selected days string
            StringBuilder selectedDaysString = new StringBuilder();
            for (int i = 0; i < selectedDayIndices.size(); i++) {
                selectedDaysString.append(daysOfWeek[selectedDayIndices.get(i)]);
                if (i != selectedDayIndices.size() - 1) {
                    selectedDaysString.append(", ");
                }
            }
            // Set the selected days to the TextView
            txtSelectDays.setText(selectedDaysString.toString());
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

}