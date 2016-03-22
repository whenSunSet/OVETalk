package talk.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by heshixiyang on 2016/3/20.
 */
public class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    private boolean isSuccess;
    private Context context;

    public MyAsyncHttpResponseHandler(Context context) {
        this.context = context;
    }

    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        if (i==200){
            DialogUtil.showToast(context,"上传成功");
        }
        isSuccess=true;
    }
    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        DialogUtil.showToast(context,"上传失败");
        isSuccess=false;
    }

    public boolean isSuccess(){
        return isSuccess;
    }
}
