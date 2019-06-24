package com.example.ugc.ugc;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ugc.ugc.reports.Date_Reports;
import com.example.ugc.ugc.reports.Vehicle_Wise_Report;
import com.example.ugc.ugc.session.Login;
import com.example.ugc.ugc.session.SessionManager;
import com.example.ugc.ugc.vehicle_in_out.NewTrans;
import com.example.ugc.ugc.vehicle_in_out.Vehical_In;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import android.app.DatePickerDialog.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Home";
    SessionManager session;
    String USER_NAME;
    CardView btnNewEntry,btnInEntry,btnOutEntry,btnReport;
    AppCompatButton buttonConfirm;
    EditText editTextVeNo, editTrans;
    RequestQueue requestQueue;
    String VEHICLE_NO, message;

    private IntentIntegrator qrScan;
    private IntentIntegrator qrScan1;

    ImageView imageView;
    public  static final int RequestPermissionCode  = 1 ;
    Bitmap bitmap;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    EditText dates,todates;
    String TID;
    String VEHICLENO;
    String OUTTIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        USER_NAME = user.get(SessionManager.USER_NAME);

        requestQueue = Volley.newRequestQueue(this);

        btnNewEntry = (CardView)findViewById(R.id.newEntry);
        btnInEntry = (CardView)findViewById(R.id.inEntry);
        btnOutEntry = (CardView)findViewById(R.id.outEntry);
        btnReport = (CardView)findViewById(R.id.report);

        btnNewEntry.setOnClickListener(this);
        btnInEntry.setOnClickListener(this);
        btnOutEntry.setOnClickListener(this);
        btnNewEntry.setVisibility(View.GONE);
        btnReport.setOnClickListener(this);

        qrScan = new IntentIntegrator(this);

        qrScan1 = new IntentIntegrator(this);

        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

    }


    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dates.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                todates.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.newEntry){
            InTimes();
        }if (id == R.id.inEntry){
            showChooceOption();
        }if(id == R.id.outEntry){
            vehicleOut();
        }if (id == R.id.report){

            reports();
        }if(id == R.id.in_date){
            fromDatePickerDialog.show();
        }if (id == R.id.in_dates){
            toDatePickerDialog.show();
        }
    }

    private void reports() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.reports, (ViewGroup) findViewById(R.id.root));

        Button btnDate = (Button)view.findViewById(R.id.btn1);
        Button btnVehicle = (Button)view.findViewById(R.id.btn2);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateReport();
                alertDialog.dismiss();
            }
        });
        btnVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicle_report();
                alertDialog.dismiss();
            }
        });
    }

    private void vehicle_report() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm, (ViewGroup) findViewById(R.id.root));

        AppCompatButton btnReport = (AppCompatButton)view.findViewById(R.id.buttonConfirm1);
        final EditText vno = (EditText)view.findViewById(R.id.veh_no);
        vno.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String veno = vno.getText().toString().trim();
                if (veno.isEmpty()){
                    vno.setError("enter vehicle no");
                }else {
                    Intent i = new Intent(getApplicationContext(), Vehicle_Wise_Report.class);
                    i.putExtra("VEHICLENO",veno.toString());
                    startActivity(i);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void dateReport() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.date_dailog, (ViewGroup) findViewById(R.id.root));
        setDateTimeField();

        Button btnReport = (Button)view.findViewById(R.id.btnSs);
        dates = (EditText)view.findViewById(R.id.in_date);
        dates.setInputType(InputType.TYPE_NULL);
        dates.requestFocus();

        todates  = (EditText)view.findViewById(R.id.in_dates);
        todates.setInputType(InputType.TYPE_NULL);
//        todates.requestFocus();
        dates.setOnClickListener(this);
        todates.setOnClickListener(this);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String da = dates.getText().toString().trim();
                String da1 = todates.getText().toString().trim();
                if(da.isEmpty()){
                    dates.setError("enter date");
                }else {
                    Intent i = new Intent(Home.this, Date_Reports.class);
                    i.putExtra("INTIME",da);
                    i.putExtra("INTIME1",da1);
                    startActivity(i);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void showChooceOption() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dailog, (ViewGroup) findViewById(R.id.root));

        AppCompatButton button1 = (AppCompatButton) view.findViewById(R.id.buttonConfirm1);
        AppCompatButton button2 = (AppCompatButton) view.findViewById(R.id.buttonConfirm2);
        AppCompatButton button3 = (AppCompatButton) view.findViewById(R.id.buttonConfirm3);

       final LinearLayout ll1 = (LinearLayout)view.findViewById(R.id.l1);
       ll1.setVisibility(View.GONE);
       final LinearLayout l2 = (LinearLayout)view.findViewById(R.id.l2);
        l2.setVisibility(View.GONE);

        buttonConfirm = (AppCompatButton) view.findViewById(R.id.buttonConfirm14);
        editTextVeNo = (EditText)view.findViewById(R.id.veh_no);
        editTextVeNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        imageView = (ImageView)view.findViewById(R.id.imageView);
        Button btn = (Button)view.findViewById(R.id.button);
        final Button btn1 = (Button)view.findViewById(R.id.button1);
        final Button btn2 = (Button)view.findViewById(R.id.button2);
        btn2.setVisibility(View.GONE);
        final EditText editTextVeNo1 = (EditText)view.findViewById(R.id.veh_no1);
        editTextVeNo1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        final RelativeLayout rr = (RelativeLayout)view.findViewById(R.id.r1);
        rr.setVisibility(View.GONE);
        final Button btns = (Button)view.findViewById(R.id.buRotate);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setRotation(imageView.getRotation() + 90);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational()){
                    Toast.makeText(getApplicationContext(),"Could not get the text",Toast.LENGTH_SHORT).show();
                }else {
                    Frame frame  = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i=0; i<items.size(); i++){
                        TextBlock textBlock = items.valueAt(i);
                        stringBuilder.append(textBlock.getValue());
                        stringBuilder.append("\n");
                    }
                    editTextVeNo1.setText(stringBuilder.toString());
                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.VISIBLE);
                }
            }
        });
//      Open Camera
        EnableRuntimePermission();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
                rr.setVisibility(View.VISIBLE);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String veh_no1 = editTextVeNo1.getText().toString().trim();
                String url = Config.CHECK_VEHICLES;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!=null){
                            try{
                                JSONArray array = new JSONArray(response);
                                for (int i=0; i<array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);
                                    VEHICLE_NO = object.getString("VEHICLE_NO");
                                }
                                String ab = "null";
                                if(ab.equals(VEHICLE_NO)){
                                    Intent i = new Intent(getApplicationContext(),NewTrans.class);
                                    i.putExtra("VEHICLE_NO",veh_no1);
                                    startActivity(i);
                                    alertDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Fill All Vehicle Details",Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent i = new Intent(getApplicationContext(), Vehical_In.class);
                                    i.putExtra("VEHICLE_NO",veh_no1);
                                    startActivity(i);
                                    alertDialog.dismiss();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        NetworkResponse networkResponse = error.networkResponse;
                        Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String > para = new HashMap<>();
                        para.put("VEHICLE_NO",veh_no1);
                        return para;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String veh_no = editTextVeNo.getText().toString().trim();
                String url = Config.CHECK_VEHICLES;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!=null){
                            try{
                                JSONArray array = new JSONArray(response);
                                for (int i=0; i<array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);
                                    VEHICLE_NO = object.getString("VEHICLE_NO");
                                }
                                String ab = "null";
                                if(ab.equals(VEHICLE_NO)){
                                    Intent i = new Intent(getApplicationContext(),NewTrans.class);
                                    i.putExtra("VEHICLE_NO",veh_no);
                                    startActivity(i);
                                    alertDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Fill All Vehicle Details",Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent i = new Intent(getApplicationContext(), Vehical_In.class);
                                    i.putExtra("VEHICLE_NO",veh_no);
                                    startActivity(i);
                                    alertDialog.dismiss();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        NetworkResponse networkResponse = error.networkResponse;
                        Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String > para = new HashMap<>();
                        para.put("VEHICLE_NO",veh_no);
                        return para;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll1.setVisibility(View.VISIBLE);
                l2.setVisibility(View.GONE);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan1.initiateScan();
                ll1.setVisibility(View.VISIBLE);
                l2.setVisibility(View.GONE);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll1.setVisibility(View.GONE);
                l2.setVisibility(View.VISIBLE);
            }
        });

    }


    private void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.CAMERA))
        {

            Toast.makeText(Home.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_SHORT).show();

        } else {

            ActivityCompat.requestPermissions(Home.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                   // Toast.makeText(Home.this,"Permission Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(Home.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void vehicleOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Choose any one option")
                .setPositiveButton("QR Code Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        qrScan.initiateScan();
                    }
                })

                .setNegativeButton("Vehical No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater li = LayoutInflater.from(Home.this);
                        View confirmDialog = li.inflate(R.layout.out_vehicle, null);

                        AppCompatButton  buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm1);
                        final EditText editTextVeNo = (EditText) confirmDialog.findViewById(R.id.tranid);
                        final EditText editTrans = (EditText) confirmDialog.findViewById(R.id.veh_no);

                        editTextVeNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                        editTrans.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

                        AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
                        alert.setView(confirmDialog);
                        final AlertDialog alertDialog = alert.create();
                        alertDialog.show();

                        buttonConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TID = editTextVeNo.getText().toString().trim();
                                VEHICLENO = editTrans.getText().toString().trim();

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                System.out.println("Current time =>" + formattedDate);
                                OUTTIME = formattedDate.toString();

                                if((TID.isEmpty())&&(VEHICLENO.isEmpty())){
                                    Toast.makeText(getApplicationContext(),"enter Trans ID and Vehicle No.",Toast.LENGTH_SHORT).show();
                                }else{
                                    alertDialog.dismiss();
//                                    Check loading / Unloading vehicle out time
                                    checkLoding_Unloading(TID,VEHICLENO);
                                }
                            }
                        });
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {

                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();

            } else {
                //QR Code contains some data
                try {
                    //Convert the QR Code Data to JSON
                    JSONObject obj = new JSONObject(result.getContents());
                    //Set up the TextView Values using the data from JSON
                    String TID = obj.getString("TID");
                    String VEHICLENO = obj.getString("VEHICLENO");

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//("dd-MMM-yyyy HH:mm:ss:aa")
                    String formattedDate = df.format(c.getTime());
                    System.out.println("Current time =>" + formattedDate);
                    String OUTTIME = formattedDate.toString();

                    if((TID.isEmpty()) && (VEHICLENO.isEmpty()) && (!OUTTIME.isEmpty())){
                        Intent i = new Intent(getApplicationContext(), Vehical_In.class);
                        i.putExtra("VEHICLE_NO",VEHICLENO);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                    else if((!TID.isEmpty()) && (!VEHICLENO.isEmpty()) && (!OUTTIME.isEmpty())){
                        checkLoding_Unloading(TID,VEHICLENO);
                    }else {
                        Intent i = new Intent(getApplicationContext(), Vehical_In.class);
                        i.putExtra("VEHICLE_NO",VEHICLENO);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    //In case of exception, display whatever data is available on the QR Code
                    //This can be caused due to the format MisMatch of the JSON
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            if( requestCode == 7 && resultCode == RESULT_OK){
                bitmap = (Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    private void checkLoding_Unloading(final String tid, final String vehicleno) {
        String urls = Config.CHECK_LOADING_UNLOADING;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    JSONObject object = null;
                    String ss="";
                    try{
                        JSONArray array = new JSONArray(response);
                        for(int i=0; i<array.length(); i++){
                            object = array.getJSONObject(i);
                            ss = object.getString("LOADUNLOAD");
                        }
                        String s1 = "Loading Vehicle";
                        if(ss.equals(s1)){
                            vehicalOut1(TID,VEHICLENO,OUTTIME);
                        }else{
                            openDialogBox();
                        }

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> p = new HashMap<>();
                p.put("TID",tid);
                p.put("VEHICLENO",vehicleno);
                return p;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void vehicalOut1(final String tid, final String vehicleno, final String outtime) {
        String urls = Config.OUT_VEHICLE1;
        StringRequest sr = new StringRequest(Request.Method.POST, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    try{
                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++){
                            JSONObject json =  array.getJSONObject(i);
                            Toast.makeText(getApplicationContext(),json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("TID",tid);
                params.put("VEHICLENO",vehicleno);
                params.put("OUTTIME",outtime);
                return params;
            }
        };
        requestQueue.add(sr);
    }
    private void openDialogBox() {
        LayoutInflater li = LayoutInflater.from(Home.this);
        final View confirmDialog1 = li.inflate(R.layout.check_unloading, null);
        final Button  buttonConfirm1 = (Button) confirmDialog1.findViewById(R.id.btnSs1);
        final RadioGroup unlos=(RadioGroup)confirmDialog1.findViewById(R.id.radioGroup);
        final EditText spname = (EditText) confirmDialog1.findViewById(R.id.supplyname);
        final EditText inno = (EditText) confirmDialog1.findViewById(R.id.invno);

        spname.setVisibility(View.GONE);
        inno.setVisibility(View.GONE);
        buttonConfirm1.setVisibility(View.GONE);
        AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
        alert.setView(confirmDialog1);
        final AlertDialog alertDialog1 = alert.create();
        alertDialog1.show();

        unlos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton:
                        spname.setVisibility(View.VISIBLE);
                        inno.setVisibility(View.VISIBLE);
                        buttonConfirm1.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButton2:
                        spname.setVisibility(View.GONE);
                        inno.setVisibility(View.GONE);
                        buttonConfirm1.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        buttonConfirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId=unlos.getCheckedRadioButtonId();
                RadioButton radioSexButton=(RadioButton)confirmDialog1.findViewById(selectedId);
                String ul = radioSexButton.getText().toString();
                String spn = spname.getText().toString().trim();
                String invno = inno.getText().toString().trim();
                vehicalOut(TID,VEHICLENO,invno,spn,OUTTIME,ul);
                alertDialog1.dismiss();
            }
        });
    }

    private void vehicalOut(final String TID, final String VEHICLENO,final String invno,final String spn,
                            final String OUTTIME,final String ul) {
        String urls = Config.OUT_VEHICLE;

        StringRequest sr = new StringRequest(Request.Method.POST, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    try{
                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++){
                            JSONObject json =  array.getJSONObject(i);
                            Toast.makeText(getApplicationContext(),json.getString("message"), Toast.LENGTH_SHORT).show();

                        }
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("TID",TID);
                params.put("VEHICLENO",VEHICLENO);
                params.put("INVNO",invno);
                params.put("SUPPLIERNAME",spn);
                params.put("OUTTIME",OUTTIME);
                params.put("LOADUNLOAD",ul);
                return params;
            }
        };
        requestQueue.add(sr);
    }

    //  Vehicle In
    private void InTimes() {
        LayoutInflater li = LayoutInflater.from(this);
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm1);
        editTextVeNo = (EditText) confirmDialog.findViewById(R.id.veh_no);
        editTextVeNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(confirmDialog);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String veh_no = editTextVeNo.getText().toString().trim();
                Intent i = new Intent(getApplicationContext(),NewTrans.class);
                i.putExtra("VEHICLE_NO",veh_no);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                alertDialog.dismiss();
            }
        });
    }

    private void checkVehical() {
        LayoutInflater li = LayoutInflater.from(this);
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm1);
        editTextVeNo = (EditText) confirmDialog.findViewById(R.id.veh_no);
        editTextVeNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(confirmDialog);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String veh_no = editTextVeNo.getText().toString().trim();
                String url = Config.CHECK_VEHICLES;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!=null){
                            try{
                                JSONArray array = new JSONArray(response);
                                for (int i=0; i<array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);
                                    VEHICLE_NO = object.getString("VEHICLE_NO");
                                }
                                String ab = "null";
                                if(ab.equals(VEHICLE_NO)){
                                    Toast.makeText(getApplicationContext(),"Please Enter Regiser Vehicle No..",Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent i = new Intent(getApplicationContext(), Vehical_In.class);
                                    i.putExtra("VEHICLE_NO",veh_no);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    alertDialog.dismiss();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Please Enter Regiser Vehicle No..",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        NetworkResponse networkResponse = error.networkResponse;
                        Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String > para = new HashMap<>();
                        para.put("VEHICLE_NO",veh_no);
                        return para;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item1) {
            session.logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        System.exit(0);
        super.onBackPressed();
    }
}