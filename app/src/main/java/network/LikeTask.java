package network;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;

import org.json.JSONObject;

/**
 * Created by haseeb on 18/8/16.
 */
public class LikeTask extends AsyncTask<JSONObject,Void,Void> implements ApiCommunication{

    private Context mContext;

    public LikeTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(JSONObject... params) {
        ApiService.getInstance(mContext, 1).postData((ApiCommunication) mContext, EnvConstants.APP_BASE_URL + "/user/like_product/", params[0], "Like", "unlike");
        return null;
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}
