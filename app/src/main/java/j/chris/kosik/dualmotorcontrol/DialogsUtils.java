package j.chris.kosik.dualmotorcontrol;

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

}
