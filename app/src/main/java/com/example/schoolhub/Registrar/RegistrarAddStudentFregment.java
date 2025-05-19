package com.example.schoolhub.Registrar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.InfoClass;
import com.example.schoolhub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class RegistrarAddStudentFregment extends Fragment {
    private final String URL = "http://192.168.56.1/schoolhub/Add_user.php"; // Replace with your actual PHP URL
    EditText edtStudentName,edtParent, edtDateOfBirth, edtEmail, edtPhone, edtClass, edtPassword, edtConfirmPassword;
    TextView txtStatusMessage;
    Spinner spnClasses;
    Button btnAddStudent;

    InfoClass ChoosenClass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_add_student, container, false);

        edtStudentName = view.findViewById(R.id.edtStudentName);
        edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        spnClasses = view.findViewById(R.id.spnClasses);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        btnAddStudent = view.findViewById(R.id.btnAddStudent);
        edtParent =view.findViewById(R.id.edtParent);

        txtStatusMessage=view.findViewById(R.id.txtStatusMessage);

        edtDateOfBirth.setOnClickListener(v -> showDatePicker());

        loadClasses();


        btnAddStudent.setOnClickListener(e->{
            if (edtStudentName.getText().toString().isEmpty() ||
                    edtEmail.getText().toString().isEmpty() ||
                    edtPassword.getText().toString().isEmpty() ||
                    edtConfirmPassword.getText().toString().isEmpty() ||
                    edtDateOfBirth.getText().toString().isEmpty() ||
                    edtParent.getText().toString().isEmpty()) {

                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (checkPasword()==false){
                Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();                return;

            }
            else{
                sendUserDataToServer();


            }
        });

        return view;
    }

    private void sendUserDataToServer() {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> Toast.makeText(getContext(), "User Added Successfully", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getContext(), "Couldnt Add user", Toast.LENGTH_SHORT).show()) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", edtStudentName.getText().toString().trim());
                    params.put("email", edtEmail.getText().toString().trim());
                    params.put("role", "Student");
                    params.put("dob", edtDateOfBirth.getText().toString().trim());
                    params.put("password", edtPassword.getText().toString().trim());
                    params.put("parent", edtParent.getText().toString().trim());
                    params.put("class", ChoosenClass.getName());
                    params.put("phone", edtPhone.getText().toString().trim());


                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(request);
        }catch(Exception ex){
        }
    }

    void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) ->
                edtDateOfBirth.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    boolean checkPasword() {
    String Password=edtPassword.getText().toString();
    String confirmPassword=edtConfirmPassword.getText().toString();
    if(Password.equals(confirmPassword)){
        return true;
    }
        return false;
    }

    private void loadClasses() {
        String url = "http://192.168.56.1/schoolhub/get_classes.php";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<InfoClass> Classes = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");

                            Classes.add(new InfoClass(id,name));
                        }

                        ArrayAdapter<InfoClass> adapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_spinner_item,
                                Classes
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnClasses.setAdapter(adapter);
                        spnClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                InfoClass selectedClass = (InfoClass) parent.getItemAtPosition(position);
                                ChoosenClass=selectedClass;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Optional: Handle case when nothing is selected
                            }
                        });

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}

