package cs4474.g9.debtledger.data;

import android.content.Context;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;

public class ConnectionAdapter
{
    private String mMobileBackendUrl = "https://m4474.azurewebsites.net";
    private Context mContext;
    private MobileServiceClient mClient;
    private static ConnectionAdapter cInstance = null;

    private ConnectionAdapter(Context context)
    {
        mContext = context;
        try
        {
            mClient = new MobileServiceClient(mMobileBackendUrl, mContext);
        }
        catch (MalformedURLException e)
        {
            System.err.println("Error connecting to database");
        }
    }

    public static void Initialize(Context context) {
        if (cInstance == null) {
            cInstance = new ConnectionAdapter(context);
        } else {
            throw new IllegalStateException("ConnectionAdapter is already initialized");
        }
    }

    public static ConnectionAdapter getInstance() {
        if (cInstance == null) {
            throw new IllegalStateException("ConnectionAdapter is not initialized");
        }
        return cInstance;
    }

    public MobileServiceClient getClient() {
        return mClient;
    }
}
