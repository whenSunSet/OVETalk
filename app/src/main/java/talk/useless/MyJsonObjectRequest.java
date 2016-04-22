package talk.useless;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by heshixiyang on 2016/3/14.
 */
public class MyJsonObjectRequest extends JsonObjectRequest {
    private HashMap map;
    public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest, HashMap map,MyResponseErrorListenerAndListener myResponseErrorListenerAndListener) {
        super(method, url, jsonRequest,myResponseErrorListenerAndListener,myResponseErrorListenerAndListener);
        this.map=map;
    }

    @Override
    protected HashMap<String, String> getPostParams() throws AuthFailureError {
        return map;
    }

    @Override
    protected HashMap<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
