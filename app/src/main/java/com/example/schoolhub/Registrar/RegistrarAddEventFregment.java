package com.example.schoolhub.Registrar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class RegistrarAddEventFregment extends Fragment {
    String[] eventTypes = {
            "Parent-Teacher Meeting",
            "Registration Deadline",
            "Fee Payment Deadline",
            "School Assembly",
            "Field Trip",
            "Sports Day",
            "Health Checkup",
            "Cultural Day",
            "Workshop",
            "Graduation Ceremony"
    };
    Spinner spnType;

    EditText edtEventDescription,edtEventLocation,edtEventEndTime,edtEventStartTime,edtEventDate,edtEventTiltle;
    Button btnPostEvent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_add_event, container, false);
        edtEventDescription = view.findViewById(R.id.edtEventDescription);
        edtEventLocation = view.findViewById(R.id.edtEventLocation);
        edtEventEndTime = view.findViewById(R.id.edtEventEndTime);
        edtEventStartTime = view.findViewById(R.id.edtEventStartTime);
        edtEventDate = view.findViewById(R.id.edtEventDate);
        edtEventTiltle = view.findViewById(R.id.edtEventTiltle);
        btnPostEvent = view.findViewById(R.id.btnPostEvent);

        edtEventDate.setOnClickListener(v -> showDatePicker());

        spnType = view.findViewById(R.id.spnType);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                eventTypes
        );

        edtEventStartTime.setOnClickListener(v -> showStartTimePicker());
        edtEventEndTime.setOnClickListener(v -> showEndTimePicker());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        btnPostEvent.setOnClickListener(e->{
            if (edtEventDescription.getText().toString().isEmpty() ||
                    edtEventLocation.getText().toString().isEmpty() ||
                    edtEventEndTime.getText().toString().isEmpty() ||
                    edtEventStartTime.getText().toString().isEmpty() ||
                    edtEventDate.getText().toString().isEmpty() ||
                    edtEventTiltle.getText().toString().isEmpty()) {

                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                PostEvent();


            }
        });


        return view ;
    }
    private void PostEvent() {
        String url = LoginActivity.baseUrl+"Add_event.php";

        String eventType = spnType.getSelectedItem().toString();
        Log.e("spnType", eventType);
        String eventTitle = edtEventTiltle.getText().toString().trim();
        String eventDate = edtEventDate.getText().toString().trim();
        String eventStartTime = edtEventStartTime.getText().toString().trim();
        String eventEndTime = edtEventEndTime.getText().toString().trim();
        String location = edtEventLocation.getText().toString().trim();
        String description = edtEventDescription.getText().toString().trim();



        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("EVENT_ADD_RESPONSE", response);
                    Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("EVENT_ADD_ERROR", error.toString());
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Couldnt Add Event", Toast.LENGTH_SHORT).show();

                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_type", eventType);
                params.put("event_title", eventTitle);
                params.put("event_date", eventDate);
                params.put("event_start_time", eventStartTime);
                params.put("event_end_time", eventEndTime);
                params.put("location", location);
                params.put("event_description", description);
                Log.e("spnType", eventType);

                Log.e("event_date", eventDate);
                Log.e("event_title", eventTitle);

                Log.e("event_start_time", eventStartTime);
                Log.e("event_end_time", eventEndTime);
                Log.e("location", location);
                Log.e("event_description", description);

                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) ->
                edtEventDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
    void showStartTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hour, minute) ->
                edtEventStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }
    void showEndTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hour, minute) ->
                edtEventEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }
}