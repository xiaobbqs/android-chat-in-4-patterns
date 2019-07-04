package nju.androidchat.client.hw1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private HttpUtil(){

    }
    public static InputStream getImageViewInputStream(String URL_PATH) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(URL_PATH);
        if (url != null) {
            System.out.println("访问图片1： "+URL_PATH);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            System.out.println("访问图片2： "+URL_PATH);
            httpURLConnection.setConnectTimeout(5000);
            System.out.println("访问图片3： "+URL_PATH);
            httpURLConnection.setRequestMethod("GET");
            System.out.println("访问图片4： "+URL_PATH);
            httpURLConnection.setDoInput(true);
            int response_code = httpURLConnection.getResponseCode();

            System.out.println("请求码： "+response_code);
            if (response_code == 200) {
                inputStream = httpURLConnection.getInputStream();
                System.out.println("Http Request Success!");
            }
        }

//        byte[] buffer = readInputStream(inputStream);
//        inputStream = new ByteArrayInputStream(buffer);
        return inputStream;
    }

    private static byte[] readInputStream(InputStream inStream) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; //创建一个Buffer字符串
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        inStream.close();   //关闭输入流
        return outStream.toByteArray();  //把outStream里的数据写入内存
    }
}