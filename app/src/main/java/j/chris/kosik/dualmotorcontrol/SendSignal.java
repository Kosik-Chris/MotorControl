package j.chris.kosik.dualmotorcontrol;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;

public class SendSignal {

    private MainActivity main = new MainActivity();
    private BluetoothSocket socket = main.getBT();

    public void forwardSignal(String distance, Context context){
        //the appended string to be sent
        String msgSend = "a"+distance+"z";

        if ( socket != null ) {
            try {
                socket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                main.dialog.msg(context,"Error in sending forward Signal.");
            }
        }
    }

    public void backwardsSignal(String distance, Context context){
        //the appended string to be sent
        String msgSend = "b"+distance+"z";
        if ( socket != null ) {
            try {
                socket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                main.dialog.msg(context,"Error in sending backwards Signal.");
            }
        }
    }

    public void startSignal(String distance, String time, Context context){
        //the appended string to be send
        String msgSend = "c"+distance+time+"z";
        if ( socket != null ) {
            try {
                socket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                main.dialog.msg(context,"Error in sending start Signal.");
            }
        }
    }

    public void stopSignal(Context context){
        //the appended string to be send
        String msgSend = "dz";
        if ( socket != null ) {
            try {
                socket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                main.dialog.msg(context,"Error in sending stop Signal.");
            }
        }
    }

    public void cycleSignal(String distance, String time, String cycle, Context context){
        //the appended string to be send
        String msgSend = "e"+distance+time+cycle+"z";
        if ( socket != null ) {
            try {
                socket.getOutputStream().write(msgSend.getBytes());
            } catch (IOException e) {
                main.dialog.msg(context,"Error in sending cycle Signal.");
            }
        }
    }

}
