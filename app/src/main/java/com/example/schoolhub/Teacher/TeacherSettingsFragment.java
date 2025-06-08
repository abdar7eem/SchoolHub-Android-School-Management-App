package com.example.schoolhub.Teacher;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.AboutUsFragment;
import org.json.JSONException;

public class TeacherSettingsFragment extends Fragment {

    private TextView tvTeacherName, tvAge, tvPhone;
    private Button btnAboutUs, btnLogout;
    private final String baseUrl = LoginActivity.baseUrl;
    private final int teacherId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_settings, container, false);

        tvTeacherName = view.findViewById(R.id.tvTeacherName);
        tvAge = view.findViewById(R.id.tvAge);
        tvPhone = view.findViewById(R.id.tvPhone);
        btnAboutUs = view.findViewById(R.id.btnAboutUs);
        btnLogout = view.findViewById(R.id.btnLogout);

        fetchTeacherInfo();

        btnAboutUs.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.teacherFragmentContainer, new TeacherAboutUsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            // Optional: clear session, navigate to login
        });

        return view;
    }

    private void fetchTeacherInfo() {
        String url = baseUrl + "get_teacher_profile.php?teacher_id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("error")) {
                            Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = response.getString("name");
                        String age = response.getString("age");
                        String phone = response.getString("phone");

                        tvTeacherName.setText("Name: " + name);
                        tvAge.setText("Age: " + age);
                        tvPhone.setText("Phone: " + phone);
                    } catch (JSONException e) {
                        Log.e("TeacherInfo", "Parse error", e);
                        Toast.makeText(getContext(), "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("TeacherInfo", "Request error", error);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Failed to load teacher info", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
