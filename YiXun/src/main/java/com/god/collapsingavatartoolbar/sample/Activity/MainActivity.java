package com.god.collapsingavatartoolbar.sample.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.god.collapsingavatartoolbar.sample.Adapter.MyAdapter;
import com.god.collapsingavatartoolbar.sample.Service.BluetoothChatService;
import com.god.collapsingavatartoolbar.sample.View.SlideMenu;
import com.sloydev.collapsingavatartoolbar.sample.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private MyAdapter mAdapter;
    private static List<String> mData;
    private List<Integer> mImages;
    private CircleImageView mHeadIcon;
    private SlideMenu mSlidemenu;

    Vibrator vibrator;
    AudioManager audioService;
    AssetFileDescriptor file;
    MediaPlayer mediaPlayer;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothChatService mBluetoothService = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private TextView texttime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// Get local Bluetooth adapter

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_main);

        initData();
        intiView();
        intiEvent();
        initAudio();

    }

    private void initAudio() {

        file = MainActivity.this.getResources().openRawResourceFd(R.raw.music3);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                //player.seekTo(0);
                //循环播放
                player.start();
                player.setLooping(true);
            }
        });

        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }

        vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        audioService = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);

    }

    private void intiEvent() {

        mAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener() {

            @Override
            public void OnItemClick(View view, int position) {
                Intent mIntent = new Intent(MainActivity.this, ItemDialog.class);
                startActivity(mIntent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "long click" + mData.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidemenu.Toggle();
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceFindActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothService.start();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mBluetoothService == null)
                mBluetoothService = new BluetoothChatService(this, mHandler);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {

                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    mediaPlayer.start();

                    vibrator.vibrate(new long[]{300, 500}, 0);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    if (msg.getData().getString("toast").toString().equals("Device connection was lost")) {

                        mediaPlayer.start();
                        vibrator.vibrate(new long[]{300, 500}, 0);
                    }
                    break;
            }
        }
    };

    private void intiView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_avatar_toolbar_sample);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new MyAdapter(mData, mImages);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager
                (this.getApplicationContext()));

        mHeadIcon = (CircleImageView) findViewById(R.id.cat_avatar);

        mSlidemenu = (SlideMenu) findViewById(R.id.slidemenu);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
    }

    private void initData() {
        mData = new ArrayList<String>();
        for (int i = 'A'; i <= 'z'; i++)
            mData.add("" + (char) i);
        mImages = new ArrayList<Integer>();
        for (int i = 'A'; i <= 'z'; i++)
            mImages.add(R.drawable.ic_contact);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceFindActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceFindActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBluetoothService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_avatar_toolbar_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        switch (item.getItemId()) {
            case R.id.action_share:
                    Toast.makeText(MainActivity.this, "hehe", Toast.LENGTH_SHORT).show();

                mediaPlayer.pause();
                vibrator.cancel();
                break;
            case R.id.action_settings:
                vibrator.vibrate(new long[]{300, 500}, 0);
                break;
        }
    return true;
    }
}
