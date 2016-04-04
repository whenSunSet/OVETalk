package talk.util;

import android.content.Context;

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
    private JSONObject jsonObject;

    public MyAsyncHttpResponseHandler(Context context,int statu) {
        this.statu=statu;
        this.context = context;
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        switch (statu){
            case GlobleData.PHOTO_MESSAGE:
                if (statusCode==200){
                    DialogUtil.showToast(context,"发送消息成功");
                }
                break;
            case GlobleData.CREATE_GROUP:
                if (statusCode==200){
                    DialogUtil.showToast(context,"群组创建成功");
                }
                break;
            case GlobleData.SEND_HOMEWORK:
                if (statusCode==200){
                    DialogUtil.showToast(context,"作业上传成功");
                }
                break;
            case GlobleData.SEND_TASK:
                if (statusCode==200){
                    DialogUtil.showToast(context,"任务上传成功");
                }
                break;
        }
        jsonObject=response;
        isSuccess=true;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        DialogUtil.showToast(context,"无法连接服务器");
        isSuccess=false;
    }

    public boolean isSuccess(){
        return isSuccess;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
