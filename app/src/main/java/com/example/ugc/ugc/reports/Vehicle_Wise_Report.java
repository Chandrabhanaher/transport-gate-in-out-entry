package com.example.ugc.ugc.reports;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ugc.ugc.Config;
import com.example.ugc.ugc.R;
import com.example.ugc.ugc.printing.DeviceListActivity;
import com.example.ugc.ugc.printing.UnicodeFormatter;
import com.example.ugc.ugc.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



public class Vehicle_Wise_Report extends AppCompatActivity implements Runnable {
    private RecyclerView transReport;
    ArrayList<Date_Report> disNewsList;
    RecyclerView.LayoutManager layoutManager;
    private Date_Adapter date_adapter;
    RequestQueue requestQueue;
    SessionManager session;
    String CUST_NAME;
    String VEHICLENO;
    private SearchView searchView;

//    Printing
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    JSONArray array;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle__wise__report);

        requestQueue = Volley.newRequestQueue(this);

        Intent i = getIntent();
        VEHICLENO = i.getStringExtra("VEHICLENO");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        CUST_NAME = user.get(SessionManager.USER_CUST);




         showReport(VEHICLENO,CUST_NAME);

        transReport = (RecyclerView)findViewById(R.id.itemsss);
        disNewsList = new ArrayList<>();

        transReport.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        transReport.setLayoutManager(layoutManager);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_scan:
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Toast.makeText(Vehicle_Wise_Report.this, "Not found device", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent,
                                        REQUEST_ENABLE_BT);
                            } else {
                                ListPairedDevices();
                                Intent connectIntent = new Intent(Vehicle_Wise_Report.this, DeviceListActivity.class);
                                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                            }
                        }
                        break;
                    case R.id.navigation_print:
                        printing();
                        break;
                    case R.id.navigation_disc:
                        if (mBluetoothAdapter != null)
                            mBluetoothAdapter.disable();
                        Toast.makeText(getApplicationContext(),"Disconnect",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }
    private void printing() {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket.getOutputStream();
                    String SST = ("           Report               ");
                    os.write(SST.getBytes());
                    String ST = ("VehicleNo"+" "+"Supp.Name"+" "+"INTime"+" "+"OutTime");
                    String dd = ("================================");
                    String nn = "\n";
                    os.write(ST.getBytes());
                    os.write(nn.getBytes());
                    os.write(dd .getBytes());
                    for(int ii = 0; ii<array.length();ii++){
                        JSONObject object = array.getJSONObject(ii);
                        String vno = object.getString("VEHICLENO");
                        String sname = object.getString("SUPPLIERNAME");
                        String intime = object.getString("INTIME");
                        String outTi = object.getString("OUTTIME");
                        String SS;
                        String tt = "null";
                        if(tt.equals(outTi))
                        {
                            SS = (vno+"  "+sname+"  "+intime+"  ");

                        }else{
                            SS = (vno+"  "+sname+"  "+intime+"  "+outTi);

                        }
                        os.write(nn.getBytes());
                        os.write(SS.getBytes());
                        os.write(nn.getBytes());
                    }
                    os.write(nn.getBytes());
                    os.write(nn.getBytes());

//                     Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("Prints", "Exe ", e);
                }
            }
        };
        t.start();

    }
    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }
    public void onActivityResult(int mRequestCode, int mResultCode, Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();

                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(Vehicle_Wise_Report.this, DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(Vehicle_Wise_Report.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(Vehicle_Wise_Report.this, "Device is Connected", Toast.LENGTH_SHORT).show();
        }
    };
    private void showReport(final String vehicleno, final String cust_name) {
        String url = Config.VEHICLE_REPORT;
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!=null){

                            try {
                                array = new JSONArray(response);
                                for(int i=0;i<array.length();i++){
                                    Date_Report dr = new Date_Report();
                                    JSONObject data = array.getJSONObject(i);
                                    dr.setVEHICLENO(data.getString("VEHICLENO"));
                                    dr.setSUPPLIERNAME(data.getString("SUPPLIERNAME"));
                                    dr.setINTIME(data.getString("INTIME"));
                                    dr.setOUTTIME(data.getString("OUTTIME"));

                                    disNewsList.add(dr);
                                }
                                date_adapter = new Date_Adapter(Vehicle_Wise_Report.this,disNewsList);
                                transReport.setAdapter(date_adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("VEHICLENO",vehicleno);
                params.put("CUSTOMER",cust_name);
                return params;
            }
        };
        requestQueue.add(sr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_data, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                date_adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                date_adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.g_pdf) {

            /*transReport.measure(
                    View.MeasureSpec.makeMeasureSpec(transReport.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            Bitmap bm = Bitmap.createBitmap(transReport.getWidth(), transReport.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            transReport.draw(new Canvas(bm));

            //saveImage(bm);
            ImageView im = new ImageView(this);
            im.setImageBitmap(bm);
            new AlertDialog.Builder(this).setView(im).show();


            return true;*/

        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

}
