package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import adapters.CommentAdaptor;
import application.MyApplicationClass;
import network.ApiCommunication;
import network.ApiService;
import utils.CommonFinish;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

//import com.//Appsee.////Appsee;


public class CommentActivity extends ActionBarActivity implements ApiCommunication, CommentAdaptor.customButtonListener {


//    changed


    public int user_id;
    public static ScrollView mentionListview;
    Context context = CommentActivity.this;
    public static ArrayList<String> commentString = new ArrayList<String>();
    public static Integer album_id;

    ProgressBar progressBar;
    String SCREEN_NAME = "COMMENTPAGE";
    String strproductname;
    WebSocketClient mWebSocketClient;
    JSONArray mention_objects;


    MixpanelAPI mixpanel, mixpanel2;
    Date today;
    Boolean feedStatus = false;

    public String messagetext;
    ImageButton getcommentPost;
    TextView EmptyAddressHolder;
    String callingActivity = "HomePageNew";
    public static Boolean C_Status = false;
    RelativeLayout comment_main_layout;
    public static EditText message;
    int click_count = 0;
    ScrollView comment_scrollview;
    JSONArray data = new JSONArray();
    public static int change_index = 1000;
    public static ArrayList<Integer> positionList = new ArrayList<Integer>();
    int CurserPosition = 1000;
    Boolean DeleteStatus = false;
    Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        MyApplicationClass application = (MyApplicationClass) getApplication();
        mTracker = application.getDefaultTracker();
        comment_main_layout = (RelativeLayout) findViewById(R.id.comment_main_layout);
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        LocalBroadcastManager.getInstance(this).registerReceiver(CommonFinish.broadcastReceiver, new IntentFilter("CommentActivity"));
//        if(!MyApplicationClass.cContextArray.contains(getApplicationContext())) {
        ExternalFunctions.cContextArray[21] = CommentActivity.this;
//        }
        connectWebSocket();
        try {
            callingActivity = "activity." + getIntent().getStringExtra("activity");

        } catch (Exception e) {
            callingActivity = "activity." + "HomePageNew";

        }

        mixpanel2 = MixpanelAPI.getInstance(CommentActivity.this, getResources().getString(R.string.mixpanelToken));


        today = new Date();
        mixpanel = MixpanelAPI.getInstance(CommentActivity.this, getResources().getString(R.string.mixpanelToken));

        try {
            feedStatus = getIntent().getBooleanExtra("FeedStatus", false);
            strproductname = getIntent().getStringExtra("product_title");

        } catch (Exception e) {

        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        View view = findViewById(R.id.commentitemlayout);
        FontUtils.setCustomFont(view, getAssets());
        album_id = Integer.valueOf(getIntent().getStringExtra("album_id"));
        progressBar = (ProgressBar) findViewById(R.id.Commment_progressBar);
        EmptyAddressHolder = (TextView) findViewById(R.id.EmptyAddressHolder);
        comment_scrollview = (ScrollView) findViewById(R.id.comment_scrollview);
        mentionListview = (ScrollView) findViewById(R.id.mentionListView);


        Getdata();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        TextView title = (TextView) findViewById(R.id.product_title_text);
        title.setText("COMMENTS");

        ImageView Skip = (ImageView) findViewById(R.id.productfeedButton);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        message = (EditText) findViewById(R.id.editCommenText);

        message.addTextChangedListener(CommentWatcher);

        getcommentPost = (ImageButton) findViewById(R.id.sendComment);

        getcommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.isNetworkAvailable(getApplicationContext())) {

                    if (GetSharedValues.LoginStatus(CommentActivity.this)) {
//                        messagetext = "";
                        for (int i = 0; i < commentString.size(); i++) {
                            //////System.out.println("positionlist:" + CommentActivity.commentString.get(i));
                            if (CommentActivity.positionList.contains(i)) {
                                if (i == 0) {
                                    messagetext = messagetext + "@" + CommentActivity.commentString.get(i);
                                } else {
                                    messagetext = messagetext + " @" + CommentActivity.commentString.get(i);
                                }
                            } else {
                                if (i == 0) {
                                    messagetext = messagetext + CommentActivity.commentString.get(i);
                                } else {
                                    messagetext = messagetext + " " + CommentActivity.commentString.get(i);
                                }
                            }
//                            }

                            //////System.out.println("tttttttttttt:" + commentString.get(i));
                        }

                        //////System.out.println("nnnnnnn:" + messagetext);
                        if (messagetext.length() >= 4) {
                            if (messagetext.substring(0, 4).toString().contains("null")) {
                                //////System.out.println("nullll:" + messagetext.substring(3, messagetext.length()).toString());
                                messagetext = messagetext.substring(4, messagetext.length()).toString();
                            }

                        } else {
                            if (messagetext.contains("null")) {
                                messagetext = "";
                            }
                        }

                        if (!messagetext.trim().isEmpty()) {
                            if (EmptyAddressHolder.getVisibility() == View.VISIBLE) {
                                comment_scrollview.setVisibility(View.VISIBLE);
                                EmptyAddressHolder.setVisibility(View.GONE);
//                                messagetext = message.getText().toString();
                                getcommentPost.setEnabled(false);
                                JSONObject newObj = new JSONObject();
                                JSONObject userObj = new JSONObject();
                                try {
                                    userObj.put("profile_pic", GetSharedValues.getUserprofilepic(CommentActivity.this));
                                    userObj.put("zap_username", GetSharedValues.getZapname(CommentActivity.this));
                                    userObj.put("id", GetSharedValues.getuserId(CommentActivity.this));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("GETCOMMENTPOST1 : " +e);
                                }

                                try {
                                    newObj.put("id", 0);
                                    newObj.put("comment", messagetext);
                                    newObj.put("comment_time", "2 seconds ago");
                                    newObj.put("commented_by", userObj);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("GETCOMMENTPOST2 : " +e);
                                }
                                data.put(newObj);
                                message.setText("");
                                comment_scrollview.removeAllViews();
                                getDataInList(data);

//                            -------------------------------------------

                            } else {
//                                messagetext = message.getText().toString();
                                getcommentPost.setEnabled(false);
                                JSONObject newObj = new JSONObject();
                                JSONObject userObj = new JSONObject();
                                try {
                                    userObj.put("profile_pic", GetSharedValues.getUserprofilepic(CommentActivity.this));
                                    userObj.put("zap_username", GetSharedValues.getZapname(CommentActivity.this));
                                    userObj.put("id", GetSharedValues.getuserId(CommentActivity.this));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("GETCOMMENTPOST3 : " +e);
                                }

                                try {
                                    newObj.put("id", 0);
                                    newObj.put("comment", messagetext);
                                    newObj.put("comment_time", "2 seconds ago");
                                    newObj.put("commented_by", userObj);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("GETCOMMENTPOST4 : " +e);
                                }
                                data.put(newObj);
                                message.setText("");
                                comment_scrollview.removeAllViews();
                                getDataInList(data);
                            }


                            JSONObject superprop = new JSONObject();
                            try {
                                superprop.put("product title", strproductname);
                                superprop.put("comment text", messagetext);
                                superprop.put("Event Name", "comment on product");
                                mixpanel.track("comment on product", superprop);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            hideSoftKeyboard();
                            postComment();

                            positionList.clear();
                        }
                    } else {

                        Alerts.loginAlert(getApplicationContext());
                    }
                } else {
                    Alerts.InternetAlert(getApplicationContext());
                }
            }
        });

//

        comment_scrollview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (C_Status) {
                    if (mentionListview.getVisibility() == View.VISIBLE) {
                        mentionListview.setVisibility(View.GONE);
                    }
                }
            }


        });
    }


//    Textwatcher for edittext
//    ======================================================

    private final TextWatcher CommentWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            CurserPosition = message.getSelectionStart();
            C_Status = false;
            commentString.clear();


            String[] first = s.toString().split(" ");
            for (int i = 0; i < first.length; i++) {
                if (first[i].startsWith("@")) {
                    commentString.add(first[i].substring(1, first[i].length()));
                } else {
                    commentString.add(first[i]);
                }
            }


            String temp = s.toString().substring(0, CurserPosition);
            String temp1 = new StringBuilder(temp).reverse().toString();
            String[] tempArray = temp1.split(" ");
            String tempArrayfirst = new StringBuilder(tempArray[0]).reverse().toString();
            if (tempArrayfirst.startsWith("@")) {
                change_index = tempArray.length - 1;
                if (!positionList.contains(change_index)) {
                    positionList.add(change_index);
                }
                C_Status = true;
            } else {
                change_index = 1000;
                C_Status = false;
            }


            if (C_Status && change_index < 1000 && first.length > change_index) {
                if (commentString.get(change_index).length() > 0) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("name", commentString.get(change_index).substring(0, commentString.get(change_index).length()));
                        obj.put("request_type", "post");
                        obj.put("product_id", album_id);
                        obj.put("user_id", GetSharedValues.getuserId(CommentActivity.this));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mWebSocketClient.getConnection().isOpen()) {
                        mWebSocketClient.send(obj.toString());
                    } else {
                        connectWebSocket();
                        if (mWebSocketClient.getConnection().isOpen()) {
                            mWebSocketClient.send(obj.toString());
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mentionListview.removeAllViews();
                            mentionListview.setVisibility(View.GONE);
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mentionListview.getVisibility() == View.VISIBLE) {
                            mentionListview.removeAllViews();
                            mentionListview.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
        connectWebSocket();
        if (!GetSharedValues.GetgcmId(CommentActivity.this).equals("")) {
            ApiService.getInstance(CommentActivity.this, 1).getData(CommentActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/?gcm_reg_id=" + GetSharedValues.GetgcmId(CommentActivity.this), "session");
        } else {
            ApiService.getInstance(CommentActivity.this, 1).getData(CommentActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //////System.out.println("inside on pause");


        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);

        connectWebSocket();
//        ApiService.getInstance(CommentActivity.this, 1).getData(CommentActivity.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/user/session/", "session");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
        C_Status = false;
        positionList.clear();
    }


    private void Getdata() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService.getInstance(context, 1).getData((ApiCommunication) context, true, "COMMENTPAGE", EnvConstants.URL_FEED + "/get_comments/?product_id=" + album_id, "getComments");
    }


    public void postComment() {

        final JSONObject commentObject = new JSONObject();
        try {
            commentObject.put("product", album_id);
            commentObject.put("comment", messagetext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(CommentActivity.this, 1).postData(CommentActivity.this, EnvConstants.URL_FEED + "/comment/", commentObject, SCREEN_NAME, "postcomment");

    }


    public void deleteComment(Integer comment_id) {
        ApiService.getInstance(context, 1).deleteData((ApiCommunication) context, true, "COMMENTPAGE", EnvConstants.URL_FEED + "/comment/" + comment_id + "/", "deleteComment");
    }




    private void getDataInList(final JSONArray data) {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < data.length(); i++) {
            LayoutInflater comment_inflater = LayoutInflater.from(CommentActivity.this);
            final View view;
            view = comment_inflater.inflate(R.layout.comment_list_item, null, false);
            TextView tvTitle = (TextView) view.findViewById(R.id.username);
            TextView tvDesc = (TextView) view.findViewById(R.id.commenttext);
            TextView tvTime = (TextView) view.findViewById(R.id.time);
            RoundedImageView ivIcon = (RoundedImageView) view.findViewById(R.id.ivIcon);
            ImageView delete_comment = (ImageView) view.findViewById(R.id.delete_comment);

            try {
                if (GetSharedValues.getuserId(context) == Integer.parseInt(data.getJSONObject(i).getJSONObject("commented_by").getString("id"))) {
//                    if(i == data.length()) {
                    if (data.getJSONObject(i).getInt("id") != 0) {
                        delete_comment.setVisibility(View.VISIBLE);
                    } else {
                        delete_comment.setVisibility(View.INVISIBLE);
                    }

                } else {
                    delete_comment.setVisibility(View.GONE);
                }

                tvTitle.setText(data.getJSONObject(i).getJSONObject("commented_by").getString("zap_username"));
                final String comment = data.getJSONObject(i).getString("comment");
                final Spannable wordtoSpan = new SpannableString(comment);


                final ArrayList<String> c_array = new ArrayList<String>();
                c_array.addAll(checkcharnew(comment));
                //////System.out.println("ArraySize:" + c_array.size());
                for (int j = 0; j < c_array.size(); j++) {
                    //////System.out.println("hhh:" + c_array.get(j));
                    String[] temp_index = c_array.get(j).split(">");
                    String[] indexes = temp_index[temp_index.length - 1].split("-");
                    final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                    wordtoSpan.setSpan(bss, Integer.parseInt(indexes[0]) - 1, Integer.parseInt(indexes[1]) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                tvDesc.setText(wordtoSpan);
                tvTime.setText(data.getJSONObject(i).getString("comment_time"));
                tvTitle.setTag(data.getJSONObject(i).getJSONObject("commented_by").getString("id"));
                tvDesc.setTag(data.getJSONObject(i).getInt("id"));
                tvDesc.setMovementMethod(LinkMovementMethod.getInstance());


                tvDesc.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        Layout layout = ((TextView) v).getLayout();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if (layout != null) {
                            int line = layout.getLineForVertical(y);
                            int offset = layout.getOffsetForHorizontal(line, x);
                            Log.v("index", "" + offset);
                            for (int j = 0; j < c_array.size(); j++) {
                                //////System.out.println("hhh:" + c_array.get(j));
                                String[] temp_index = c_array.get(j).split(">");
                                String[] indexes = temp_index[temp_index.length - 1].split("-");
                                if (offset < Integer.parseInt(indexes[1]) && offset > Integer.parseInt(indexes[0])) {
                                    //////System.out.println("text:" + comment.substring(Integer.parseInt(indexes[0]), Integer.parseInt(indexes[1]) - 1));
                                    JSONObject obj = new JSONObject();
                                    try {
                                        obj.put("username", comment.substring(Integer.parseInt(indexes[0]), Integer.parseInt(indexes[1]) - 1));
                                        obj.put("request_type", "get");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mWebSocketClient.send(obj.toString());
                                }
                            }
                        }
                        return true;
                    }
                });


                Glide.with(context)
                        .load(data.getJSONObject(i).getJSONObject("commented_by").getString("profile_pic"))
                        .fitCenter()
                        .placeholder(R.drawable.prof_placeholder)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivIcon);

                final int finalI = i;
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pro_page = new Intent(context, ProfilePage.class);
                        pro_page.putExtra("activity", "CommentActivity");
                        pro_page.putExtra("product_id", CommentActivity.album_id);
                        try {
                            pro_page.putExtra("user_id", Integer.parseInt(data.getJSONObject(finalI).getJSONObject("commented_by").getString("id")));
                            pro_page.putExtra("p_username", data.getJSONObject(finalI).getJSONObject("commented_by").getString("zap_username"));
                            pro_page.putExtra("p_usertype", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pro_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(pro_page);
                    }
                });


                delete_comment.setOnClickListener(new View.OnClickListener() {
                    //
                    @Override
                    public void onClick(View v) {
                        try {
                            removeItemFromList(finalI, data.getJSONObject(finalI).getInt("id"), data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


            linearLayout.addView(view);

        }
        comment_scrollview.addView(linearLayout);
        progressBar.setVisibility(View.GONE);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public void removeItemFromList(final int position, final int comment_id, final JSONArray data) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                CommentActivity.this);

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this comment?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (data.length() == 1) {
                    comment_scrollview.setVisibility(View.GONE);
                    EmptyAddressHolder.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        data.remove(0);
                    }
                    deleteComment(comment_id);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        data.remove(position);
                    }
                    comment_scrollview.removeAllViews();
                    getDataInList(data);
                    deleteComment(comment_id);

                }

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alert.show();

    }


//    Responses
//    -------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("getComments")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        progressBar.setVisibility(View.GONE);
                        data = resp.getJSONArray("data");
                        if (data.length() > 0) {
                            comment_scrollview.setVisibility(View.VISIBLE);
                            EmptyAddressHolder.setVisibility(View.GONE);
                            getDataInList(data);
                            JSONObject propTagselected = new JSONObject();
                            try {
                                propTagselected.put("product title", resp.getString("product_title"));
                                propTagselected.put("number of comments", data.length());
                                propTagselected.put("Event Name", "Looking at comments");
                                mixpanel2.track("Looking at comments", propTagselected);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            comment_scrollview.setVisibility(View.GONE);
                            EmptyAddressHolder.setVisibility(View.VISIBLE);
                        }

                    } else {
                        progressBar.setVisibility(View.GONE);
                         CustomMessage.getInstance().CustomMessage(context, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                     CustomMessage.getInstance().CustomMessage(context, "Oops. Something went wrong!");
                }
            }
        } else if (flag.equals("postcomment")) {
            ////////System.out.println("postcomment resp____" + response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        DeleteStatus = true;

                        JSONObject data1 = resp.getJSONObject("data");
                        JSONObject temp = (JSONObject) data.get(data.length() - 1);
                        temp.put("id", data1.getInt("id"));

                        comment_scrollview.removeAllViews();
                        getDataInList(data);
                        messagetext = "";
//                        Integer commetId = data.getInt("id");
//                        CommentData commentdata = myList.get(myList.size() - 1);
//                        commentdata.setCommentId(commetId);
//                        commentId1.add(commetId);
                        getcommentPost.setEnabled(true);
                    } else {
                        hideSoftKeyboard();
                        getcommentPost.setEnabled(true);
                        messagetext = "";
                         CustomMessage.getInstance().CustomMessage(context, "Oops. Something went wrong!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideSoftKeyboard();
                    messagetext = "";
                    getcommentPost.setEnabled(true);
                     CustomMessage.getInstance().CustomMessage(context, "Oops. Something went wrong!");

                }
            }
        } else if (flag.equals("deleteComment")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                ////////System.out.println(resp);
            } else {

            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        getcommentPost.setEnabled(true);
        System.out.println("ERROR: " +error);

    }

    @Override
    public void onButtonClickListner(int position, Integer commentid, String comment, View v, CommentAdaptor.MyViewHolder viewHolder) {
//        removeItemFromList(position, commentid);
    }


    @Override
    public void onBackPressed() {
        if (click_count == 0) {
            click_count = click_count + 1;
            if (C_Status) {
                if (mentionListview.getVisibility() == View.VISIBLE) {
                    mentionListview.setVisibility(View.GONE);
                }
            } else {

                Intent mydialog = null;
                try {
                    mydialog = new Intent(CommentActivity.this, Class.forName(callingActivity));
                    mydialog.putExtra("album_id", album_id);
                    mydialog.putExtra("pta", false);
                    startActivity(mydialog);
                    finish();
                } catch (ClassNotFoundException e) {
                    mydialog = new Intent(CommentActivity.this, HomePageNew.class);
                    mydialog.putExtra("activity", "SplashScreen");
                    startActivity(mydialog);
                    finish();
                }
            }

        } else {
            Intent mydialog = null;
            try {
                mydialog = new Intent(CommentActivity.this, Class.forName(callingActivity));
                mydialog.putExtra("album_id", album_id);
                mydialog.putExtra("pta", false);
                startActivity(mydialog);
                finish();
            } catch (ClassNotFoundException e) {
                mydialog = new Intent(CommentActivity.this, HomePageNew.class);
                mydialog.putExtra("activity", "SplashScreen");
                startActivity(mydialog);
                finish();
            }
        }
    }


    //    Socket android
//    ----------------------------------------------------------------
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://prodsocket.zapyle.com/mention");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String socketMessage = s;
                //////System.out.println("Websocket message:" + s);
                if (!mWebSocketClient.getConnection().isOpen()) {
                    connectWebSocket();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!socketMessage.contains("no msg")) {
                            try {
                                JSONObject maindata = new JSONObject(socketMessage);
                                if (maindata.has("user_id")) {
                                    int userID = maindata.getInt("user_id");
                                    Intent pro_page = new Intent(context, ProfilePage.class);
                                    pro_page.putExtra("user_id", userID);
                                    pro_page.putExtra("activity", "CommentActivity");
                                    pro_page.putExtra("product_id", album_id);
                                    context.startActivity(pro_page);
                                    ((Activity) context).finish();

                                } else {
                                    mention_objects = new JSONArray();
                                    mention_objects = maindata.getJSONArray("data");
                                    if (mention_objects.length() > 0) {
                                        if (mentionListview.getVisibility() == View.VISIBLE) {
                                            mentionListview.removeAllViews();
                                            mentionListview.setVisibility(View.GONE);
                                        }
                                        showPopup(mention_objects);
                                    } else {
                                        if (mentionListview.getVisibility() == View.VISIBLE) {
                                            mentionListview.removeAllViews();
                                            mentionListview.setVisibility(View.GONE);
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            //////System.out.println("inside result else");
                            if (C_Status && mentionListview.getVisibility() == View.VISIBLE) {
                                //////System.out.println("insideeeee checkkkk");
                                C_Status = false;
                                mentionListview.removeAllViews();
                                mentionListview.setVisibility(View.GONE);
                            }
                        }
                    }
                });

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error: " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }


// Show mention popup
//    ---------------------------------------------------

    private void showPopup(JSONArray data1) {
        //////System.out.println("ShowPopup");
        if (data.length() == 0) {
            EmptyAddressHolder.setVisibility(View.INVISIBLE);
        }

        mentionListview.setVisibility(View.VISIBLE);
        mentionListview.removeAllViews();
        JSONObject measure = ExternalFunctions.displaymetrics(this);
        int screenWidth = 0;
        try {
            screenWidth = measure.getInt("width");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) ((screenWidth) / (1.25)), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        lp.addRule(RelativeLayout.ABOVE, R.id.commentholder);
        mentionListview.setLayoutParams(lp);
        mentionListview.setBackgroundColor(Color.WHITE);

        final LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < data1.length(); i++) {
            final JSONObject item;
            try {
                item = data1.getJSONObject(i);
                final LayoutInflater comment_inflater = LayoutInflater.from(CommentActivity.this);
                final View view;

                view = comment_inflater.inflate(R.layout.mention_item, null, false);
                RoundedImageView image = (RoundedImageView) view.findViewById(R.id.men_prof_pic);
                TextView fullname = (TextView) view.findViewById(R.id.men_fullname);
                TextView zapname = (TextView) view.findViewById(R.id.men_zapname);

                Glide.with(context)
                        .load(item.getString("profile_pic"))
                        .fitCenter()
                        .placeholder(R.drawable.prof_placeholder)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(image);


                fullname.setText(item.getString("full_name"));
                zapname.setText(item.getString("zap_username"));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            commentString.set(change_index, item.getString("zap_username") + " ");
                            String text = "";

                            for (int i = 0; i < commentString.size(); i++) {
                                if (positionList.contains(i)) {
                                    if (!commentString.get(i).startsWith("@")) {
                                        if (i == 0) {
                                            text = text + "@" + commentString.get(i);
                                        } else {
                                            text = text + " " + "@" + commentString.get(i);
                                        }
                                    } else {
                                        if (i == 0) {
                                            text = text + commentString.get(i);
                                        } else {
                                            text = text + " " + commentString.get(i);
                                        }
                                    }
                                } else {
                                    if (i == 0) {
                                        text = text + commentString.get(i);
                                    } else {
                                        text = text + " " + commentString.get(i);
                                    }
                                }
                            }
                            Spannable wordtoSpan = new SpannableString(text);
                            ArrayList<String> c_array = new ArrayList<String>();
                            c_array.addAll(checkcharnew(text));
                            //////System.out.println("ArraySize:" + c_array.size());
                            for (int j = 0; j < c_array.size(); j++) {
                                //////System.out.println("hhh:" + c_array.get(j));
                                String[] temp_index = c_array.get(j).split(">");
                                String[] indexes = temp_index[temp_index.length - 1].split("-");
                                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                                wordtoSpan.setSpan(bss, Integer.parseInt(indexes[0]) - 1, Integer.parseInt(indexes[1]) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                            message.setText(wordtoSpan);
                            message.setSelection(message.getText().toString().length());
                            C_Status = false;
                            //////System.out.println("to hide listview");
                            mentionListview.removeAllViews();
                            mentionListview.setVisibility(View.GONE);
                            change_index = 1000;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (data.length() == 0) {
                            EmptyAddressHolder.setVisibility(View.VISIBLE);
                        }
                    }
                });
                linearLayout1.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mentionListview.addView(linearLayout1);
    }


    public static ArrayList checkcharnew(String str) {
        String[] strTemp = str.split(" ");
        ArrayList<String> strFound = new ArrayList<String>();
        int ln = 0;
        for (int i = 0; i < strTemp.length; i++) {
            ln = ln + strTemp[i].length() + 1;
            if (strTemp[i].contains("@")) {
                strFound.add(strTemp[i] + ">" + (ln - strTemp[i].length()) + "-" + ln);
            }
        }
        return strFound;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.set("UserName", GetSharedValues.getUsername(getApplicationContext()));mTracker.set("ZapUsername", GetSharedValues.getZapname(getApplicationContext()));
        mTracker.setScreenName(GetSharedValues.getZapname(getApplicationContext()) + "~" + "Comment page");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}