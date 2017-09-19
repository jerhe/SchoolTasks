package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.Location;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.base.BaseReleaseActivity;
import com.edu.schooltask.view.ContentView;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.PermissionUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import server.api.client.DynamicClient;
import server.api.client.QiniuClient;
import server.api.result.Result;

import static com.edu.schooltask.utils.PermissionUtil.LOCATION_PERMISSION;

public class ReleaseDynamicActivity extends BaseReleaseActivity {

    @BindView(R.id.rd_content) ContentView contentView;
    @BindView(R.id.rd_location) ImageTextView locationBtn;
    @BindView(R.id.rd_anonymous) Switch anonymousSwitch;

    @OnClick(R.id.rd_location)
    public void getLocation(){
        KeyBoardUtil.hideKeyBoard(this);
        if (PermissionUtil.checkLocationPermission(this))
            openActivityForResult(LocationActivity.class, LOCATION_REQUEST);
    }

    @OnCheckedChanged(R.id.rd_anonymous)
    public void anonymous(){
        dynamic.setAnonymous(anonymousSwitch.isChecked());
    }

    private Result releaseDynamicResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort("发布成功");
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result getDynamicUploadKeyResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            String imageUrl = dynamic.getUserId() + Calendar.getInstance().getTimeInMillis();
            dynamic.setImageUrl(imageUrl);
            QiniuClient.uploadDynamicImage(GsonUtil.toUploadKey(data), imageUrl, tempFiles);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            tempFiles.clear();
            progressDialog.dismiss();
            toastShort(error);
        }
    };

    private Dynamic dynamic = new Dynamic();

    @Override
    public int getLayout() {
        return R.layout.activity_release_dynamic;
    }

    @Override
    public void init() {
        initDialog(9);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dynamic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        release();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case LOCATION_REQUEST:
                if(data == null){
                    dynamic.setLocation("");
                    locationBtn.setText("我的位置");
                    locationBtn.setIcon(R.drawable.ic_location);
                }
                else{
                    String l;
                    Location location = (Location)data.getSerializableExtra("location");
                    if(location.getDetail() == null) l = location.getLocation();
                    else l = location.getCity() + " " + location.getLocation();
                    locationBtn.setText(l);
                    dynamic.setLocation(l);
                    locationBtn.setIcon(R.drawable.ic_location_light);
                    dynamic.setLatitude(location.getLatitude());
                    dynamic.setLongitude(location.getLongitude());
                }
                break;
        }
    }

    private void release(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user == null){
            EventBus.getDefault().post(new UnloginEvent());
            return;
        }
        dynamic.setSchool(user.getSchool());
        dynamic.setUserId(user.getUserId());
        int imageNum = paths.size();
        dynamic.setImageNum(imageNum);
        String content = contentView.getText();
        if(content.length() == 0 && imageNum == 0){
            toastShort("说点什么吧");
            return;
        }
        dynamic.setContent(content);
        KeyBoardUtil.hideKeyBoard(this);
        progressDialog = ProgressDialog.show(this, "", "发布中...", true, false);
        if(imageNum != 0){
            //压缩图片
            for(String path : paths){
                tempFiles.add(new Compressor.Builder(ReleaseDynamicActivity.this)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setQuality(100).build().compressToFile(new File(path)));
            }
            KeyBoardUtil.hideKeyBoard(this);
            QiniuClient.getUploadKey(getDynamicUploadKeyResult);
        }
        else{
            DynamicClient.releaseDynamic(releaseDynamicResult, dynamic);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ReleaseDynamicActivity.this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_REQUEST);
                }
                else toastShort("获取定位权限失败！请在设置中开启权限");
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean exit() {
        return StringUtil.isEmpty(contentView.getText()) && StringUtil.isEmpty(dynamic.getLocation());
    }

    @Override
    protected void uploadSuccess() {
        DynamicClient.releaseDynamic(releaseDynamicResult, dynamic);
    }

    @Override
    protected void uploadFailed() {
        toastShort("发布动态失败！");
    }
}
