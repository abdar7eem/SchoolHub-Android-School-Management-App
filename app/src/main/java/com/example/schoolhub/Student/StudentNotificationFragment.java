package com.example.schoolhub.Student;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.NotificationItem;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.NotificationAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentNotificationFragment extends Fragment {

    private ListView lstBooks;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    private final int studentId = 1; // Replace with actual logged-in student id
    private final String CHANNEL_ID = "schoolhub_notifications";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_notification, container, false);

        lstBooks = view.findViewById(R.id.lstBooks);
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationList);
        lstBooks.setAdapter(adapter);



        RadioGroup rgFilter = view.findViewById(R.id.rgFilter);
        rgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAscending) {
                fetchNotifications("asc");
            } else if (checkedId == R.id.rbDescending) {
                fetchNotifications("desc");
            } else if (checkedId == R.id.rbUnread) {
                fetchNotifications("unread");
            }
        });


        createNotificationChannel();
        fetchNotifications("asc");

        return view;
    }

    private void fetchNotifications(String filter) {
        String url = "http://192.168.1.18/SchoolHub/get_notifications.php?user_id=" + studentId + "&filter=" + filter;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    notificationList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            NotificationItem item = new NotificationItem(
                                    obj.getInt("id"),
                                    obj.optString("sender_name", ""),
                                    obj.getString("title"),
                                    obj.getString("message"),
                                    obj.getString("created_at"),
                                    obj.getInt("is_read") == 1
                            );
                            notificationList.add(item);

                            if (!item.isRead) {
                                showLocalPopupNotification(item.title, item.message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                        Log.e("Notification", "Error parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("Notification", "Error fetching notifications: " + error.getMessage());
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "schoolhub_notifications",
                    "SchoolHub Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Used for sending school alerts");

            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void sendNotificationToServerAndDevice(String title, String message, int recipientId, int senderId) {
        String url = "http://192.168.1.18/SchoolHub/send_notification.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    showLocalPopupNotification(title, message);
                },
                error -> {
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("sender_id", String.valueOf(senderId));
                params.put("recipient_id", String.valueOf(recipientId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }



    private void showLocalPopupNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "schoolhub_notifications")
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(requireContext());
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
