package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

public class ImageSelectActivity extends BaseActivity implements MultiImageSelectorFragment.Callback{

    private ArrayList<String> resultList = new ArrayList();
    private TextView finishedButton;
    private int mDefaultCount = 9;

    public ImageSelectActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        finishedButton = (TextView) findViewById(R.id.is_finished);

        Intent intent = this.getIntent();
        mDefaultCount = intent.getIntExtra("max_select_count", 9);
        int mode = intent.getIntExtra("select_count_mode", 1);
        boolean isShow = intent.getBooleanExtra("show_camera", true);
        if(mode == 1 && intent.hasExtra("default_list")) {
            resultList = intent.getStringArrayListExtra("default_list");
        }

        if(mode == 1) {
            this.updateDoneText(this.resultList);
            finishedButton.setVisibility(View.VISIBLE);
            finishedButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(ImageSelectActivity.this.resultList != null && ImageSelectActivity.this.resultList.size() > 0) {
                        Intent data = new Intent();
                        data.putStringArrayListExtra("select_result", ImageSelectActivity.this.resultList);
                        ImageSelectActivity.this.setResult(-1, data);
                    } else {
                        ImageSelectActivity.this.setResult(0);
                    }

                    ImageSelectActivity.this.finish();
                }
            });
        } else {
            finishedButton.setVisibility(View.GONE);
        }

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("max_select_count", this.mDefaultCount);
            bundle.putInt("select_count_mode", mode);
            bundle.putBoolean("show_camera", isShow);
            bundle.putStringArrayList("default_list", this.resultList);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.is_content, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }

    }

    private void updateDoneText(ArrayList<String> resultList) {
        int size = 0;
        if(resultList != null && resultList.size() > 0) {
            size = resultList.size();
            finishedButton.setEnabled(true);
        } else {
            finishedButton.setText("完成");
            finishedButton.setEnabled(false);
        }
        finishedButton.setText( "完成(" + Integer.valueOf(size) + "/" + Integer.valueOf(this.mDefaultCount) +  ")");
    }

    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        this.resultList.add(path);
        data.putStringArrayListExtra("select_result", this.resultList);
        this.setResult(-1, data);
        this.finish();
    }

    public void onImageSelected(String path) {
        if(!this.resultList.contains(path)) {
            this.resultList.add(path);
        }

        this.updateDoneText(this.resultList);
    }

    public void onImageUnselected(String path) {
        if(this.resultList.contains(path)) {
            this.resultList.remove(path);
        }

        this.updateDoneText(this.resultList);
    }

    public void onCameraShot(File imageFile) {
        //TODO 权限
        if(imageFile != null) {
            this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(imageFile)));
            Intent data = new Intent();
            this.resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra("select_result", this.resultList);
            this.setResult(-1, data);
            this.finish();
        }

    }
}
