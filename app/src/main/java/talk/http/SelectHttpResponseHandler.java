package talk.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import talk.Globle.GlobleData;
import talk.util.DialogUtil;

/**
 * Created by heshixiyang on 2016/4/22.
 */
public class SelectHttpResponseHandler {
    private boolean isSuccess;
    private Context context;
    private int messageStatu;

    private Byte[] bytes;
    private JSONObject jsonObject;
    private String s;
    private int type;

    public SelectHttpResponseHandler(Context context,int messageStatu,int type) {
        this.context=context;
        this.messageStatu=messageStatu;
        this.type=type;
    }

    public AsyncHttpResponseHandler getResponseHandler(){
        if (messageStatu<=11
                ||messageStatu==GlobleData.GET_TASK_CLICK
                ||messageStatu==GlobleData.GET_HOMEWORK_CLICK
                ||messageStatu==GlobleData.GET_TASK_INFO
                ||messageStatu==GlobleData.GET_HOMEWORK_INFO
                ||messageStatu==GlobleData.PHOTO_MESSAGE
                ||messageStatu==GlobleData.CREATE_GROUP
                ||messageStatu==GlobleData.SEND_TASK
                ||messageStatu==GlobleData.SEND_HOMEWORK){
            return new JsonAsyncHttpResponseHandler(this);
        } else if (messageStatu==GlobleData.GET_TASK_FILE ||messageStatu==GlobleData.GET_HOMEWORK_FILE){
            return new ByteAsyncHttpResponseHandler(this);
        }else if (messageStatu==GlobleData.GET_GROUP_INFO) {
            return new StringAsyncHttpResponseHandler(this);
        }
        return null;
    }

    public void onSuccess(Object result){
        if (messageStatu==GlobleData.GET_GROUP_INFO){
            DialogUtil.showToast(context,"获取群组信息成功");
            s=(String)result;
        }else if (messageStatu==GlobleData.GET_TASK_FILE||messageStatu==GlobleData.GET_HOMEWORK_FILE){
            DialogUtil.showToast(context,"下载文件成功");
            bytes=(Byte[]) result;
        }else {
            jsonObject=(JSONObject)result;
            try {
                GlobleData.res = jsonObject.getInt("res");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (messageStatu) {
                case GlobleData.USER_REQUEST_JOIN_GROUP:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "加入请求发送失败");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "加入请求发送成功");
                    } else if (GlobleData.res == GlobleData.NO_SUCH_GROUP) {
                        DialogUtil.showToast(context, "对不起，没有该群组");
                    } else if (GlobleData.res == GlobleData.YOU_IN_GROUP) {
                        DialogUtil.showToast(context, "您已经在该群组中，请勿重复加入");
                    }
                    break;
                case GlobleData.USER_OUT_GROUP:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "退出请求发送失败");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "退出请求发送成功");
                    }
                    break;
                case GlobleData.USER_CANCEL_GROUP:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "注销操作失败,建议等等操作");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "注销操作成功");
                    }
                    break;
                case GlobleData.AGREE_USER_TO_GROUP:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "同意失败，建议等等再操作");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "操作成功");
                    }
                    break;
                case GlobleData.DISAGREE_USER_TO_GROUP:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "不同意失败，建议等等操作");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "操作成功");
                    }
                    break;
                case GlobleData.CREATE_GROUP:
                    try {
                        if (jsonObject.getInt(GlobleData.GROUP_ID) == 0) {
                            DialogUtil.showToast(context, "群组创建不成功");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DialogUtil.showToast(context, "群组创建成功");
                    break;
                case GlobleData.SEND_HOMEWORK:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "作业上传失败");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "作业上传成功");
                    }
                    break;
                case GlobleData.SEND_TASK:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "任务上传失败");
                    } else if (GlobleData.res == GlobleData.SEND_MESSAGE_SUCCESS) {
                        DialogUtil.showToast(context, "任务上传成功");
                    }
                    break;
                default:
                    if (GlobleData.res == GlobleData.SEND_MESSAGE_FAIL) {
                        DialogUtil.showToast(context, "消息发送失败");
                    }
                    break;
            }
        }
    }

    public void onFailure(Throwable throwable){
        DialogUtil.showToast(context,"网络连接失败");
        Log.d("SelectHttpResponseHandl", throwable.getMessage());
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getType() {
        return type;
    }

    public Byte[] getBytes() {
        return bytes;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getS() {
        return s;
    }

    private class JsonAsyncHttpResponseHandler extends JsonHttpResponseHandler {
        private SelectHttpResponseHandler selectHttpResponseHandler;
        public JsonAsyncHttpResponseHandler(SelectHttpResponseHandler selectHttpResponseHandler) {
            this.selectHttpResponseHandler=selectHttpResponseHandler;
        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            selectHttpResponseHandler.isSuccess = true;
            selectHttpResponseHandler.onSuccess(response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            selectHttpResponseHandler.isSuccess = false;
            selectHttpResponseHandler.onFailure(throwable);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            selectHttpResponseHandler.isSuccess = false;
            selectHttpResponseHandler.onFailure(throwable);
        }
    }

    private class StringAsyncHttpResponseHandler extends TextHttpResponseHandler {
        private SelectHttpResponseHandler selectHttpResponseHandler;
        public StringAsyncHttpResponseHandler(SelectHttpResponseHandler selectHttpResponseHandler) {
            this.selectHttpResponseHandler = selectHttpResponseHandler;
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            selectHttpResponseHandler.isSuccess = true;
            selectHttpResponseHandler.onSuccess(s);
        }

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            selectHttpResponseHandler.isSuccess=false;
            selectHttpResponseHandler.onFailure(throwable);
        }
    }

    private class ByteAsyncHttpResponseHandler extends BinaryHttpResponseHandler {
        private SelectHttpResponseHandler selectHttpResponseHandler;
        public ByteAsyncHttpResponseHandler(SelectHttpResponseHandler selectHttpResponseHandler) {
            this.selectHttpResponseHandler = selectHttpResponseHandler;
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            selectHttpResponseHandler.isSuccess = true;
            selectHttpResponseHandler.onSuccess(bytes);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            selectHttpResponseHandler.isSuccess = false;
            selectHttpResponseHandler.onFailure(throwable);
        }
    }
}
