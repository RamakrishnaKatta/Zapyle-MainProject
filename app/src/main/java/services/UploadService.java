package services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import activity.upload;
import activity.SplashScreen;
import network.ApiCommunication;
import network.ApiService;
import recievers.ConnectivityDetector_Receiver;
import utils.ExternalFunctions;
import utils.ServiceHandle;

/**
 * Created by haseeb on 26/2/16.
 */
public class UploadService extends IntentService implements ApiCommunication {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "UploadService";
    public ResultReceiver receiver;
    int AlbumCount = 0;
    int CurrentCount = 0;
    int ProductId;
    Boolean Add = false;
    Boolean Status = false;
    String datapath;
    int noOfTries = 0;
    Boolean FromActivity = false;
    Boolean PTA = false;

    public UploadService() {
        super(UploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        ////System.out.println("1");
        receiver = intent.getParcelableExtra("receiver");
        if (upload.mReceiver != null) {
            receiver = upload.mReceiver;
        } else {
            receiver = SplashScreen.mReceiver;
        }
        ////System.out.println("2");
        FromActivity = intent.getBooleanExtra("FromActivity", false);
        ////System.out.println("3");
        Bundle bundle = new Bundle();
        ////System.out.println("4");

        if (FromActivity) {
            ////System.out.println("5");
            datapath = intent.getStringExtra("Filepath");
            AlbumCount = intent.getIntExtra("NumberOfProducts", 0);
            Add = intent.getBooleanExtra("Add", false);
            ProductId = intent.getIntExtra("ProductId", 0);
            PTA = intent.getBooleanExtra("PTA", false);

            SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("datapath", datapath);
            editor.putBoolean("Add", Add);
            editor.putBoolean("PTA", PTA);
            editor.putInt("AlbumCount", AlbumCount);
            editor.putInt("ProductId", ProductId);
            editor.apply();
        } else {
            ////System.out.println("6");
            datapath = intent.getStringExtra("Filepath");
            AlbumCount = intent.getIntExtra("NumberOfProducts", 0);
            Add = intent.getBooleanExtra("Add", false);
            ProductId = intent.getIntExtra("ProductId", 0);
            CurrentCount = intent.getIntExtra("CurrentCount", 0);
            PTA = intent.getBooleanExtra("PTA", false);
            SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("datapath", datapath);
            editor.putInt("AlbumCount", AlbumCount);
            editor.putBoolean("PTA", PTA);
            editor.putBoolean("Add", Add);
            editor.putInt("ProductId", ProductId);
            editor.apply();
        }
        ////System.out.println(datapath + "__" + AlbumCount + "__" + ProductId + "__" + CurrentCount);
        ////System.out.println("7");

        if (AlbumCount > 0) {
            ////System.out.println("8");
            /* Update UI: Download Service is Running */
            if(upload.mReceiver != null && SplashScreen.mReceiver != null) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            }
            ////System.out.println("9");
            try {
                ////System.out.println("10");
                if (FromActivity) {
                    ////System.out.println("inside from activity");
                    CurrentCount = 0;
                    if (CheckConnectivity.isNetworkAvailable(UploadService.this) && CurrentCount < 6) {
                        readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                        ////System.out.println(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                    }
                } else {
                    ////System.out.println("inside from brodcast reciever");
                    if (CheckConnectivity.isNetworkAvailable(UploadService.this) && CurrentCount < 6) {
                        readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                        ////System.out.println(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");

                    }
                }
                /* Sending result back to activity */
                ////System.out.println("inside handle successs");

                if (AlbumCount == CurrentCount || Status) {
                    ////System.out.println("11");
                    if(upload.mReceiver != null && SplashScreen.mReceiver != null) {
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                }
            } catch (Exception e) {
                ////System.out.println("12");
                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                if(upload.mReceiver != null && SplashScreen.mReceiver != null) {
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }


    //    Image upload task
//    -----------------------------------------------------
    private void PostData(String file) {
//        if (Add) {
        if (!file.isEmpty()) {
            JSONObject ImagetoServer = new JSONObject();
            try {
                ImagetoServer.put("image", file);
                ImagetoServer.put("add", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(PTA) {
                ApiService.getInstance(UploadService.this, 1).postData(UploadService.this, EnvConstants.APP_BASE_URL + "/upload/album/image/" + ProductId + "/?action=p_t_a", ImagetoServer, "UPLOADSERVICE", "postimage");
            }
            else {
                ApiService.getInstance(UploadService.this, 1).postData(UploadService.this, EnvConstants.APP_BASE_URL + "/upload/album/image/" + ProductId + "/", ImagetoServer, "UPLOADSERVICE", "postimage");

            }
        }
//        }
    }


//    Read file from directory
//    -----------------------------------------------------

    private void readFromFile(String filepath) {
        String ret = "";
        try {
            FileInputStream inputStream = new FileInputStream(new File(filepath));
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();

            }
            ////System.out.println("inside try");
//            if (Add) {
            if (CurrentCount < 6) {

                PostData(ret);
            }
//            } else {
//                PostEditData(ret);
//            }
        } catch (FileNotFoundException e) {
            //Log.e("login activity", "File not found: " + e.toString());
            CurrentCount = CurrentCount + 1;
            readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
        } catch (IOException e) {
            //Log.e("login activity", "Can not read file: " + e.toString());
        }
        ////System.out.println("fileeeeeee:" + ret);

    }

    private void PostEditData(String ret) {
        JSONArray imgArray = new JSONArray();
        JSONObject ImagetoServer = new JSONObject();
        try {
            ImagetoServer.put("images", ret);
            ImagetoServer.put("add", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiService.getInstance(UploadService.this, 1).postData(UploadService.this, EnvConstants.APP_BASE_URL + "/upload/album/image/" + ProductId + "/", ImagetoServer, "UPLOADSERVICE", "posteditdata");
    }


    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("postimage")) {
            ////System.out.println(response);
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("CurrentCount", CurrentCount + 1);
                editor.apply();
                if (!CheckConnectivity.isNetworkAvailable(UploadService.this)) {
                    ConnectivityDetector_Receiver.Status = false;
                    ServiceHandle.Status = false;
                }
                ////System.out.println(resp);
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        editor.putInt("CurrentCount", CurrentCount + 1);
                        editor.apply();
                        if (CurrentCount == (AlbumCount - 1)) {
                            if (!CheckConnectivity.isNetworkAvailable(UploadService.this)) {
                                ConnectivityDetector_Receiver.Status = false;
                                ServiceHandle.Status = false;
                            }
                            Status = true;
                            ////System.out.println("inside complete");
                            for (int i = 0; i < AlbumCount; i++) {
                                File dir = getFilesDir();
                                File file = new File(dir, datapath + "img" + i + ".txt");
                                file.delete();
                            }
                            ExternalFunctions.AlbumCRUD_Shared(UploadService.this, String.valueOf(ProductId), String.valueOf(AlbumCount), false);
                            Bundle bundle = new Bundle();
                            if(upload.mReceiver != null && SplashScreen.mReceiver != null) {
                                receiver.send(STATUS_FINISHED, bundle);
                            }
                            this.stopSelf();
                            editor.putBoolean("BackgroundUploadStatus", true);
                            editor.apply();
                        } else {
//                           CustomMessage.getInstance().CustomMessage(UploadService.this, "Image number " + CurrentCount + " uploaded successfully");
                            if (!Status) {
                                CurrentCount = CurrentCount + 1;
                                readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                ////System.out.println("inside else complete");
                            }
                        }

                    } else {
                        if(resp.getString("detail").contains("Maximum number")){
                            ExternalFunctions.AlbumCRUD_Shared(UploadService.this, String.valueOf(ProductId), String.valueOf(AlbumCount), false);
                        }
                        else {
                            JSONObject detail = resp.getJSONObject("detail");
                            if (detail.has("image")) {
                                String error_msg = detail.getJSONArray("image").getString(0);
                                if (error_msg.contains("file is empty")) {
                                    ////System.out.println("inside error block first if");
                                    if (CurrentCount <= (AlbumCount - 1)) {
                                        if (!Status) {
                                            CurrentCount = CurrentCount + 1;
                                            readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                        }
                                    }

                                } else {

                                    if (!Status) {
                                        readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                        ////System.out.println("inside else error");
                                    }

                                }
                            } else {
                                if (!Status) {
                                    readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                    ////System.out.println("inside else error");
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (!Status) {
                    readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                    ////System.out.println("inside else error");

                }
            }
        } else if (flag.equals("posteditdata")) {
            ////System.out.println(response);

            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                SharedPreferences settings = getSharedPreferences("UploadServiceSession",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("CurrentCount", CurrentCount + 1);
                editor.apply();
                if (!CheckConnectivity.isNetworkAvailable(UploadService.this)) {
                    ConnectivityDetector_Receiver.Status = false;
                    ServiceHandle.Status = false;
                }
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {
                        editor.putInt("CurrentCount", CurrentCount + 1);

                        if (CurrentCount == (AlbumCount - 1)) {
                            if (!CheckConnectivity.isNetworkAvailable(UploadService.this)) {
                                ConnectivityDetector_Receiver.Status = false;
                                ServiceHandle.Status = false;
                            }
                            Status = true;
                            ////System.out.println("inside complete");
                            Bundle bundle = new Bundle();
                            if(upload.mReceiver != null && SplashScreen.mReceiver != null) {
                                receiver.send(STATUS_FINISHED, bundle);
                            }
                            this.stopSelf();
                            editor.putBoolean("BackgroundUploadStatus", true);
                            editor.apply();

                        } else {
//                           CustomMessage.getInstance().CustomMessage(UploadService.this, "Image number " + CurrentCount + " uploaded successfully");
                            File dir = getFilesDir();
                            File file = new File(dir, datapath + "img" + CurrentCount + ".txt");
                            file.delete();
                            if (!Status) {
                                CurrentCount = CurrentCount + 1;
                                readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                ////System.out.println("inside else complete");
                            }

                        }

                    } else {

                        JSONObject detail = resp.getJSONObject("detail");
                        if (detail.has("image")) {
                            String error_msg = detail.getJSONArray("image").getString(0);
                            if (error_msg.contains("file is empty")) {
                                if (noOfTries < 2) {
                                    noOfTries = noOfTries + 1;
                                    if (CurrentCount <= (AlbumCount - 1)) {
                                        if (!Status) {
                                            CurrentCount = CurrentCount + 1;
                                            readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                            ////System.out.println("inside else error");
                                        }
                                    }
                                }
                            } else {

                                if (noOfTries < 2) {
                                    noOfTries = noOfTries + 1;
                                    if (!Status) {
                                        readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                        ////System.out.println("inside else error");
                                    }
                                }
                            }
                        } else {
                            if (noOfTries < 2) {
                                noOfTries = noOfTries + 1;
                                if (!Status) {
                                    readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                                    ////System.out.println("inside else error");
                                }
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (noOfTries < 2) {
                    noOfTries = noOfTries + 1;
                    if (!Status) {
                        readFromFile(getFilesDir().getAbsolutePath() + "/" + datapath + "img" + CurrentCount + ".txt");
                        ////System.out.println("inside else error");
                    }
                }
            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}