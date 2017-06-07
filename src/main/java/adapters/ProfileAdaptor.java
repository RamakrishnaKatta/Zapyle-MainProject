package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.Myinfopage;
import activity.ProductPage;
import activity.ProfilePage;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import models.ProfileData;
import models.ProfileDetails;
import network.AdmireTask;
import network.LikeTask;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 28/11/15.
 */
public class ProfileAdaptor extends RecyclerView.Adapter<ProfileAdaptor.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    ArrayList<ProfileData> profiledata = new ArrayList<ProfileData>();
    ProfileDetails profileDetails;
    LayoutInflater inflater;
    Context context;
    int screenWidth, screenHeight;
    private int lastPosition = -1;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;
        RoundedImageView profileimage;
        TextView profilename, descritption, admirersCount, admiringCount, listingCount, admireClick, editClick, shareClick;

        ImageView albumImage, like, emptyImage;
        TextView albumTitle, o_price, l_price, discount, likeCount;
        LinearLayout EmptyLayout, albumLayout, amountLayout;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;

                profileimage = (RoundedImageView) itemView.findViewById(R.id.profilePic);
                profilename = (TextView) itemView.findViewById(R.id.profilename);
                descritption = (TextView) itemView.findViewById(R.id.description);
                admirersCount = (TextView) itemView.findViewById(R.id.admirersCount);
                admiringCount = (TextView) itemView.findViewById(R.id.admiringCount);
                listingCount = (TextView) itemView.findViewById(R.id.lisingCount);
                admireClick = (TextView) itemView.findViewById(R.id.profileadmire);
                editClick = (TextView) itemView.findViewById(R.id.profileEdit);
                shareClick = (TextView) itemView.findViewById(R.id.profileShare);


            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
                mView = itemView;

                like = (ImageView) itemView.findViewById(R.id.like);
                albumTitle = (TextView) itemView.findViewById(R.id.productTitle);
                albumImage = (ImageView) itemView.findViewById(R.id.productImage);
                discount = (TextView) itemView.findViewById(R.id.discount);
                o_price = (TextView) itemView.findViewById(R.id.o_price);
                l_price = (TextView) itemView.findViewById(R.id.l_price);
                likeCount = (TextView) itemView.findViewById(R.id.likeCount);
                albumLayout = (LinearLayout) itemView.findViewById(R.id.albumLayout);
                amountLayout = (LinearLayout) itemView.findViewById(R.id.amountLayout);


            } else {
                Holderid = 2;
                mView = itemView;
                emptyImage = (ImageView) itemView.findViewById(R.id.emptyImage);
            }

        }

    }


    public ProfileAdaptor(ArrayList<ProfileData> data, Context context, ProfileDetails profileDetails) { // MyAdapter Constructor with titles and icons parameter
        this.profiledata = data;
        this.profileDetails = profileDetails;
        RecyclerViewClosetAdapter.context = context;
        JSONObject measure = ExternalFunctions.displaymetrics(context);

        try {
            screenWidth = measure.getInt("width");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            screenHeight = measure.getInt("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_placeholder, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else if (viewType == TYPE_EMPTY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_placeholder_empty, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_load_more, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        }

    }


    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        System.out.println("DATAALLLL: inside adaptor 1 out");
            if (holder.Holderid == 0) {
                System.out.println("PROFILEADAPTOR INSIDE if");
                holder.albumLayout.setVisibility(View.VISIBLE);

                try {
                    holder.albumImage.getLayoutParams().height = (int) (screenWidth * 1.333);
                    holder.albumImage.getLayoutParams().width = screenWidth;
                } catch (Exception e) {
                    holder.albumImage.getLayoutParams().height = (int) (screenWidth * 1.333);
                    holder.albumImage.getLayoutParams().width = screenWidth;
                }
                final ProfileData item = profiledata.get(position);


                if (ExternalFunctions.UploadCheck(context, item.getAlbumid())) {
                    //////System.out.println("jjjjj:"+item.getAlbumid());
                    holder.albumImage.setImageURI(Uri.parse(item.getImage()));
                } else {
                    Glide.with(context)
                            .load(EnvConstants.APP_MEDIA_URL + item.getImage())
                            .fitCenter()
                            .placeholder(R.drawable.playholderscreen)
                            .crossFade()
                            .override(screenWidth, (int) (screenWidth * 1.333))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.albumImage);
                }

                holder.albumTitle.setText(item.getTitle());
                if (item.getL_price() == 0) {
                    holder.l_price.setText("Style Inspiration");
                    holder.o_price.setVisibility(View.GONE);
                    holder.discount.setVisibility(View.GONE);
                } else {
                    if (item.getO_price() > item.getL_price()) {
                        holder.o_price.setVisibility(View.VISIBLE);
                        holder.discount.setVisibility(View.VISIBLE);
                        holder.o_price.setText(context.getResources().getString(R.string.Rs) + String.valueOf(item.getO_price()));
                        holder.l_price.setText(context.getResources().getString(R.string.Rs) + String.valueOf(item.getL_price()));
                    } else {
                        holder.o_price.setVisibility(View.GONE);
                        holder.discount.setVisibility(View.GONE);
                        holder.l_price.setText(context.getResources().getString(R.string.Rs) + String.valueOf(item.getL_price()));
                    }
                }


                if (Integer.parseInt(item.getDiscount().replace("% off", "")) > 0) {
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.discount.setText(item.getDiscount().toUpperCase());
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
//            if (item.getO_price() == 0){
//                holder.amountLayout.setVisibility(View.GONE);
//            }

                if (item.getLikecount() > 0) {
                    holder.likeCount.setVisibility(View.VISIBLE);
                    holder.likeCount.setText(String.valueOf(item.getLikecount()));
                } else {
                    holder.likeCount.setVisibility(View.GONE);
                }
                if (item.getLoved()) {
                    holder.like.setImageResource(R.drawable.orangeheart);
                } else {
                    holder.like.setImageResource(R.drawable.heartblack);
                }

                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (GetSharedValues.LoginStatus(context)) {

                            if (item.getLoved()) {
                                holder.like.setImageResource(R.drawable.heartblack);
                                item.setLoved(false);
                                item.setLikecount(item.getLikecount() - 1);
                                holder.likeCount.setText(String.valueOf(item.getLikecount()));
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", Integer.parseInt(item.getAlbumid()));
                                    likeObject.put("action", "unlike");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new LikeTask(context).execute(likeObject);
                            } else {
                                holder.like.setImageResource(R.drawable.orangeheart);
                                item.setLoved(true);
                                item.setLikecount(item.getLikecount() + 1);
                                holder.likeCount.setText(String.valueOf(item.getLikecount()));
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", Integer.parseInt(item.getAlbumid()));
                                    likeObject.put("action", "like");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new LikeTask(context).execute(likeObject);
                            }
                        } else {
                            Alerts.loginAlert(context);
                        }

                    }
                });


                holder.albumImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ExternalFunctions.UploadCheck(context, item.getAlbumid())) {
                            ProfilePage.showUploadDialog(context);
                        } else {
                            Intent product = new Intent(context, ProductPage.class);
                            product.putExtra("activity", "ProfilePage");
                            product.putExtra("album_id", Integer.parseInt(item.getAlbumid()));
                            context.startActivity(product);
                            ((Activity) context).finish();
                        }
                    }
                });


            } else if (holder.Holderid == 1) {

                Glide.with(context)
                        .load(profileDetails.getProfilepic())
                        .fitCenter()
                        .placeholder(R.drawable.prof_placeholder)
                        .error(R.drawable.prof_placeholder)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.profileimage);


                holder.profilename.setText(profileDetails.getUsername());
                FontUtils.setGeorgiaItalicDisplayFont(holder.descritption, context.getAssets());
                if (profileDetails.getDescription() != null) {
                    holder.descritption.setText("\\" + profileDetails.getDescription() + "\\");
                } else {
                    holder.descritption.setVisibility(View.GONE);
                }

                if (!profileDetails.getDescription().equals("null")) {
                    holder.descritption.setText(profileDetails.getDescription());
                } else {
                    holder.descritption.setVisibility(View.GONE);
                }

                holder.admiringCount.setText(String.valueOf(profileDetails.getAdmiringCount()));
                holder.admirersCount.setText(String.valueOf(profileDetails.getAdmirecount()));
                holder.listingCount.setText(String.valueOf(profileDetails.getLisingcount()));
                if (profileDetails.getAdmiredBy()) {
                    holder.admireClick.setText("Admiring");
                } else {
                    holder.admireClick.setText("Admire");
                }


                holder.admireClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GetSharedValues.LoginStatus(context)) {
                            if (profileDetails.getAdmiredBy()) {
                                holder.admireClick.setText("Admire");
                                profileDetails.setAdmiredBy(false);
                                JSONObject admireObject = null;
                                try {
                                    admireObject = new JSONObject();
                                    admireObject.put("action", "unadmire");
                                    admireObject.put("user", GetSharedValues.getuserId(context));
                                } catch (Exception e) {

                                }
                                profileDetails.setAdmirecount(profileDetails.getAdmirecount() - 1);
                                holder.admirersCount.setText(String.valueOf(profileDetails.getAdmirecount()));
                                new AdmireTask(context).execute(admireObject);
                            } else {
                                holder.admireClick.setText("Admiring");
                                profileDetails.setAdmiredBy(true);
                                JSONObject admireObject = null;
                                try {
                                    admireObject = new JSONObject();
                                    admireObject.put("action", "admire");
                                    admireObject.put("user", GetSharedValues.getuserId(context));
                                } catch (Exception e) {

                                }
                                profileDetails.setAdmirecount(profileDetails.getAdmirecount() + 1);
                                holder.admirersCount.setText(String.valueOf(profileDetails.getAdmirecount()));
                                new AdmireTask(context).execute(admireObject);
                            }
                        }else {
                            Alerts.loginAlert(context);
                        }
                    }
                });

                holder.shareClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GetSharedValues.LoginStatus(context)) {
                            Share();
                        }else {
                            Alerts.loginAlert(context);
                        }

                    }
                });

                holder.editClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GetSharedValues.LoginStatus(context)) {
                            Share();

                        ProfilePage.intpassactivity = 1;
                        Intent myinfo = new Intent(context, Myinfopage.class);
                        myinfo.putExtra("booltype", true);
                        context.startActivity(myinfo);
                        ((Activity) context).finish();
                        }else {
                            Alerts.loginAlert(context);
                        }

                    }
                });

                holder.profileimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GetSharedValues.LoginStatus(context)) {
                            Share();


                        ProfilePage.intpassactivity = 1;
                        Intent myinfo = new Intent(context, Myinfopage.class);
                        myinfo.putExtra("booltype", true);
                        context.startActivity(myinfo);
                        ((Activity) context).finish();
                        }else {
                            Alerts.loginAlert(context);
                        }
                    }
                });


                if (GetSharedValues.getuserId(context) == profileDetails.getUserId()) {
                    holder.admireClick.setVisibility(View.GONE);
                    holder.shareClick.setVisibility(View.VISIBLE);
                    holder.editClick.setVisibility(View.VISIBLE);
                } else {
                    holder.admireClick.setVisibility(View.VISIBLE);
                    holder.shareClick.setVisibility(View.GONE);
                    holder.editClick.setVisibility(View.GONE);
                }
            } else {
                holder.emptyImage.setImageResource(R.drawable.empty_closet);
                try {
                    holder.emptyImage.getLayoutParams().height = (screenHeight / 2) - 100;
                } catch (Exception e) {
                    holder.emptyImage.getLayoutParams().height = (screenHeight / 2) - 100;
                }
            }
        }
//        setAnimation(holder.itemView, position);



    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return profiledata.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (profiledata.get(position) != null) {
            if (profiledata.get(position).getAlbumid().equals("0")) {
                return TYPE_EMPTY;
            } else {
                return TYPE_LOAD_MORE;
            }

        } else {
            return TYPE_ITEM;

        }
    }

    public void Share() {
        BranchUniversalObject branchUniversalObject = null;


        branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle(String.valueOf(profileDetails.getUsername()))
                .setContentDescription(profileDetails.getDescription())
                .setContentImageUrl(EnvConstants.APP_MEDIA_URL + profileDetails.getProfilepic())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("prof_id", String.valueOf(profileDetails.getUserId()))
                .addContentMetadata("social", "false")
                .addContentMetadata("user", "000");


        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://zapyle.com/#/user/profile/" + String.valueOf(profileDetails.getUserId()))
                .addControlParameter("$ios_url", "http://zapyle.com/#/user/profile/" + String.valueOf(profileDetails.getUserId()));


        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(context, "zapyle", "Check this out -" + profileDetails.getUsername())
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet((Activity) context, linkProperties, shareSheetStyle, null);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}