package com.example.ugc.ugc.vehicle_in_out;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ugc.ugc.Config;
import com.example.ugc.ugc.R;
import com.example.ugc.ugc.session.SessionManager;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Vehical_In extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Vehical_In";
    EditText txtVno,txtOwner, txtCapacity, txtCust,txtIntime;
    EditText txtAdd, txtContact, txtPanNo, txtInsNo,txtIns_Ex_Date, txtrTO_No, txtRTo_Ex_Date,txtPol_No,txtPol_Ex_Date;
    EditText txtTranPass_No,txtTrans_Pass_Ex_Date;
    private MaterialBetterSpinner vehicalType;
    AppCompatButton btnSubmit;
    SessionManager session;
    String VEHICLE_NO,CUST_NAME;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehical__in);

        Intent i = getIntent();
        VEHICLE_NO = i.getStringExtra("VEHICLE_NO");
        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        CUST_NAME = user.get(SessionManager.USER_CUST);

        txtVno=(EditText) findViewById(R.id.v_no);
        txtIntime=(EditText) findViewById(R.id.in_time);
        vehicalType = (MaterialBetterSpinner)findViewById(R.id.vehicle_type);
        txtCapacity=(EditText) findViewById(R.id.capacity);
        txtCust=(EditText) findViewById(R.id.custName);
        txtOwner=(EditText) findViewById(R.id.owners);
        txtAdd=(EditText) findViewById(R.id.address);
        txtContact=(EditText) findViewById(R.id.mobile);
        txtPanNo=(EditText) findViewById(R.id.panno);
        txtInsNo=(EditText) findViewById(R.id.insno);
        txtIns_Ex_Date=(EditText) findViewById(R.id.ins_ex_date);
        txtrTO_No=(EditText) findViewById(R.id.rtono);
        txtRTo_Ex_Date=(EditText) findViewById(R.id.rtoexdate);
        txtPol_No=(EditText) findViewById(R.id.pollution);
        txtPol_Ex_Date=(EditText) findViewById(R.id.poexdate);
        txtTranPass_No=(EditText) findViewById(R.id.transpassno);
        txtTrans_Pass_Ex_Date=(EditText) findViewById(R.id.transpassexdate);

        txtVno.setText(VEHICLE_NO);
        txtCust.setText(CUST_NAME);
        txtCust.setFocusable(false);
        btnSubmit = (AppCompatButton)findViewById(R.id.btn_submit);

        txtVno.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtIntime.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        vehicalType.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtCapacity.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtCust.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtOwner.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtAdd.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtContact.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtPanNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtInsNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtIns_Ex_Date.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtrTO_No.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtRTo_Ex_Date.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtPol_No.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtPol_Ex_Date.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtTranPass_No.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtTrans_Pass_Ex_Date.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =>"+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//("dd-MMM-yyyy HH:mm:ss")
        String formattedDate = df.format(c.getTime());
        txtIntime.setText(formattedDate);

        showData();

        String[] SPINNERLIST = {"Own", "Other"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        vehicalType.setAdapter(arrayAdapter);
        btnSubmit.setOnClickListener(this);

    }

    private void showData() {
        final String cc = txtVno.getText().toString().trim();
        String url = Config.VEH_DETAILS;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try{
                        String VEHICLETYPE ="";
                        String CAPACITY ="";
                        String OWNER ="";
                        String ADDRESS ="";
                        String CONTACTNO ="";
                        String PANNUMBER ="";
                        String INSNUMBER ="";
                        String INSEXPIRYDATE ="";
                        String RTONUMBER ="";
                        String RTONOEXPDATE ="";
                        String POLLUTIONN0 ="";
                        String POLLUTIONEXPDATE ="";
                        String TRANSITPASSNO ="";
                        String TRANSITEXPDATE ="";

                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            VEHICLETYPE = jsonObject.getString("VEHICLETYPE");
                            CAPACITY = jsonObject.getString("CAPACITY");
                            OWNER = jsonObject.getString("OWNER");
                            ADDRESS = jsonObject.getString("ADDRESS");
                            CONTACTNO = jsonObject.getString("CONTACTNO");
                            PANNUMBER = jsonObject.getString("PANNUMBER");
                            INSNUMBER = jsonObject.getString("INSNUMBER");
                            INSEXPIRYDATE = jsonObject.getString("INSEXPIRYDATE");
                            RTONUMBER = jsonObject.getString("RTONUMBER");
                            RTONOEXPDATE = jsonObject.getString("RTONOEXPDATE");
                            POLLUTIONN0 = jsonObject.getString("POLLUTIONN0");
                            POLLUTIONEXPDATE = jsonObject.getString("POLLUTIONEXPDATE");
                            TRANSITPASSNO = jsonObject.getString("TRANSITPASSNO");
                            TRANSITEXPDATE = jsonObject.getString("TRANSITEXPDATE");
                        }
                        vehicalType.setText(VEHICLETYPE);
                        txtCapacity.setText(CAPACITY);
                        txtOwner.setText(OWNER);
                        txtAdd.setText(ADDRESS);
                        txtContact.setText(CONTACTNO);
                        txtPanNo.setText(PANNUMBER);
                        txtInsNo.setText(INSNUMBER);
                        txtIns_Ex_Date.setText(INSEXPIRYDATE);
                        txtrTO_No.setText(RTONUMBER);
                        txtRTo_Ex_Date.setText(RTONOEXPDATE);
                        txtPol_No.setText(POLLUTIONN0);
                        txtPol_Ex_Date.setText(POLLUTIONEXPDATE);
                        txtTranPass_No.setText(TRANSITPASSNO);
                        txtTrans_Pass_Ex_Date.setText(TRANSITEXPDATE);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("VEHICLE_NO",cc);
                return params;
            }
        };
        requestQueue.add(sr);
    }

    @Override
    public void onClick(View v) {
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String VEHICLETYPE = vehicalType.getText().toString().trim();
        final String CAPACITY = txtCapacity.getText().toString().trim();
        final String CUSTOMER = txtCust.getText().toString().trim();
        final String OWNER = txtOwner.getText().toString().trim();
        final String ADDRESS = txtAdd.getText().toString().trim();
        final String CONTACTNO = txtContact.getText().toString().trim();
        final String PANNUMBER = txtPanNo.getText().toString().trim();
        final String INSNUMBER = txtInsNo.getText().toString().trim();
        final String INSEXPIRYDATE = txtIns_Ex_Date.getText().toString().trim();
        final String RTONUMBER =txtrTO_No.getText().toString().trim();
        final String RTONOEXPDATE =txtRTo_Ex_Date.getText().toString().trim();
        final String POLLUTIONN0 =  txtPol_No.getText().toString().trim();
        final String POLLUTIONEXPDATE = txtPol_Ex_Date.getText().toString().trim();
        final String TRANSITPASSNO = txtTranPass_No.getText().toString().trim();
        final String TRANSITEXPDATE =txtTrans_Pass_Ex_Date.getText().toString().trim();

        if(validate()){

            Intent i = new Intent(getApplicationContext(), Tran_Veh_In.class);
            i.putExtra("VEHICLENO",VEHICLENO);
            i.putExtra("VEHICLETYPE",VEHICLETYPE);
            i.putExtra("CAPACITY",CAPACITY);
            i.putExtra("CUSTOMER",CUSTOMER);
            i.putExtra("OWNER",OWNER);
            i.putExtra("ADDRESS",ADDRESS);
            i.putExtra("CONTACTNO",CONTACTNO);
            i.putExtra("PANNUMBER",PANNUMBER);
            i.putExtra("INSNUMBER",INSNUMBER);
            i.putExtra("INSEXPIRYDATE",INSEXPIRYDATE);
            i.putExtra("RTONUMBER",RTONUMBER);
            i.putExtra("RTONOEXPDATE",RTONOEXPDATE);
            i.putExtra("POLLUTIONN0",POLLUTIONN0);
            i.putExtra("POLLUTIONEXPDATE",POLLUTIONEXPDATE);
            i.putExtra("TRANSITPASSNO",TRANSITPASSNO);
            i.putExtra("TRANSITEXPDATE",TRANSITEXPDATE);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            clearAllText();
            this.finish();

        }
    }

    private void clearAllText() {
        txtVno.setText("");
        txtIntime.setText("");
        vehicalType.setText("");
        txtCapacity.setText("");
        txtCust.setText("");
        txtOwner.setText("");
        txtAdd.setText("");
        txtContact.setText("");
        txtPanNo.setText("");
        txtInsNo.setText("");
        txtIns_Ex_Date.setText("");
        txtrTO_No.setText("");
        txtRTo_Ex_Date.setText("");
        txtPol_No.setText("");
        txtPol_Ex_Date.setText("");
        txtTranPass_No.setText("");
        txtTrans_Pass_Ex_Date.setText("");
    }


    private boolean validate() {
        boolean valid = true;
        final String VEHICLENO = txtVno.getText().toString().trim();
        if (VEHICLENO .isEmpty()){
            txtVno.setError("Enter Vehicle no");
            valid = false;
        }else {
            txtVno.setError(null);
        }
        return valid;
    }
}
