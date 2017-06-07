package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import utils.ExternalFunctions;

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
                //    Toast.makeText(Branch_Redirect_Activity.this,referringParams.toString(),Toast.LENGTH_LONG).show();

                    if (referringParams.toString().contains("albumid")) {
                        try {
                            if (referringParams.getInt("albumid") > 0) {

                                Intent styleinspiration = new Intent(getApplicationContext(), product.class);
                                styleinspiration.putExtra("album_id", referringParams.getInt("albumid"));
                                styleinspiration.putExtra("sale", false);
                                styleinspiration.putExtra("pta", false);
                                styleinspiration.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(styleinspiration);
                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (referringParams.toString().contains("prof_id")) {
                        try {
                        Intent intent = new Intent(getApplicationContext(), profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                            intent.putExtra("user_id", referringParams.getInt("prof_id"));

                        startActivity(intent);
                        finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{

                        try {
                            String action_type=referringParams.getString("action_type");
                            String target=referringParams.getString("target");
                            System.out.println(action_type+"route databranch"+target);
                          Intent intent= ExternalFunctions.routeActivity(Branch_Redirect_Activity.this,action_type,target);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent= ExternalFunctions.routeActivity(Branch_Redirect_Activity.this,"discover","");
                            startActivity(intent);
                        }


                    }







                }
            }
        }, this.getIntent().getData(), this);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        //Checking if the previous activity is launched on branch Auto deep link.
//        if(requestCode == getResources().getInteger(R.integer.AutoDeeplinkRequestCode)){
//            //Decide here where  to navigate  when an auto deep linked activity finishes.
//            //For e.g. Go to HomeActivity or a  SignUp Activity.
//
//        }
//    }
    //}

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}
