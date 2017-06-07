package network;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by haseeb on 17/8/16.
 */
public class AdmireTask extends AsyncTask<JSONObject,Void,Void> implements ApiCommunication{
    private Context mContext;

    public AdmireTask(Context context) {
        mContext = context;
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    @Override
    protected Void doInBackground(JSONObject... params) {
        System.out.println("ADMIRE : inside admire");
        ApiService.getInstance(mContext, 1).postData((ApiCommunication) mContext, EnvConstants.APP_BASE_URL + "/user/admire/", params[0], "ADMIRE", "unadmire");

        return null;
    }
}
