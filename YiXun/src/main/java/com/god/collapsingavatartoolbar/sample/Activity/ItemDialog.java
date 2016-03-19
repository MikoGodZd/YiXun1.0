package com.god.collapsingavatartoolbar.sample.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.god.collapsingavatartoolbar.sample.Service.BluetoothChatService;
import com.sloydev.collapsingavatartoolbar.sample.R;

import java.util.Timer;

//import android.view.KeyEvent;
//import android.view.inputmethod.EditorInfo;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;

/**
 * This is the main Activity that displays the current chat session.
 */
public class ItemDialog extends Activity {
    // Layout Views
    private TextView texttime;
    private Button mFindButton;
    private Button mSafeButton;
    private Button mFindEndButton;
    private Button mFindThingEndButton;
    private Button mSightButton;
    private Button mSoundButton;
    private Button mSightEndButton;
    private Button mSoundEndButton;
    Timer timer;

    private BluetoothChatService mChatService = MainActivity.mBluetoothService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item);
        initView();
        initEvent();
        timer = new Timer();
    }

    private void initView() {
        mFindButton = (Button) findViewById(R.id.button_find);
        mFindEndButton = (Button) findViewById(R.id.button_find_end);
        mSafeButton = (Button) findViewById(R.id.button_safe);
        mFindThingEndButton = (Button) findViewById(R.id.button_find_thing_end);
        mSightButton = (Button) findViewById(R.id.button_sight);
        mSightEndButton = (Button) findViewById(R.id.button_sight_end);
        mSoundButton = (Button) findViewById(R.id.button_sound);
        mSoundEndButton = (Button) findViewById(R.id.button_sound_end);
        texttime = (TextView) findViewById(R.id.text_time);
    }

    private void initEvent() {

        mFindButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "防丢功能启动", Toast.LENGTH_SHORT).show();
                sendMessage("a");
                sendMessage("c");
            }
        });

        mFindEndButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "手机防丢功能停止", Toast.LENGTH_SHORT).show();
            }
        });

        mSafeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                String message = "防盗功能启动";
                sendMessage(message);
            }
        });

        mFindThingEndButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "物品防丢功能停止", Toast.LENGTH_SHORT).show();
                sendMessage("b");
                sendMessage("d");
            }
        });

        mSightButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "防丢（发光）功能启动", Toast.LENGTH_SHORT).show();
                sendMessage("a");
            }
        });

        mSightEndButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "防丢（发光）功能停止", Toast.LENGTH_SHORT).show();
                sendMessage("b");
            }
        });

        mSoundButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "防丢（响声）功能启动", Toast.LENGTH_SHORT).show();
                sendMessage("c");
            }
        });

        mSoundEndButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //*** Send a message using for find something
                Toast.makeText(ItemDialog.this, "防丢（响声）功能停止", Toast.LENGTH_SHORT).show();
                sendMessage("d");
            }
        });
    }

    /**
     * Sends a message.
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }


    private final Handler timeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > 0) {
                int hour = msg.what / 3600;
                int minute = msg.what % 3600 / 60;
                int second = msg.what % 60;
                texttime.setText("预计时间还有�? + hour" + ":" + minute + ":" + second);
            } else {
                texttime.setText("已超出预期时间，请及时更换电�?");
                timer.cancel();
            }

        }
    };


}