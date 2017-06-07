package fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import utils.FontUtils;

/**
 * Created by zapyle on 22/10/16.
 */

public class messagefragement extends Fragment {
    TextView title, description;
    LinearLayout message_layout;
    JSONObject contentData = null;
    JSONObject discoverData = null;
   // Context context;
    JSONObject objectData;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle data =  getArguments();
        try {
            objectData= new JSONObject(data.getString("object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view=inflater.inflate(R.layout.home_message, container, false);
        title = (TextView) view.findViewById(R.id.message_title);
        description = (TextView) view.findViewById(R.id.message_description);
        message_layout = (LinearLayout) view.findViewById(R.id.message_layout);
        title.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            description.setTextColor(Color.parseColor("#" + discoverData.getString("text_color")));
            message_layout.setBackgroundColor(Color.parseColor("#" + discoverData.getString("background_color")));
            if (!discoverData.isNull("title")) {
                if (discoverData.getString("title").length() > 0) {
                    title.setVisibility(View.VISIBLE);
                    title.setText(discoverData.getString("title"));
                } else {
                    title.setVisibility(View.GONE);
                }
            } else {
                title.setVisibility(View.GONE);
            }
            description.setText(discoverData.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }
}
