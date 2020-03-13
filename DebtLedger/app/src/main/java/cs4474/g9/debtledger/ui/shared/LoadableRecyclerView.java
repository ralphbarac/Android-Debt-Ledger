package cs4474.g9.debtledger.ui.shared;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;

/**
 * This class is an extension of a RecylerView which accounts for different states of the recycler
 * view such as loading and empty. It should be used whenever a RecyclerView needs to display data
 * which is loaded from the backend and may take an unknown amount of time.
 * <p>
 * From the xml layout file, the following parameters should be passed: app:loadingMessage,
 * app:failedToLoadMessage, app:failedToLoadAction, app:emptyMessage, app:emptyAction
 * <p>
 * These should be strings and represent the messages that will appear in loading state, failed to
 * load state, or empty state - if an empty string is given for actions or the message, these
 * views will be excluded
 * <p>
 * In addition, from the xml layout file, the following parameters may be passed: app:statusContainer,
 * app:loadingProgressBar, app:messageTextView, app:actionButton
 * <p>
 * These all represent view references (ids) and must be present in the given layout or else this
 * class will throw RuntimeExceptions. If these parameters are not passed, these ids are assumed
 * to use default values (status_container, loading_progress_bar, message_text_view, action_button)
 * <p>
 * Finally, the LoadableRecyclerView provides the OnActionButtonClickedListener, this listener
 * can be added to respond to the clicks of the action button.
 */
public class LoadableRecyclerView extends RecyclerView {

    private static final int STATE_LOADING = 0;
    private static final int STATE_FAILED_TO_LOAD = 1;
    private static final int STATE_EMPTY = 2;
    private static final int STATE_NON_EMPTY = 3;

    private int state;

    private List<OnActionButtonClickedListener> actionButtonClickedListeners = new ArrayList<>();

    private View statusContainer = null;
    private ProgressBar loadingProgressBar = null;
    private TextView messageTextView = null;
    private MaterialButton actionButton = null;

    private int statusContainerId = R.id.status_container;
    private int loadingProgressBarId = R.id.loading_progress_bar;
    private int messageTextViewId = R.id.message_text_view;
    private int actionButtonId = R.id.action_button;

    private String loadingMessage = "Loading list...";
    private String failedToLoadMessage = "Sorry, unable to load the list ):";
    private String failedToLoadAction = "Retry";
    private String emptyMessage = "Sorry, the list is empty ):";
    private String emptyAction = "";

    public LoadableRecyclerView(@NonNull Context context) {
        super(context);
    }

    public LoadableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
    }

    public LoadableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(context, attrs);
    }

    /**
     * This method should be called before recycler view's data is being loaded, ideally in the
     * onPreExecute method of an AsyncTask
     */
    public void onBeginLoading() {
        state = STATE_LOADING;
        onStateChanged();
    }

    /**
     * This method should be called after recycler view's data is unsuccesfully loaded, ideally in
     * onPostExecute method of an AsyncTask
     */
    public void onFailToFinishLoading() {
        state = STATE_FAILED_TO_LOAD;
        onStateChanged();
    }

    /**
     * Internal method to deal with state changes...
     */
    private void onStateChanged() {
        if (state == STATE_NON_EMPTY) {
            setVisibility(VISIBLE);
            statusContainer.setVisibility(GONE);
        } else {
            setVisibility(GONE);
            statusContainer.setVisibility(VISIBLE);

            if (state == STATE_LOADING) {
                loadingProgressBar.setVisibility(VISIBLE);
                if (loadingMessage.isEmpty()) {
                    messageTextView.setVisibility(GONE);
                } else {
                    messageTextView.setVisibility(VISIBLE);
                    messageTextView.setText(loadingMessage);
                }
                actionButton.setVisibility(GONE);
            } else if (state == STATE_FAILED_TO_LOAD) {
                loadingProgressBar.setVisibility(GONE);
                messageTextView.setVisibility(VISIBLE);
                messageTextView.setText(failedToLoadMessage);
                if (failedToLoadAction.isEmpty()) {
                    actionButton.setVisibility(GONE);
                } else {
                    actionButton.setVisibility(VISIBLE);
                    actionButton.setText(failedToLoadAction);
                }
            } else if (state == STATE_EMPTY) {
                loadingProgressBar.setVisibility(GONE);
                messageTextView.setText(emptyMessage);
                messageTextView.setVisibility(VISIBLE);
                if (emptyAction.isEmpty()) {
                    actionButton.setVisibility(GONE);
                } else {
                    actionButton.setVisibility(VISIBLE);
                    actionButton.setText(emptyAction);
                }
            }
        }
    }

    /**
     * Internal method which loads necessary attributes while creating the view
     *
     * @param context given context from constructor
     * @param attrs   given attrs from constructor
     */
    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadableRecyclerView, 0, 0);

        try {
            // Get provided resource ids for necessary views
            statusContainerId = array.getResourceId(R.styleable.LoadableRecyclerView_statusContainer, R.id.status_container);
            loadingProgressBarId = array.getResourceId(R.styleable.LoadableRecyclerView_loadingProgressBar, R.id.loading_progress_bar);
            messageTextViewId = array.getResourceId(R.styleable.LoadableRecyclerView_messageTextView, R.id.message_text_view);
            actionButtonId = array.getResourceId(R.styleable.LoadableRecyclerView_actionButton, R.id.action_button);

            // Get string messages and actions
            loadingMessage = array.getString(R.styleable.LoadableRecyclerView_loadingMessage);
            failedToLoadMessage = array.getString(R.styleable.LoadableRecyclerView_failedToLoadMessage);
            failedToLoadAction = array.getString(R.styleable.LoadableRecyclerView_failedToLoadAction);
            emptyMessage = array.getString(R.styleable.LoadableRecyclerView_emptyMessage);
            emptyAction = array.getString(R.styleable.LoadableRecyclerView_emptyAction);
        } finally {
            array.recycle();
        }

    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);

        // Gather view references and throw exception if necessary view references are n ot found
        View view = getRootView();
        statusContainer = view.findViewById(statusContainerId);
        if (statusContainer == null) {
            throw new RuntimeException("LoadableRecyclerView requires a specified statusContainer");
        }
        loadingProgressBar = statusContainer.findViewById(loadingProgressBarId);
        messageTextView = statusContainer.findViewById(messageTextViewId);
        actionButton = statusContainer.findViewById(actionButtonId);
        if (loadingProgressBar == null || messageTextView == null || actionButton == null) {
            throw new RuntimeException("LoadableRecyclerView requires a statusContainer with a loadingProgressBar, messageTextView, and actionButton");
        }

        // Register click listener for action button to call appropriate methods on click (depending on state)
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_FAILED_TO_LOAD) {
                    notifyListenersOnFailedToLoadActionButtonClicked();
                } else if (state == STATE_EMPTY) {
                    notifyListenersOnEmptyActionButtonClicked();
                }
            }
        });

        // Listen for changes to data, and change state if necessary depending on item count
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (getAdapter().getItemCount() > 0) {
                    state = STATE_NON_EMPTY;
                } else {
                    state = STATE_EMPTY;
                }
                onStateChanged();
            }
        });

        onBeginLoading();
    }

    public void addOnActionButtonClickedClickListener(OnActionButtonClickedListener listener) {
        actionButtonClickedListeners.add(listener);
    }

    private void notifyListenersOnFailedToLoadActionButtonClicked() {
        for (OnActionButtonClickedListener actionButtonClickedListener : actionButtonClickedListeners) {
            actionButtonClickedListener.onFailedToLoadActionButtonClicked();
        }
    }

    private void notifyListenersOnEmptyActionButtonClicked() {
        for (OnActionButtonClickedListener actionButtonClickedListener : actionButtonClickedListeners) {
            actionButtonClickedListener.onEmptyActionButtonClicked();
        }
    }

}
