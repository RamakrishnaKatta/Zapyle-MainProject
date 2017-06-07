package utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zapyle on 7/2/16.
 */
public class Mixpanelutils {

    public  String getTimeDiff(String time1, String time2 ){
        try
        {

            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
            try {
                Date Date1 = format.parse(time1);
                Date Date2 = format.parse(time2);

                long difference = Date2.getTime() - Date1.getTime();
                int days = (int) (difference / (1000*60*60*24));
                int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                int  min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                int sec=(int) ((difference - (1000*60*60*24*days) - (1000*60*60*hours)- (1000*60*min)/(1000*60)))/(1000*60);
                ////System.out.println("mixpaneltimespent minut" +min+"start"+time1+"end"+time2+"second"+sec);
                return min +" minutes" +sec + "seconds";
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;


    }
}
