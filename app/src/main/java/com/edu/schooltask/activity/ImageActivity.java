package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImagePagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.item.ImageItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends BaseActivity {
    @BindView(R.id.image_vp) ViewPager viewPager;
    @BindView(R.id.image_back) ImageView back;
    @BindView(R.id.image_delete) ImageView delete;

    @OnClick(R.id.image_back)
    public void back(){
        finish();
    }

    @OnClick(R.id.image_delete)
    public void delete(){
        if(editable){
            EventBus.getDefault().post(new DeleteImageEvent(viewPager.getCurrentItem()));
            imageViews.remove(viewPager.getCurrentItem());
            if(imageViews.size() == 0) finish();
            adapter.notifyDataSetChanged();
        }
    }

    private List<View> imageViews = new ArrayList<>();
    private ImagePagerAdapter adapter;
    private List<ImageItem> imageItems;

    private boolean editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        editable = intent.getBooleanExtra("editable", false);
        //根据editable显示删除按钮
        delete.setVisibility(editable ? View.VISIBLE : View.GONE);
        //当前点击的图像序号
        int index = intent.getIntExtra("index",-1);
        //新建View显示图片
        imageItems = (ArrayList<ImageItem>)intent.getSerializableExtra("images");
        for (ImageItem item : imageItems){
            if(item.getType() == 1) addImageViewToList(item.getPath());
        }
        adapter = new ImagePagerAdapter(imageViews);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
    }

    private void addImageViewToList(String path){
        View view = getLayoutInflater().inflate(R.layout.layout_show_image,null);
        GestureImageView imageView = (GestureImageView) view.findViewById(R.id.si_image);
        imageView.getController().enableScrollInViewPager(viewPager);
        Glide.with(this).load(path).into(imageView);
        imageViews.add(view);
    }
}
