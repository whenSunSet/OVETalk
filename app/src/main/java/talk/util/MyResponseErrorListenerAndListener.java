package talk.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heshixiyang.ovetalk.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import talk.Globle.GlobleData;

/**
 * Created by heshixiyang on 2016/3/14.
 */
public class MyResponseErrorListenerAndListener implements Response.ErrorListener ,Response.Listener<JSONObject>{
    Context context;
    int messageStatu;
    public MyResponseErrorListenerAndListener(Context context, int messageStatu) {
        this.context=context;
        this.messageStatu=messageStatu;
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            GlobleData.res=jsonObject.getInt("res");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (messageStatu){
            case GlobleData.USER_REQUEST_JOIN_GROUP:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"加入请求发送失败");
                }else if (GlobleData.res==GlobleData.SEND_MESSAGE_SUCCESS){
                    DialogUtil.showToast(context,"加入请求发送成功");
                }else if (GlobleData.res== GlobleData.NO_SUCH_GROUP){
                    DialogUtil.showToast(context,"对不起，没有该群组");
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
            default:
                if (GlobleData.res== GlobleData.SEND_MESSAGE_FAIL){
                    DialogUtil.showToast(context,"消息发送失败");
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (BuildConfig.DEBUG) Log.d("MyResponseErrorListener", "volleyError:" + volleyError.toString());
        DialogUtil.showToast(context,"无法连接服务器");
    }

}
