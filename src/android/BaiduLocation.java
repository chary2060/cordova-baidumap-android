package com.qdc.plugins.baidu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * 百度定位插件
 *
 * @author mrwutong modify by liangzhenghui 2017/12/11
 *
 */
public class BaiduLocation extends CordovaPlugin {

    /** LOG TAG */
    private static final String LOG_TAG = BaiduLocation.class.getSimpleName();

    /** JS回调接口对象 */
    public static CallbackContext cbCtx = null;


    public int REQUEST_CODE = 127;

    /**
     * 插件主入口
     */
    @Override
    public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(LOG_TAG, "BaiduLocation#execute");

        boolean ret = false;
        if ("getCurrentPosition".equalsIgnoreCase(action)) {
            cbCtx = callbackContext;
            //Android M对权限管理系统进行了改版，之前我们的App需要权限，只需在manifest中申明即可，用户安装后，一切申明的权限都可来去自如的使用。但是Android M把权限管理做了加强处理，在manifest申明了，在使用到相关功能时，还需重新授权方可使用
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(!this.hasPermisssion()){
                    this.requestPermissions(REQUEST_CODE);
                }else{
                    getLocation();
                }
            }else{
                getLocation();
            }
            ret = true;
        }

        return ret;
    }

    public void getLocation(){
        Intent intent = new Intent(cordova.getActivity(), MapActivity.class);
        cordova.startActivityForResult(BaiduLocation.this, intent, 6789);
    }

    public void requestPermissions(int requestCode) {
        ArrayList<String> permissionsToRequire = new ArrayList<String>();
        if (!cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsToRequire.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsToRequire.add(Manifest.permission.ACCESS_FINE_LOCATION);
        String[] _permissionsToRequire = new String[permissionsToRequire.size()];
        _permissionsToRequire = permissionsToRequire.toArray(_permissionsToRequire);
        cordova.requestPermissions(this, REQUEST_CODE, _permissionsToRequire);
    }

    @Override
    public boolean hasPermisssion() {
        return cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (cbCtx == null || requestCode != REQUEST_CODE)
            return;
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                LOG.i("requestPermission", "---请求权限失败==");
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "定位权限没开启,功能没法使用");
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
                return;
            }
        }
        getLocation();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode==0){
            cbCtx.success("已返回");
        }else{
            String result = intent.getStringExtra("result");
            Map data = new Gson().fromJson(result, Map.class);
            cbCtx.success(new Gson().toJson(data));
        }
    }
}
