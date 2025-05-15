package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.NotificationItem;
import com.example.schoolhub.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
    private Context context;

    public NotificationAdapter(Context context, List<NotificationItem> list) {
        super(context, 0, list);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationItem notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_student_notification, parent, false);
        }

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtSender = convertView.findViewById(R.id.txtSender);
        TextView txtMessage = convertView.findViewById(R.id.txtMessage);
        TextView txtTime = convertView.findViewById(R.id.txtTime);
        View viewStatusDot = convertView.findViewById(R.id.viewStatusDot);
        View expandableContent = convertView.findViewById(R.id.expandableContent);

        txtTitle.setText(notification.title);
        txtSender.setText(notification.sender);
        txtMessage.setText(notification.message);
        txtTime.setText("Time: " + notification.time);

        // Initial visibility
        expandableContent.setVisibility(View.GONE);
        viewStatusDot.setBackgroundResource(notification.isRead ? R.drawable.dot_green : R.drawable.dot_red);

        convertView.setOnClickListener(v -> {
            // Expand/collapse content
            boolean expanded = expandableContent.getVisibility() == View.VISIBLE;
            expandableContent.setVisibility(expanded ? View.GONE : View.VISIBLE);

            // Mark as read if not already
            if (!notification.isRead) {
                markAsRead(notification.id, viewStatusDot);
                notification.isRead = true;
            }
        });

        return convertView;
    }



    private void markAsRead(int id, View dotView) {
        String url = "http://192.168.1.13/SchoolHub/mark_notification_read.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> dotView.setBackgroundResource(R.drawable.dot_green),
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }



}
