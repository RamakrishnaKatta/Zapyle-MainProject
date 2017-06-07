package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.ProductPage;
import activity.ProfilePage;
import models.ProfileData;
import models.ProfileDetails;
import network.AdmireTask;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 28/11/15.
 */
public class DesignerAdapter extends RecyclerView.Adapter<DesignerAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    ArrayList<ProfileData> profiledata = new ArrayList<ProfileData>();
    ProfileDetails profileDetails;
    LayoutInflater inflater;
    Context context;
    int screenWidth, screenHeight;
    private int lastPosition = -1;
    String SCREEN_NAME = "PROFILEPAGE";


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;
        TextView descritption;

        ImageView emptyImage, coverPic;
        ImageView image, like;
        TextView title, usertype, shortDescription, toggleDescription, listings, admirers, admire_button, brand;
        TextView price, orgPrice, discount;
        int screenHeight = 0, screenWeight = 0;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;

                coverPic = (ImageView) itemView.findViewById(R.id.cover_pic);
                descritption = (TextView) itemView.findViewById(R.id.description);
                shortDescription = (TextView) itemView.findViewById(R.id.description_small);
                toggleDescription = (TextView) itemView.findViewById(R.id.toggleDescription);
                toggleDescription.setPaintFlags(toggleDescription.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                listings = (TextView) itemView.findViewById(R.id.listings);
                admirers = (TextView) itemView.findViewById(R.id.admirers);
                admire_button = (TextView) itemView.findViewById(R.id.admire_button);

            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
                mView = itemView;
                image = (ImageView) itemView.findViewById(R.id.feedgrid);
                title = (TextView) itemView.findViewById(R.id.feedgridtitle);
                like = (ImageView) itemView.findViewById(R.id.imageview_like);
                price = (TextView) itemView.findViewById(R.id.feedgridprice);
                brand = (TextView) itemView.findViewById(R.id.brand);
                usertype = (TextView) itemView.findViewById(R.id.feeduser);
                orgPrice = (TextView) itemView.findViewById(R.id.feedgridOrgprice);
                orgPrice.setPaintFlags(orgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                orgPrice.setTextColor(Color.GRAY);
                discount = (TextView) itemView.findViewById(R.id.feedgriddiscount);
                discount.getLayoutParams().width = (int) Double.parseDouble(String.valueOf(((screenHeight - 6) / 2) * 0.4));
                image.getLayoutParams().height = ((screenHeight - 6) / 2) + 30;
                image.getLayoutParams().width = (screenHeight - 6) / 2;

            } else {
                Holderid = 2;
                mView = itemView;
                emptyImage = (ImageView) itemView.findViewById(R.id.emptyImage);
            }

        }

    }


    public DesignerAdapter(ArrayList<ProfileData> data, Context context, ProfileDetails profileDetails) { // MyAdapter Constructor with titles and icons parameter
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_placeholder_designer, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else if (viewType == TYPE_EMPTY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_placeholder_empty, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_griditem_new, parent, false); //Inflating the layout
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
                final ProfileData item = profiledata.get(position);

                holder.title.setText(item.getTitle());
                holder.brand.setText(item.getBrand());

                if (!profileDetails.getUsertype().contains("designer")) {
                    holder.usertype.setText("PRE LOVED");
                }
                else {
                    holder.usertype.setText("Brand New");
                }
                if (ExternalFunctions.UploadCheck(context, item.getAlbumid())) {
                    //////System.out.println("jjjjj:"+item.getAlbumid());
                    holder.image.setImageURI(Uri.parse(item.getImage()));
                } else {
                    Glide.with(context)
                            .load(EnvConstants.APP_MEDIA_URL + item.getImage())
                            .fitCenter()
                            .placeholder(R.drawable.playholderscreen)
                            .crossFade()
                            .override((screenWidth - 6) / 2, (int) (((screenWidth - 6) / 2) * 1.333))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.image);


                }

                holder.image.getLayoutParams().height = (int) (((screenWidth - 6) / 2) * 1.333);
                holder.image.getLayoutParams().width = (screenWidth - 6) / 2;

                System.out.println("DETAILS : " + item.getL_price() + "__" + item.getTitle() + "___" + item.getSale());

                if (item.getSale()) {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.orgPrice.setVisibility(View.VISIBLE);
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.price.setText(context.getResources().getString(R.string.Rs) + item.getL_price());
                    if (item.getO_price() > item.getL_price()) {
                        holder.orgPrice.setText(context.getResources().getString(R.string.Rs) + item.getO_price());
                        holder.discount.setText(item.getDiscount() + "% Off".toUpperCase());
                        holder.discount.setTextColor(Color.BLACK);
                        holder.discount.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        holder.discount.setVisibility(View.GONE);
                        holder.orgPrice.setVisibility(View.GONE);
                    }

                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.orgPrice.setVisibility(View.VISIBLE);
                    holder.discount.setVisibility(View.VISIBLE);
                    if (item.getL_price() == 0) {
                        holder.price.setText("Style Inspiration");
                        holder.orgPrice.setVisibility(View.GONE);
                        holder.discount.setVisibility(View.GONE);
                    } else {
                        holder.price.setText("Sold out");
                        holder.orgPrice.setVisibility(View.GONE);
                        holder.discount.setVisibility(View.GONE);
                    }
                }
                if (item.getLoved()) {
                    //////System.out.println("inside is liked");
                    holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.orangeheart));
                } else {
                    //////System.out.println("inside is unliked");
                    holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.heartblack));
                }
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckConnectivity.isNetworkAvailable(context)) {
                            if (GetSharedValues.LoginStatus(context)) {
                                if (item.getLoved()) {
                                    item.setLoved(false);
                                    final JSONObject likeObject = new JSONObject();
                                    try {
                                        likeObject.put("product_id", item.getAlbumid());
                                        likeObject.put("action", "unlike");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "unlike");

                                } else {
                                    item.setLoved(true);
                                    JSONObject superprop = new JSONObject();
                                    final JSONObject likeObject = new JSONObject();
                                    try {
                                        likeObject.put("product_id", item.getAlbumid());
                                        likeObject.put("action", "like");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "like");

                                }
                                notifyDataSetChanged();
                            } else {
                                Alerts.loginAlert(context);
                            }
                        } else {
                            CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                        }
                    }
                });


                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckConnectivity.isNetworkAvailable(context)) {
                            if (ExternalFunctions.UploadCheck(context, item.getAlbumid())) {
                                ProfilePage.showUploadDialog(context);
                            } else {
                                Intent product = new Intent(context, ProductPage.class);
                                product.putExtra("activity", "ProfilePage");
                                product.putExtra("album_id", Integer.parseInt(item.getAlbumid()));
                                context.startActivity(product);
                                ((Activity) context).finish();
                            }
                        } else {
                            CustomMessage.getInstance().CustomMessage(context, "Check Your Internet connection.");
                        }
                    }
                });


            } else {

                Glide.with(context)
                        .load(profileDetails.getCoverPic())
                        .fitCenter()
                        .placeholder(R.drawable.playholderscreen)
                        .error(R.drawable.playholderscreen)
                        .crossFade()
                        .override(GetSharedValues.getScreenWidth(context), GetSharedValues.getScreenWidth(context))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.coverPic);

                holder.coverPic.getLayoutParams().height = GetSharedValues.getScreenWidth(context);
                holder.coverPic.getLayoutParams().width = GetSharedValues.getScreenWidth(context);
                //   System.out.println("DESCRIP : " + profileDetails.getDescription() + "__" + profileDetails.getShortDescription());

                if (!profileDetails.getDescription().isEmpty()) {
                    holder.descritption.setVisibility(View.VISIBLE);
                    holder.descritption.setText(profileDetails.getDescription());
                    holder.toggleDescription.setVisibility(View.VISIBLE);
                } else {
                    holder.descritption.setVisibility(View.GONE);
                    holder.toggleDescription.setVisibility(View.GONE);
                }


                holder.descritption.setVisibility(View.GONE);
                holder.descritption.setGravity(Gravity.CENTER);
                holder.shortDescription.setGravity(Gravity.CENTER);
                holder.shortDescription.setText(profileDetails.getShortDescription());

                holder.admirers.setText(String.valueOf(profileDetails.getAdmirecount()));
                holder. listings.setText(String.valueOf(profileDetails.getLisingcount()));
                if (profileDetails.getAdmiredBy()) {
                    holder.admire_button.setText("Admiring");
                } else {
                    holder.admire_button.setText("Admire");
                }

                holder.toggleDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.descritption.getVisibility() != View.VISIBLE) {
                            holder.toggleDescription.setText("HIDE STORY");
                            ExternalFunctions.expand(holder.descritption);

                        } else {

                            holder.toggleDescription.setText("READ FULL STORY");
                            ExternalFunctions.collapse(holder.descritption);
                        }
                    }
                });



                holder.admire_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GetSharedValues.LoginStatus(context)) {
                            if (profileDetails.getAdmiredBy()) {
                                holder.admire_button.setText("Admire");
                                profileDetails.setAdmiredBy(false);
                                JSONObject admireObject = null;
                                try {
                                    admireObject = new JSONObject();
                                    admireObject.put("action", "unadmire");
                                    admireObject.put("user", GetSharedValues.getuserId(context));
                                } catch (Exception e) {

                                }
                                profileDetails.setAdmirecount(profileDetails.getAdmirecount() - 1);
                                holder.admirers.setText(String.valueOf(profileDetails.getAdmirecount()));
                                new AdmireTask(context).execute(admireObject);
                            } else {
                                holder.admire_button.setText("Admiring");
                                profileDetails.setAdmiredBy(true);
                                JSONObject admireObject = null;
                                try {
                                    admireObject = new JSONObject();
                                    admireObject.put("action", "admire");
                                    admireObject.put("user", GetSharedValues.getuserId(context));
                                } catch (Exception e) {

                                }
                                profileDetails.setAdmirecount(profileDetails.getAdmirecount() + 1);
                                holder.admirers.setText(String.valueOf(profileDetails.getAdmirecount()));
                                new AdmireTask(context).execute(admireObject);
                            }
                        } else {
                            Alerts.loginAlert(context);
                        }

                    }
                });

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



}
