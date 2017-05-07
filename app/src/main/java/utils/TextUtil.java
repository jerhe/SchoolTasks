package utils;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class TextUtil {
    public static void setPhoneFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[0123456789]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(!matcher.find())return "";
                if(source.length() + dest.length() > 11)return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void setSchoolFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！ @#￥%……&*（）——+|{}【】‘；：”“’。，、？_-]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                if(source.length() + dest.length() > 20)return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void setNameFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.length() + dest.length() > 8)return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void setPwdFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\]<>/?~！ @#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                if(source.length() + dest.length() > 16)return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static String getMD5(String val) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }

    /**
     * 弹出软键盘
     * @param editText
     */
    public static void inputBoard(final EditText editText){
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                InputMethodManager inputManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 300);    //延时300毫秒
    }

    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideBoard(Activity activity){
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);}
    }
}
