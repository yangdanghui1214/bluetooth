package com.tsign.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tsign.bluetooth.adapter.BaseRecyclerAdapter;
import com.tsign.bluetooth.adapter.BaseRecyclerHolder;
import com.tsign.bluetooth.model.BluetoothMessage;
import com.tsign.bluetooth.model.BluetoothModel;
import com.tsign.bluetooth.utlis.BluetoothUtil;

import java.util.ArrayList;

import static com.tsign.bluetooth.common.ChatConstant.ACTION_CLIENT_REPEAT_COMPLETE;
import static com.tsign.bluetooth.common.ChatConstant.ACTION_CONNECTED_SERVER;
import static com.tsign.bluetooth.common.ChatConstant.EXTRA_REMOTE_ADDRESS;

/**
 * 蓝牙通讯
 *
 * @author 13001
 */
public class MainActivity extends AppCompatActivity implements BaseRecyclerAdapter.OnItemClickListener {

    public static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

//    ActivityMainBinding binding;

    RecyclerView rvDevices;
    RecyclerView rvEquipment;

    /**
     * 蓝牙设备列表
     */
    private ArrayList<BluetoothDevice> list = new ArrayList<>();
    private BaseRecyclerAdapter<BluetoothDevice> adapter;

    private ArrayList<BluetoothModel> listEquipment = new ArrayList<>();
    private BaseRecyclerAdapter<BluetoothModel> adapterEquipment;
    private String remoteAddress = "";

    private boolean status= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        binding=DataBindingUtil.setContentView(this,R.layout.activity_main);

        rvEquipment = findViewById(R.id.rv_equipment);
        rvDevices = findViewById(R.id.rv_devices);

        if (!checkDangerousPermissions(this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 1);
            }
        }

        if (!BluetoothUtil.getInstance().isEnabled()) {
            BluetoothUtil.getInstance().getBluetoothAdapterEnable();
        }

        initReceiver();

        BluetoothUtil.getInstance().startDiscovery();


        rvEquipment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvEquipment.setAdapter(adapterEquipment = new BaseRecyclerAdapter<BluetoothModel>(this, listEquipment, R.layout.item_bluetooth) {
            @Override
            public void convert(BaseRecyclerHolder holder, BluetoothModel item, int position, boolean isScrolling) {
                holder.setText(R.id.name, item.getName());
                holder.setText(R.id.mac, item.getMac());
//                holder.setText(R.id.bt_connect, item.getType());
            }
        });
        rvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDevices.setAdapter(adapter = new BaseRecyclerAdapter<BluetoothDevice>(this, list, R.layout.item_bluetooth) {
            @Override
            public void convert(BaseRecyclerHolder holder, BluetoothDevice item, int position, boolean isScrolling) {
                holder.setText(R.id.name, item.getName());
                holder.setText(R.id.mac, item.getAddress());
                holder.getView(R.id.bt_connect).setOnClickListener(v -> {
//                    Constants.remoteAddressName = item.getName();
                    setBluetooth(item);
                });
            }
        });
        adapterEquipment.setOnItemClickListener(this);


        BluetoothUtil.getInstance().initServer();

//        rvDevices.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (status) {
//                    BluetoothMessage bluetoothMessage = new BluetoothMessage();
//                    bluetoothMessage.setContent("asdas");
//                    //自己的昵称
//                    String nickName = BluetoothUtil.getInstance().getBluetoothAdapter().getName();
//                    bluetoothMessage.setSenderNick(nickName);
////                    bluetoothMessage.setSenderIcon(Bitmap2Bytes(bitmaps));
////                        bluetoothMessage.setSender(UserManager.getMyUser(ChatActivity.this).getUserId());
//                    bluetoothMessage.setIsMe(1);
//                    BluetoothUtil.getInstance().sendMessageHandle(bluetoothMessage, remoteAddress);
//                }
//                rvDevices.postDelayed(this, 1000);
//            }
//        }, 20000);

    }

    /**
     * 检查是否已被授权危险权限
     *
     * @param permissions
     * @return
     */
    public boolean checkDangerousPermissions(Activity ac, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ac, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全选申请结果返回
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //通过requestCode来识别是否同一个请求
        if (requestCode == 1) {
            boolean isAllGranted = true;//是否全部权限已授权
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                //已全部授权
//                mPermissionInterface.requestPermissionsSuccess();
            } else {
                //权限有缺失
//                mPermissionInterface.requestPermissionsFail();
            }
        }
    }

    /**
     * 连接蓝牙
     *
     * @param btDevice
     */
    private void setBluetooth(BluetoothDevice btDevice) {
        try {
            //通过工具类ClsUtils,调用createBond方法
//            ClsUtils.createBond(btDevice.getClass(), btDevice);
            BluetoothUtil.getInstance().connect(btDevice);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        //注册设备被发现时的广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(Configuration.PAIRING_REQUEST);
        //注册一个搜索结束时的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(ACTION_CONNECTED_SERVER);
        filter.addAction(ACTION_CLIENT_REPEAT_COMPLETE);
        registerReceiver(mReceiver, filter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice btDevice;
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e("zxy", "发现设备:[" + btDevice.getName() + "]" + ":" + btDevice.getAddress());
                    for (int i = 0; i < list.size(); i++) {
                        if (btDevice.getAddress() == null || btDevice.getAddress().equals(list.get(i).getAddress())) {
                            return;
                        }
                    }
                    list.add(btDevice);
                    adapter.notifyDataSetChanged();
                    break;
                case ACTION_CONNECTED_SERVER:
                    remoteAddress = intent.getStringExtra(EXTRA_REMOTE_ADDRESS);
                    status=true;
                    showToast("连接成功");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.e("zxy", "onReceive: 搜索结束时的广播");
                    break;
                case ACTION_CLIENT_REPEAT_COMPLETE:
                    Log.e("zxy", "onReceive: 客户端重复");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        BluetoothUtil.getInstance().onDestroy();
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open) {
            list.clear();
            adapter.notifyDataSetChanged();
            if (!BluetoothUtil.getInstance().isEnabled()) {
                showToast("正在打开");
                BluetoothUtil.getInstance().getBluetoothAdapterEnable();
            } else {
                showToast("已打开");

            }
            return true;
        } else if (id == R.id.action_clone) {
//            list.clear();
//            adapter.notifyDataSetChanged();
//            if (BluetoothUtils.getInstance(this).isEnabled()) {
//                showToast("正在关闭");
//                BluetoothUtils.getInstance(this).enableBluetooth();
//            } else {
//                showToast("已关闭");
//            }
            return true;
        } else if (id == R.id.action_start) {
            list.clear();
            if (!BluetoothUtil.getInstance().isEnabled()) {
                showToast("请开启蓝牙");
                return true;
            }
            list.addAll(BluetoothUtil.getInstance().getAvailableDevices());
            BluetoothUtil.getInstance().startDiscovery();
            return true;
        } else if (id == R.id.action_stop) {
//            if (!BluetoothUtils.getInstance(mContext).isEnabled()) {
//                showToast("请开启蓝牙");
//                return true;
//            }
//            BluetoothUtils.getInstance(this).stopScanLeDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
