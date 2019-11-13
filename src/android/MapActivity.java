package com.qdc.plugins.baidu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.example.hello.R;

public class MapActivity extends AppCompatActivity {

    public static final Integer LOCATION_PLUGIN_RESULT_CODE = 9876;

    // 定位相关
    private LocationClient mLocationClient;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocation locResult; // 当前定位结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("定位");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        this.initLocation();

       //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sure_result) { // 确定
            // 通过判断定位结果
            if (locResult == null) {
                Toast.makeText(MapActivity.this, "正在努力的定位中...", Toast.LENGTH_SHORT).show();
                return true;
            }

            this.finishActivity(locResult, null);
            return true;
        }

        if (itemId == android.R.id.home) { // 返回
            this.finishActivity(null, "取消定位");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            locResult = location;

            // 结果展示在地图上
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    /**
     * 配置定位SDK参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
         option.setScanSpan(1000);
        // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        // 可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(false);
        /* 可选，默认false，设置是否需要位置语义化结果，
         * 可以在BDLocation.getLocationDescribe里得到，
         * 结果类似于“在北京天安门附近”
         */
        option.setIsNeedLocationDescribe(true);

        mLocationClient.setLocOption(option);
    }

    private void finishActivity(final BDLocation location, String errorMessage) {
        Map<String, Object> data = new HashMap<>();
        if (location != null) { // 定位成功结果
            int resultCode = location.getLocType();
            if (resultCode == 61 || resultCode == 161) {
                // 定位成功处理，定位失败在 onLocDiagnosticMessage中处理
                data.put("latitude", String.valueOf(location.getLatitude())); //获取纬度信息
                data.put("longitude", String.valueOf(location.getLongitude())); //获取经度信息
                data.put("address", location.getAddrStr() != null ? location.getAddrStr() : "");

                Map<String, String> rgcData = new HashMap<>();
                rgcData.put("country", location.getCountry() != null ? location.getCountry() : "");
                rgcData.put("countryCode", location.getCountryCode() != null ? location.getCountryCode() : "");
                rgcData.put("city", location.getCity() != null ? location.getCity() : "");
                rgcData.put("province", location.getProvince() != null ? location.getProvince() : "");
                rgcData.put("district", location.getDistrict() != null ? location.getDistrict() : "");
                rgcData.put("street", location.getStreet() != null ? location.getStreet() : "");
                rgcData.put("streetNumber", location.getStreetNumber() != null ? location.getStreetNumber() : "");
                rgcData.put("cityCode", location.getCityCode() != null ? location.getCityCode() : "");
                rgcData.put("adCode", location.getAdCode() != null ? location.getAdCode() : "");
                rgcData.put("locationDescribe", location.getLocationDescribe() != null ? location.getLocationDescribe() : "");
                data.put("rgcData", rgcData);
            } else {
                String errMsg = "";
                switch (resultCode) {
                    case 66:
                    case 67:
                        errMsg = "无法获取地址详细信息，请检查网络是否畅通";
                        break;
                    case 62:
                        errMsg = "无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启";
                        break;
                    case 63:
                        errMsg = "网络异常，没有成功向服务器发送请求";
                        break;
                    case 162:
                        errMsg = "请求串密文解析失败，可能是SO文件加载失败";
                        break;
                    case 167:
                        errMsg = "服务端定位失败，请检查是否禁用获取位置信息权限";
                        break;
                    case 505:
                        errMsg = "AK不存在或者非法";
                        break;
                    default:
                        errMsg = "未知";
                        break;
                }
                errorMessage = String.format("错误码：%s，原因：%s。", resultCode, errMsg);
            }
        }

        if (errorMessage != null) {
            data.put("latitude", "0");
            data.put("longitude", "0");
            data.put("address", errorMessage);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("result", new Gson().toJson(data));
                MapActivity.this.setResult(LOCATION_PLUGIN_RESULT_CODE, intent);
                MapActivity.this.finish();
            }
        });
    }

}
