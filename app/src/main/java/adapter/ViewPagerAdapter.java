package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class ViewPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {
    private List<T> mList;
    private int position;
    public ViewPagerAdapter(FragmentManager fm, List<T> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        this.position = position;
    }

    public int getPosition(){
        return position;
    }
}
