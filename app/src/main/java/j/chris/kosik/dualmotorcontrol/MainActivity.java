package j.chris.kosik.dualmotorcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button forwardBtn,backwardsBtn,startBtn,stopBtn,discBtn,cycleBtn;

    String address = null;

    public Context mainContext;
    private ProgressDialog progress;
    public BluetoothAdapter myBluetooth = null;
    public BluetoothSocket btSocket = null;
    public boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Create new DialogUtils object
    DialogsUtils dialog = new DialogsUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mainContext = getApplicationContext();
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_main);

        //Controlled to only be number input
        final EditText distanceInput = findViewById(R.id.numericTxtInput);
        final EditText timeInput = findViewById(R.id.timeTxtInput);
        final EditText cycleInput = findViewById(R.id.numdutycyclesTxtInput);


        forwardBtn = findViewById(R.id.forwardBtn);
        backwardsBtn = findViewById(R.id.backwardsBtn);
        startBtn = findViewById(R.id.StartBtn);
        stopBtn = findViewById(R.id.StopBtn);
        cycleBtn = findViewById(R.id.cycleBtn);
        discBtn = findViewById(R.id.disconnectBtn);


        //Connect to Bluetooth
        new ConnectBT().execute();
        //Instantiate Signal class
        final SendSignal signal = new SendSignal();

        //TODO: Append the string further before transmission. distance 4 char, pause 4 char, cycle 3 char
        //Define event listeners
        forwardBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                signal.forwardSignal(distance,mainContext);
            }
        });

        backwardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                signal.backwardsSignal(distance,mainContext);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                String time = timeInput.getText().toString();
                signal.startSignal(distance,time,mainContext);
            }
        });

        cycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                String time = timeInput.getText().toString();
                String cycle = cycleInput.getText().toString();
                signal.cycleSignal(distance, time, cycle,mainContext);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send 0,0 to stop? have arduino do a return function?
                signal.stopSignal(mainContext);
            }
        });

        discBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }
    //TODO: Move this over into a seperate instance that can be passed context and used globally
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please Wait.");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                dialog.msg(mainContext,"Connection Failed. Check Bluetooth Connection Try again.");
                finish();
            } else {
                dialog.msg(mainContext,"Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
                Intent i = new Intent(MainActivity.this, DeviceList.class);
                startActivity(i);
            } catch(IOException e) {
                dialog.msg(mainContext,"Error Disconnecting!");
            }
        }

        finish();
    }
    public BluetoothSocket getBT(){
        return this.btSocket;
    }
}
