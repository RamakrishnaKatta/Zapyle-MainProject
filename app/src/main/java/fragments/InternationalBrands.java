package fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zapyle.zapyle.R;



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Viewholder.DividerItemDecoration;

import adapters.MainBrandAdapter;
import models.BrandModel;


/**
 * Created by haseeb on 9/12/16.
 */
public class InternationalBrands extends Fragment {
    public String SCREEN_NAME = "BRANDS";
    ArrayList<BrandModel> international = new ArrayList<BrandModel>();
    RecyclerView recyclerView;
    MainBrandAdapter adapter;
    LinearLayout side_index;
    Map<String, Integer> mapIndex;
    int selected_index = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View view = inflater.inflate(R.layout.main_brand_fragment_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.brand_text_recyclerview);
        side_index = (LinearLayout) view.findViewById(R.id.side_index);

        Bundle b = getArguments();
        international = b.getParcelableArrayList("InternationalBrands");
        System.out.println("BRANDS INTERNATIONAL : " + international);
        getIndexList(international);

        displayIndex();
        adapter = new MainBrandAdapter(international, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.line_divider)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void displayIndex() {
        TextView textView;
        Map<String, Integer> map = new TreeMap<String, Integer>(mapIndex);
        List<String> indexList = new ArrayList<String>(map.keySet());
        for (int i = 0; i < indexList.size(); i++) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(indexList.get(i));
            final TextView finalTextView = textView;
            final int[] color = {Color.TRANSPARENT};
            finalTextView.setBackgroundColor(color[0]);
            if (i == selected_index) {
                finalTextView.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selected_index = finalI;
                            finalTextView.setBackgroundColor(Color.parseColor("#f2f2f2"));

                            side_index.removeAllViews();
                            displayIndex();
                            recyclerView.scrollToPosition(mapIndex.get(finalTextView.getText()));
                        }
                    });
                }
            });
            side_index.addView(textView);
        }
//        progress.setVisibility(View.GONE);
    }

    private void getIndexList(ArrayList<BrandModel> international) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < international.size(); i++) {
            String category = international.get(i).getName();
            String index = category.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }
}
