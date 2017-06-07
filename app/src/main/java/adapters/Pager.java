package adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import fragments.IndianBrands;
import fragments.InternationalBrands;
import models.BrandModel;

/**
 * Created by haseeb on 9/12/16.
 */
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    private String[] tabTitles;

    ArrayList<BrandModel> indian = new ArrayList<BrandModel>();
    ArrayList<BrandModel> international = new ArrayList<BrandModel>();
    String type1;

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount, ArrayList<BrandModel> indian, ArrayList<BrandModel> international, String type1, String type2) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
        this.indian = indian;
        this.international = international;
        this.type1 = type1;
        this.tabTitles = new String[]{type1, type2};
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                if (type1.equals("INDIAN")) {
                    IndianBrands tab1 = new IndianBrands();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList("IndianBrands", indian);
//                data.putSerializable("IndianBrands",  indian);
                    tab1.setArguments(data);
                    return tab1;
                } else {
                    InternationalBrands tab2 = new InternationalBrands();
                    Bundle data1 = new Bundle();
                    data1.putParcelableArrayList("InternationalBrands", international);
                    tab2.setArguments(data1);
                    return tab2;
                }

            case 1:
                if (type1.equals("INDIAN")) {
                    InternationalBrands tab2 = new InternationalBrands();
                    Bundle data1 = new Bundle();
                    data1.putParcelableArrayList("InternationalBrands", international);
                    tab2.setArguments(data1);
                    return tab2;
                } else {

                    IndianBrands tab1 = new IndianBrands();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList("IndianBrands", indian);
//                data.putSerializable("IndianBrands",  indian);
                    tab1.setArguments(data);
                    return tab1;
                }
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabTitles.length;
    }
}