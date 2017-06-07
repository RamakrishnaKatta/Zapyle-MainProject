package network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haseeb on 26/11/15.
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {

    final String TAG = "CustomJsonObjectRequest";

    public CustomJsonObjectRequest(int method, String url, JSONObject params,
                                   Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, params, listener,
                errorListener);

    }

    private Priority mPriority = Priority.NORMAL;
    private String mToken;

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        String token = "Bearer " + mToken;
        ////Log.e(TAG, token);
        params.put("Authorization", token);
        return params;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setHeaders(String token) {
        mToken = token;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }


}

