
package com.example.thomasmorphew.roomies;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.util.ArrayList;


/*
 * This class was copied from the internet, relevant links to understanding this are below:
 * https://www.youtube.com/watch?v=o7mECYJ7TKU
 * https://learningandroid360.blogspot.com/2016/10/saving-content-of-edittextcheckbox-in.html
 */
public class EditTextListViewAdapter2 extends ArrayAdapter<String> {

    Context mContent;
    LayoutInflater inflater;
    ArrayList<String> arrayNames;
    ArrayList<String> arrayAmounts;



    public EditTextListViewAdapter2(Context context, ArrayList<String> arrayNames){
        super(context, R.layout.edit_friend_payment_amount_layout, arrayNames);


        this.mContent = context;
        this.arrayNames = arrayNames;
        this.arrayAmounts = new ArrayList<>();
        for(int i=0;i<arrayNames.size();i++){
            arrayAmounts.add("");
        }
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolder holder; //change?
        if(convertView == null){
            convertView = inflater.inflate(R.layout.edit_friend_payment_amount_layout, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.textViewFriendName);
            holder.edAmount = (EditText) convertView.findViewById(R.id.editTextAmountOfMoneyForFriend);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String name = arrayNames.get(position);
        holder.tvName.setText(name);
        String amount = arrayAmounts.get(position);

        //Fill EditText with the value you have in data source
        holder.edAmount.setText(amount);
        holder.edAmount.setId(position);

        //We need to update adapter once we finish with editing
        holder.edAmount.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    arrayAmounts.set(position,Caption.getText().toString());
                }

            }

        });

        return convertView;

    }

    @Override
    public int getCount(){
        return arrayNames.size();
    }

    public ArrayList<String> getPrimaryResource() { return arrayNames; }

    public ArrayList<String> getSecondaryResource(){
        return arrayAmounts;
    }

    public void setSecondaryResource(ArrayList<String> newResource){
        arrayAmounts = newResource;
    }


    static class ViewHolder{
        TextView tvName;
        EditText edAmount;
    }


}

