package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import nju.androidchat.client.R;
import nju.androidchat.client.hw1.HttpUtil;

public class ItemTextReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;
    private Handler handler;


    public ItemTextReceive(Context context, CharSequence text, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_receive, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.messageId = messageId;
        setText(text);
    }

    public void init(Context context) {

    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    Spanned getData(String URL) {
        Html.ImageGetter imgGetter = url -> {
            final Drawable[] drawable = {null};
            new Thread(){
                public void run(){
                    InputStream in = null;
                    try {
                        in = HttpUtil.getImageViewInputStream(url);
                        drawable[0] = Drawable.createFromStream(in, url);//从输入流创建Drawable
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return drawable[0];
        };
        Spanned text = null;
        try {
            text = Html.fromHtml(URL, imgGetter, null);//创建一个Spanned
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
}