package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.schoolhub.Model.MarkItem;
import com.example.schoolhub.R;

import java.util.ArrayList;

public class MarkAdapter extends ArrayAdapter<MarkItem> {

    public MarkAdapter(Context context, ArrayList<MarkItem> marks) {
        super(context, 0, marks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MarkItem mark = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_student_marks, parent, false);
        }

        TextView txtSubject = convertView.findViewById(R.id.txtSubject);
        TextView txtDetails = convertView.findViewById(R.id.txtDetails);
        TextView txtGrade = convertView.findViewById(R.id.txtGrade);

        txtSubject.setText("Subject: " + mark.getSubjectName());
        txtDetails.setText(mark.getDetails());
        txtGrade.setText("Grade: " + mark.getGrade());

        return convertView;
    }
}