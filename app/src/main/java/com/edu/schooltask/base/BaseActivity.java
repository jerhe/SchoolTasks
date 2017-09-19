package com.edu.schooltask.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.schooltask.R;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.other.DataCache;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.activity.ImageSelectActivity;
import com.edu.schooltask.activity.ReleaseCommentActivity;
import com.edu.schooltask.activity.ReplyActivity;
import com.edu.schooltask.activity.UserActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.edu.schooltask.activity.ReleaseCommentActivity.RELEASE_TYPE_DYNAMIC;
import static com.edu.schooltask.activity.ReleaseCommentActivity.RELEASE_TYPE_ORDER;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public final static int SELECT_IMAGE_REQUEST = 0;   //从图库中选取多张图片
    public final static int ALBUM_PICK_REQUEST = 1;     //从图库中选取一张图片
    public final static int CAMERA_REQUEST = 2;
    public static final int LOCATION_REQUEST = 3;
    public static final int COMMENT_REQUEST = 4;

    protected static List<BaseActivity> activities = new ArrayList<>();
    protected Toolbar toolbar;
    protected TextView titleText;
    protected ViewGroup layout;
    protected Toast toast;
    protected static DataCache mDataCache;

    public static int foregroundCount = 0;  //前台Activity计数

    public abstract int getLayout();
    public abstract void init();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        if(mDataCache == null) mDataCache = new DataCache(this);
        activities.add(this);
        layout = getView(R.id.layout);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        foregroundCount ++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foregroundCount --;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initActionBar();
        initStateBar();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if(titleText!=null) titleText.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuResId = createMenu();
        if(menuResId != 0){
            getMenuInflater().inflate(menuResId, menu);
            return true;
        }
        return false;
    }

    public int createMenu(){
        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuClick(item.getItemId());
    }

    public boolean menuClick(int menuId){
        return false;
    }

    //-----------------------------------------------------------

    protected void initStateBar(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLACK);
        }
    }

    protected void initActionBar(){
        View v = findViewById(R.id.toolbar);
        initStateBar();
        if(v != null){
            toolbar = (Toolbar)v;
            setSupportActionBar(toolbar);
            titleText = (TextView)v.findViewById(R.id.toolbar_title);
            if(titleText!=null){
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置返回按钮
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {   //返回按钮事件
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public static DataCache getDataCache(){
        return mDataCache;
    }

    //判断应用是否在前台
    public static boolean isForeground(){
        return foregroundCount > 0;
    }

    //获取打开的活动
    public static List<BaseActivity> getActivities(){
        return activities;
    }

    //-----------------------------------------------------------

    public void addView(View view){
        if(layout != null) layout.addView(view);
    }

    protected <T extends View> T getView(int resId){
        return (T) findViewById(resId);
    }

    public static void remainHome(){
        for(int i=activities.size()-1; i>0; i--){
            activities.get(i).finish();
        }
    }

    public static void destroyUserActivity(){
        for(int i=activities.size()-1; i>0; i--){
            Activity activity = activities.get(i);
            if(activity.getClass().equals(UserActivity.class)) activity.finish();
        }
    }

    public void openActivity(Class cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void openActivity(Class cls, String parameterName, Serializable value){
        Intent intent = new Intent(this, cls);
        intent.putExtra(parameterName, value);
        startActivity(intent);
    }


    public void openImageSelectActivity(ArrayList<String> paths, int maxSize){
        Intent imageIntent = new Intent(this, ImageSelectActivity.class);
        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxSize);
        imageIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        imageIntent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
        startActivityForResult(imageIntent, SELECT_IMAGE_REQUEST);
    }

    public void openActivityForResult(Class cls, int requestCode){
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    public void openOrderCommentActivity(String orderId){
        Intent intent = new Intent(this, ReleaseCommentActivity.class);
        intent.putExtra("releaseType", RELEASE_TYPE_ORDER);
        intent.putExtra("orderId", orderId);
        startActivityForResult(intent, COMMENT_REQUEST);
    }

    public void openOrderCommentActivity(String orderId, int commentId, String name, boolean isParent){
        Intent intent = new Intent(this, ReleaseCommentActivity.class);
        intent.putExtra("releaseType", RELEASE_TYPE_ORDER);
        intent.putExtra("orderId", orderId);
        intent.putExtra("commentId", commentId);
        intent.putExtra("toName", name);
        intent.putExtra("isParent", isParent);
        startActivityForResult(intent, COMMENT_REQUEST);
    }

    public void openDynamicCommentActivity(int dynamicId){
        Intent intent = new Intent(this, ReleaseCommentActivity.class);
        intent.putExtra("releaseType", RELEASE_TYPE_DYNAMIC);
        intent.putExtra("dynamicId", dynamicId);
        startActivityForResult(intent, COMMENT_REQUEST);
    }

    public void openDynamicCommentActivity(int dynamicId, int commentId, String name, boolean isParent){
        Intent intent = new Intent(this, ReleaseCommentActivity.class);
        intent.putExtra("releaseType", RELEASE_TYPE_DYNAMIC);
        intent.putExtra("dynamicId", dynamicId);
        intent.putExtra("commentId", commentId);
        intent.putExtra("toName", name);
        intent.putExtra("isParent", isParent);
        startActivityForResult(intent, COMMENT_REQUEST);
    }

    public void openReplyActivity(int commentType, int commentId, String orderId, int dynamicId,
                                  int replyCount, String name){
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("commentType", commentType);
        intent.putExtra("commentId", commentId);
        intent.putExtra("orderId", orderId);
        intent.putExtra("dynamicId", dynamicId);
        intent.putExtra("replyCount", replyCount);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    //打开相机
    public Uri openCamera(){
        Uri imageUri;
        String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".png";
        File outputImage = new File(getExternalCacheDir(), fileName);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.edu.camera.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        return imageUri;
    }

    public void openAlbumActivity(){
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_PICK_REQUEST);
    }

    public View inflate(int layoutId){
        return LayoutInflater.from(this).inflate(layoutId, null);
    }

    public void toast(final String text, final int duration){
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), text, duration);
                    } else {
                        toast.setText(text);
                        toast.setDuration(duration);
                    }
                    toast.show();
                }
            });
        }
    }

    public void toastShort(String text){
        toast(text, Toast.LENGTH_SHORT);
    }

    public void toastLong(String text){
        toast(text, Toast.LENGTH_LONG);
    }

    public String s(int stringId){
        return getString(stringId);
    }

    public String s(int stringId, String str){
        return getString(stringId, str);
    }

}
