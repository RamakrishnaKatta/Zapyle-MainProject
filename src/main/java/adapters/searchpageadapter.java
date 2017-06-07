package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zapyle.zapyle.R;

import java.util.HashMap;
import java.util.Map;

import fragments.fragmentcloset;
import fragments.fragmentproduct;

/**
 * Created by zapyle on 11/5/16.
 */
public class searchpageadapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context ctx;
    public fragmentproduct tab1;
    public fragmentcloset tab2;
    private Map <Integer,String> mFragmentTags ;
    private FragmentManager mFragment;


    public searchpageadapter(FragmentManager fm, int NumOfTabs, Context ctx) {
        super(fm);
        this.mFragment=fm;
        this.mFragmentTags= new HashMap<Integer,String>();
        this.mNumOfTabs = NumOfTabs;
        this.ctx=ctx;
    }
    private String tabTitles[] = new String[] { "PRODUCT", "CLOSET" };


    public View getTabView(int position) {

        View v = LayoutInflater.from(ctx).inflate(R.layout.tabviewcustomsearch, null);
        TextView tv = (TextView) v.findViewById(R.id.tvtext);
        tv.setText(tabTitles[position]);
        return v;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(tab1==null) {
                    tab1 = new fragmentproduct();
                }
                return tab1;
            case 1:
                if(tab2==null) {
                    tab2 = new fragmentcloset();
                }

                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj=super.instantiateItem(container, position);
        if(obj instanceof  Fragment){
            Fragment f= (Fragment) obj;
            String tag=f.getTag();
            mFragmentTags.put(position,tag);
        }
        return obj;
    }
    public Fragment GetFragment(int position){
        String tag=mFragmentTags.get(position);
        if(tag==null){
            return null;
        }
        return mFragment.findFragmentByTag(tag);

    }
}

