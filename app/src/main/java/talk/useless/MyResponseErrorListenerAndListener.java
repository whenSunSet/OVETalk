package talk.useless;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heshixiyang.ovetalk.BuildConfig;

import org.json.JSONObject;

import talk.util.DialogUtil;

/**
 * Created by heshixiyang on 2016/3/14.
 */
public class MyResponseErrorListenerAndListener implements Response.ErrorListener ,Response.Listener<JSONObject>{
    private Context context;
    private int messageStatu;
    private boolean isSuccess;
    public MyResponseErrorListenerAndListener(Context context, int messageStatu) {
        this.context=context;
        this.messageStatu=messageStatu;
    }

    @Override
    public void onResponse(JSONObject jsonObject) {

        isSuccess=true;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (BuildConfig.DEBUG) Log.d("MyResponseErrorListener", "volleyError:" + volleyError.toString());
        DialogUtil.showToast(context,"无法连接服务器");
        isSuccess=false;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
