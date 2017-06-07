package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import io.realm.RealmObject;

/**
 * Created by haseeb on 24/8/15.
 */
public class RealmString{
    public static String strSeparator = ",";
    public static String convertArrayToString(List<String> array){
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+array.get(i);
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static List<String> convertStringToArray(String str){
        List<String> arr = Arrays.asList(str.split(strSeparator));
        return arr;
    }



//    Arraylistt
//    -------------------------

    public static String convertArrayListToString(ArrayList<String> array){
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+array.get(i);
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static ArrayList<String> convertStringToArrayList(String str){
        ArrayList<String> arr = new ArrayList<String>();
        arr.addAll(Arrays.asList(str.split(strSeparator)));
        return arr;
    }

}
