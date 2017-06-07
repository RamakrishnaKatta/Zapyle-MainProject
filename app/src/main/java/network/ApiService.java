package network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import activity.SplashScreen;
import utils.CustomMessage;
import utils.GetSharedValues;

/**
 * Created by haseeb on 25/11/15.
 */
public class ApiService {
    private static ApiService instance;
    private static Context mCtx;
    private static Context context;
    private RequestQueue mRequestQueue;
    private static final String SESSION_COOKIE = "zsd";
    private static final String SESSION_PALTFORM= "PLATFORM";
    private static final String SESSION_VERSION= "VER";

    private static final String CSRF_COOKIE = "csrftoken";
    private static final String COOKIE_KEY = "Cookie";
    private static int intClearCache = 0;

    private static int buildnumber = 0;

    // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;
    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "photos";
    private static ProgressDialog pd1;

    private ApiService(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            if (intClearCache==1){
                mRequestQueue.getCache().clear();
            }

        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }





    public static synchronized ApiService getInstance(Context context) {

        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }
    public static synchronized ApiService getInstance(Context context,int intClearCache) {
        ApiService.context = context;
        ApiService.intClearCache = intClearCache;
        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }

    public static synchronized ApiService getInstance(Context context,int intClearCache,ProgressDialog pd11) {
        ApiService.context = context;
        ApiService.intClearCache = intClearCache;
        ApiService.pd1=pd11;
        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }



    private RequestQueue newRequestQueue(Context context) {
        // define cache folder
        File rootCache = context.getExternalCacheDir();
        if (rootCache == null) {
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
        cacheDir.mkdirs();

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
        RequestQueue queue = new RequestQueue(diskBasedCache, network);
        queue.start();

        return queue;
    }






    // Services
    public void getData(final ApiCommunication listener, boolean isCached, final String SCREEN_NAME, final String url, final String flag) {

        final String csrf_token;
        final String sessionId;
        sessionId = GetSharedValues.GetSessionId(mCtx);
        csrf_token = GetSharedValues.GetCsrf(mCtx);
        ////System.out.println("sessionid___" + sessionId);

        CachedNetworkRequest jsObjRequest = new CachedNetworkRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ////System.out.println("___" + response + "___");

                        listener.onResponseCallback(response, flag);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        if(error.networkResponse!=null) {
                            ////Log.e(SCREEN_NAME + "-" + flag, error.networkResponse.data.toString() + "");
                            ////System.out.println("apiService error :" + error.networkResponse.data.toString() + "__" + flag);
                            //  CustomMessage.getInstance().CustomMessage(context,error.networkResponse.data.toString());
                            try {
                                 CustomMessage.getInstance().CustomMessage(context, "Internal Error");
                            }catch (Exception e){

                            }



                        }


                        listener.onErrorCallback(error, flag);
                        getRequestQueue().getCache().remove(url);
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                if (sessionId.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(SESSION_COOKIE);
                    builder.append("=");
                    builder.append(sessionId);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }


                StringBuilder builder = new StringBuilder();
                builder.append(SESSION_PALTFORM);
                builder.append("=");
                builder.append("ANDROID");
                if (headers.containsKey(COOKIE_KEY)) {
                    builder.append("; ");
                    builder.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder.toString());

                StringBuilder builder1 = new StringBuilder();
                builder1.append(SESSION_VERSION);
                builder1.append("=");
                builder1.append(SplashScreen.int_Buildnumber);
                if (headers.containsKey(COOKIE_KEY)) {
                    builder1.append("; ");
                    builder1.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder1.toString());
                return headers;
            }
        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(jsObjRequest);
        Log.e(SCREEN_NAME+"URLHIT", jsObjRequest.getUrl() + " ");
    }







    public void postData(final ApiCommunication listener, final String url, JSONObject params, final String SCREEN_NAME, final String flag) {
        final String csrf_token,sessionId;
        sessionId = GetSharedValues.GetSessionId(mCtx);
        csrf_token = GetSharedValues.GetCsrf(mCtx);

        PostNetworkRequest jsObjRequest = new PostNetworkRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.e(SCREEN_NAME, response + "");
                        listener.onResponseCallback(response, flag);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse!=null) {
                            ////Log.e(SCREEN_NAME + "-" + flag, error.networkResponse.data.toString() + "");
                             CustomMessage.getInstance().CustomMessage(context,error.networkResponse.data.toString());
                        }
                        // TODO Auto-generated method stub
                        listener.onErrorCallback(error, flag);
                        getRequestQueue().getCache().remove(url);
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {

                Map headers = new HashMap();
                if (sessionId.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(SESSION_COOKIE);
                    builder.append("=");
                    builder.append(sessionId);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                if(csrf_token.isEmpty()) {
                    headers.put("X-CSRFToken", csrf_token);
                }
                else {
                    headers.put("X-CSRFToken", SplashScreen.csrf_token);
                }
                if (csrf_token.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(CSRF_COOKIE);
                    builder.append("=");
                    builder.append(csrf_token);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(CSRF_COOKIE);
                    builder.append("=");
                    builder.append(SplashScreen.csrf_token);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                StringBuilder builder = new StringBuilder();
                builder.append(SESSION_PALTFORM);
                builder.append("=");
                builder.append("ANDROID");
                if (headers.containsKey(COOKIE_KEY)) {
                    builder.append("; ");
                    builder.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder.toString());

                StringBuilder builder1 = new StringBuilder();
                builder1.append(SESSION_VERSION);
                builder1.append("=");
                builder1.append(SplashScreen.int_Buildnumber);
                if (headers.containsKey(COOKIE_KEY)) {
                    builder1.append("; ");
                    builder1.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder1.toString());
                return headers;
            }
        };
        addToRequestQueue(jsObjRequest);
        Log.e(SCREEN_NAME+"URLHIT", jsObjRequest.getUrl() + " ");
    }



    public void putData(final ApiCommunication listener, final String url, JSONObject params, final String SCREEN_NAME, final String flag) {
        final String sessionId,csrf_token;
        sessionId = GetSharedValues.GetSessionId(mCtx);
        csrf_token = GetSharedValues.GetCsrf(mCtx);
        PostNetworkRequest jsObjRequest = new PostNetworkRequest
                (Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ////Log.e(SCREEN_NAME, response + "");
                        listener.onResponseCallback(response, flag);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse!=null) {
                            ////Log.e(SCREEN_NAME + "-" + flag, error.networkResponse.data.toString() + "");
                             CustomMessage.getInstance().CustomMessage(context,error.networkResponse.data.toString());
                        }
                        // TODO Auto-generated method stub
                        listener.onErrorCallback(error, flag);
                        getRequestQueue().getCache().remove(url);
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {

                Map headers = new HashMap();
                if (sessionId.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(SESSION_COOKIE);
                    builder.append("=");
                    builder.append(sessionId);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                if(csrf_token.isEmpty()) {
                    headers.put("X-CSRFToken", csrf_token);
                }
                else {
                    headers.put("X-CSRFToken", SplashScreen.csrf_token);
                }
                if (csrf_token.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(CSRF_COOKIE);
                    builder.append("=");
                    builder.append(csrf_token);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(CSRF_COOKIE);
                    builder.append("=");
                    builder.append(SplashScreen.csrf_token);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                StringBuilder builder = new StringBuilder();
                builder.append(SESSION_PALTFORM);
                builder.append("=");
                builder.append("ANDROID");
                if (headers.containsKey(COOKIE_KEY)) {
                    builder.append("; ");
                    builder.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder.toString());

                StringBuilder builder1 = new StringBuilder();
                builder1.append(SESSION_VERSION);
                builder1.append("=");
                builder1.append(SplashScreen.int_Buildnumber);
                if (headers.containsKey(COOKIE_KEY)) {
                    builder1.append("; ");
                    builder1.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder1.toString());
                return headers;
            }
        };
        addToRequestQueue(jsObjRequest);
        Log.e(SCREEN_NAME, jsObjRequest.getUrl() + " ");
    }




    // Services
    public void deleteData(final ApiCommunication listener, boolean isCached, final String SCREEN_NAME, final String url, final String flag) {

        final String csrf_token;
        final String sessionId;
        sessionId = GetSharedValues.GetSessionId(mCtx);
        csrf_token = GetSharedValues.GetCsrf(mCtx);
        ////System.out.println("sessionid___" + sessionId);

        CachedNetworkRequest jsObjRequest = new CachedNetworkRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ////System.out.println("___" + response + "___");
                        listener.onResponseCallback(response, flag);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        if(error.networkResponse!=null) {
                            ////Log.e(SCREEN_NAME + "-" + flag, error.networkResponse.data.toString() + "");
                            ////System.out.println("apiService error :" + error.networkResponse.data.toString() + "__" + flag);
                             CustomMessage.getInstance().CustomMessage(context,error.networkResponse.data.toString());
                        }
                        listener.onErrorCallback(error, flag);
                        getRequestQueue().getCache().remove(url);
                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                if (sessionId.length() > 0) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(SESSION_COOKIE);
                    builder.append("=");
                    builder.append(sessionId);
                    if (headers.containsKey(COOKIE_KEY)) {
                        builder.append("; ");
                        builder.append(headers.get(COOKIE_KEY));
                    }
                    headers.put(COOKIE_KEY, builder.toString());
                }
                StringBuilder builder = new StringBuilder();
                builder.append(SESSION_PALTFORM);
                builder.append("=");
                builder.append("ANDROID");
                if (headers.containsKey(COOKIE_KEY)) {
                    builder.append("; ");
                    builder.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder.toString());

                StringBuilder builder1 = new StringBuilder();
                builder1.append(SESSION_VERSION);
                builder1.append("=");
                builder1.append(SplashScreen.int_Buildnumber);
                if (headers.containsKey(COOKIE_KEY)) {
                    builder1.append("; ");
                    builder1.append(headers.get(COOKIE_KEY));
                }
                headers.put(COOKIE_KEY, builder1.toString());
                return headers;
            }
        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(jsObjRequest);
        Log.e(SCREEN_NAME, jsObjRequest.getUrl() + " ");
    }

}