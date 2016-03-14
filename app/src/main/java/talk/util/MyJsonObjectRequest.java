package talk.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by heshixiyang on 2016/3/14.
 */
public class MyJsonObjectRequest extends JsonObjectRequest {
    private Map map;
    public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.ErrorListener errorListener,Map map, Response.Listener<JSONObject> listener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.map=map;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
