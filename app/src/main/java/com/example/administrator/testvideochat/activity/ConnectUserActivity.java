package com.example.administrator.testvideochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.testvideochat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMServiceNotReadyException;

/**
 * Created by Administrator on 2017/12/6.
 */
public class ConnectUserActivity extends AppCompatActivity{
    private Button btn_video_chat,btn_chat,btn_login_out;
    private EditText et_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_user);
        initView();
        setListener();
    }

    private void setListener() {
       btn_chat.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               cheat();
           }
       });

        btn_video_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoChat();
            }
        });

        btn_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOut();
            }
        });
    }

    private void loginOut() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.e("yufs","退出成功");
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.e("yufs","退出失败："+code+","+message);
            }
        });
    }

    /**
     * 视频通话
     */
    private void videoChat() {
        String userName = et_username.getText().toString().trim();

        try {//单参数
            EMClient.getInstance().callManager().makeVideoCall(userName);
            //跳转视频聊天界面
            Intent intent=new Intent(ConnectUserActivity.this,VideoChatActivity.class);
            startActivity(intent);
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("yufs","视频拨打失败："+e.toString());
        }

    }

    /**
     * 文字聊天
     */
    private void cheat() {
        String userName = et_username.getText().toString().trim();
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("userName",userName);
        startActivity(intent);
    }

    private void initView() {
        btn_chat= (Button) findViewById(R.id.btn_chat);
        btn_video_chat= (Button) findViewById(R.id.btn_video_chat);
        btn_login_out= (Button) findViewById(R.id.btn_login_out);
        et_username= (EditText) findViewById(R.id.et_username);
    }
}
