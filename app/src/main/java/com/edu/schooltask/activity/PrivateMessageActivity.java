package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.PrivateMessageAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.event.ReadMessageEvent;
import com.edu.schooltask.event.ShowMessageEvent;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.item.PrivateMessageItem;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.view.MessageInputBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import server.api.SchoolTask;
import server.api.qiniu.UploadMessageImageEvent;
import server.api.qiniu.GetMessageUploadKeyEvent;

public class PrivateMessageActivity extends BaseActivity {
    MessageInputBoard messageInputBoard;
    RecyclerView recyclerView;
    PrivateMessageAdapter adapter;
    List<PrivateMessageItem> messageItems = new ArrayList<>();

    public UserBaseInfo user;  //对方用户

    Uri imageUri;
    final static int CAMERA = 0;
    final static int PICTURE = 1;
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
            dialog.dismiss();
            switch (position){
                case 0: //拍照
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
                    break;
                case 1: //图库
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(albumIntent, PICTURE);
                    break;
            }
        }
    };
    List<String> imageSelectList = new ArrayList<>();
    BaseAdapter imageSelectAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return imageSelectList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageSelectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(PrivateMessageActivity.this).inflate(R.layout.item_text,null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(imageSelectList.get(position));
            return view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);
        privateMessageActivity = this;
        EventBus.getDefault().register(this);
        messageInputBoard = getView(R.id.pm_mib);
        messageInputBoard.setActivity(this);
        recyclerView = getView(R.id.pm_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        adapter = new PrivateMessageAdapter(messageItems, width/2);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PrivateMessageItem privateMessageItem = messageItems.get(position);
                if(privateMessageItem.getItemType() == PrivateMessageItem.SYSTEM){
                    Intent intent = new Intent(PrivateMessageActivity.this, TaskOrderActivity.class);
                    intent.putExtra("order_id", privateMessageItem.getPoll().getOrderId());
                    startActivity(intent);
                }
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                PrivateMessageItem item = messageItems.get(position);
                switch (view.getId()){
                    case R.id.pm_image:
                        Intent intent = new Intent(PrivateMessageActivity.this, ImageActivity.class);
                        ArrayList<ImageItem> images = new ArrayList<>();
                        images.add(new ImageItem(1, messageItems.get(position).getPoll().getImage()));
                        intent.putExtra("images", images);
                        startActivity(intent);
                        break;
                    case R.id.pm_head:
                        destroyUserActivity();
                        Intent userIntent = new Intent(PrivateMessageActivity.this, UserActivity.class);
                        userIntent.putExtra("user", item.getItemType() == 1 ? user : new UserBaseInfo(mDataCache.getUser()));
                        startActivity(userIntent);
                        break;
                }
            }
        });
        imageSelectList.add("拍照");
        imageSelectList.add("从相册中选择");
        messageInputBoard.setSendMessageListener(new MessageInputBoard.SendMessageListener() {
            @Override
            public void onSend(String message) {
                if(message.length() == 0) return;
                if(message.length() > 200){
                    toastShort("最多200个字符");
                    return;
                }
                SchoolTask.sendPrivateMessage(user.getUserId(), message, "",0,0);
                messageInputBoard.clear();
            }
        });
        messageInputBoard.setSelectImageListener(new MessageInputBoard.SelectImageListener() {
            @Override
            public void onSelectImage() {
                DialogUtil.createListDialog(PrivateMessageActivity.this,
                        imageSelectAdapter, itemClickListener).show();
            }
        });

        Intent intent = getIntent();
        user = (UserBaseInfo) intent.getSerializableExtra("user");
        setTitle(user.getName());
        User me = mDataCache.getUser();
        List<PrivateMessageItem> privateMessageItems =
                mDataCache.getData("privateMessage"+me.getUserId() + user.getUserId());
        if(privateMessageItems != null) messageItems.addAll(privateMessageItems);
        refreshMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        privateMessageActivity = null;
        EventBus.getDefault().unregister(this);
    }

    private void refreshMessage(){
        String lastTime = null;
        for(PrivateMessageItem messageItem : messageItems){
            if(lastTime == null){
                lastTime = messageItem.getPoll().getTime();
                messageItem.setShowTime(true);
            }
            else{
                String time = messageItem.getPoll().getTime();
                Calendar c1 = DateUtil.stringToCalendar(lastTime);
                c1.add(Calendar.MINUTE, 5);
                Calendar c2 = DateUtil.stringToCalendar(time);
                if(c2.after(c1)){
                    lastTime = time;
                    messageItem.setShowTime(true);
                }
                else{
                    messageItem.setShowTime(false);
                }
            }
            messageItem.getPoll().setHasRead(true);
        }
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageItems.size() - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(ShowMessageEvent event){
        Poll poll = event.getPoll();
        boolean isReceive = poll.getFromUser().getUserId().equals(user.getUserId());
        boolean isSend = poll.getToUser().getUserId().equals(user.getUserId());
        if(isReceive || isSend){
            PrivateMessageItem privateMessageItem = null;
            switch (poll.getType()){
                case 0:
                    privateMessageItem = new PrivateMessageItem(PrivateMessageItem.SYSTEM, poll);
                    break;
                case 1:
                    privateMessageItem = new PrivateMessageItem(
                            isReceive ? PrivateMessageItem.RECEIVE : PrivateMessageItem.SEND, poll);
                    break;
            }
            messageItems.add(privateMessageItem);
        }
        refreshMessage();
        EventBus.getDefault().post(new ReadMessageEvent(poll));
    }

    @Override
    public void onBackPressed() {
        if(messageInputBoard.isEmojiShow){
            messageInputBoard.hideEmoji();
        }
        else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA:
                if (resultCode == RESULT_OK) {
                    SchoolTask.getMessageImageUploadKey();
                }
                break;
            case PICTURE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    SchoolTask.getMessageImageUploadKey();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessageImageUploadKey(GetMessageUploadKeyEvent event){
        if(event.isOk()){
            File file = new File(imageUri.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            int imageWidth = bitmap.getWidth();
            int imageHeight = bitmap.getHeight();
            UploadKey uploadKey = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UploadKey>(){}.getType());
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

}
