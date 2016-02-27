package talk.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.heshixiyang.ovetalk.BuildConfig;

import org.apache.commons.httpclient.NameValuePair;

import java.util.List;

import talk.Globle.GlobleMethod;

/**
 * Created by asus on 2015/12/1.
 */
public class MyRunnable implements Runnable {
    private List<NameValuePair> formparams;
    private String url;
    private Handler handler;
    public MyRunnable(List<NameValuePair> formparams,String url,Handler handler) {
        this.formparams=formparams;
        this.url=url;
        this.handler=handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        Message msg = new Message();
        msg.obj = GlobleMethod.GetResult(url, formparams);

        if (BuildConfig.DEBUG) Log.d("MyRunnable", "run");
        if (msg.obj.equals(" ")) {
            //返回值错误
            msg.what = 1;
        }else if (msg.obj.equals("")) {
            //网络错误
            msg.what = 2;
        }else{
            //成功
            msg.what = 3;
            formparams.clear();


        }
        handler.sendMessage(msg);

    }
}
