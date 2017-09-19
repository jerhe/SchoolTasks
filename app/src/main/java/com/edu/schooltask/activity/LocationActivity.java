package com.edu.schooltask.activity;

import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.LocationAdapter;
import com.edu.schooltask.beans.Location;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;

import java.util.List;

import server.api.result.Result;

public class LocationActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener{

    private RefreshRecyclerView<Location> recyclerView;

    private AMapLocationClient locationClient = null;
    private AMapLocationListener locationListener = null;
    private AMapLocationClientOption locationOption = null;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    @Override
    public int getLayout() {
        return R.layout.activity_location;
    }

    @Override
    public void init() {
        recyclerView = new RefreshRecyclerView<Location>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List<Location> list) {
                return new LocationAdapter(list);
            }

            @Override
            protected void requestData(Result result) {
                locationClient.startLocation();
                recyclerView.add(new Location("不显示位置"));
            }

            @Override
            protected void onSuccess(int id, Object data) {}

            @Override
            protected void onFailed(int id, int code, String error) {}

            @Override
            protected void onRefreshing() {
                setTitle("正在获取位置...");
            }

            @Override
            protected void onItemClick(int position, Location location) {
                Intent intent = new Intent();
                if(position == 0){
                    setResult(RESULT_OK, null);
                }
                else{
                    intent.putExtra("location", location);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        };
        addView(recyclerView);
        locationInit();
        recyclerView.refresh();
    }

    private void locationInit(){
        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        locationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        double latitude = aMapLocation.getLatitude();    //纬度
                        double longitude = aMapLocation.getLongitude();    //经度
                        String city = aMapLocation.getCity();
                        recyclerView.add(new Location(city, latitude, longitude));
                        locationClient.stopLocation();
                        poiSearch(city, latitude, longitude); //搜索附近
                    }
                    else {
                        recyclerView.setRefreshing(false);
                        setTitle("定位失败,请下拉重试");
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        locationClient.setLocationListener(locationListener); //设置定位回调监听
        locationOption = new AMapLocationClientOption();   //初始化AMapLocationClientOption对象
        locationOption.setOnceLocation(true);
        locationClient.setLocationOption(locationOption);
    }

    private void poiSearch(String city, double latitude, double longitude){
        String type="汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        query = new PoiSearch.Query("", type, city);
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 2000));
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        recyclerView.setRefreshing(false);
        if(i == 1000){
            List<PoiItem> list = poiResult.getPois();
            for (PoiItem poiItem : list){
                String city = poiItem.getCityName();
                String location = poiItem.getTitle();
                String detail = poiItem.getProvinceName()
                        +poiItem.getCityName()
                        +poiItem.getAdName()
                        +poiItem.getSnippet();
                double latitude = poiItem.getLatLonPoint().getLatitude();
                double longitude = poiItem.getLatLonPoint().getLongitude();
                recyclerView.add(new Location(city, location, detail, latitude, longitude));
            }
            setTitle("请选择位置");
        }
        else{
            setTitle("获取位置失败,请下拉重试");
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {}


}
