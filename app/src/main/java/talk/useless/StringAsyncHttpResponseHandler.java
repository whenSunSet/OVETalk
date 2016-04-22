package talk.useless;

import android.content.Context;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import talk.util.DialogUtil;


/**
 * Created by heshixiyang on 2016/4/17.
 */
public class StringAsyncHttpResponseHandler extends TextHttpResponseHandler{
    private boolean isSuccess;
    private Context context;
    private int statu;

    public StringAsyncHttpResponseHandler(Context context, int statu) {
        this.statu=statu;
        this.context = context;
    }

    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
        DialogUtil.showToast(context,"无法连接服务器");
        isSuccess=false;
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s) {
        isSuccess=true;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
