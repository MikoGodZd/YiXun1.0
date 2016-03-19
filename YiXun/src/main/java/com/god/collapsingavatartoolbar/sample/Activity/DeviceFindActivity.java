package com.god.collapsingavatartoolbar.sample.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.god.collapsingavatartoolbar.sample.View.CircleProgress;
import com.sloydev.collapsingavatartoolbar.sample.R;

import java.util.Set;


public class DeviceFindActivity extends Activity {
    // 调试用
    private static final String TAG = "DeviceFindActivity";
    private static final boolean D = true;

    // 返回时数据标签
    public static String EXTRA_DEVICE_ADDRESS = "设备地址";
    // 成员域
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private TextView mScanResultText;
    private ListView mPairedListView;
    private ListView mNewDevicesListView;
    private Button scanButton;
    private Set<BluetoothDevice> pairedDevices;
    private ImageView mStartFindDeviceView;
    private ImageView mResertView;
    private MyHandler myHandler;
    private MyThread myThread;
    private CircleProgress mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initScreen();
        initView();
        initData();
        RegisterAction();
        /*
        * 获取已配对列表
        * */
        initBlutoothAdapter();
        /*
        * 初始化已匹配列表
        * */
        initPairedList();
        intiEvent();

    }

    private void initScreen() {

        setContentView(R.layout.device_find);
        /*
        * 设置默认返回值
        * */
        setResult(Activity.RESULT_CANCELED);

    }

    private void initPairedList() {
        // 添加已配对设备到列表并显示
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "                           没有已配对设备";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    private void intiEvent() {

        // 设定扫描按键响应
        mStartFindDeviceView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpdatePromptText("正在搜寻贴片请稍后...");
                mProgress.startAnim();
                doDiscovery();

            }
        });


        mPairedListView.setOnItemClickListener(mDeviceClickListener);
        mNewDevicesListView.setOnItemClickListener(mDeviceClickListener);

    }

    private void UpdatePromptText(final String Text) {
        myHandler = new MyHandler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DeviceFindActivity.this.mScanResultText.setText(Text);
            }
        };
        myThread = new MyThread();
        myThread.run();
    }


    private void RegisterAction() {

        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 注册查找结束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    private void initBlutoothAdapter() {
        // 得到本地蓝牙句柄
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // 得到已配对蓝牙设备列表
        pairedDevices = mBtAdapter.getBondedDevices();

    }

    private void initData() {

        // 初使化设备存储数组
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // 设置已配队设备列表
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);

        // 设置新查找设备列表
        mNewDevicesListView.setAdapter(mNewDevicesArrayAdapter);
    }

    private void initView() {

        findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        mScanResultText = (TextView) findViewById(R.id.tv_scanresult);
        mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mNewDevicesListView = (ListView) findViewById(R.id.new_devices);
        mStartFindDeviceView = (ImageView) findViewById(R.id.iv_startsearch);

        mProgress = (CircleProgress) findViewById(R.id.progress);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭服务查找
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // 注销action接收器
        this.unregisterReceiver(mReceiver);
    }

    public void OnCancel(View v) {
        finish();
    }

    /**
     * 开始服务和设备查找
     */
    private void doDiscovery() {

        // 关闭再进行的服务查找
        /*if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }*/
        //并重新开始
        mBtAdapter.startDiscovery();
    }

    // 选择设备响应函数
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 准备连接设备，关闭服务查找
            mBtAdapter.cancelDiscovery();

            // 得到mac地址
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 设置返回数据
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // 设置返回值并结束程序
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };


    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

    }

    class MyThread implements Runnable {
        public void run() {
            Message msg = new Message();
            DeviceFindActivity.this.myHandler.sendMessage(msg); // 向Handler发送消息，更新UI
        }

    }

    // 查找到设备和搜索完成action监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(DeviceFindActivity.this,device.getName(),Toast.LENGTH_SHORT).show();
                // 如果是已配对的则略过，已得到显示，其余的在添加到列表中进行显示
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                } else {  //添加到已配对设备列表
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // 搜索完成action

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    UpdatePromptText("未能找到新的贴片请重试");
                } else
                    UpdatePromptText("请选择贴片");

                mProgress.reset();
            }
        }
    };

}

