package com.example.administrator.testvideochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.testvideochat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Administrator on 2017/12/6.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REGIST_WHAT_FAILE = 1;
    private static final int REGIST_WHAT_SUCCESS = 0;
    private static final int LOGIN_WHAT_SUCCESS=2;
    private static final int LOGIN_WHAT_FAIL=3;
    private Button btn_login,btn_regist;
    private EditText et_username,et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
    }

    private void setListener() {
        btn_login.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
    }

    private void initView() {
        btn_login= (Button) findViewById(R.id.btn_login);
        btn_regist= (Button) findViewById(R.id.btn_regist);
        et_username= (EditText) findViewById(R.id.et_username);
        et_password= (EditText) findViewById(R.id.et_password);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_regist:
                regist();
                break;

        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REGIST_WHAT_SUCCESS:
                    Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    break;
                case REGIST_WHAT_FAILE:
                    String dis= (String) msg.obj;
                    Toast.makeText(LoginActivity.this,dis,Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_WHAT_SUCCESS:
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,ConnectUserActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void regist() {
        final String password=et_password.getText().toString().trim();
        final String userName=et_username.getText().toString().trim();
        new Thread(new Runnable() {
            @Override
            public void run() {

                //注册失败会抛出HyphenateException
                try {
                    EMClient.getInstance().createAccount(userName, password);//同步方法
                    Log.e("yufs", "注册成功");
                    Message message=Message.obtain();
                    message.what=REGIST_WHAT_SUCCESS;
                    mHandler.sendMessage(message);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.e("yufs", "注册失败" + ":" + e.getErrorCode() + "," + e.getDescription());
                    Message message=Message.obtain();
                    message.obj="注册失败" + ":" + e.getErrorCode() + "," + e.getDescription();
                    message.what=REGIST_WHAT_FAILE;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void login() {
         String password=et_password.getText().toString().trim();
         String userName=et_username.getText().toString().trim();
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Message message=Message.obtain();
                message.what=LOGIN_WHAT_SUCCESS;
                mHandler.sendMessage(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.e("yufs","登录状态："+progress+"=="+status);
            }

            @Override
            public void onError(int code, final String message) {
                Log.d("main", "登录聊天服务器失败！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"登录聊天服务器失败:"+message,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


}
