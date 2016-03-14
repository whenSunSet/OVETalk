package talk.util;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by heshixiyang on 2016/3/14.
 */
public class MyResponseErrorListener implements Response.ErrorListener {
    Context context;
    int messageStatu;
    public MyResponseErrorListener(Context context,int messageStatu) {
        this.context=context;
        this.messageStatu=messageStatu;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        DialogUtil.showToast(context,"无法连接服务器");
    }

}
