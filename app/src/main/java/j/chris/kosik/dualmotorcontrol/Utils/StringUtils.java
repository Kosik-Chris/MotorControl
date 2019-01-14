package j.chris.kosik.dualmotorcontrol.Utils;

import android.content.Context;
import android.widget.Toast;

public class StringUtils {

    private DialogsUtils dialog = new DialogsUtils();

    public String appendDistance(String editTxt, Context context, Toast toast){

        //User can only submit decimal / int values
        //Arduino needs 4 char msg: for exampl;e 00.5 for 0.5mm
        String editTxtApp = "";
        //handle if msg longer than 4 (should never happen)
        if(editTxt.length() > 4){
            dialog.InitializeToast(context,toast,"Too many characters");
        }
        else if(editTxt.length() == 0){
            //need to return 4 chars!
            dialog.InitializeToast(context,toast,"No Character read.");
        }
        else if(editTxt.length() == 1){
            //Check for decimal character input
            if(editTxt.equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else{
                //example: typed 5 => 05.0 sent
                editTxtApp = "0"+editTxt+".0";
            }
        }
        else if(editTxt.length() == 2){
            //. could be before or after number
            // example .5 or 5.
            //if . before
            if(editTxt.substring(0,0).equals(".")){
                editTxtApp = "00"+editTxt;
            }
            else if(editTxt.substring(0,1).equals(".")){
                editTxtApp = "0"+editTxt+"0";
            }
        }
        else if(editTxt.length() == 3){
            if(editTxt.substring(0,0).equals(".")){
                editTxtApp = "0"+editTxt;
            }
            else if(editTxt.substring(0,1).equals(".")){
                editTxtApp = "0"+editTxt;
            }
            else if(editTxt.substring(0,2).equals(".")){
                editTxtApp = editTxt+"0";
            }
        }
        return editTxtApp;

    }

    public String appendCycle(String editTxt, Context context, Toast toast){
        //User can only submit decimal / int values
        //Arduino needs 4 char msg: for exampl;e 00.5 for 0.5mm
        String editTxtApp = "";
        //handle if msg longer than 3 (should never happen)
        if(editTxt.length() > 3){
            dialog.InitializeToast(context,toast,"Too many characters");
        }
        else if(editTxt.length() == 0){
            //need to return 4 chars!
            dialog.InitializeToast(context,toast,"No Character read.");
        }
        else if(editTxt.length() == 1){
            //Check for decimal character input
            if(editTxt.equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else{
                //example: typed 5 => 05.0 sent
                editTxtApp = "00"+editTxt;
            }
        }
        else if(editTxt.length() == 2){
            //. could be before or after number
            // example .5 or 5.
            //if . before
            if(editTxt.substring(0,0).equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else if(editTxt.substring(0,1).equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else{
                editTxtApp = "0"+editTxt;
            }
        }
        else{
            if(editTxt.substring(0,0).equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else if(editTxt.substring(0,1).equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else if(editTxt.substring(0,2).equals(".")){
                dialog.InitializeToast(context,toast,"Invalid single character. Enter a value.");
            }
            else{
                editTxtApp = editTxt;
            }
        }

        return editTxtApp;
    }
}
