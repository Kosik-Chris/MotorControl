package j.chris.kosik.dualmotorcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.UUID;

import j.chris.kosik.dualmotorcontrol.Utils.DialogsUtils;
import j.chris.kosik.dualmotorcontrol.Utils.StringUtils;

public class MainActivity extends AppCompatActivity {

    private int progressStatus = 0;
    public double mmPerSecMove = 0.55;
    double totalTime;
    private Handler handler = new Handler();
    Timer timer = new Timer();

    Button stopBtn,discBtn,cycleBtn;

    String address = null;

    private Toast toast;
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

        //String Utils class
        final StringUtils appendString = new StringUtils();

        setContentView(R.layout.activity_main);


        //Controlled to only be number input
        final EditText distanceInput = findViewById(R.id.numericTxtInput);
        final EditText timeInput = findViewById(R.id.timeTxtInput);
        final EditText cycleInput = findViewById(R.id.numdutycyclesTxtInput);
        final TextView numCycleLeftText = findViewById(R.id.cyclesRemainingTxt);
        final ProgressBar cycleProgress = findViewById(R.id.pb);

        stopBtn = findViewById(R.id.StopBtn);
        cycleBtn = findViewById(R.id.cycleBtn);
        discBtn = findViewById(R.id.disconnectBtn);


        //Connect to Bluetooth
        new ConnectBT().execute();
        //Instantiate Signal class
//        final SendSignal signal = new SendSignal();

        //TODO: Append the string further before transmission. distance 4 char, pause 4 char, cycle 3 char
        //Define event listeners

        cycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int numCycles = Integer.parseInt(numCycleLeftText.getText().toString());
                String distance = appendString.appendDistance(distanceInput.getText().toString(),mainContext,toast);
                String time = appendString.appendDistance(timeInput.getText().toString(),mainContext,toast);
                String cycle = appendString.appendCycle(cycleInput.getText().toString(),mainContext,toast);
                cycleSignal(distance, time, cycle);

                //Create new thread to update the cycles!
                new Thread(new Runnable() {


                    @Override
                    public void run() {
                        while(progressStatus < 100){
                            progressStatus +=1;


                            try{
                                //multiply by 1000 to get milli seconds from user input - just pause time
                                //TODO: Figure out rate of movement and * by distance to get travel time
                                Thread.sleep(getCycleTime(Integer.parseInt(timeInput.getText().toString()),distanceInput));
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }

                            // Update the progress bar
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    cycleProgress.setProgress(progressStatus);
                                    // Show the progress on TextView
                                    numCycleLeftText.setText(numCycles);
                                }
                            });
                        }
                    }
                });
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send 0,0 to stop? have arduino do a return function?
                stopSignal();
            }
        });

        discBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public long getCycleTime(long timeInput, EditText distanceInput){
        //Receives a decimal of time (seconds)
        //mult by 1000 to get millisec = pause time in milliseconds
        //then need travel time
        long pauseTimeMillisec = timeInput*1000;
        //At time of this method call distance will be input
        long distanceTime = (Integer.parseInt(distanceInput.getText().toString())*(long)mmPerSecMove);
        long cycleTime = pauseTimeMillisec+(2*distanceTime);

        return cycleTime;
    }

    public double TotalTime(long timeInput, EditText distanceInput, int numCycles){
        totalTime = 0;
        //convert to milliseconds first
        double cycleTime = getCycleTime(timeInput,distanceInput);
        totalTime = cycleTime*numCycles;

        return totalTime;
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

    //**Signal Section

    public void forwardSignal(String distance){
        //the appended string to be sent
        String msgSend = "a"+distance+"z";

        if ( btSocket!= null ) {
            try {
                btSocket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                dialog.msg(mainContext,"Error in sending forward Signal.");
            }
        }
    }

    public void backwardsSignal(String distance){
        //the appended string to be sent
        String msgSend = "b"+distance+"z";
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                dialog.msg(mainContext,"Error in sending backwards Signal.");
            }
        }
    }

    public void startSignal(String distance, String time){
        //the appended string to be send
        String msgSend = "c"+distance+time+"z";
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                dialog.msg(mainContext,"Error in sending start Signal.");
            }
        }
    }

    public void stopSignal(){
        //the appended string to be send
        String msgSend = "dz";
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                dialog.msg(mainContext,"Error in sending stop Signal.");
            }
        }
    }

    public void cycleSignal(String distance, String time, String cycle){
        //the appended string to be send
        String msgSend = "e"+distance+time+cycle+"z";
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                dialog.msg(mainContext,"Error in sending cycle Signal.");
            }

        }
    }
}
