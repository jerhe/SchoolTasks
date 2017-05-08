package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/8.
 */

public class ImageDisplayLoader extends LinearLayout{
    private ImageDisplayItem imageLoader1;
    private ImageDisplayItem imageLoader2;
    private ImageDisplayItem imageLoader3;
    List<ImageDisplayItem> imageDisplayItemList = new ArrayList<>();
    private ImageView addBtn;
    public ImageDisplayLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_image_display_loader,this);
        imageLoader1 = (ImageDisplayItem) findViewById(R.id.idl_image_1);
        imageLoader2 = (ImageDisplayItem) findViewById(R.id.idl_image_2);
        imageLoader3 = (ImageDisplayItem) findViewById(R.id.idl_image_3);
        imageDisplayItemList.add(imageLoader1);
        imageDisplayItemList.add(imageLoader2);
        imageDisplayItemList.add(imageLoader3);
        addBtn = (ImageView) findViewById(R.id.idl_add);
    }

    public void setOnAddClickListener(OnClickListener listener){
        addBtn.setOnClickListener(listener);
    }

    public void setOnItemClickListener(OnClickListener listener){
        for(ImageDisplayItem imageDisplayItem : imageDisplayItemList){
            imageDisplayItem.setOnClickListener(listener);
        }
    }

    public void loadImages(List<String> paths){
        int i = 0;
        for(; i<paths.size(); i++){
            imageDisplayItemList.get(i).setVisibility(VISIBLE);
            imageDisplayItemList.get(i).loadImage(paths.get(i));
        }
        for(;i<imageDisplayItemList.size(); i++){
            imageDisplayItemList.get(i).setVisibility(GONE);
        }
    }
}
