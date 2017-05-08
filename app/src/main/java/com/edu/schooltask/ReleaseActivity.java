package com.edu.schooltask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import beans.User;
import http.HttpCheckToken;
import http.HttpResponse;
import http.HttpUtil;
import utils.TextUtil;
import view.Content;
import view.InputText;

public class ReleaseActivity extends BaseActivity {
    private InputText schoolText;
    private InputText titleText;
    private Content contentText;
    private InputText costText;
    private InputText limitTimeText;
    private Button releaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        schoolText = (InputText) findViewById(R.id.release_school);
        titleText = (InputText) findViewById(R.id.release_title);
        contentText = (Content) findViewById(R.id.release_content);
        costText = (InputText) findViewById(R.id.release_cost);
        limitTimeText = (InputText) findViewById(R.id.release_limit_time);
        releaseBtn = (Button) findViewById(R.id.release_release);

        schoolText.setInputFilter(1);
        titleText.setLengthFilter(20);  //设置标题长度限制
        costText.setInputFilter(4);
        limitTimeText.setInputFilter(5);

        schoolText.setText(user.getSchool());

        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        titleText.requestFocus();   //设置标题为默认焦点
    }

    private void release(){
        if(releaseBtn.getText().equals("发布中..."))return;
        final String school = schoolText.getText();
        final String title = titleText.getText();
        final String content = contentText.getText();
        final String cost = costText.getText();
        final String limitTime = limitTimeText.getText();

        if(school.length() == 0){
            TextUtil.toast(this, "请输入学校");
            return;
        }
        if(title.length() == 0){
            TextUtil.toast(this, "请输入标题");
            return;
        }
        if(content.length() == 0){
            TextUtil.toast(this, "请输入内容");
            return;
        }
        if(cost.length() == 0){
            TextUtil.toast(this, "请输入金额");
            return;
        }
        if(limitTime.length() == 0){
            TextUtil.toast(this, "请输入时限");
            return;
        }
        if(".".equals(cost)){
            TextUtil.toast(this, "金额错误，请重新输入");
            costText.clean();
            return;
        }
        final float money = Float.parseFloat(cost);
        if(money < 1){
            TextUtil.toast(this, "最小金额为1元，请重新输入");
            costText.clean();
            return;
        }
        final int time = Integer.parseInt(limitTime);
        if(time == 0 || time >= 7 * 24){
            TextUtil.toast(this, "时限错误,请重新输入");
            limitTimeText.clean();
            return;
        }

        releaseBtn.setText("发布中...");
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "发布中...", true, false);
        HttpUtil.postWithToken(this, user, new HttpCheckToken() {
            @Override
            public void handler() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        releaseBtn.setText("发布");
                        if(isSuccess){
                            HttpUtil.release(user.getUserId(), school, title, content, money, time, new HttpResponse() {
                                @Override
                                public void handler() throws Exception {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            switch (code){
                                                case 0:
                                                    TextUtil.toast(ReleaseActivity.this, "发布成功");
                                                    finish();
                                                    break;
                                                default:
                                                    TextUtil.toast(ReleaseActivity.this, "发布失败");
                                            }
                                        }
                                    });

                                }
                            });
                        }
                        else{
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        });
    }
}
