package talk.util;

import android.content.Context;
import android.util.Log;

import com.example.heshixiyang.ovetalk.BuildConfig;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;

/**
 * Created by heshixiyang on 2016/3/20.
 */
public class MyAsyncHttpResponseHandler extends JsonHttpResponseHandler{
    private boolean isSuccess;
    private Context context;
    private int statu;
    public MyAsyncHttpResponseHandler(Context context,int statu) {
        this.statu=statu;
        this.context = context;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        if (BuildConfig.DEBUG) Log.d("MyAsyncHttpResponseHand", "statusCode:" + statusCode);
        if (BuildConfig.DEBUG) Log.d("MyAsyncHttpResponseHand", response.toString());
        switch (statu){
            case GlobleData.SEND_FILE:
                if (statusCode==200){
                    DialogUtil.showToast(context,"上传成功");
                }
                break;
            case GlobleData.MAKE_GROUP:
                if (statusCode==200){
                    DialogUtil.showToast(context,"创建成功");
                }
                break;
        }
        isSuccess=true;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        switch (statu){
            case GlobleData.SEND_FILE:
                DialogUtil.showToast(context,"上传失败");
                break;
            case GlobleData.MAKE_GROUP:
                DialogUtil.showToast(context,"创建失败");
                break;
        }
        isSuccess=false;
    }

    public boolean isSuccess(){
        return isSuccess;
    }
}
