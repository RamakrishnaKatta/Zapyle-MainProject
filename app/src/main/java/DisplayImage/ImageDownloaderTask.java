package DisplayImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.ImageView;

import com.zapyle.zapyle.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by haseeb on 10/12/15.
 */
public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    public static Bitmap bitmapImage;

    public ImageDownloaderTask(ImageView imageView) {
        //////System.out.println("inside async");
        imageViewReference = new WeakReference<ImageView>(imageView);
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        //////System.out.println("inside async" + "__" + params[0]);
        if (params[0].contains("graph.facebook.com")) {
            return downloadFacebookBitmap(params[0]);
        } else {
            return downloadBitmap(params[0]);

        }

    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        //////System.out.println("inside async" + "__bitmap" + bitmap);

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeResource( bitmap, options);
                    imageView.setImageBitmap(bitmap);
                } else {
                  //  //int imgid=R.drawable.profile;
                   Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.profile);
                   imageView.setImageDrawable(placeholder);
                   // imageView.setImageResource(imgid);

                }
            }
        }
    }


    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            //////System.out.println("inside async" + "status___" + statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                BitmapFactory.Options bfOptions = new BitmapFactory.Options();

                bfOptions.inDither = false;                     //Disable Dithering mode

                bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

                bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

                bfOptions.inTempStorage = new byte[32 * 1024];
                bitmapImage = BitmapFactory.decodeStream(inputStream, null, bfOptions);

                return bitmapImage;
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    private Bitmap downloadFacebookBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            HttpGet httpRequest = new HttpGet(URI.create(url));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();

            bfOptions.inDither = false;                     //Disable Dithering mode

            bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

            bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

            bfOptions.inTempStorage = new byte[32 * 1024];
            bitmapImage = BitmapFactory.decodeStream(bufHttpEntity.getContent(), null, bfOptions);
            httpRequest.abort();
            return bitmapImage;

        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    public static boolean checkBitmapFitsInMemory(long bmpwidth, long bmpheight, int bmpdensity) {
        long reqsize = bmpwidth * bmpheight * bmpdensity;
        long allocNativeHeap = Debug.getNativeHeapAllocatedSize();


        final long heapPad = (long) Math.max(4 * 1024 * 1024, Runtime.getRuntime().maxMemory() * 0.1);
        return (reqsize + allocNativeHeap + heapPad) < Runtime.getRuntime().maxMemory();

    }
}
