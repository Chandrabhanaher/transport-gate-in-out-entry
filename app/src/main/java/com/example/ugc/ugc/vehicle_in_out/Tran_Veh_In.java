package com.example.ugc.ugc.vehicle_in_out;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ugc.ugc.Config;
import com.example.ugc.ugc.R;
import com.example.ugc.ugc.printing.Prints;
import com.example.ugc.ugc.session.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Tran_Veh_In extends Activity implements View.OnClickListener {
    private static final String TAG = "Tran_Veh_In";
    EditText txtVno, txtInsNo,txtyyymm, txtSuppler,txtIntime, txtTid,txtDname,txtDCon;
    AppCompatButton btnSubmit;
    SessionManager session;
    String VEHICLENO,VEHICLETYPE,CAPACITY,CUSTOMER,OWNER,ADDRESS,CONTACTNO,PANNUMBER,INSNUMBER,INSEXPIRYDATE;
    String RTONUMBER,RTONOEXPDATE,POLLUTIONN0,POLLUTIONEXPDATE,TRANSITPASSNO,TRANSITEXPDATE;
    String USER_NAME;
    RequestQueue requestQueue;
    int r;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    private ProgressDialog dialog;
    private MaterialBetterSpinner loading_Spinner;
    TextInputLayout ss11;
    ArrayAdapter<String> adapter;
    String ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran__veh__in);

        Intent i = getIntent();
        VEHICLENO = i.getStringExtra("VEHICLENO");
        VEHICLETYPE = i.getStringExtra("VEHICLETYPE");
        CAPACITY = i.getStringExtra("CAPACITY");
        CUSTOMER = i.getStringExtra("CUSTOMER");
        OWNER = i.getStringExtra("OWNER");
        ADDRESS = i.getStringExtra("ADDRESS");
        CONTACTNO = i.getStringExtra("CONTACTNO");
        PANNUMBER = i.getStringExtra("PANNUMBER");
        INSNUMBER = i.getStringExtra("INSNUMBER");
        INSEXPIRYDATE = i.getStringExtra("INSEXPIRYDATE");
        RTONUMBER = i.getStringExtra("RTONUMBER");
        RTONOEXPDATE = i.getStringExtra("RTONOEXPDATE");
        POLLUTIONN0 = i.getStringExtra("POLLUTIONN0");
        POLLUTIONEXPDATE = i.getStringExtra("POLLUTIONEXPDATE");
        TRANSITPASSNO = i.getStringExtra("TRANSITPASSNO");
        TRANSITEXPDATE = i.getStringExtra("TRANSITEXPDATE");

        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        USER_NAME = user.get(SessionManager.USER_NAME);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//("dd-MMM-yyyy HH:mm:ss:aa")
        String formattedDate = df.format(c.getTime());
        System.out.println("Current time =>" + formattedDate);


        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMM" );
        String today = formatter.format( new java.util.Date() );

        txtIntime = (EditText) findViewById(R.id.in_time);
        txtSuppler = (EditText) findViewById(R.id.supplyname);
        txtVno = (EditText) findViewById(R.id.v_no);
        txtInsNo = (EditText) findViewById(R.id.invno);
        txtyyymm = (EditText) findViewById(R.id.yyymm);
        txtTid = (EditText) findViewById(R.id.tid);

        txtDname = (EditText) findViewById(R.id.drame);
        txtDCon = (EditText) findViewById(R.id.drcontact);

        loading_Spinner = (MaterialBetterSpinner)findViewById(R.id.loading_spinner);
        txtSuppler.setVisibility(View.GONE);
        txtInsNo.setVisibility(View.GONE);
        txtyyymm.setVisibility(View.GONE);
        ss11 = (TextInputLayout)findViewById(R.id.yymm) ;
        ss11.setVisibility(View.GONE);
        txtyyymm.setFocusable(false);
        String ss[] = {"For Loading","For Unloading"};
        adapter = new ArrayAdapter<>(this,R.layout.cust_item,R.id.txtName,ss);
        loading_Spinner.setAdapter(adapter);

       loading_Spinner.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {}
           @Override
           public void afterTextChanged(Editable s) {
               ll = loading_Spinner.getText().toString();
                String dd = "For Loading";
               if(ll.equals(dd)){
                   txtSuppler.setVisibility(View.VISIBLE);
                   txtInsNo.setVisibility(View.VISIBLE);
                   txtyyymm.setVisibility(View.VISIBLE);
                   ss11.setVisibility(View.VISIBLE);
               }else{
                   txtSuppler.setVisibility(View.GONE);
                   txtInsNo.setVisibility(View.GONE);
                   txtyyymm.setVisibility(View.GONE);
                   ss11.setVisibility(View.GONE);
               }
           }
       });

        txtVno.setText(VEHICLENO);
        txtyyymm.setText(today);
        txtIntime.setText(formattedDate);
        getDriverDetails(VEHICLENO);
        btnSubmit = (AppCompatButton) findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(this);


        txtIntime.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtSuppler.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtVno.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtInsNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtyyymm.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtTid.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        txtDname.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        txtDCon.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


    }

    public void getDriverDetails(final String vehicle) {
        String url = Config.DRIVER_DETAILS;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    JSONArray array;
                    JSONObject object;
                    String driver_name="";
                    String dr_mobile="";
                    try{
                        array = new JSONArray(response);
                        for (int i=0; i<array.length(); i++){
                            object = array.getJSONObject(i);
                            driver_name = object.getString("DRIVERNAME");
                            dr_mobile = object.getString("CONTACT");
                        }
                        txtDname.setText(driver_name);
                        txtDCon.setText(dr_mobile);

                    } catch (JSONException e) {
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
                Map<String,String> pa = new HashMap<>();
                pa.put("VEHICLENO",vehicle);
                return pa;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String INVNO = txtInsNo.getText().toString().trim();
        final String SUPPLIERNAME = txtSuppler.getText().toString().trim();
        final String INTIME  = txtIntime.getText().toString().trim();
        final String YYYYMM  = txtyyymm.getText().toString().trim();

        final String DRIVERNAME = txtDname.getText().toString().trim();
        String DR_CONTACT = txtDCon.getText().toString();

        if(validate()){
            saveRow(VEHICLENO,VEHICLETYPE,CAPACITY,CUSTOMER,OWNER,ADDRESS,CONTACTNO,PANNUMBER,INSNUMBER,INSEXPIRYDATE,RTONUMBER,RTONOEXPDATE,POLLUTIONN0,POLLUTIONEXPDATE,TRANSITPASSNO,TRANSITEXPDATE,INVNO,SUPPLIERNAME,INTIME,YYYYMM,DRIVERNAME,DR_CONTACT,ll,USER_NAME);
        }

    }

    private boolean validate() {
        boolean valid = true;
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String DRIVERNAME = txtDname.getText().toString().trim();
        final String DR_CONTACT = txtDCon.getText().toString().trim();

        if(VEHICLENO.isEmpty()){
            txtVno.setError("Enter Vehical No");
            valid=false;
        }else{
            txtVno.setError(null);
        }if (DRIVERNAME.isEmpty()){
            txtDname.setError("Enter driver name.");
            valid=false;
        }else{
            txtDname.setError(null);
        }
        String regexStr = "^+[0-9]{10,13}$";
        String ss = "0123456789";
        if (ss.length() != DR_CONTACT.length() ){
            txtDCon.setError("Enter valid mobile no.");
            valid=false;
        }else{
            txtDCon.setError(null);
        }
        return valid;
    }

    private void saveRow(final String vehicleno,final String vehicletype, final String capacity, final String customer,
                         final String owner, final String address, final String contactno, final String pannumber,
                         final String insnumber, final String insexpirydate, final String rtonumber, final String rtonoexpdate,
                         final String pollutionn0, final String pollutionexpdate, final String transitpassno,
                         final String transitexpdate, final String invno, final String suppliername, final String intime, final String yyyymm, final String drivername, final String dr_contact,final String ll,final String user_name) {

        String urls = Config.NEWENTRY;
        dialog = new ProgressDialog(this);
        dialog.setMessage("please wait...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls, new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {

                if(response!=null){
                        btnSubmit.setVisibility(View.GONE);
                        Handler handler  = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                JSONArray array = null;
                                try {
                                    array = new JSONArray(response);
                                    for(int i=0; i<array.length(); i++){
                                        JSONObject object = array.getJSONObject(i);
                                        Toast.makeText(Tran_Veh_In.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                        getTpid();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();

                            }
                        },1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.hide();
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(getApplicationContext(),"Network error",Toast.LENGTH_SHORT).show();
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("VEHICLENO",vehicleno);
                params.put("VEHICLETYPE",vehicletype);
                params.put("CAPACITY",capacity);
                params.put("CUSTOMER",customer);
                params.put("OWNER",owner);
                params.put("ADDRESS",address);
                params.put("CONTACTNO",contactno);
                params.put("PANNUMBER",pannumber);
                params.put("INSNUMBER",insnumber);
                params.put("INSEXPIRYDATE",insexpirydate);
                params.put("RTONUMBER",rtonumber);
                params.put("RTONOEXPDATE",rtonoexpdate);
                params.put("POLLUTIONN0",pollutionn0);
                params.put("POLLUTIONEXPDATE",pollutionexpdate);
                params.put("TRANSITPASSNO",transitpassno);
                params.put("TRANSITEXPDATE",transitexpdate);
                params.put("INVNO",invno);
                params.put("SUPPLIERNAME",suppliername);
                params.put("INTIME",intime);
                params.put("YYYYMM",yyyymm);
                params.put("DRIVERNAME",drivername);
                params.put("DR_CONTACT",dr_contact);
                params.put("LOADUNLOAD",ll);
                params.put("APP_USER",user_name);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void getTpid() {
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String INTIME  = txtIntime.getText().toString().trim();
        String urlss = Config.TPID;
        StringRequest sr = new StringRequest(Request.Method.POST, urlss, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response!=null){
                    String  TID="";
                    try{
                        JSONArray array = new JSONArray(response);
                        for(int i=0; i<array.length(); i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            TID = jsonObject.getString("TID");
                        }
                        String ss = TID.toString();
                        if (!ss.isEmpty()){
                            txtTid.setText(TID);
                            genarateQRCode();
                            ggQRCode();
                        }else {
                            Toast.makeText(getApplicationContext(),"Hello, not agree",Toast.LENGTH_SHORT).show();
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
                Map<String, String> param = new HashMap<>();
                param.put("VEHICLENO",VEHICLENO);
                param.put("INTIME",INTIME);
                return param;
            }
        };
        requestQueue.add(sr);
    }

    private void ggQRCode() {
        final String TID = txtTid.getText().toString().trim();
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String DRIVERNAME = txtDname.getText().toString().trim();
        final String DR_CONTACT = txtDCon.getText().toString().trim();
        final String INTIME  = txtIntime.getText().toString().trim();
        final String INVNO = txtInsNo.getText().toString().trim();
        final String SUPPLIERNAME = txtSuppler.getText().toString().trim();

        Intent i = new Intent(getApplicationContext(), Prints.class);
        i.putExtra("TID",TID);
        i.putExtra("VEHICLENO",VEHICLENO);
        i.putExtra("DRIVERNAME",DRIVERNAME);
        i.putExtra("DR_CONTACT",DR_CONTACT);
        i.putExtra("INTIME",INTIME);
        i.putExtra("INVNO",INVNO);
        i.putExtra("SUPPLIERNAME",SUPPLIERNAME);
        i.putExtra("status_lod",ll);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    private void genarateQRCode() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Tran_Veh_In.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.custom_dialog, null);
        ImageView imageView = (ImageView)dialoglayout.findViewById(R.id.imageView);
        TextView txtId = (TextView)dialoglayout.findViewById(R.id.name);
        TextView txtVehicalNo = (TextView)dialoglayout.findViewById(R.id.address);
        TextView txtDriName = (TextView)dialoglayout.findViewById(R.id.driname);
        TextView txtdriContatc = (TextView)dialoglayout.findViewById(R.id.dricontact);
        TextView txtInvNo = (TextView)dialoglayout.findViewById(R.id.invno);
        TextView txtSname = (TextView)dialoglayout.findViewById(R.id.sname);
        builder.setView(dialoglayout);
        builder.show();

        final String TID = txtTid.getText().toString().trim();
        final String VEHICLENO = txtVno.getText().toString().trim();
        final String DRIVERNAME = txtDname.getText().toString().trim();
        final String DR_CONTACT = txtDCon.getText().toString().trim();
        final String INTIME  = txtIntime.getText().toString().trim();
        final String INVNO = txtInsNo.getText().toString().trim();
        final String SUPPLIERNAME = txtSuppler.getText().toString().trim();

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("TID",TID.toString().trim());
            jsonObj.put("VEHICLENO",VEHICLENO.toString().trim());
            jsonObj.put("DRIVERNAME",DRIVERNAME.toString().trim());
            jsonObj.put("DR_CONTACT",DR_CONTACT.toString().trim());

            String ss = jsonObj.toString();

            bitmap = TextToImageEncode(ss);
            imageView.setImageBitmap(bitmap);

            txtId.setText("Tans ID : "+TID);
            txtVehicalNo.setText("Vehicle No. : "+VEHICLENO);
            txtDriName.setText("Driver Name : "+DRIVERNAME);
            txtdriContatc.setText("Driver Contact : "+DR_CONTACT);
            txtInvNo.setText("INV No. : "+INVNO+"\n"+"Date : "+INTIME);
            txtSname.setText("Supplier Name : "+SUPPLIERNAME);

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Yes Button
        builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
    private Bitmap TextToImageEncode(String ss) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(ss,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
