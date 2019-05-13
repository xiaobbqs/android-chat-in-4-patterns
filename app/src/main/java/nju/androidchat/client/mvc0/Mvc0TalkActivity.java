package nju.androidchat.client.mvc0;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;

@Log
public class Mvc0TalkActivity extends AppCompatActivity implements Mvc0TalkModel.MessageListUpdateListener, TextView.OnEditorActionListener
{

    private Mvc0TalkModel model = new Mvc0TalkModel();
    private Mvc0TalkController controller = new Mvc0TalkController(model, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Input事件处理
        EditText editText = findViewById(R.id.et_content);
        editText.setOnEditorActionListener(this);

        // View向Model注册事件并开始监听
        model.setMessageListener(this);
        model.startListening();
    }

    // 处理Model更新事件，更新UI。注意这是在另外一个线程，所以不能直接操作
    // 这里的处理事件的方法比较暴力，就是删除到
    @Override
    public void onListUpdate(List<ClientMessage> messages) {
        runOnUiThread(() -> {
            LinearLayout content = findViewById(R.id.chat_content);

            // 删除所有已有的ItemText
            content.removeAllViews();

            // 增加ItemText
            for (ClientMessage message: messages) {
                String text = String.format("%s", message.getMessage());
                // 如果是自己发的，增加ItemTextSend
                if (message.getSenderUsername().equals(model.getUsername())) {
                    content.addView(new ItemTextSend(this, text));
                } else {
                    content.addView(new ItemTextReceive(this, text));
                }
            }

            // scroll to bottom
            ScrollView scrollView = findViewById(R.id.content_scroll_view);
            scrollView.post(() -> {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            });

        });

    }

    @Override
    public void onBackPressed() {
        controller.jumpBackToHome();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            controller.sendInformation(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }
}
