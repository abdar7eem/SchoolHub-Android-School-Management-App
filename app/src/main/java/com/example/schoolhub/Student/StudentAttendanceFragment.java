    package com.example.schoolhub.Student;

    import android.app.DatePickerDialog;
    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.fragment.app.Fragment;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.schoolhub.R;
    import com.example.schoolhub.Registration.LoginActivity;
    import com.journeyapps.barcodescanner.CaptureActivity;
    import com.journeyapps.barcodescanner.ScanContract;
    import com.journeyapps.barcodescanner.ScanOptions;

    import java.util.Calendar;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;

    public class StudentAttendanceFragment extends Fragment {

        private TextView txtDay, txtStatus;
        private Button btnSelectDate, btnScanQR;
        private int studentId;

        private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        String contents = result.getContents();
                        if (contents.contains("session_id=")) {
                            String sessionId = Uri.parse(contents).getQueryParameter("session_id");
                            submitAttendance(sessionId);
                        } else {
                            Toast.makeText(requireContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);

            if (getArguments() != null) {
                studentId = getArguments().getInt("student_id", -1);
            }

            txtDay = view.findViewById(R.id.txtDay);
            txtStatus = view.findViewById(R.id.txtStatus);
            btnSelectDate = view.findViewById(R.id.removeFromCartButton);
            btnScanQR = view.findViewById(R.id.btnScanQR);

            btnSelectDate.setOnClickListener(v -> openDatePicker());

            btnScanQR.setOnClickListener(v -> {
                ScanOptions options = new ScanOptions();
                options.setPrompt("Scan the attendance QR code");
                options.setBeepEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(CaptureActivity.class);
                qrLauncher.launch(options);
            });

            return view;
        }

        private void openDatePicker() {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        fetchAttendance(dateStr);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        }

        private void fetchAttendance(String selectedDate) {
            String url = LoginActivity.baseUrl + "get_attendance.php?student_id=" + studentId + "&date=" + selectedDate;

            RequestQueue queue = Volley.newRequestQueue(requireContext());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        String status = response.optString("status", null);
                        txtStatus.setText(status != null ? status : "Absent");
                        txtDay.setText("Selected: " + selectedDate);
                    },
                    error -> {
                        txtStatus.setText("Absent");
                        txtDay.setText("Selected: " + selectedDate);
                        Toast.makeText(requireContext(), "Unable to fetch attendance", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    });

            queue.add(request);
        }

        private void submitAttendance(String sessionCode) {
            String url = LoginActivity.baseUrl + "mark_attendance.php";

            RequestQueue queue = Volley.newRequestQueue(requireContext());

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                        Log.e("Attendance", response);
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to submit attendance", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("student_id", String.valueOf(studentId));
                    params.put("session_code", sessionCode);  // 🟢 Use session_code now
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            queue.add(request);
        }

    }
