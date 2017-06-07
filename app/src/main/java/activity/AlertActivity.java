package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.uxcam.UXCam;
import com.zapyle.zapyle.R;

import utils.FontUtils;
//import com.//Appsee.////Appsee;

public class AlertActivity extends AppCompatActivity {

    RelativeLayout rl;
    String stractivity;
    String strcalling;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customalert);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        ////////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        rl=(RelativeLayout)findViewById(R.id.rl);
        FontUtils.setCustomFont(rl, getAssets());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tvbartitle=(TextView)findViewById(R.id.product_title_text);
        ImageView imgback=(ImageView)findViewById(R.id.profilebackButton);
        tvbartitle.setText("");
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bundle bundle=this.getIntent().getExtras();
       // int imgid=bundle.getInt("imgid", 0);
        String strmsg,strbutton,strtip,strtext;
        tvbartitle.setText("ERROR");
        try{
             strmsg=bundle.getString("Message");
             strbutton=bundle.getString("Buttontext");
             strtip=bundle.getString("tip");
            strtext=bundle.getString("header");
            stractivity="activity."+bundle.getString("activity");
            strcalling="activity."+bundle.getString("calling");
            tvbartitle.setText(strtext);

        }catch (Exception e ){
            strmsg=bundle.getString("Message");
            strbutton=bundle.getString("Buttontext");
            stractivity="activity."+bundle.getString("activity");
            strcalling=stractivity;
            strtip=bundle.getString("tip");
            tvbartitle.setText("ERROR");
        }


        if(strbutton.contains("RETRY")){
            strbutton=" GO BACK ";
        }


        TextView tvalert=(TextView)findViewById(R.id.tvalert);

        tvalert.setText(strmsg);

        if (strtip.length()>0){
            TextView tvtip=(TextView)findViewById(R.id.tvtip);
            tvtip.setVisibility(View.VISIBLE);
            tvtip.setText(strtip);
        }
        final Button btnalert = (Button)findViewById(R.id.btnalert);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.text_size));

        params1.gravity= Gravity.CENTER;
        params1.setMargins(10, 40, 10, 10);
        btnalert.setLayoutParams(params1);
        btnalert.setText(" "+strbutton+" ");
        btnalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mydialog = null;
                try {
                    if (stractivity.contains("FeedPage")){
                        mydialog = new Intent(AlertActivity.this, Class.forName(stractivity));
                        mydialog.putExtra("activity","SplashScreen");
                        startActivity(mydialog);
                        finish();
                    }
                    else{
                        mydialog = new Intent(AlertActivity.this, Class.forName(stractivity));
                        startActivity(mydialog);
                        finish();
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    public void onBackPressed() {

        Intent dintent = null;
        try {
            dintent = new Intent(AlertActivity.this,  Class.forName(strcalling));
            startActivity(dintent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        finish();
    }
}
