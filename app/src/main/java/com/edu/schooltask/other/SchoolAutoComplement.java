package com.edu.schooltask.other;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.view.recyclerview.SchoolRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.TextRecyclerView;
import com.edu.schooltask.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/3.
 */

//学校自动补全
public class SchoolAutoComplement implements TextWatcher{
    private EditText editText;

    private List<String> allSchools;

    private PopupWindow popupWindow;
    private View popupView;
    private SchoolRecyclerView recyclerView;


    public SchoolAutoComplement(EditText editText, List<String> allSchools){
        this.editText = editText;
        this.allSchools = allSchools;
        initPopupWindow();
    }

    //初始化
    private void initPopupWindow(){
        popupView = LayoutInflater.from(editText.getContext()).inflate(R.layout.list_school,null);
        recyclerView = (SchoolRecyclerView) popupView.findViewById(R.id.school_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(popupView.getContext()));
        recyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() { //选择学校
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                editText.setText(recyclerView.get(position));
                editText.setSelection(editText.getText().length());
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(allSchools == null) return;
        if(allSchools.size() == 0) return;
        if(s.length() == 0) return;

        recyclerView.clear(true);
        String inputSchool = s.toString();
        List<String> findSchool = new ArrayList<>();
        for(String school : allSchools){
            if(school.indexOf(inputSchool) != -1 && !school.equals(inputSchool))
                findSchool.add(school);
        }
        recyclerView.add(findSchool);

        if(recyclerView.get().size()>3){
            popupWindow.setHeight(DensityUtil.dipToPx(editText.getContext(), 150));
        }else{
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        popupWindow.update();
        if(recyclerView.get().size() != 0) {
            recyclerView.scrollToPosition(0);
            popupWindow.showAsDropDown(editText);
        }
    }
}
