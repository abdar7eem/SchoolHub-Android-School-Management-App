package com.example.schoolhub.Model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.Manifest;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

    private static final String CHANNEL_ID = "schoolhub_notifications";

    public static void sendNotification(Context context, String title, String message,
                                        String mode, int senderId, int subjectId, int targetId) {
        String url = LoginActivity.baseUrl + "send_notification.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    showLocalPopupNotification(context, title, message);
                },
                error -> Log.e("NotificationHelper", "Failed to send: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("sender_id", String.valueOf(senderId));

                if ("student".equals(mode)) {
                    params.put("recipient_id", String.valueOf(targetId));
                } else if ("class".equals(mode)) {
                    params.put("class_id", String.valueOf(targetId));
                }

                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private static void showLocalPopupNotification(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationHelper", "Notification permission not granted");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "schoolhub_notifications")
                .setSmallIcon(R.drawable.logo2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

