package j.chris.kosik.dualmotorcontrol.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class DialogsUtils {
    public static ProgressDialog showProgressDialog(Context context, String message){
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }

    public void msg (Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public void InitializeToast(Context context, Toast toast, String res){
        cancelToast(toast);
        if(toast == null){ // prevent create many instance of toast
            toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        }else{
            toast.setText(res);
        }
        toast.show();
    }

    private void cancelToast(Toast toast) {
        if (toast != null) {
            toast.cancel();
        }
    }

}
