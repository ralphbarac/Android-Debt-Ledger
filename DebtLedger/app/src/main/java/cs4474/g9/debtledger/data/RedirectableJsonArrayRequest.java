package cs4474.g9.debtledger.data;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.net.HttpURLConnection;

/**
 * A class to handle redirect, since api redirects (status code 301 or 302) from original end point,
 * and Volley does not seem to handle this out of the box...
 */
public class RedirectableJsonArrayRequest extends JsonArrayRequest {
    public RedirectableJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public RedirectableJsonArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public void deliverError(VolleyError error) {
        // In case of status 301 or 302 (redirect), send new request with redirect location
        final int status = error.networkResponse != null ? error.networkResponse.statusCode : -1;
        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
            final String location = error.networkResponse.headers.get("Location");
            final RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                    location,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            deliverResponse(response);
                        }
                    },
                    this.getErrorListener()
            );
            ConnectionAdapter.getInstance().getRequestQueue().add(request);
        } else {
            super.deliverError(error);
        }
    }
}
