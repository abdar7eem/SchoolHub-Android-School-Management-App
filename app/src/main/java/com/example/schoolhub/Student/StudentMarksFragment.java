package com.example.schoolhub.Student;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.MarkItem;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.Adapter.MarkAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentMarksFragment extends Fragment {


    ListView lstMarks;
    ArrayList<MarkItem> markList;
    MarkAdapter adapter;
    private  int studentId ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_marks, container, false);
        if (getArguments() != null) {
            studentId = getArguments().getInt("student_id", -1);
        } else {
            studentId = -1; // fallback
        }
        lstMarks = view.findViewById(R.id.lstBooks);
        markList = new ArrayList<>();
        adapter = new MarkAdapter(requireContext(), markList);
        lstMarks.setAdapter(adapter);

        loadMarks();
        return view;
    }

    private void loadMarks() {
        String url = LoginActivity.baseUrl+"get_student_marks.php?student_id="+studentId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded()) return;

                    markList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String subject = obj.getString("subject");
                            String details = obj.getString("details");
                            String grade = obj.getString("grade");
                            markList.add(new MarkItem(subject, details, grade));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_LONG).show();
                    }
                }   );
        request.setTag("SCHEDULE_REQUEST");

        queue.add(request);
    }
}