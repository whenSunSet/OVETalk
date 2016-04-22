package talk.useless;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;
import talk.util.DialogUtil;

/**
 * Created by heshixiyang on 2016/3/20.
 */
public class JsonAsyncHttpResponseHandler extends JsonHttpResponseHandler{
    private boolean isSuccess;
    private Context context;
    private int statu;

    public JsonAsyncHttpResponseHandler(Context context, int statu) {
        this.statu=statu;
        this.context = context;
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("JsonAsyncHttpResponseHa", "chengg");
        try {
            GlobleData.res=response.getInt("res");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (statu){
            case GlobleData.USER_REQUEST_JOIN_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context, "加入请求发送失败");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"加入请求发送成功");
                }else if (GlobleData.res== GlobleData.NO_SUCH_GROUP){
                    DialogUtil.showToast(context,"对不起，没有该群组");
                }else if (GlobleData.res==GlobleData.YOU_IN_GROUP){
                    DialogUtil.showToast(context,"您已经在该群组中，请勿重复加入");
                }
                break;
            case GlobleData.USER_OUT_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"退出请求发送失败");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"退出请求发送成功");
                }
                break;
            case GlobleData.USER_CANCEL_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"注销操作失败,建议等等操作");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"注销操作成功");
                }
                break;
            case GlobleData.AGREE_USER_TO_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"同意失败，建议等等再操作");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"操作成功");
                }
                break;
            case GlobleData.DISAGREE_USER_TO_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"不同意失败，建议等等操作");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"操作成功");
                }
                break;
            case GlobleData.PHOTO_MESSAGE:
                    DialogUtil.showToast(context, "发送消息成功");
                break;
            case GlobleData.CREATE_GROUP:
                try {
                    if(response.getInt(GlobleData.GROUP_ID)==0){
                        DialogUtil.showToast(context,"群组创建不成功");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    DialogUtil.showToast(context,"群组创建成功");
                break;
            case GlobleData.SEND_HOMEWORK:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"作业上传失败" );
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"作业上传成功");
                }
                break;
            case GlobleData.SEND_TASK:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"任务上传失败" );
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"任务上传成功");
                }
                break;
            default:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"消息发送失败");
                }
                break;
        }

        isSuccess=true;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d("JsonAsyncHttpResponseHa", "headers:" + headers);
        Log.d("JsonAsyncHttpResponseHa", "throwable:" + throwable);
        Log.d("JsonAsyncHttpResponseHa", "shib");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d("JsonAsyncHttpResponseHa", "headers:" + headers);
        Log.d("JsonAsyncHttpResponseHa", "throwable:" + throwable);
        Log.d("JsonAsyncHttpResponseHa", "shib");
        DialogUtil.showToast(context,"无法连接服务器");
        isSuccess=false;
    }

    public boolean isSuccess(){
        return isSuccess;
    }

}
