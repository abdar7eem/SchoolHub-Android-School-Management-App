package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Subject;
import com.example.schoolhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentLeaderboardFragment extends Fragment {

    private Spinner spnSubject;
    private List<Subject> subjectList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_student_leaderboard, container, false);

        spnSubject = root.findViewById(R.id.spnSubject);

        // Initialize and load subjects into spinner
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubject.setAdapter(spinnerAdapter);

        loadSubjects();

        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int subjectId = subjectList.get(position).getId();
                fetchTop3(subjectId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return root;
    }

    private void loadSubjects() {
        String url = "http://192.168.3.246//SchoolHub/get_subjects.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        subjectList.clear();
                        spinnerAdapter.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");

                            subjectList.add(new Subject(id, name));
                            spinnerAdapter.add(name);
                        }

                        spinnerAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // handle error
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchTop3(int subjectId) {
        String url = "http://192.168.3.246/SchoolHub/get_top3_by_subject.php?subject_id=" + subjectId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < 3; i++) {
                            JSONObject obj = i < response.length() ? response.getJSONObject(i) : null;
                            String name = obj != null ? obj.getString("name") : "N/A";
                            String grade = obj != null ? "Grade: " + obj.getInt("obtained") + "/" + obj.getInt("total") : "Grade: -";

                            updateCard(i + 1, name, grade);  // 1: gold, 2: silver, 3: bronze
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Show error message
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void updateCard(int rank, String name, String grade) {
        int nameId = 0, gradeId = 0;

        switch(rank) {
            case 1:
                nameId = R.id.card1_name;
                gradeId = R.id.card1_grade;
                break;
            case 2:
                nameId = R.id.card2_name;
                gradeId = R.id.card2_grade;
                break;
            case 3:
                nameId = R.id.card3_name;
                gradeId = R.id.card3_grade;
                break;
        }

        ((TextView) root.findViewById(nameId)).setText(name);
        ((TextView) root.findViewById(gradeId)).setText(grade);
    }
}
