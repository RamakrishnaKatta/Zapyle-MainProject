package utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Created by haseeb on 19/1/16.
 */
public class NumberPickerStylingUtils {

    private static final Drawable PICKER_DIVIDER_DRAWABLE = null;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 4;

    private NumberPickerStylingUtils() {
    }


    public static void changeDispalyCount(NumberPicker numberPicker) {
        try {
            Field field = NumberPicker.class.getField("ELECTOR_WHEEL_ITEM_COUNT");
            field.setAccessible(true);
            field.set(numberPicker, SELECTOR_WHEEL_ITEM_COUNT);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static void applyStyling(NumberPicker numberPicker) {
        try {
            Field field = NumberPicker.class.getDeclaredField("mSelectionDivider");
            field.setAccessible(true);
            field.set(numberPicker, PICKER_DIVIDER_DRAWABLE);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color,float size,float pixel)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setTextSize(size);
                    ((EditText)child).setTextColor(color);
                    ((EditText)child).setTextSize(pixel);
                    child.setEnabled(false);

                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    //  Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    // Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    //Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }


}
