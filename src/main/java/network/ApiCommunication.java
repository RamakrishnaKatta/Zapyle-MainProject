package network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by haseeb on 25/11/15.
 */
public interface ApiCommunication {

    void onResponseCallback(JSONObject response, String flag);
    void onErrorCallback(VolleyError error, String flag);
}