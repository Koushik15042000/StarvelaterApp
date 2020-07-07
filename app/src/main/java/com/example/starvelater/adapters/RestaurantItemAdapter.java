package com.example.starvelater.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.starvelater.R;
import com.example.starvelater.activities.restaurant.RestaurantProfile;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class RestaurantItemAdapter extends RecyclerView.Adapter<RestaurantItemAdapter.ViewHolder> {

    List<String> titles;
    List<String> prices;

    LayoutInflater inflater;

    public RestaurantItemAdapter(Context ctx, List<String> titles, List<String> prices){
        this.titles = titles;
        this.prices = prices;
        this.inflater = LayoutInflater.from(ctx);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_card_design,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(titles.get(position));
        holder.price.setText(prices.get(position));
        holder.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        v.getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(v.getContext())
                        .inflate(
                                R.layout.activity_ordered_items_bottom_sheet,
                                (LinearLayout) v.findViewById(R.id.menuBottomSheet)
                        );
                bottomSheetView.findViewById(R.id.checkOut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Received Order", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Button addItem;
        TextView title;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            price = itemView.findViewById(R.id.price);
            addItem = itemView.findViewById(R.id.add_item);

        }
    }
}
