package network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import activity.SplashScreen;
import utils.ExternalFunctions;

/**
 * Created by haseeb on 25/11/15.
 */
public class CachedNetworkRequest extends JsonObjectRequest {
    protected static final int defaultClientCacheExpiry = 60 * 72;
   // protected static final int defaultClientCacheExpiry = 1 * 1;
      Context context;
      //3 days
    final String TAG = "CACHEDNETWORKREQUEST";

    public CachedNetworkRequest(int method, String url, JSONObject params,
                                Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        super(method,url, params, listener,
                errorListener);

    }

    private Priority mPriority = Priority.NORMAL;
    private String  mToken;

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        String uuid = "";
        ////Log.e(TAG, uuid);
        headers.put("X-UuidKey", uuid);
        return headers;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setHeaders(String token){
        mToken = token;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        ////System.out.println("ffffffff_______"+response.headers);

        if(ExternalFunctions.csrfStatus == 0){
            String[] parts =  response.headers.get("Set-Cookie").split(";");
            String[] csrfParts = parts[0].split("=");
            String csrf = csrfParts[1];
            ////System.out.println("csrfToken__"+csrf);
            SplashScreen.csrf_token = csrf;
            ExternalFunctions.csrfStatus = 1;
        }


        try {
            String jsonString = new String(response.data);
            JSONObject payload = new JSONObject(jsonString);
            ////System.out.println("inside cached nw__"+jsonString);
            return Response.success(payload, enforceClientCaching(HttpHeaderParser.parseCacheHeaders(response), response));
        }catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    protected Cache.Entry enforceClientCaching(Cache.Entry entry, NetworkResponse response) {
        if (getClientCacheExpiry() == null) return entry;

        long now = System.currentTimeMillis();

        if (entry == null) {
            entry = new Cache.Entry();
            entry.data = response.data;
            entry.etag = response.headers.get("ETag");
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
            entry.serverDate = now;
            entry.responseHeaders = response.headers;
        } else if (entry.isExpired()) {
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
        }


        return entry;
    }

    protected Integer getClientCacheExpiry() {
        return defaultClientCacheExpiry;
    }

}
