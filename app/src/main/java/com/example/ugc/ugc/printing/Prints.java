package com.example.ugc.ugc.printing;

/**
 * Created by hp on 12/23/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;
import com.example.ugc.ugc.Home;
import com.example.ugc.ugc.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;


public class Prints extends Activity implements Runnable {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    Bitmap bitmap;
    private static OutputStream outputStream;
    private static byte[] readBuf = new byte[1024];

    String TID,VEHICLENO,DRIVERNAME,DR_CONTACT,INVNO,SUPPLIERNAME,INTIME,TITLE,statusload;
    ImageView imageView;
    TextView txtId,txtVehicalNo,txtDriName,txtdriContatc,txtInvNo,txtSname,txtDate,txtTitle;
    public final static int QRcodeWidth = 150 ;

    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.activity_prints);

        Intent i = getIntent();
        TID = i.getStringExtra("TID");
        VEHICLENO = i.getStringExtra("VEHICLENO");
        DRIVERNAME = i.getStringExtra("DRIVERNAME");
        DR_CONTACT = i.getStringExtra("DR_CONTACT");
        INTIME = i.getStringExtra("INTIME");
        INVNO = i.getStringExtra("INVNO");
        statusload = i.getStringExtra("status_lod");
        SUPPLIERNAME = i.getStringExtra("SUPPLIERNAME");

        imageView = (ImageView)findViewById(R.id.imageView);
        txtId = (TextView)findViewById(R.id.name);
        txtVehicalNo = (TextView)findViewById(R.id.address);
        txtDriName = (TextView)findViewById(R.id.driname);
        txtdriContatc = (TextView)findViewById(R.id.dricontact);
        txtInvNo = (TextView)findViewById(R.id.invno);
        txtSname = (TextView)findViewById(R.id.sname);
        txtDate = (TextView)findViewById(R.id.tranDate);
        txtTitle = (TextView)findViewById(R.id.titles);
        ganarateQRCode();
        TITLE = txtTitle.getText().toString();

        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(Prints.this, "Not found device", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(Prints.this, DeviceListActivity.class);
                        startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {

                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket.getOutputStream();

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                            byte[] byteArray = bytes.toByteArray();
                            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            byte[] command = Utils.decodeBitmap(bmp);

                            String BILL =
                                    "            IN\n"
                                    +"TID        :"+TID+"\n"
                                    +"VEHICLE No.:"+VEHICLENO+"\n"
                                    +"DRIVER NAME:"+DRIVERNAME+"\n"
                                    +"DRIVER No. :"+DR_CONTACT+"\n"
                                    +"INVNO      :"+INVNO+"\n"
                                    +"SUPP. NAME :"+SUPPLIERNAME+"\n"
                                    +"Lod. Stat:"+ statusload+"\n"
                                    +"DATE  :"+INTIME;

                            String TITILE = "UGC Supply Chain Soln. PVT. LTD.";
                            String TT   = "******* Thank You ********";
                            String BB = "     \n \n   \n";
                            String B = "\n";
                            os.write(TITILE.getBytes());
                            os.write(B.getBytes());
                            os.write(BILL.getBytes());
                            os.write(B.getBytes());
                            os.write(command);
                            os.write(B.getBytes());
                            os.write(TT.getBytes());
                            os.write(BB.getBytes());


                            // Setting height
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

        });

        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

    }// onCreate

    private void ganarateQRCode() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("TID",TID.toString().trim());
            jsonObj.put("VEHICLENO",VEHICLENO.toString().trim());

            String ss = jsonObj.toString();

            bitmap = TextToImageEncode(ss);
            imageView.setImageBitmap(bitmap);


            txtId.setText("Tans ID : "+TID);
            txtVehicalNo.setText("Vehicle No. : "+VEHICLENO);
            txtDriName.setText("Driver Name : "+DRIVERNAME);
            txtdriContatc.setText("Driver Contact : "+DR_CONTACT);
            txtInvNo.setText("INV No. : "+INVNO);
            txtSname.setText("Supplier Name : "+SUPPLIERNAME);
            txtDate.setText("Date  :"+INTIME);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private Bitmap TextToImageEncode(String ss) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(ss,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,QRcodeWidth,
                    QRcodeWidth, null);

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
        bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
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

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
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
                    Intent connectIntent = new Intent(Prints.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(Prints.this, "Message", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Prints.this, "Device is Connected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

}
