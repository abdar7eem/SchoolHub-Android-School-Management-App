package com.example.schoolhub.Registrar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.InfoClass;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RegistrarAddTeacherFregment extends Fragment {
    EditText edtTeacherName, edtEmail, edtPhone, edtClass, edtPassword, edtConfirmPassword,edtDateOfBirth;
    Spinner spnClasses;
    List<InfoClass> classList = new ArrayList<>();
    List<SubjectInfo> subjectList = new ArrayList<>();

    TextView txtStatusMessage;
    Button btnAddTeacher;
    TextView txtSelectClasses;
    boolean[] selectedClassItems;
    List<Integer> selectedClassIds = new ArrayList<>();
    String[] classNames;
    SubjectInfo ChoosenSubject;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_add_teacher, container, false);

        edtTeacherName = view.findViewById(R.id.edtTeacherName);
    edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        btnAddTeacher = view.findViewById(R.id.btnAddTeacher);
        txtSelectClasses = view.findViewById(R.id.txtSelectClasses);


        LoadClasses();

        edtDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnAddTeacher.setOnClickListener(e->{
            if (edtTeacherName.getText().toString().isEmpty() ||
                    edtEmail.getText().toString().isEmpty() ||
                    edtPassword.getText().toString().isEmpty() ||
                    edtConfirmPassword.getText().toString().isEmpty() ||
                    edtDateOfBirth.getText().toString().isEmpty() ) {

                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (checkPasword()==false){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

            }
            else{
                AddTeacher();


            }
        });

        return view;
    }

    private void AddTeacher() {
        String url = LoginActivity.baseUrl+"Add_teacher.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(requireContext(), "Teacher Added Successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Couldn't Add Teacher"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.e("error ",error.getMessage().toString());

                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", edtTeacherName.getText().toString().trim());
                params.put("email", edtEmail.getText().toString().trim());
                params.put("dob", edtDateOfBirth.getText().toString().trim()); // ✅ MUST BE SENT!
                params.put("role", "Teacher");
                params.put("password", edtPassword.getText().toString().trim());
                params.put("phone", edtPhone.getText().toString().trim());


                String classIdsJson = new Gson().toJson(selectedClassIds);
                Log.d("CLASS_IDS", new Gson().toJson(selectedClassIds));
                params.put("class_ids", classIdsJson);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void LoadClasses() {
        String url = LoginActivity.baseUrl+"get_classes.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        classList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");
                            classList.add(new InfoClass(id, name));
                        }

                        // Init boolean array for selections
                        selectedClassItems = new boolean[classList.size()];
                        classNames = new String[classList.size()];

                        for (int i = 0; i < classList.size(); i++) {
                            classNames[i] = classList.get(i).getName(); // make sure InfoClass has getName()
                        }

                        txtSelectClasses.setOnClickListener(v -> showClassMultiDialog());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void showClassMultiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Classes");

        builder.setMultiChoiceItems(classNames, selectedClassItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedClassIds.add(classList.get(which).getId());
            } else {
                selectedClassIds.remove(Integer.valueOf(classList.get(which).getId()));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classList.size(); i++) {
                if (selectedClassItems[i]) {
                    sb.append(classList.get(i).getName()).append(", ");
                }
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2); // remove trailing comma
            txtSelectClasses.setText(sb.toString());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    boolean checkPasword() {
        String Password=edtPassword.getText().toString();
        String confirmPassword=edtConfirmPassword.getText().toString();
        if(Password.equals(confirmPassword)){
            return true;
        }
        return false;
    }
    void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) ->
                edtDateOfBirth.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

}

