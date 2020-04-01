package cs4474.g9.debtledger.data;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import cs4474.g9.debtledger.R;

public class ConnectionAdapter {

    public static final String BASE_URL = "http://ec2-18-191-166-106.us-east-2.compute.amazonaws.com";
    private static final int TIMEOUT_MS = 30000; // 30s

    private static ConnectionAdapter instance;
    private RequestQueue requestQueue;
    private Context context;

    private ConnectionAdapter(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = getRequestQueue();
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new ConnectionAdapter(context);
        }
    }

    public static synchronized ConnectionAdapter getInstance() {
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("ConnectionAdapter is not initialized");
        }
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            try {
                // Certificate is not trusted, so need to create SSL exception by creating custom SSLSocketFactory
                // See http://blog.crazybob.org/2010/02/android-trusting-ssl-certificates.html
                // and https://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore keyStore = KeyStore.getInstance("BKS");
                InputStream stream = context.getResources().openRawResource(R.raw.cs4474_heliohost);
                keyStore.load(stream, null);
                stream.close();
                trustManagerFactory.init(keyStore);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                requestQueue = Volley.newRequestQueue(context, new HurlStack(null, sslSocketFactory));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return requestQueue;
    }

    /**
     * Adds given request with given tag to end of the request queue
     *
     * @param request request to be executed
     * @param tag     custom integer related to each activity
     */
    public <T> void addToRequestQueue(Request<T> request, int tag) {
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        getRequestQueue().add(request);
    }

    /**
     * Cancels all pending requests with given tag
     *
     * @param tag custom integer related to each activity
     */
    public void cancelAllRequests(int tag) {
        getRequestQueue().cancelAll(tag);
    }

}
