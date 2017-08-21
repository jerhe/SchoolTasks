package com.edu.schooltask.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.PrivateMessageAdapter;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.ShowMessageEvent;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.beans.PrivateMessage;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.MessageUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.MessageInputBoard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.qiniu.UploadMessageImageEvent;
import server.api.qiniu.GetMessageUploadKeyEvent;

public class PrivateMessageActivity extends BaseActivity {
    @BindView(R.id.pm_mib) MessageInputBoard messageInputBoard;
    @BindView(R.id.pm_rv) RecyclerView recyclerView;

    PrivateMessageAdapter adapter;
    List<PrivateMessage> allMessages = new ArrayList<>();
    List<PrivateMessage> privateMessages = new ArrayList<>();

    public UserInfo user;  //私信用户

    int offset = 0;   //消息序号

    Uri imageUri;
    final static int CAMERA = 0;
    final static int PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        messageInputBoard.setActivity(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //获取宽度用于图片显示计算大小
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        adapter = new PrivateMessageAdapter(privateMessages, width/2);
        recyclerView.setAdapter(adapter);
        //开启下拉加载历史消息
        adapter.setUpFetchEnable(true);
        adapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                upFetch();
            }
        });
        //点击订单消息事件
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PrivateMessage privateMessage = privateMessages.get(position);
                if(privateMessage.getItemType() == PrivateMessage.SYSTEM){
                    openOrderInfo(privateMessage.getPoll().getOrderId());
                }
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.pm_image:
                        showImage(position);
                        break;
                    case R.id.pm_head:
                        openUserHomePage(privateMessages.get(position).getItemType());
                        break;
                }
            }
        });

        messageInputBoard.setSendMessageListener(new MessageInputBoard.SendMessageListener() {
            @Override
            public void onSend(String message) {
                sendPrivateMessage(message);
            }
        });
        messageInputBoard.setSelectImageListener(new MessageInputBoard.SelectImageListener() {
            @Override
            public void camera() {
                camera();
            }

            @Override
            public void selectPicture() {
                selectPicture();
            }
        });

        //设置标题
        Intent intent = getIntent();
        user = (UserInfo) intent.getSerializableExtra("user");
        setTitle(user.getName());
        //更新消息
        refreshMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageUtil.isPrivateMessage = false;
        EventBus.getDefault().unregister(this);
    }

    //发送私信
    private void sendPrivateMessage(String message){
        if(message.length() == 0) return;
        if(message.length() > 200){
            toastShort("最多200个字符");
            return;
        }
        SchoolTask.sendPrivateMessage(user.getUserId(), message, "",0,0);
        messageInputBoard.clear();
    }

    //拍照
    private void camera(){
        File outputImage = new File(getExternalCacheDir(), "camera_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(PrivateMessageActivity.this, "com.edu.camera.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA);
    }

    //从图库中选择图片
    private void selectPicture(){
        //检测权限
        if (ContextCompat.checkSelfPermission(PrivateMessageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrivateMessageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(albumIntent, PICTURE);
        }
    }

    //打开订单信息
    private void openOrderInfo(String orderId){
        Intent intent = new Intent(PrivateMessageActivity.this, TaskOrderActivity.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
    }

    //打开用户主页:根据消息类型判断是自身还是对象
    private void openUserHomePage(int type){
        destroyUserActivity();
        Intent userIntent = new Intent(PrivateMessageActivity.this, UserActivity.class);
        userIntent.putExtra("user", type == 1 ? user : new UserInfo(UserUtil.getLoginUser()));
        startActivity(userIntent);
    }

    //浏览图片:消息序号用于获取消息中的图片
    private void showImage(int position){
        Intent intent = new Intent(PrivateMessageActivity.this, ImageActivity.class);
        ArrayList<ImageItem> images = new ArrayList<>();
        images.add(new ImageItem(1, privateMessages.get(position).getPoll().getImage()));
        intent.putExtra("images", images);
        startActivity(intent);
    }


    //刷新消息
    private void refreshMessage(){
        allMessages.clear();
        allMessages.addAll(mDataCache.getPrivateMessage(UserUtil.getLoginUser().getUserId(), user.getUserId()));
        String lastTime = null;
        for(PrivateMessage privateMessage : allMessages){
            if(lastTime == null){
                lastTime = privateMessage.getPoll().getCreateTime();
                privateMessage.setShowTime(true);
            }
            else{
                String time = privateMessage.getPoll().getCreateTime();
                Calendar c1 = DateUtil.stringToCalendar(lastTime);
                c1.add(Calendar.MINUTE, 5);
                Calendar c2 = DateUtil.stringToCalendar(time);
                if(c2.after(c1)){
                    lastTime = time;
                    privateMessage.setShowTime(true);
                }
                else{
                    privateMessage.setShowTime(false);
                }
            }
        }
        //初始加载
        if(privateMessages.size() == 0){
            for(int i=0; i<10 && i<allMessages.size(); i++){
                privateMessages.add(0, allMessages.get(allMessages.size() - i - 1));
                offset++;
            }
        }
        else{
            //添加消息后加载
            PrivateMessage lastMessage = privateMessages.get(privateMessages.size() - 1);
            int index = allMessages.indexOf(lastMessage) + 1;
            for(int i=index; i<allMessages.size(); i++){
                privateMessages.add(allMessages.get(i));
                offset++;
            }
        }

        mDataCache.setHasRead(UserUtil.getLoginUser().getUserId(), user.getUserId());
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(privateMessages.size() - 1);
    }

    //上拉加载历史消息
    private void upFetch(){
        adapter.setUpFetching(true);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i=offset; i<offset + 10 && i < allMessages.size(); i++){
                    adapter.addData(0, allMessages.get(allMessages.size() - i - 1));
                    offset ++;
                }
                adapter.setUpFetching(false);
                if (offset >= allMessages.size()) {
                    adapter.setUpFetchEnable(false);
                }
            }
        }, 300);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(ShowMessageEvent event){
        refreshMessage();
    }

    @Override
    public void onBackPressed() {
        if(messageInputBoard.isEmojiShow){
            messageInputBoard.hideEmoji();
        }
        else{
            MessageUtil.close();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case CAMERA:
                    SchoolTask.getMessageImageUploadKey();
                    break;
                case PICTURE:
                    imageUri = data.getData();
                    SchoolTask.getMessageImageUploadKey();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageUtil.open(user.getUserId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessageImageUploadKey(GetMessageUploadKeyEvent event){
        if(event.isOk()){
            File file = new File(imageUri.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            int imageWidth = bitmap.getWidth();
            int imageHeight = bitmap.getHeight();
            UploadKey uploadKey = GsonUtil.toUploadKey(event.getData());
            SchoolTask.uploadMessageImage(file,imageWidth, imageHeight, uploadKey);
        }
        else{
            toastShort("发送失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadMessageImage(UploadMessageImageEvent event){
        if (event.isOk()){
            SchoolTask.sendPrivateMessage(user.getUserId(), "", event.getImage(),
                    event.getImageWidth(), event.getImageHeight());
        }
        else{
            toastShort("发送失败");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case 1: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(albumIntent, PICTURE);
                    } else {
                        toastShort("获取权限失败");
                    }
                    return;
                }
            }
    }
}
