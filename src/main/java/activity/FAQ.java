package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.//Appsee;

public class FAQ extends ActionBarActivity implements ApiCommunication{

    RelativeLayout step1,step2,step3,step4,step5,step6,step7,step8,step9,step10,step11,step12,step13,step14,step15,step16,step17,step18,step19,step20,step21;
    TextView step_text1,step_text2,step_text3,step_text4,step_text5_1,step_text5_2,step_text6_1,step_text6_2,step_text7,step_text8,step_text9,step_text10,step_text11,step_text12,step_text13,step_text14_1,step_text14_2,step_text15,step_text16,step_text17,step_text18,step_text19_1,step_text19_2,step_text20,step_text21_1,step_text21_2;

    String SCREEN_NAME = "FAQ";
    String callingActivity = "activity.HomePage";
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");         overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        View view = findViewById(R.id.faqlayout);
        FontUtils.setCustomFont(view, getAssets());

        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");
        }
        catch (Exception e){
            callingActivity = "activity.HomePage";
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView tv_headTitle = (TextView) findViewById(R.id.product_title_text);
        tv_headTitle.setText("FAQ");


        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        step1 = (RelativeLayout) findViewById(R.id.step1);
        step2 = (RelativeLayout) findViewById(R.id.step2);
        step3 = (RelativeLayout) findViewById(R.id.step3);
        step4 = (RelativeLayout) findViewById(R.id.step4);
        step5 = (RelativeLayout) findViewById(R.id.step5);
        step6 = (RelativeLayout) findViewById(R.id.step6);
        step7 = (RelativeLayout) findViewById(R.id.step7);
        step8 = (RelativeLayout) findViewById(R.id.step8);
        step9 = (RelativeLayout) findViewById(R.id.step9);
        step10 = (RelativeLayout) findViewById(R.id.step10);
        step11 = (RelativeLayout) findViewById(R.id.step11);
        step12 = (RelativeLayout) findViewById(R.id.step12);
        step13 = (RelativeLayout) findViewById(R.id.step13);
        step14 = (RelativeLayout) findViewById(R.id.step14);
        step15 = (RelativeLayout) findViewById(R.id.step15);
        step16 = (RelativeLayout) findViewById(R.id.step16);
        step17 = (RelativeLayout) findViewById(R.id.step17);
        step18 = (RelativeLayout) findViewById(R.id.step18);
        step19 = (RelativeLayout) findViewById(R.id.step19);
        step20 = (RelativeLayout) findViewById(R.id.step20);
        step21 = (RelativeLayout) findViewById(R.id.step21);

        step1.setTag("down");
        step2.setTag("down");
        step3.setTag("down");
        step4.setTag("down");
        step5.setTag("down");
        step6.setTag("down");
        step7.setTag("down");
        step8.setTag("down");
        step9.setTag("down");
        step10.setTag("down");
        step11.setTag("down");
        step12.setTag("down");
        step13.setTag("down");
        step14.setTag("down");
        step15.setTag("down");
        step16.setTag("down");
        step17.setTag("down");
        step18.setTag("down");
        step19.setTag("down");
        step20.setTag("down");
        step21.setTag("down");





        step_text1 = (TextView) findViewById(R.id.step1text);
        step_text2 = (TextView) findViewById(R.id.step2text);
        step_text3 = (TextView) findViewById(R.id.step3text);
        step_text4 = (TextView) findViewById(R.id.step4text);
        step_text5_1 = (TextView) findViewById(R.id.step5text1);
        step_text5_2 = (TextView) findViewById(R.id.step5text2);
        step_text6_1 = (TextView) findViewById(R.id.step6text1);
        step_text6_2 = (TextView) findViewById(R.id.step6text2);
        step_text7 = (TextView) findViewById(R.id.step7text);
        step_text8 = (TextView) findViewById(R.id.step8text);
        step_text9 = (TextView) findViewById(R.id.step9text);
        step_text10 = (TextView) findViewById(R.id.step10text);
        step_text11 = (TextView) findViewById(R.id.step11text);
        step_text12 = (TextView) findViewById(R.id.step12text);
        step_text13 = (TextView) findViewById(R.id.step13text);
        step_text14_1 = (TextView) findViewById(R.id.step14text1);
        step_text14_2 = (TextView) findViewById(R.id.step14text2);
        step_text15 = (TextView) findViewById(R.id.step15text);
        step_text16 = (TextView) findViewById(R.id.step16text);
        step_text17 = (TextView) findViewById(R.id.step17text);
        step_text18 = (TextView) findViewById(R.id.step18text);
        step_text19_1 = (TextView) findViewById(R.id.step19text1);
        step_text19_2 = (TextView) findViewById(R.id.step19text2);
        step_text20 = (TextView) findViewById(R.id.step20text);
        step_text21_1 = (TextView) findViewById(R.id.step21text1);
        step_text21_2 = (TextView) findViewById(R.id.step21text2);

        step_text1.setVisibility(View.GONE);
        step_text2.setVisibility(View.GONE);
        step_text3.setVisibility(View.GONE);
        step_text4.setVisibility(View.GONE);
        step_text5_1.setVisibility(View.GONE);
        step_text5_2.setVisibility(View.GONE);
        step_text6_1.setVisibility(View.GONE);
        step_text6_2.setVisibility(View.GONE);
        step_text7.setVisibility(View.GONE);
        step_text8.setVisibility(View.GONE);
        step_text9.setVisibility(View.GONE);
        step_text10.setVisibility(View.GONE);
        step_text11.setVisibility(View.GONE);
        step_text12.setVisibility(View.GONE);
        step_text13.setVisibility(View.GONE);
        step_text14_1.setVisibility(View.GONE);
        step_text14_2.setVisibility(View.GONE);
        step_text15.setVisibility(View.GONE);
        step_text16.setVisibility(View.GONE);
        step_text17.setVisibility(View.GONE);
        step_text18.setVisibility(View.GONE);
        step_text19_1.setVisibility(View.GONE);
        step_text19_2.setVisibility(View.GONE);
        step_text20.setVisibility(View.GONE);
        step_text21_1.setVisibility(View.GONE);
        step_text21_2.setVisibility(View.GONE);


    }


//    onclick functions
//    ----------------------------------------------------


    public void step1(View v){
        if(step1.getTag().toString().equals("down")){
            step_text1.setVisibility(View.VISIBLE);
            step1.setTag("up");
        }
        else {
            step_text1.setVisibility(View.GONE);
            step1.setTag("down");
        }
    }

    public void step2(View v){
        if(step2.getTag().toString().equals("down")){
            step_text2.setVisibility(View.VISIBLE);
            step2.setTag("up");
        }
        else {
            step_text2.setVisibility(View.GONE);
            step2.setTag("down");
        }
    }

    public void step3(View v){
        if(step3.getTag().toString().equals("down")){
            step_text3.setVisibility(View.VISIBLE);
            step3.setTag("up");
        }
        else {
            step_text3.setVisibility(View.GONE);
            step3.setTag("down");
        }
    }

    public void step4(View v){
        if(step4.getTag().toString().equals("down")){
            step_text4.setVisibility(View.VISIBLE);
            step4.setTag("up");
        }
        else {
            step_text4.setVisibility(View.GONE);
            step4.setTag("down");
        }
    }

    public void step5(View v){
        if(step5.getTag().toString().equals("down")){
            step_text5_1.setVisibility(View.VISIBLE);
            step_text5_2.setVisibility(View.VISIBLE);
            step5.setTag("up");
        }
        else {
            step_text5_1.setVisibility(View.GONE);
            step_text5_2.setVisibility(View.GONE);
            step5.setTag("down");
        }
    }

    public void step6(View v){
        if(step6.getTag().toString().equals("down")){
            step_text6_1.setVisibility(View.VISIBLE);
            step_text6_2.setVisibility(View.VISIBLE);
            step6.setTag("up");
        }
        else {
            step_text6_1.setVisibility(View.GONE);
            step_text6_2.setVisibility(View.GONE);
            step6.setTag("down");
        }
    }

    public void step7(View v){
        if(step7.getTag().toString().equals("down")){
            step_text7.setVisibility(View.VISIBLE);
            step7.setTag("up");
        }
        else {
            step_text7.setVisibility(View.GONE);
            step7.setTag("down");
        }
    }

    public void step8(View v){
        if(step8.getTag().toString().equals("down")){
            step_text8.setVisibility(View.VISIBLE);
            step8.setTag("up");
        }
        else {
            step_text8.setVisibility(View.GONE);
            step8.setTag("down");
        }
    }

    public void step9(View v){
        if(step9.getTag().toString().equals("down")){
            step_text9.setVisibility(View.VISIBLE);
            step9.setTag("up");
        }
        else {
            step_text9.setVisibility(View.GONE);
            step9.setTag("down");
        }
    }

    public void step10(View v){
        if(step10.getTag().toString().equals("down")){
            step_text10.setVisibility(View.VISIBLE);
            step10.setTag("up");
        }
        else {
            step_text10.setVisibility(View.GONE);
            step10.setTag("down");
        }
    }
    public void step11(View v){
        if(step11.getTag().toString().equals("down")){
            step_text11.setVisibility(View.VISIBLE);
            step11.setTag("up");
        }
        else {
            step_text11.setVisibility(View.GONE);
            step11.setTag("down");
        }
    }
    public void step12(View v){
        if(step12.getTag().toString().equals("down")){
            step_text12.setVisibility(View.VISIBLE);
            step12.setTag("up");
        }
        else {
            step_text12.setVisibility(View.GONE);
            step12.setTag("down");
        }
    }
    public void step13(View v){
        if(step13.getTag().toString().equals("down")){
            step_text13.setVisibility(View.VISIBLE);
            step13.setTag("up");
        }
        else {
            step_text13.setVisibility(View.GONE);
            step13.setTag("down");
        }
    }

    public void step14(View v){
        if(step14.getTag().toString().equals("down")){
            step_text14_1.setVisibility(View.VISIBLE);
            step_text14_2.setVisibility(View.VISIBLE);
            step14.setTag("up");
        }
        else {
            step_text14_1.setVisibility(View.GONE);
            step_text14_2.setVisibility(View.GONE);
            step14.setTag("down");
        }
    }

    public void step15(View v){
        if(step15.getTag().toString().equals("down")){
            step_text15.setVisibility(View.VISIBLE);
            step15.setTag("up");
        }
        else {
            step_text15.setVisibility(View.GONE);
            step15.setTag("down");
        }
    }
    public void step16(View v){
        if(step16.getTag().toString().equals("down")){
            step_text16.setVisibility(View.VISIBLE);
            step16.setTag("up");
        }
        else {
            step_text16.setVisibility(View.GONE);
            step16.setTag("down");
        }
    }
    public void step17(View v){
        if(step17.getTag().toString().equals("down")){
            step_text17.setVisibility(View.VISIBLE);
            step17.setTag("up");
        }
        else {
            step_text17.setVisibility(View.GONE);
            step17.setTag("down");
        }
    }

    public void step18(View v){
        if(step18.getTag().toString().equals("down")){
            step_text18.setVisibility(View.VISIBLE);
            step18.setTag("up");
        }
        else {
            step_text18.setVisibility(View.GONE);
            step18.setTag("down");

        }
    }
    public void step19(View v){
        if(step19.getTag().toString().equals("down")){
            step_text19_1.setVisibility(View.VISIBLE);
            step_text19_2.setVisibility(View.VISIBLE);
            step19.setTag("up");
        }
        else {
            step_text19_1.setVisibility(View.GONE);
            step_text19_2.setVisibility(View.GONE);
            step19.setTag("down");
        }
    }
    public void step20(View v){
        if(step20.getTag().toString().equals("down")){
            step_text20.setVisibility(View.VISIBLE);
            step20.setTag("up");
        }
        else {
            step_text20.setVisibility(View.GONE);
            step20.setTag("down");
        }
    }
    public void step21(View v){
        if(step21.getTag().toString().equals("down")){
            step_text21_1.setVisibility(View.VISIBLE);
            step_text21_2.setVisibility(View.VISIBLE);
            step21.setTag("up");
        }
        else {
            step_text21_1.setVisibility(View.GONE);
            step_text21_2.setVisibility(View.GONE);
            step21.setTag("down");
        }
    }




    @Override
    public void onBackPressed() {
        Intent dintent = null;
        try {
            dintent = new Intent(this, Class.forName(callingActivity));
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity","SplashScreen");
            startActivity(dintent);
            finish();
        }
        catch (Exception e){
            dintent = new Intent(this, HomePageNew.class);
            dintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dintent.putExtra("activity","SplashScreen");
            startActivity(dintent);
            finish();
        }

    }







    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();

        unbindDrawables(findViewById(R.id.faqlayout));
        //System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();

        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GetSharedValues.GetgcmId(FAQ.this).equals("")) {
            ApiService.getInstance(FAQ.this, 1).getData(FAQ.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id="+GetSharedValues.GetgcmId(FAQ.this), "session");
        }
        else {
            ApiService.getInstance(FAQ.this, 1).getData(FAQ.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));   mTracker.setScreenName("Faq");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
