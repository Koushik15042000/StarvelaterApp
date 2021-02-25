package com.example.starvelater.adapters.restaurantprofile_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.starvelater.R;
import com.example.starvelater.interfaces.CartItemClickListener;
import com.example.starvelater.jsonmodels.CategoryItemsModel;
import com.example.starvelater.model.Product;

import java.util.ArrayList;
import java.util.List;

public class RestaurantItemAdapter extends RecyclerView.Adapter<RestaurantItemAdapter.ViewHolder> {

    private Context mContext;
    /*private List<Product> itemsModelList;*/
    private CartItemClickListener cartItemClickListener;
    private List<String> categoryNamesList;

    private List<Product> categoryItemsList;
    private String operationStatus;
    LayoutInflater inflater;

    public RestaurantItemAdapter(Context mContext, List<String> categoryNamesList, List<Product> categoryItemsList,CartItemClickListener cartItemClickListener,String operationStatus) {

        this.mContext = mContext;
        this.categoryNamesList = categoryNamesList;
        this.categoryItemsList = categoryItemsList;
        this.cartItemClickListener = cartItemClickListener;
        this.operationStatus = operationStatus;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_category_card_design, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        /*final Product normalProductsModel = itemsModelList.get(position);

        holder.title.setText(normalProductsModel.getTitles());
        holder.price.setText("₹ "+normalProductsModel.getUnitPrice());
        //  holder.productQuantity.setText(""+productsModel.getQuantity());

        if (normalProductsModel.getQuantity() == 0) {
            holder.productQuantity.setText("ADD");
            holder.productMinus.setVisibility(View.GONE);
            holder.productPlus.setVisibility(View.GONE);
            Log.d(TAG, "onBindViewHolder: " + normalProductsModel.getQuantity());
        } else {
            holder.productQuantity.setText("" + normalProductsModel.getQuantity());
            holder.productMinus.setVisibility(View.VISIBLE);
            holder.productPlus.setVisibility(View.VISIBLE);
            Log.d(TAG, "onBindViewHolder1: " + normalProductsModel.getQuantity());
        }

        holder.productQuantity.setOnClickListener(view -> {
            if (holder.productQuantity.getText().toString().equalsIgnoreCase("ADD")) {
                holder.productQuantity.setText("" + normalProductsModel.getQuantity());
                holder.productMinus.setVisibility(View.VISIBLE);
                holder.productPlus.setVisibility(View.VISIBLE);
                cartItemClickListener.onAddItemClick(position, normalProductsModel);
            }
        });



        holder.productMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cartItemClickListener.onItemMinusClick(normalProductsModel);
            }
        });

        holder.productPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cartItemClickListener.onItemPlusClick(normalProductsModel);
            }
        });


*/

        List<Product> categoryInsideItems = new ArrayList<>();


        // for sending the Items in the particular Category
        for(int i=0;i<categoryItemsList.size();i++) {

            if(categoryItemsList.get(i).getItemCategory().equals(categoryNamesList.get(position)) ) {
                categoryInsideItems.add(categoryItemsList.get(i));
            }

        }

        if(categoryInsideItems.size() > 0) {
            holder.categoryName.setText(categoryNamesList.get(position));
        } else {
            holder.categoryName.setVisibility(View.GONE);
            holder.categoryItemsList.setVisibility(View.GONE);
            holder.viewSpace.setVisibility(View.GONE);
        }


        //Initialize Adapter
        CategoryCardDesignAdapter categoryCardDesignAdapter = new CategoryCardDesignAdapter(mContext,categoryInsideItems,cartItemClickListener,operationStatus);

        //Initialize Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        holder.categoryItemsList.setLayoutManager(linearLayoutManager);

        //Set Adapter
        holder.categoryItemsList.setAdapter(categoryCardDesignAdapter);

    }

    @Override
    public int getItemCount() {
        return categoryNamesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*Button addItem;
        TextView title, price;
        TextView productMinus,productPlus,productQuantity;*/
        TextView categoryName;
        RecyclerView categoryItemsList;
        View viewSpace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*title = itemView.findViewById(R.id.textView);
            price = itemView.findViewById(R.id.price);
            productMinus= itemView.findViewById(R.id.product_minus);
            productPlus= itemView.findViewById(R.id.product_plus);
            productQuantity= itemView.findViewById(R.id.product_quantity);*/
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryItemsList = itemView.findViewById(R.id.categoryItemlist);
            viewSpace = itemView.findViewById(R.id.space);

        }
    }
}
