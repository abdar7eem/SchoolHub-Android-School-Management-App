package com.example.schoolhub.Registrar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import org.json.JSONException;

public class RegistrarSettingsFregment extends Fragment {

    TextView tvRegistrarName,tvAge,tvId;
    Button btnAboutUs,btnLogout;
   int  RegistrarID; ; //Change it to the real id :)


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_settings_fregment, container, false);
        if (getArguments() != null) {
            RegistrarID = getArguments().getInt("user_id", -1);
        } else {
            RegistrarID = -1; // fallback
        }

        Log.e("RegistrarID", String.valueOf(RegistrarID));

        tvRegistrarName=view.findViewById(R.id.tvRegistrarName);
        tvAge=view.findViewById(R.id.tvAge);
        tvId=view.findViewById(R.id.tvId);
        btnAboutUs=view.findViewById(R.id.btnAboutUs);
        btnLogout=view.findViewById(R.id.btnLogout);
        getRegistrarData();

        btnAboutUs.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.registrarFragmentContainer, new AboutUsFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();


        });

        return view;
    }
    void getRegistrarData(){
        String url = LoginActivity.baseUrl+"get_profile.php?id="+RegistrarID;

        Log.e("RegId", String.valueOf(RegistrarID));

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("name") && response.has("age")) {
                            String name = response.getString("name");
                            int age = response.getInt("age");

                            // Update your UI, for example:
                            tvRegistrarName.setText(name);
                            tvId.setText(String.valueOf(RegistrarID));
                            tvAge.setText(String.valueOf(age));
                        } else if (response.has("error")) {
                            Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to fetch student name", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }
    }
