package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;


public class PopupOverlay1 extends Activity {
    RelativeLayout rl,rlparent;
    TextView txttitle,txtdescription;
    ImageView imagepath,imgclose;
    TextView txtclick;

    int intdelay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        super.setTheme(R.style.AppTheme_CustomThemeclose);


        //UXCam.startWithKey("1dfb25141864376");
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        //getWindow().setLayout((int)(width*.9),(int)(height*.6));
        rl=(RelativeLayout)findViewById(R.id.rlbotttm);
        rlparent=(RelativeLayout)findViewById(R.id.rlparent);
        txttitle=(TextView)findViewById(R.id.txttitle);
        txtdescription=(TextView)findViewById(R.id.txtdescription);
        imagepath=(ImageView)findViewById(R.id.image);
        imgclose=(ImageView)findViewById(R.id.imgclose);
        rl.bringToFront();
        txtclick=(TextView)findViewById(R.id.btclick);
        Intent receive_i=getIntent();
        Bundle my_bundle_received=receive_i.getExtras();
        String image= my_bundle_received.getString("strimage");
        String strtitle= my_bundle_received.getString("strtilte");
        String straction=my_bundle_received.getString("str_action");
        intdelay=my_bundle_received.getInt("int_delay");
        final String activityname= "activity."+my_bundle_received.getString("activity_name");
        final String struri=my_bundle_received.getString("str_uri");
        String strdescription= my_bundle_received.getString("str_description");
        String strcta= my_bundle_received.getString("str_cta");
        boolean bl_fullscreen= my_bundle_received.getBoolean("bl_fullscreen");

        Glide.with(PopupOverlay1.this)
                .load(EnvConstants.APP_MEDIA_URL + image)
                .fitCenter()
                .placeholder(R.drawable.playholderscreen)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imagepath);

        txttitle.setText(strtitle);
        txtdescription.setText(strdescription);
        txtclick.setText(strcta);

        Glide.with(PopupOverlay1.this)
                .load(image)
                .fitCenter()
                .placeholder(R.drawable.playholderscreen)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imagepath);

        txtclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = null;
                try {
                    newintent = new Intent(PopupOverlay1.this, Class.forName(activityname));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(newintent);
                finish();
            }
        });

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }


}
