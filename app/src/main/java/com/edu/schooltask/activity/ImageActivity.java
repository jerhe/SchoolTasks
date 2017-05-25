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
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.DeleteImageEvent;
import com.edu.schooltask.item.ImageItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends BaseActivity {

    private List<View> imageViews;
    private List<ImageItem> imageItems;
    private ViewPager viewPager;
    ImageView back;
    ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        boolean editable = intent.getBooleanExtra("editable", false);
        int index = intent.getIntExtra("index",-1);
        imageItems = (ArrayList<ImageItem>)intent.getSerializableExtra("images");
        imageViews = new ArrayList<>();
        for(int i=0; i<imageItems.size(); i++){
            if(imageItems.get(i).getType()==1){
                View view = getLayoutInflater().inflate(R.layout.layout_show_image,null);
                GestureImageView imageView = (GestureImageView) view.findViewById(R.id.si_image);
                Glide.with(this).load(imageItems.get(i).getPath()).into(imageView);
                imageViews.add(view);
            }
        }

        back = (ImageView) findViewById(R.id.image_back);
        delete = (ImageView) findViewById(R.id.image_delete);
        viewPager = (ViewPager) findViewById(R.id.image_vp);
        final PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViews.get(position));
                return imageViews.get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(index);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(editable){   //可编辑
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new DeleteImageEvent(viewPager.getCurrentItem()));
                    imageViews.remove(viewPager.getCurrentItem());
                    if(imageViews.size() == 0) finish();
                    pagerAdapter.notifyDataSetChanged();
                }
            });
        }
        else{   //只用于查看
            delete.setVisibility(View.GONE);
        }

    }
}
