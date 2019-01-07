package com.example.administrator.testvideochat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.testvideochat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.media.EMCallSurfaceView;

import java.util.List;

/**
 * Created by Administrator on 2017/12/6.
 */
public class ChatActivity extends AppCompatActivity implements EMMessageListener{
    private static final int MESSAGE_SEND_SUCCESS = 0;
    private EditText et_content;
    private Button btn_send;
    private TextView tv_message_list;
    private String toChatUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toChatUsername=getIntent().getStringExtra("userName");
        initView();
        setListener();
    }

    private void setListener() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_SEND_SUCCESS:
                    tv_message_list.setText(tv_message_list.getText().toString()+"\n"+et_content.getText().toString().trim());
                    et_content.setText("");
                    break;
            }
        }
    };

    /**
     * 发送消息
     */
    private void sendMessage() {
        final String content = et_content.getText().toString().toString();

        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        //如果是群聊，设置chattype，默认是单聊

        message.setChatType(EMMessage.ChatType.Chat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

       //监听消息发送状态
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("yufs", "消息发送成功to:" + toChatUsername);
                Message message=Message.obtain();
                message.what=MESSAGE_SEND_SUCCESS;
                mHandler.sendMessage(message);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("yufs","消息发送失败"+":"+i+","+s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }

    private void initView() {
        et_content= (EditText) findViewById(R.id.et_content);
        tv_message_list= (TextView) findViewById(R.id.tv_message_list);
        btn_send= (Button) findViewById(R.id.btn_send);
    }

    @Override
    public void onMessageReceived(final List<EMMessage> messages) {
        //收到消息
        for (final EMMessage eMmessage : messages){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_message_list.setText(tv_message_list.getText().toString()+"\n"+((EMTextMessageBody)eMmessage.getBody()).getMessage());
                }
            });
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //收到透传消息
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        //收到已读回执
    }

    @Override
    public void onMessageDelivered(List<EMMessage> message) {
        //收到已送达回执
    }
    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        //消息被撤回
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        //消息状态变动

    }

    @Override
    protected void onResume() {
        super.onResume();

        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
}
