package CustomClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zapyle.zapyle.R;

/**
 * Created by haseeb on 3/8/16.
 */
public class UpdateProgress {

    public static void getProgress(Activity activity,ViewGroup view,   ProgressBar progressBar){

        Boolean progressbarshow = false;
        LayoutInflater inflater =
                (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_progressbar, view, true);
        progressBar= (ProgressBar) view.findViewById(R.id.custom_progress_bar);
      //  return progressBar;
    }
}