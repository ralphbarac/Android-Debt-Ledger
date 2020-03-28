package cs4474.g9.debtledger.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ConnectionAdapter {

    public static final String BASE_URL = "";

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
            requestQueue = Volley.newRequestQueue(context);
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
