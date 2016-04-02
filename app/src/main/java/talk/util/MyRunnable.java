package talk.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.apache.http.NameValuePair;

import java.util.List;

import talk.Globle.GlobleData;

/**
 * Created by asus on 2015/12/1.
 */
public class MyRunnable implements Runnable {
    private List<NameValuePair> formparams;
    private String url;
    private Handler handler;
    private int messageStatu;
    public MyRunnable(List<NameValuePair> formparams,String url,Handler handler,int messageStatu) {
        this.formparams=formparams;
        this.url=url;
        this.handler=handler;
        this.messageStatu=messageStatu;
    }


    @Override
    public void run() {
        Looper.prepare();
        Message msg = new Message();
//        msg.obj = GlobleMethod.GetResult(url, formparams);
        if (msg.obj.equals("")) {


            //网络错误
            msg.what = GlobleData.NET_ERROR;
        }else if (msg.obj.equals("0")) {
            //发送信息失败
            msg.what = GlobleData.SEND_MESSAGE_FAIL;
        }else if (msg.obj.equals("1")){
            //发送成功
            msg.what = GlobleData.SEND_MESSAGE_SUCCESS;
            formparams.clear();
        }
        msg.arg1=messageStatu;
        handler.sendMessage(msg);
    }
}
