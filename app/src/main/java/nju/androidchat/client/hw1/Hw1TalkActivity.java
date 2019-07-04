package nju.androidchat.client.hw1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.TalkView, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.TalkPresenter presenter;
//    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    new Thread(() -> {
                        for (ClientMessage message : messages) {
                            String text = String.format("%s", message.getMessage());
                            // 如果是自己发的，增加ItemTextSend
                            CharSequence charSequence = null;
                            if (isImage(text)) {
                                charSequence = getData(getUrl(text));
                            } else {
                                charSequence = text;
                            }
                            CharSequence cs = charSequence;


                            runOnUiThread(() -> {

                                if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                    content.addView(new ItemTextSend(this, cs, message.getMessageId(), this));
                                } else {
                                    content.addView(new ItemTextReceive(this, cs, message.getMessageId()));
                                }
                            });
                        }

                    }).start();

                    Utils.scrollListToBottom(this);
                }
        );
    }

    CharSequence getData(String URL) {
        String htmlText =
                "<img src=\""+URL+"\">";
        Html.ImageGetter imgGetter = url -> {
            Drawable drawable = null;
            System.out.println("URL: "+url);
            try {
                InputStream in = HttpUtil.getImageViewInputStream(url);
                drawable = Drawable.createFromStream(in, url);//从输入流创建Drawable
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            return drawable;
        };
        CharSequence text = null;
        try {
            text = Html.fromHtml(htmlText, imgGetter, null);//创建一个Spanned
            System.out.println("Text Exist");
        } catch (Exception e1) {
            text = null;
        }
        return text;
    }

    private boolean isImage(String text){
        if(text!=null&&text.length()>7&&text.substring(0,5).equals("![]({")&&text.substring(text.length()-2).equals("})")){
            return true;
        }return false;
    }

    private String getUrl(String text){
        return text.substring(5,text.length()-2);
    }

    @Override
    public void setPresenter(Mvp0Contract.TalkPresenter presenter) {
        this.presenter = presenter;
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
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么
    @Override
    public void onRecallMessageRequested(UUID messageId) {
        this.presenter.recallMessage(messageId);
    }

}