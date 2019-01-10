package j.chris.kosik.dualmotorcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button forwardBtn,backwardsBtn,startBtn,stopBtn,discBtn,cycleBtn;

    String address = null;

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_main);

        final EditText distanceInput = findViewById(R.id.numericTxtInput);
        final EditText timeInput = findViewById(R.id.timeTxtInput);
        final EditText cycleInput = findViewById(R.id.numdutycyclesTxtInput);


        forwardBtn = findViewById(R.id.forwardBtn);
        backwardsBtn = findViewById(R.id.backwardsBtn);
        startBtn = findViewById(R.id.StartBtn);
        stopBtn = findViewById(R.id.StopBtn);
        cycleBtn = findViewById(R.id.cycleBtn);
        discBtn = findViewById(R.id.disconnectBtn);


        new ConnectBT().execute();

        forwardBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String distancea = distanceInput.getText().toString();
                sendSignal("a"+distancea+"z");
            }
        });

        backwardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distanceb = distanceInput.getText().toString();
                sendSignal("b"+distanceb+"z");
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                String time = timeInput.getText().toString();
                sendSignal("c"+distance+time+"z");
            }
        });

        cycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distance = distanceInput.getText().toString();
                String time = timeInput.getText().toString();
                String cycle = cycleInput.getText().toString();
                sendSignal("e"+distance+time+cycle+"z");
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send 0,0 to stop? have arduino do a return function?
                sendSignal("dz");
            }
        });

        discBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }

    private void sendSignal ( String value ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(value.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void sendCycleSignal(String time, String cycles){
        if (btSocket != null){
            try{
                btSocket.getOutputStream().write(time.toString().getBytes());
                btSocket.getOutputStream().write(cycles.toString().getBytes());
            }catch (IOException e){
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
                Intent i = new Intent(MainActivity.this, DeviceList.class);
                startActivity(i);
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

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
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
}
