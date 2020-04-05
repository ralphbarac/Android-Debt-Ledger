package cs4474.g9.debtledger.ui.dashboard;

import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.ViewContactActivity;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.settings.AccessibleColours;

public class OutstandingBalanceListAdapter extends RecyclerView.Adapter<OutstandingBalanceListAdapter.Item> {

    private UserAccount loggedInUser;
    private List<Pair<UserAccount, Long>> outstandingBalances;

    private boolean isInAccessibleMode;

    public OutstandingBalanceListAdapter(UserAccount loggedInUser, boolean isInAccessibleMode) {
        super();
        this.loggedInUser = loggedInUser;
        this.outstandingBalances = new ArrayList<>();

        this.isInAccessibleMode = isInAccessibleMode;
    }

    public void setOutstandingBalances(List<Pair<UserAccount, Long>> outstandingBalances) {
        this.outstandingBalances = outstandingBalances;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_outstanding_balance, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        UserAccount user = outstandingBalances.get(position).first;
        Long amount = outstandingBalances.get(position).second;

        holder.myAvatar.setColorFilter(ColourGenerator.generateFromName(loggedInUser.getFirstName(), loggedInUser.getLastName()));
        holder.myAvatarCharacter.setText(loggedInUser.getFirstName().substring(0, 1));
        holder.amount.setText(String.format(Locale.CANADA, "$%.2f", Math.abs(amount) / 100.0));
        holder.them.setText(user.getFirstName());
        holder.theirAvatar.setColorFilter(ColourGenerator.generateFromName(user.getFirstName(), user.getLastName()));
        holder.theirAvatarCharacter.setText(user.getFirstName().substring(0, 1));

        if (amount < 0) {
            holder.arrow.setRotationY(0);
            holder.arrow.setColorFilter(AccessibleColours.getNegativeColour(isInAccessibleMode));
            holder.amount.setTextColor(AccessibleColours.getNegativeColour(isInAccessibleMode));
        } else {
            holder.arrow.setRotationY(180);
            holder.arrow.setColorFilter(AccessibleColours.getPositiveColour(isInAccessibleMode));
            holder.amount.setTextColor(AccessibleColours.getPositiveColour(isInAccessibleMode));
        }
    }

    @Override
    public int getItemCount() {
        return outstandingBalances == null ? 0 : outstandingBalances.size();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView myAvatar;
        public TextView myAvatarCharacter;
        public TextView amount;
        public ImageView arrow;
        public TextView them;
        public ImageView theirAvatar;
        public TextView theirAvatarCharacter;

        public Item(View view) {
            super(view);
            this.myAvatar = view.findViewById(R.id.my_avatar);
            this.myAvatarCharacter = view.findViewById(R.id.my_avatar_character);
            this.amount = view.findViewById(R.id.amount);
            this.arrow = view.findViewById(R.id.arrow);
            this.them = view.findViewById(R.id.them);
            this.theirAvatar = view.findViewById(R.id.their_avatar);
            this.theirAvatarCharacter = view.findViewById(R.id.their_avatar_character);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("DASHBOARD", "Contact clicked.");
            Intent toViewContact = new Intent(v.getContext(), ViewContactActivity.class);
            toViewContact.putExtra(ViewContactActivity.CONTACT, outstandingBalances.get(getAdapterPosition()).first);
            v.getContext().startActivity(toViewContact);
        }
    }
}
