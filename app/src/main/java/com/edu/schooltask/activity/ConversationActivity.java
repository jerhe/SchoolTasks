package com.edu.schooltask.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;

public class ConversationActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_conversation;
    }

    @Override
    public void init() {
        setTitle(getIntent().getData().getQueryParameter("title"));
        View spaceView = findViewById(R.id.toolbar_space);
        TextView titleText = (TextView) findViewById(R.id.toolbar_title2);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            spaceView.setVisibility(View.VISIBLE);
            titleText.setText(bundle.getString("school"));
            titleText.setVisibility(View.VISIBLE);
        }
        else{
            spaceView.setVisibility(View.GONE);
            titleText.setVisibility(View.GONE);
        }
    }
}
