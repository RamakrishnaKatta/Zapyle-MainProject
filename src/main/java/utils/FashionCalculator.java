package utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haseeb on 7/9/15.
 */
public class FashionCalculator {

    private Integer[] AgeOfProduct = {0,1,2,3};
    public static ArrayList<String> conditionTags = new ArrayList<String>();
    private static String[] condition = {"New_with_tags","Pre_Loved", "Pre_Pre_Loved", "Further"};
    public static ArrayList<Double> New_with_tags = new ArrayList<Double>();
    public static ArrayList<Double> Pre_Loved = new ArrayList<Double>();
    public static ArrayList<Double> Pre_Pre_Loved = new ArrayList<Double>();
    public static ArrayList<Double> Further = new ArrayList<Double>();
    private static HashMap<String, ArrayList> conditions = new HashMap<>();


    public static Double GetPrice(int age,String condition, Double originalPrice){
        conditions.put(conditionTags.get(0).toString(),New_with_tags);
        conditions.put(conditionTags.get(1).toString(),Pre_Loved);
        conditions.put(conditionTags.get(2).toString(), Pre_Pre_Loved);
        conditions.put(conditionTags.get(3).toString(), Further);

        ArrayList<Double> a = conditions.get(condition);
        Double percentage = a.get(age);
        Double Price = originalPrice - (originalPrice * percentage);
        ////System.out.println("the price is:" + Price);
        return Price;

    }

}
