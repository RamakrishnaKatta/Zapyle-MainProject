package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zapyle.zapyle.R;

import activity.SizeguideActivity;

/**
 * Created by zapyle on 4/2/16.
 */
public class CustomAlert {
    Context ctx;
   String strtype;
    String id;
    int j=0;
    public CustomAlert(Context context, final String type, final String id) {


        this.ctx=context;
        this.strtype=type;
        this.id=id;

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.sizeselected, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

        alertDialogBuilder.setView(promptsView);




        final Button btnalert = (Button) promptsView
                .findViewById(R.id.btnalert);
        final Button btnreomove = (Button) promptsView
                .findViewById(R.id.btnremove);
        btnreomove.setVisibility(View.GONE);
        final Button btncancel = (Button) promptsView
                .findViewById(R.id.btncancel);
        final EditText edt=(EditText) promptsView.findViewById(R.id.edquantity);
        for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {
            if (SizeguideActivity.selectedqty.get(i).contains(strtype)) {
                int k = SizeguideActivity.selectedqty.get(i).toString().indexOf("-");
                edt.setText(SizeguideActivity.selectedqty.get(i).toString().substring(k+1,SizeguideActivity.selectedqty.get(i).length()));
                btnreomove.setVisibility(View.VISIBLE);
            }

        }
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();

        btnalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {

                    if(SizeguideActivity.selectedqty.get(i).contains(" "+id)){
                        SizeguideActivity.selectedqty.remove(i);
                    }
                }
                if (edt.getText().toString().length() > 0 && Integer.parseInt(edt.getText().toString()) != 0) {
                    for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {
                        if (SizeguideActivity.selectedqty.get(i).contains(strtype)) {
                            SizeguideActivity.selectedqty.remove(i);

                            SizeguideActivity.selectedqty.add(strtype + edt.getText());
                            if (strtype.contains("UK")) {
                                int k = SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-");
                                ////System.out.println("fhhh" + k);
                                if (k > 0) {
                                    SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                                } else {
                                    SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                                }
                                if(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }
                                if(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }

                            } else if (strtype.contains("US")) {
                                int k = SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-");
                                if (k > 0) {
                                    SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                                } else {
                                    SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                                }
                                if(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }
                                if(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }
                            } else {
                                int k = SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-");
                                if (k > 0) {
                                    SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                                } else {
                                    SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                                }
                                if(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }
                                if(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                    SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")));
                                }
                            }
                            j = 1;
                            i = SizeguideActivity.selectedqty.size();
                        }
                    }
                    if (j == 0) {
                        SizeguideActivity.selectedqty.add(strtype + edt.getText());
                        if (strtype.contains("UK")) {
                            int k = SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-");
                            ////System.out.println("fhhh" + k);
                            if (k > 0) {
                                SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                            } else {
                                SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                            }
                            if(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }
                            if(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }

                        } else if (strtype.contains("US")) {
                            int k = SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-");
                            if (k > 0) {
                                SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                            } else {
                                SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                            }
                            if(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }
                            if(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }
                        } else {
                            int k = SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-");
                            if (k > 0) {
                                SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, k) + "-(" + edt.getText() + ")");
                            } else {
                                SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText() + "-(" + edt.getText() + ")");
                            }
                            if(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }
                            if(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")>0){
                                SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-")));
                            }
                        }

                    }
                    alertDialog.dismiss();
                    ////System.out.println("aas" + SizeguideActivity.selectedqty);
                } else {
                    CustomMessage.getInstance().CustomMessage(ctx, "Please add valid quantity");
                }

            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inty = 0;
                for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {
                    if (SizeguideActivity.selectedqty.get(i).contains(strtype)) {
                        inty = 1;
                    }
                }
                if (inty == 0) {
                    if (strtype.contains("UK")) {
                        int k = SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-");
                        if (k <= 0) {
                            SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                            SizeguideActivity.tuk[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                        }

                    } else if (strtype.contains("US")) {
                        int k = SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-");
                        if (k <= 0) {
                            SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                            SizeguideActivity.tus[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                        }
                    } else {
                        int k = SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-");
                        if (k <= 0) {
                            SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                            SizeguideActivity.teu[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
                alertDialog.dismiss();
            }
        });

        btnreomove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < SizeguideActivity.selectedqty.size(); i++) {
                    if (SizeguideActivity.selectedqty.get(i).contains(strtype)) {
                        SizeguideActivity.selectedqty.remove(i);
                        SizeguideActivity.selectedSizes.remove(i);
                        SizeguideActivity.selectedid.remove(i);

                        if (strtype.contains("UK")) {
                            int k = SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().indexOf("-");
                            if (k > 0) {
                                SizeguideActivity.tuk[SizeguideActivity.intj].setText(SizeguideActivity.tuk[SizeguideActivity.intj].getText().toString().substring(0, k));
                                SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                                SizeguideActivity.tuk[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                            }

                        } else if (strtype.contains("US")) {
                            int k = SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().indexOf("-");
                            if (k > 0) {
                                SizeguideActivity.tus[SizeguideActivity.intj].setText(SizeguideActivity.tus[SizeguideActivity.intj].getText().toString().substring(0, k));
                                SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                                SizeguideActivity.tus[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                            }
                        } else {
                            int k = SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().indexOf("-");
                            if (k > 0) {
                                SizeguideActivity.teu[SizeguideActivity.intj].setText(SizeguideActivity.teu[SizeguideActivity.intj].getText().toString().substring(0, k));
                                SizeguideActivity.l2h[SizeguideActivity.intj].setBackgroundColor(Color.parseColor("#ffffff"));
                                SizeguideActivity.teu[SizeguideActivity.intj].setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                        i = SizeguideActivity.selectedqty.size();
                    }
                }
                alertDialog.dismiss();
            }
        });

        // create alert dialog



    }
}
