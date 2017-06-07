package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zapyle.zapyle.Alerts;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import utils.GetSharedValues;

public class Branch_Redirect_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {

                if (error == null) {


                    System.out.println("refering param" + referringParams);

                    try {
                        if (referringParams.getInt("albumid")>0){
                            try {
                                Intent styleinspiration = new Intent(getApplicationContext(), ProductPage.class);
                                styleinspiration.putExtra("album_id", referringParams.getInt("albumid"));
                                styleinspiration.putExtra("sale", false);
                                styleinspiration.putExtra("pta", false);
                                styleinspiration.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(styleinspiration);
                                finish();
                            } catch (JSONException e) {


                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            intent.putExtra("user_id", referringParams.getInt("prof_id"));
                            startActivity(intent);
                            finish();
                        } catch (JSONException e1) {
                            e.printStackTrace();
                        }
                    }




                }
            }
        }, this.getIntent().getData(), this);

    }


    //}

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}
