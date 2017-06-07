package adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import activity.CommentActivity;
import activity.LikersActivity;
import activity.ProductPage;
import activity.ProfilePage;
import activity.searchFeedPage;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 25/11/15.
 */
public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder> {

    private static final int TYPE_LOAD_MORE = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    ArrayList<HomeFeedItem> feeds;
    static Context context;
    final String SCREEN_NAME = "FEED";
    static MixpanelAPI mixpanel;
    static Date today;
    int lastPosition = -1;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView userImage;
        TextView userName, service, discount, title, brand, newPrice, style;
        TextView likesCount, commentsCount, likelabel, commentlabel, style_inspiration, product_original_price;
        View likeSeperation, rupeeSeperation, org_rupee_seperation;
        ImageView like, comment, isVerified, share, rupeeSymbol;
        ImageView product_image;
        RelativeLayout productImageLayout;
        public View mView;
        int Holderid;
        ProgressBar customProgressBar;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                today = new Date();
                mixpanel = MixpanelAPI.getInstance(context, context.getResources().getString(R.string.mixpanelToken));

//                JSONObject superprop = new JSONObject();
//                try {
//                    superprop.put("Event Name", "Viewed product");
//                    superprop.put("Event Name", " Love a product");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                mixpanel.registerSuperProperties(superprop);
                productImageLayout = (RelativeLayout) itemView.findViewById(R.id.productImageLayout);
                title = (TextView) itemView.findViewById(R.id.title);
                userName = (TextView) itemView.findViewById(R.id.name); // Creating TextView object with the id of textView from item_row.xml
                discount = (TextView) itemView.findViewById(R.id.discount);
                service = (TextView) itemView.findViewById(R.id.service);
                style = (TextView) itemView.findViewById(R.id.style);
                likesCount = (TextView) itemView.findViewById(R.id.likersview);
                brand = (TextView) itemView.findViewById(R.id.productbrand);
                newPrice = (TextView) itemView.findViewById(R.id.product_listing_price); // Creating TextView object with the id of textView from item_row.xml
                product_original_price = (TextView) itemView.findViewById(R.id.product_original_price); // Creating TextView object with the id of textView from item_row.xml
                commentsCount = (TextView) itemView.findViewById(R.id.commentview);
                like = (ImageView) itemView.findViewById(R.id.imageview_like); // Creating TextView object with the id of textView from item_row.xml
                comment = (ImageView) itemView.findViewById(R.id.imageview_comment);
                share = (ImageView) itemView.findViewById(R.id.imageview_share);
                product_image = (ImageView) itemView.findViewById(R.id.product_image);
                productImageLayout.getLayoutParams().height = searchFeedPage.screenHeight;
                discount.getLayoutParams().width = (int) Double.parseDouble(String.valueOf(searchFeedPage.screenHeight * 0.4));
                isVerified = (ImageView) itemView.findViewById(R.id.imageview_verified);
                rupeeSymbol = (ImageView) itemView.findViewById(R.id.rupee_symbol);
                likelabel = (TextView) itemView.findViewById(R.id.label_like);
                commentlabel = (TextView) itemView.findViewById(R.id.label_commentview);
                style_inspiration = (TextView) itemView.findViewById(R.id.style_inspiration);
                likeSeperation = itemView.findViewById(R.id.likecommentseperation);
                rupeeSeperation = itemView.findViewById(R.id.rupee_seperation);
                org_rupee_seperation = itemView.findViewById(R.id.org_rupee_seperation);

                userImage = (RoundedImageView) itemView.findViewById(R.id.user_image); // Creating TextView object with the id of textView from item_row.xml
            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
                customProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            }

        }

        public void clearAnimation() {
            if (mView != null) {
                mView.clearAnimation();
            }
        }
    }


    public searchAdapter(ArrayList<HomeFeedItem> feeds, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.feeds = feeds;
        searchAdapter.context = context;
    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        }

    }


    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HomeFeedItem feed = feeds.get(position);
        searchFeedPage.comment_scroll_position = position;
        searchFeedPage.Scroll_count = position;


        if (holder.Holderid == 1) {
//      Main content
//            --------------------------------------------------------------


            //////System.out.println("album user name___" + feed.getUserName());
            //////System.out.println("album service___" + feed.getService());
            holder.title.setText(feed.getProductName());
            holder.userName.setText(feed.getUserName());
            //////System.out.println(feed.getFullname() + "____________feed.feed.getFullname()()");
            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {

                        if (feed.getUserId() == GetSharedValues.getuserId(context)) {
                            Intent pro_page = new Intent(context, ProfilePage.class);
                            pro_page.putExtra("user_id", GetSharedValues.getuserId(context));
                            pro_page.putExtra("p_username", GetSharedValues.getUsername(context));
                            pro_page.putExtra("activity", "searchFeedPage");
                            context.startActivity(pro_page);
                        } else {
                            Intent pro_page = new Intent(context, ProfilePage.class);
                            pro_page.putExtra("user_id", feed.getUserId());
                            pro_page.putExtra("p_username", feed.getUserName());
                            pro_page.putExtra("p_usertype", feed.getUsertype());
                            pro_page.putExtra("activity", "searchFeedPage");
//                    pro_page.putExtra("p_Fullname", feed.getFullname());
                            context.startActivity(pro_page);
//                        ((Activity) context).finish();
                        }
                    } else {
                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }

                }

            });

            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckConnectivity.isNetworkAvailable(context)) {
                            Intent pro_page = new Intent(context, ProfilePage.class);
                            pro_page.putExtra("user_id", GetSharedValues.getuserId(context));
                            pro_page.putExtra("p_username", GetSharedValues.getUsername(context));
                            pro_page.putExtra("activity", "searchFeedPage");
                            context.startActivity(pro_page);
                    } else {
                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }
            });


            if (feed.getService().equals("store_front")) {
                holder.service.setText("Storefront");
            } else {
                holder.service.setText("Preloved");
            }
            if (!feed.getStyle().isEmpty()) {
                holder.style.setText(feed.getStyle());
            } else {
                holder.style.setText("");
            }

            try {

                Glide.with(context)
                        .load(EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString())
                        .fitCenter()
                        .placeholder(R.drawable.playholderscreen)
                        .error(R.drawable.playholderscreen)
                        .crossFade()
                        .override(searchFeedPage.screenHeight,searchFeedPage.screenHeight)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.product_image);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.product_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {
                        JSONObject proponbcompleted = new JSONObject();
                        try {
                            proponbcompleted.put("Product title", feed.getProductName());
                            proponbcompleted.put("Price", feed.getNewPrice());
                            proponbcompleted.put("Event Name", "Viewed product");
                            mixpanel.track("Viewed product", proponbcompleted);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences settings = context.getSharedPreferences("MixPanelSession",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        int count = settings.getInt("scrollCount", 0);
                        editor.putInt("scrollCount", count + searchFeedPage.Scroll_count);
                        editor.apply();

                        Intent productPage = new Intent(context, ProductPage.class);
                        productPage.putExtra("album_id", feed.getId());
                        productPage.putExtra("pta", false);
                        productPage.putExtra("activity", "searchFeedPage");
                        productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(productPage);
//                            ((Activity) context).finish();


                    } else {
                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }
            });

            Glide.with(context)
                    .load(feed.getUserImage())
                    .fitCenter()
                    .placeholder(R.drawable.prof_placeholder)
                    .error(R.drawable.prof_placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.userImage);


//            Like comment holder function
//            ----------------------------------------------------

            final int likes = feed.getLikes();
            if (feed.isLiked()) {
                //////System.out.println("inside is liked");
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.likeround));
            } else {
                //////System.out.println("inside is unliked");
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.unlikeround));
            }
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {
                        if (GetSharedValues.LoginStatus(context)) {
                            if (feed.isLiked()) {
                                feed.setIsLiked(false);
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", feed.getId());
                                    likeObject.put("action", "unlike");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "unlike");
                                feed.setLikes(likes - 1);
                                if ((likes - 1) == 0) {
                                    holder.likeSeperation.setVisibility(View.GONE);
                                    holder.likesCount.setVisibility(View.GONE);
                                }


                            } else {
                                holder.like.setAnimation(AnimationUtils.loadAnimation(context, R.anim.zoomin));
                                feed.setIsLiked(true);
                                JSONObject superprop = new JSONObject();
                                try {
                                    superprop.put("product title", feed.getProductName());
                                    superprop.put("Event Name", "Love a product");
                                    mixpanel.track("Love a product", superprop);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (likes == 0 || holder.likesCount.getVisibility() == View.GONE) {
                                    holder.likeSeperation.setVisibility(View.VISIBLE);
                                    holder.likesCount.setVisibility(View.VISIBLE);
                                }
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", feed.getId());
                                    likeObject.put("action", "like");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "like");
                                feed.setLikes(likes + 1);

                            }
                            notifyDataSetChanged();
                            sendThanks(feed.getId());
                        } else {
                            Alerts.loginAlert(context);
                        }
                    } else {
                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      if (CheckConnectivity.isNetworkAvailable(context)) {
                                                          if (GetSharedValues.LoginStatus(context)) {
                                                              SharedPreferences settings = context.getSharedPreferences("MixPanelSession",
                                                                      Context.MODE_PRIVATE);

                                                              SharedPreferences.Editor editor = settings.edit();
                                                              int count = settings.getInt("scrollCount", 0);
                                                              editor.putInt("scrollCount", count + searchFeedPage.Scroll_count);
                                                              editor.apply();


//                        ExternalFunctions.requestComments(context, feed.getId());
//                                                          FeedPage.comment_scroll_position = position;
                                                              //////System.out.println("position" + "_____" + position);
                                                              Intent comment = new Intent(context, CommentActivity.class);
                                                              comment.putExtra("album_id", String.valueOf(feed.getId()));
                                                              comment.putExtra("FeedStatus", true);
                                                              comment.putExtra("product_title", feed.getProductName());
                                                              comment.putExtra("activity", "searchFeedPage");
                                                              context.startActivity(comment);
//                                                          ((Activity) context).finish();
                                                          } else {
                                                              Alerts.loginAlert(context);
                                                          }
                                                      } else {
                                                         CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                      }
                                                  }
                                              }

            );
            holder.commentsCount.setOnClickListener(new View.OnClickListener()

                                                    {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (CheckConnectivity.isNetworkAvailable(context)) {
                                                                if (GetSharedValues.LoginStatus(context)) {
//
                                                                    SharedPreferences settings = context.getSharedPreferences("MixPanelSession",
                                                                            Context.MODE_PRIVATE);

                                                                    SharedPreferences.Editor editor = settings.edit();
                                                                    int count = settings.getInt("scrollCount", 0);
                                                                    editor.putInt("scrollCount", count + searchFeedPage.Scroll_count);
                                                                    editor.apply();

//                                                                FeedPage.comment_scroll_position = position;
                                                                    Intent comment = new Intent(context, CommentActivity.class);
                                                                    comment.putExtra("album_id", String.valueOf(feed.getId()));
                                                                    comment.putExtra("FeedStatus", true);
                                                                    comment.putExtra("activity", "searchFeedPage");
                                                                    context.startActivity(comment);
//                                                                ((Activity) context).finish();
                                                                } else {
                                                                    Alerts.loginAlert(context);
                                                                }
                                                            } else {
                                                               CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                            }
                                                        }
                                                    }

            );
            holder.commentlabel.setOnClickListener(new View.OnClickListener()

                                                   {
                                                       @Override
                                                       public void onClick(View v) {
                                                           if (CheckConnectivity.isNetworkAvailable(context)) {
                                                               if (GetSharedValues.LoginStatus(context)) {

//                                ExternalFunctions.requestComments(context, feed.getId());
                                                                   Intent comment = new Intent(context, CommentActivity.class);
                                                                   comment.putExtra("album_id", String.valueOf(feed.getId()));
                                                                   comment.putExtra("activity", "searchFeedPage");
                                                                   context.startActivity(comment);
//                                                               ((Activity) context).finish();
                                                               } else {
                                                                   Alerts.loginAlert(context);
                                                               }
                                                           } else {
                                                              CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                           }
                                                       }
                                                   }

            );


            if (feed.getLikes() > 0)

            {
                if (feed.getLikes() > 1) {
                    holder.likesCount.setText(String.valueOf(feed.getLikes() + " Loves"));
                } else {
                    holder.likesCount.setText(String.valueOf(feed.getLikes() + " Love"));
                }
                holder.likesCount.setVisibility(View.VISIBLE);
                holder.likelabel.setVisibility(View.GONE);
            } else

            {
                holder.likesCount.setVisibility(View.GONE);
                holder.likelabel.setVisibility(View.GONE);
                holder.likeSeperation.setVisibility(View.GONE);
            }

            if (feed.getComments() > 0)

            {
                if (feed.getComments() > 1) {
                    holder.commentsCount.setText(String.valueOf(feed.getComments() + " Comments"));
                } else {
                    holder.commentsCount.setText(String.valueOf(feed.getComments() + " Comment"));
                }
                holder.commentsCount.setVisibility(View.VISIBLE);

                holder.commentlabel.setVisibility(View.GONE);
            } else

            {
                holder.commentlabel.setVisibility(View.GONE);
                holder.commentsCount.setVisibility(View.GONE);
                holder.likeSeperation.setVisibility(View.GONE);
            }

            holder.likesCount.setOnClickListener(new View.OnClickListener()

                                                 {
                                                     @Override
                                                     public void onClick(View v) {
                                                         if (CheckConnectivity.isNetworkAvailable(context)) {
                                                             if (GetSharedValues.LoginStatus(context)) {
                                                                 SharedPreferences settings = context.getSharedPreferences("MixPanelSession",
                                                                         Context.MODE_PRIVATE);

                                                                 SharedPreferences.Editor editor = settings.edit();
                                                                 int count = settings.getInt("scrollCount", 0);
                                                                 editor.putInt("scrollCount", count + searchFeedPage.Scroll_count);
                                                                 editor.apply();

                                                                 Intent gotoLike = new Intent(context, LikersActivity.class);
                                                                 gotoLike.putExtra("album_user", feed.getUserName());
                                                                 gotoLike.putExtra("album_id", String.valueOf(feed.getId()));
                                                                 gotoLike.putExtra("activity", "searchFeedPage");
                                                                 context.startActivity(gotoLike);
//                                                             ((Activity) context).finish();

                                                             } else {
                                                                 Alerts.loginAlert(context);
                                                             }
                                                         } else {
                                                            CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                         }
                                                     }
                                                 }

            );
            holder.likelabel.setOnClickListener(new View.OnClickListener()

                                                {
                                                    @Override
                                                    public void onClick(View v) {
//                    Toast.makeText(context, "Open likers", Toast.LENGTH_SHORT).show();
                                                        if (CheckConnectivity.isNetworkAvailable(context)) {
                                                            if (GetSharedValues.LoginStatus(context)) {
                                                                SharedPreferences settings = context.getSharedPreferences("MixPanelSession",
                                                                        Context.MODE_PRIVATE);

                                                                SharedPreferences.Editor editor = settings.edit();
                                                                int count = settings.getInt("scrollCount", 0);
                                                                editor.putInt("scrollCount", count + searchFeedPage.Scroll_count);
                                                                editor.apply();

                                                                Intent gotoLike = new Intent(context, LikersActivity.class);
                                                                gotoLike.putExtra("album_user", GetSharedValues.getZapname(context));
                                                                gotoLike.putExtra("album_id", feed.getId());
                                                                gotoLike.putExtra("activity", "searchFeedPage");
                                                                context.startActivity(gotoLike);
//                                                            ((Activity) context).finish();
//                                        ExternalFunctions.requestLikes(context, EnvConstants.APP_BASE_URL, feed.getId());
                                                            } else {
                                                                Alerts.loginAlert(context);
                                                            }
                                                        } else {
                                                           CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                        }
                                                    }
                                                }

            );


//Brand price holder data filling
//            ------------------------------------------------

            holder.brand.setText(feed.getBrand());
            if (feed.getSale().equals("SALE")) {

                if (feed.getSold_out()) {
                    holder.newPrice.setVisibility(View.VISIBLE);
                    holder.product_original_price.setVisibility(View.GONE);
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.style_inspiration.setVisibility(View.GONE);
                    holder.rupeeSeperation.setVisibility(View.VISIBLE);
                    holder.org_rupee_seperation.setVisibility(View.GONE);
                    if (Integer.parseInt(feed.getDiscount()) > 0) {
                        int discout_perc = (int) (Double.parseDouble(feed.getDiscount()));
                        holder.discount.setText(String.valueOf(discout_perc) + "% off");
                    }
                    else {
                        holder.discount.setVisibility(View.GONE);
                    }
                    holder.newPrice.setText(" Sold Out");
                    holder.rupeeSymbol.setVisibility(View.GONE);
                } else {
                    holder.newPrice.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(feed.getOldPrice().replace(",","")) > Integer.parseInt(feed.getNewPrice().replace(",",""))) {
                        holder.product_original_price.setVisibility(View.VISIBLE);
                        holder.product_original_price.setText(context.getResources().getString(R.string.Rs) + feed.getOldPrice().toString());
                        holder.product_original_price.setPaintFlags(holder.product_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                    }
                    else {
                        holder.product_original_price.setVisibility(View.GONE);
                    }

                    if (Integer.parseInt(feed.getDiscount()) > 0) {
                        holder.discount.setVisibility(View.VISIBLE);
                        int discout_perc = (int) (Double.parseDouble(feed.getDiscount()));

                        holder.discount.setText(String.valueOf(discout_perc) + "% off");

                    }
                    else {
                        holder.discount.setVisibility(View.GONE);
                    }


                    holder.style_inspiration.setVisibility(View.GONE);
                    holder.rupeeSymbol.setVisibility(View.GONE);
                    holder.rupeeSeperation.setVisibility(View.VISIBLE);
                    holder.org_rupee_seperation.setVisibility(View.GONE);
//                    Double price = Double.parseDouble(feed.getNewPrice());
//                    Double oldprice = Double.parseDouble(feed.getOldPrice());
//                    Double lastprice = ExternalFunctions.round(price, 2);
//                    Double oldlastprice = ExternalFunctions.round(oldprice, 2);
//                    NumberFormat myFormat = NumberFormat.getInstance();
//                    myFormat.setGroupingUsed(true);
//                    myFormat.format(lastprice);


                    //////System.out.println(myFormat.format(lastprice));
                    holder.newPrice.setText(context.getResources().getString(R.string.Rs) + feed.getNewPrice().toString());

                    ObjectAnimator mSlidOutAnimator = ObjectAnimator.ofFloat(holder.discount, "translationX", 0);
                    mSlidOutAnimator.setDuration(200);
                    mSlidOutAnimator.start();
                }


            } else if (feed.getSale().equals("SOCIAL")) {
                holder.discount.setVisibility(View.GONE);
                holder.style_inspiration.setVisibility(View.VISIBLE);
                holder.newPrice.setVisibility(View.GONE);
                holder.product_original_price.setVisibility(View.GONE);
                holder.org_rupee_seperation.setVisibility(View.GONE);
                holder.rupeeSeperation.setVisibility(View.VISIBLE);
                holder.rupeeSymbol.setVisibility(View.GONE);
            } else if (feed.getSale().equals("SOLD")) {

            }

            holder.share.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View v) {
                                                    if (CheckConnectivity.isNetworkAvailable(context)) {
                                                        BranchUniversalObject branchUniversalObject = null;


                                                        if (feed.getSale().equals("SOCIAL")) {


                                                            try {
                                                                branchUniversalObject = new BranchUniversalObject()
                                                                        .setCanonicalIdentifier("item/12345")
                                                                        .setTitle(feed.getBrand())
                                                                        .setContentDescription("My Content Description")
                                                                        .setContentImageUrl(EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString())
                                                                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                                                        .addContentMetadata("albumid", String.valueOf(feed.getId()))
                                                                        .addContentMetadata("social", "true")
                                                                        .addContentMetadata("user", String.valueOf(feed.getUserId()));

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }


                                                        } else {
                                                            try {
                                                                branchUniversalObject = new BranchUniversalObject()
                                                                        .setCanonicalIdentifier("item/12345")
                                                                        .setTitle(feed.getProductName())
                                                                        .setContentDescription(feed.getBrand())
                                                                        .setContentImageUrl(EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString())
                                                                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                                                        .addContentMetadata("albumid", String.valueOf(feed.getId()))
                                                                        .addContentMetadata("social", "false")
                                                                        .addContentMetadata("user", String.valueOf(feed.getUserId()));

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }


//                                                        try {
//                                                            System.out.println("polichu" + EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString());
//                                                        } catch (JSONException e) {
//                                                            e.printStackTrace();
//                                                        }


                                                        LinkProperties linkProperties = new LinkProperties()
                                                                .setChannel("facebook")
                                                                .setFeature("sharing")
                                                                .addControlParameter("$desktop_url", "http://zapyle.com/#/product/" + feed.getId())
                                                                .addControlParameter("$ios_url", "http://zapyle.com/#/product/" + feed.getId());


                                                        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(context, "zapyle", "Check this out -" + feed.getProductName())
                                                                //  .setCopyUrlStyle(R.id., copyUrlMessage, copiedUrlMessage)
                                                                // .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                                                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                                                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                                                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                                                                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

                                                        branchUniversalObject.showShareSheet(((Activity) context), linkProperties, shareSheetStyle, null);

//                                                    Intent intent = new Intent(Intent.ACTION_SEND);
//
//                                                    intent.putExtra(Intent.EXTRA_TEXT, "http://zapyle.com/#/product/" + feed.getId());
//                                                    intent.putExtra(Intent.EXTRA_SUBJECT, "Check this out!");
//                                                    intent.setType("text/plain");
//                                                    context.startActivity(Intent.createChooser(intent, "Share"));
                                                    } else {
                                                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                                                    }
                                                }
                                            }

            );


        } else if (holder.Holderid == 0) {
            if (CheckConnectivity.isNetworkAvailable(context)) {
                holder.customProgressBar.setVisibility(View.VISIBLE);
            } else {
                holder.customProgressBar.setVisibility(View.GONE);
            }
        }

        setAnimation(holder.itemView, position);

    }

    void sendThanks(int questionId) {
        try {
            JSONObject object = new JSONObject();
            object.put("question_id", questionId);
//            object.put("user_id", AppConstants.USER_ID);
//            ApiService.getInstance(context).postData(feedFragment, ApiConstants.URL_THANKS, object, "FEED", "thanks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return feeds.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (feeds.get(position) == null) {
            return TYPE_LOAD_MORE;
        } else {
            return TYPE_ITEM;
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.clearAnimation();
    }

}


