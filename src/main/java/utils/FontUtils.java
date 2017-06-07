package utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by haseeb on 4/12/15.
 */
public class FontUtils {
    private static Typeface normal;

    private static Typeface bold;

    private static Typeface condensed;

    private static Typeface light;

    private static void processsViewGroup(ViewGroup v, final int len) {

        for (int i = 0; i < len; i++) {
            final View c = v.getChildAt(i);
            if (c instanceof TextView) {
                setCustomFont((TextView) c);
            } else if (c instanceof ViewGroup) {
                setCustomFont((ViewGroup) c);
            }
        }
    }

    private static void setCustomFont(TextView c) {
       if(c.getTypeface() != null){
           if(c.getTypeface().getStyle() == Typeface.BOLD){
               c.setTypeface(bold);

           }
           else {
               c.setTypeface(normal);

           }
       }
        else {
           c.setTypeface(normal);

       }

    }

    public static void setCustomFont(View topView, AssetManager assetsManager) {
//        if (normal == null || bold == null || condensed == null || light == null) {
            normal = Typeface.createFromAsset(assetsManager, "fonts/Latoregular.ttf");
            bold = Typeface.createFromAsset(assetsManager, "fonts/Latobold.ttf");

//        normal = Typeface.createFromAsset(assetsManager, "fonts/Aller_Rg.ttf");
//        bold = Typeface.createFromAsset(assetsManager, "fonts/Aller_Bd.ttf");

//        normal = Typeface.createFromAsset(assetsManager, "fonts/helvetica.ttf");
//        bold = Typeface.createFromAsset(assetsManager, "fonts/helveticabold.ttf");
//            condensed = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Condensed.ttf");
//            light = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Light.ttf");
//        }

        if (topView instanceof ViewGroup) {
            setCustomFont((ViewGroup) topView);
        } else if (topView instanceof TextView) {
            setCustomFont((TextView) topView);
        }
    }

    public static void setPlayfairDisplayFont(View topView, AssetManager assetsManager) {
//        if (normal == null || bold == null || condensed == null || light == null) {
        normal = Typeface.createFromAsset(assetsManager, "fonts/PlayfairDisplay-Italic.ttf");
        bold = Typeface.createFromAsset(assetsManager, "fonts/PlayfairDisplay-Italic.ttf");
//            condensed = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Condensed.ttf");
//            light = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Light.ttf");
//        }

        if (topView instanceof ViewGroup) {
            setCustomFont((ViewGroup) topView);
        } else if (topView instanceof TextView) {
            setCustomFont((TextView) topView);
        }
    }


    public static void setGeorgiaItalicDisplayFont(View topView, AssetManager assetsManager) {
//        if (normal == null || bold == null || condensed == null || light == null) {
        normal = Typeface.createFromAsset(assetsManager, "fonts/GeorgiaItalic.ttf");
        bold = Typeface.createFromAsset(assetsManager, "fonts/GeorgiaItalic.ttf");
//            condensed = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Condensed.ttf");
//            light = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Light.ttf");
//        }

        if (topView instanceof ViewGroup) {
            setCustomFont((ViewGroup) topView);
        } else if (topView instanceof TextView) {
            setCustomFont((TextView) topView);
        }
    }


    private static void setCustomFont(ViewGroup v) {
        final int len = v.getChildCount();
        processsViewGroup(v, len);
    }
}
