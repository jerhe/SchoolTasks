package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImagePagerAdapter;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.utils.FileUtil;
import com.lzy.ninegrid.ImageInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ImageActivity extends BaseActivity {
    @BindView(R.id.image_vp) ViewPager viewPager;

    private List<View> imageViews = new ArrayList<>();
    private ImagePagerAdapter adapter;
    private List<String> images;

    private boolean editable;
    private int index;

    @Override
    public int getLayout() {
        return R.layout.activity_image;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        editable = intent.getBooleanExtra("editable", false);
        //当前点击的图像序号
        index = intent.getIntExtra("index",-1);
        //新建View显示图片
        images = (ArrayList<String>)intent.getSerializableExtra("images");
        for (String image : images){
            addImageViewToList(image);
        }

        adapter = new ImagePagerAdapter(imageViews);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                setTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(index);
    }

    @Override
    public int createMenu() {
        return editable ? R.menu.image_delete : R.menu.image_save;
    }

    @Override
    public boolean menuClick(int menuId) {
        switch (menuId){
            case R.id.image_delete:
                if(editable){
                    EventBus.getDefault().post(new DeleteImageEvent(viewPager.getCurrentItem()));
                    imageViews.remove(viewPager.getCurrentItem());
                    adapter.notifyDataSetChanged();
                    setTitle();
                    if(imageViews.size() == 0) finish();
                }
                break;
            case R.id.image_save:
                saveImage();
                toastShort("已保存");
                break;
        }
        return true;
    }

    private void addImageViewToList(String path){
        View view = inflate(R.layout.layout_show_image);
        GestureImageView imageView = (GestureImageView) view.findViewById(R.id.si_image);
        imageView.getController().getSettings().setMaxZoom(5);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.si_pb);
        final TextView textView = (TextView) view.findViewById(R.id.si_text);
        imageView.getController().enableScrollInViewPager(viewPager);
        Glide.with(this).load(path).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
        imageViews.add(view);
        setTitle();
    }

    private void setTitle(){
        setTitle("查看图片 " + (index + 1) + "/" + imageViews.size());
    }

    private void saveImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(FileUtil.getExternalStorageDirectory("SchoolTask"));
                File file = new File(dir, System.currentTimeMillis() + ".png");
                try {
                    Bitmap bitmap = Glide.with(ImageActivity.this)
                            .load(images.get(index))
                            .asBitmap()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    if(bitmap != null){
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        // 其次把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(),
                                    file.getAbsolutePath(), file.getName(), null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
