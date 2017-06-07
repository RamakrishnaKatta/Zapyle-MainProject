package payment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.payment.NetbankingOption;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import activity.shoppingcartnew;
import network.ApiService;
import utils.ExternalFunctions;

/**
 * Created by haseeb on 19/10/15.
 */
public class NetbankingAdapter extends BaseAdapter {


    private ArrayList<NetbankingOption> mNetbankingOptionList = null;
    LayoutInflater mInflater;
    private AlertDialog alert;

    public NetbankingAdapter(Context context, ArrayList<NetbankingOption> mNetbankingOptionList, AlertDialog b) {
        this.mNetbankingOptionList = mNetbankingOptionList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alert = b;
    }


    @Override
    public int getCount() {
        return mNetbankingOptionList.size();
    }

    @Override
    public NetbankingOption getItem(int position) {
        return mNetbankingOptionList.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            row = mInflater.inflate(R.layout.netbank_list, parent, false);
            holder = new ViewHolder();
            holder.txtBankName = (TextView) row.findViewById(R.id.tv);
            holder.img = (ImageView) row.findViewById(R.id.img);

            row.setTag(holder);
            row.setBackgroundColor(Color.WHITE);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        final NetbankingOption netbankingOption = getItem(position);
        holder.txtBankName.setText(netbankingOption.getBankName());
        final Handler mHandler = new Handler();
        final ViewHolder finalHolder = holder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.img.setImageResource(R.drawable.roundshape_bg_blue);
                shoppingcartnew.selectedBank = getItem(position);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(userStoppedTyping, 500);

            }

            Runnable userStoppedTyping = new Runnable() {

                @Override
                public void run() {


                    shoppingcartnew.rlbank2.setVisibility(View.GONE);
                    shoppingcartnew.rlbank3.setVisibility(View.GONE);
                    shoppingcartnew.tvbank1.setText(netbankingOption.getBankName());
                    shoppingcartnew.imbank1click.setImageResource(R.drawable.roundshape_bg_blue);
                    alert.cancel();


                }


            };
        });
        return row;
    }


    private static class ViewHolder {
        TextView txtBankName;
        ImageView img;

    }
}
