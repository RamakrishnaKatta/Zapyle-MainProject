package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zapyle.zapyle.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<String>{

    Context context;
    ArrayList<String> objects;
    String firstElement;
    boolean isFirstTime;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects, String defaultText) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.isFirstTime = true;
        setDefaultText(defaultText);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(isFirstTime) {
            objects.set(0, firstElement);
            isFirstTime = false;
        }
        return getCustomView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        return getCustomView(position, convertView, parent);
    }

    public void setDefaultText(String defaultText) {
        this.firstElement = objects.get(0);
        objects.set(0, defaultText);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.simple_spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.text1);
        label.setText(objects.get(position));

        return row;
    }

}