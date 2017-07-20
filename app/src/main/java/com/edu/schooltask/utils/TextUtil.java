package com.edu.schooltask.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.SchoolAdapter;
import com.edu.schooltask.data.DataCache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
                String speChat="[`~!@#$%^&*+=|{}':;',\\\\[\\\\].<>/?~！ @#￥%……&*——+|{}【】‘；：”“’。，、？_-]";
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

    public static void setMoneyFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[0123456789.]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(!matcher.find())return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void setNumFilter(final EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[0123456789]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(!matcher.find())return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 设置长度限制
     * @param editText
     * @param maxLength
     */
    public static void setLengthFilter(final EditText editText, final int maxLength){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.length() + dest.length() > maxLength)return "";
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
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

    public static void setSchoolWatcher(final Context context, final EditText editText, DataCache dataCache){
        final ArrayList<String> schools = dataCache.getSchool();
        final List<String> findSchools = new ArrayList<>();
        final SchoolAdapter adapter = new SchoolAdapter(R.layout.item_school, findSchools);
        View view = LayoutInflater.from(context).inflate(R.layout.list_school,null);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.school_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        final PopupWindow schoolWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,false);
        schoolWindow.setBackgroundDrawable(new BitmapDrawable());
        schoolWindow.setOutsideTouchable(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                editText.setText(findSchools.get(position));
                editText.setSelection(editText.getText().length());
                schoolWindow.dismiss();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(schools == null) return;
                if(s.length()!=0){
                    findSchools.clear();
                    String name = s.toString();
                    for(String school : schools){
                        if(school.indexOf(name) != -1 && !school.equals(name))
                            findSchools.add(school);
                    }
                    adapter.notifyDataSetChanged();
                    if(findSchools.size()>3){
                        schoolWindow.setHeight(DensityUtil.dipToPx(context, 150));
                    }else{
                        schoolWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    }
                    schoolWindow.update();
                    if(findSchools.size() != 0) {
                        recyclerView.scrollToPosition(0);
                        schoolWindow.showAsDropDown(editText);
                    }
                }
            }
        });
    }

    public static String getRandStr(int length){
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randStr = new StringBuilder();
        for(int i=0; i<length; i++){
            int rand = new Random().nextInt(str.length());
            randStr.append(str.charAt(rand));
        }
        return randStr.toString();
    }

    public static boolean moneyCompile(String money){
        Pattern pattern = Pattern.compile("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
        Matcher matcher = pattern.matcher(money);
        return matcher.matches();
    }
}
