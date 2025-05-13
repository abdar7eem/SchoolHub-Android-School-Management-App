package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.AssignmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentsFragment extends Fragment {

    private ListView lstBooks;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;

    private boolean isPending = false, isSubmitted = false, isGraded = false;
    Button btnPending, btnSubmitted, btnGraded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignments, container, false);
        btnPending = view.findViewById(R.id.btnPending);
        btnSubmitted = view.findViewById(R.id.btnSubmitted);
        btnGraded = view.findViewById(R.id.btnGraded);
        lstBooks = view.findViewById(R.id.lstBooks);

        // Sample data (you'll later load from MySQL)
        assignmentList = new ArrayList<>();
        assignmentList.add(new Assignment("Algebra HW4", "Math", "Mr. Khaled", "May 8, 2025", "Pending"));
        assignmentList.add(new Assignment("Science Quiz", "Science", "Ms. Huda", "May 10, 2025", "Submitted"));
        assignmentList.add(new Assignment("History Essay", "History", "Mr. Sami", "May 15, 2025", "Graded"));
        assignmentList.add(new Assignment("Algebra HW4", "Math", "Mr. Khaled", "May 8, 2025", "Pending"));
        assignmentList.add(new Assignment("Science Quiz", "Science", "Ms. Huda", "May 10, 2025", "Submitted"));
        assignmentList.add(new Assignment("History Essay", "History", "Mr. Sami", "May 15, 2025", "Graded"));


        // 2. Set click listeners for each button
        btnPending.setOnClickListener(v -> {

            if (!isPending) {
                // If not filtered, filter the list
                filterListByStatus("Pending");
                isPending = true;
                updateButtonColors(btnPending);
            } else {
                // If already filtered, reset to original list
                adapter = new AssignmentAdapter(requireContext(), assignmentList);
                lstBooks.setAdapter(adapter);
                btnPending.setBackgroundTintList( ContextCompat.getColorStateList(requireContext(), R.color.white));
                btnPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                isPending = false;
            }
        });
        btnSubmitted.setOnClickListener(v ->
        {
            if (!isSubmitted) {
                // If not filtered, filter the list
                filterListByStatus("Submitted");
                isSubmitted = true;
                updateButtonColors(btnSubmitted);

            } else {
                // If already filtered, reset to original list
                adapter = new AssignmentAdapter(requireContext(), assignmentList);
                lstBooks.setAdapter(adapter);
                btnSubmitted.setBackgroundTintList( ContextCompat.getColorStateList(requireContext(), R.color.white));
                btnSubmitted.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                isSubmitted = false;
            }
        });
        btnGraded.setOnClickListener(v -> {

            if (!isGraded) {
                // If not filtered, filter the list
                filterListByStatus("Graded");
                isGraded = true;
                updateButtonColors(btnGraded);

            } else {
                // If already filtered, reset to original list
                adapter = new AssignmentAdapter(requireContext(), assignmentList);
                lstBooks.setAdapter(adapter);
                btnGraded.setBackgroundTintList( ContextCompat.getColorStateList(requireContext(), R.color.white));
                btnGraded.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                isGraded = false;
            }
        });


        adapter = new AssignmentAdapter(requireContext(), assignmentList);
        lstBooks.setAdapter(adapter);

        return view;
    }


    private void filterListByStatus(String status) {
        List<Assignment> filteredList = new ArrayList<>();
        for (Assignment assignment : assignmentList) {
            if (assignment.getStatus().equalsIgnoreCase(status)) {
                filteredList.add(assignment);
            }
        }

        // Update adapter with filtered list
        adapter = new AssignmentAdapter(requireContext(), filteredList);
        lstBooks.setAdapter(adapter);
    }

    private void updateButtonColors(Button selectedButton) {
        btnPending.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btnPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        btnSubmitted.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btnSubmitted.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        btnGraded.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        btnGraded.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_red));
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
    }


}
