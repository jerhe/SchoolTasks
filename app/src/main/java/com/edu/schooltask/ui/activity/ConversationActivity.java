package com.edu.schooltask.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;

public class ConversationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
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
