package cs4474.g9.debtledger.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.ConnectionAdapter;
import cs4474.g9.debtledger.data.RedirectableJsonArrayRequest;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.TransactionManager;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.BalanceCalculator;
import cs4474.g9.debtledger.ui.shared.OnActionButtonClickedListener;
import cs4474.g9.debtledger.ui.transaction.CreateTransactionActivity;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment implements OnActionButtonClickedListener {

    public static final int CREATE_TRANSACTION_REQUEST = 0;

    private final int SEMI_TRANSPARENT = 130;
    private final int SOLID = 255;

    private DashboardPageAdapter pageAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewPager viewPager = view.findViewById(R.id.view_pager);
        final TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        view.findViewById(R.id.create_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreateTransaction = new Intent(getActivity(), CreateTransactionActivity.class);
                startActivityForResult(toCreateTransaction, CREATE_TRANSACTION_REQUEST);
            }
        });

        pageAdapter = new DashboardPageAdapter(getChildFragmentManager(), this);
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        // Creating custom views for tab icons
        ImageView owedTabView = new ImageView(getContext());
        owedTabView.setImageResource(R.drawable.ic_dashboard_owed_black);
        owedTabView.setColorFilter(Color.WHITE);
        owedTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(0).setCustomView(owedTabView);

        ImageView allTabView = new ImageView(getContext());
        allTabView.setImageResource(R.drawable.ic_dashboard_all_black);
        allTabView.setColorFilter(Color.WHITE);
        allTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(1).setCustomView(allTabView);

        ImageView oweTabView = new ImageView(getContext());
        oweTabView.setImageResource(R.drawable.ic_dashboard_owe_black);
        oweTabView.setColorFilter(Color.WHITE);
        oweTabView.setImageAlpha(SEMI_TRANSPARENT);
        tabLayout.getTabAt(2).setCustomView(oweTabView);

        // Listener to change state between solid and semi-transparent of icons on select/un-select
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView instanceof ImageView) {
                    ImageView imageView = (ImageView) tabView;
                    imageView.setImageAlpha(SOLID);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView instanceof ImageView) {
                    ImageView imageView = (ImageView) tabView;
                    imageView.setImageAlpha(SEMI_TRANSPARENT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Initial selected tab should be with "All" Outstanding balances, which appears in the middle, or at position 1
        viewPager.setCurrentItem(1);

        makeRequestForContacts(LoginRepository.getInstance().getLoggedInUser());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_TRANSACTION_REQUEST) {
            if (resultCode ==  RESULT_OK) {
                makeRequestForContacts(LoginRepository.getInstance().getLoggedInUser());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel/terminate any requests that are still running or queued
        ConnectionAdapter.getInstance().cancelAllRequests(hashCode());
    }

    @Override
    public void onFailedToLoadActionButtonClicked() {
        makeRequestForContacts(LoginRepository.getInstance().getLoggedInUser());
    }

    @Override
    public void onEmptyActionButtonClicked() {
        // No button, so this should be impossible :)
    }

    private void makeRequestForContacts(UserAccount loggedInUser) {
        pageAdapter.onBeginLoading();

        RedirectableJsonArrayRequest request = new RedirectableJsonArrayRequest(
                ConnectionAdapter.BASE_URL + TransactionManager.BALANCES_END_POINT + "/" + loggedInUser.getId() + "/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("DASHBOARD", response.toString());

                        try {
                            if (response.getJSONObject(0).has("error")) {
                                throw new Exception();
                            } else if (response.getJSONObject(0).has("empty")) {
                                pageAdapter.setData(new ArrayList<>());
                            } else {
                                // On success
                                pageAdapter.setData(BalanceCalculator.parseOutstandingBalancesFromJson(response));
                            }
                        } catch (Exception e) {
                            // On parse error, set dashboard pages as fail to finish loading mode
                            pageAdapter.onFailToFinishLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On error, set dashboard pages as fail to finish loading mode
                        Log.d("DASHBOARD", error.toString());
                        pageAdapter.onFailToFinishLoading();
                    }
                }
        );

        ConnectionAdapter.getInstance().addToRequestQueue(request, hashCode());
    }

}