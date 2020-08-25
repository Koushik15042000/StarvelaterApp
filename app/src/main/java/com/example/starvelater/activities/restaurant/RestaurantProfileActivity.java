package com.example.starvelater.activities.restaurant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.L;
import com.example.starvelater.R;
import com.example.starvelater.activities.user.UserDashboard;
import com.example.starvelater.adapters.restaurantprofile_adapters.RecycleGridAdapter1;
import com.example.starvelater.adapters.restaurantprofile_adapters.RestaurantItemAdapter;
import com.example.starvelater.api.ApiInterface;
import com.example.starvelater.api.RetrofitClient;
import com.example.starvelater.interfaces.CartItemClickListener;
import com.example.starvelater.interfaces.CartProductClickListener;
import com.example.starvelater.jsonmodels.CategoryItemsModel;
import com.example.starvelater.model.NormalProducts;
import com.example.starvelater.model.Product;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantProfileActivity extends AppCompatActivity implements CartProductClickListener,CartItemClickListener{

    RecyclerView datalist;
    RecyclerView itemlist;

    SwipeRefreshLayout swipeRefreshLayout;


    private ShimmerFrameLayout recommendedshimmerFrameLayout;

    TextView txtOrderSummary;
    TextView txtItemCount;

    TextView txtVegBadge;

    List<String> categoryNamesList;

    LinearLayout cartLayout;

    CartProductClickListener cartProductClickListener;
    CartItemClickListener cartItemClickListener;

    List<Product> productArrayList = new ArrayList<>();
    List<Product> itemsArrayList = new ArrayList<>();

    List<String> itemNameList;
    List<Integer> itemPriceList;
    List<Integer> itemCountList;

    List<CategoryItemsModel.DataBean> categoryItemsList;

    HashMap<String, Integer> nameCountList;
    HashMap<String, Integer> namePriceList;

    RecycleGridAdapter1 adapter;
    RestaurantItemAdapter itemAdapter;
    FloatingActionButton fabCart;

    ImageView backbutton;
    Toolbar restaurantToolBar;
    int itemCost, totalCost, itemCount;

    TextView txtRestaurantName, txtRestaurantLocation, txtTotalCost;

    Switch aSwitch;

    LinearLayout linearLayout;

    private View mBottomSheetView;

    private CoordinatorLayout mParent;

    private RecyclerView mBottomSheetRecycler;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile_activity);

        recommendedshimmerFrameLayout = findViewById(R.id.recommended_shimmer);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        cartProductClickListener = (CartProductClickListener) this;
        cartItemClickListener = (CartItemClickListener) this;

        txtOrderSummary = findViewById(R.id.view_cart);

        txtVegBadge = findViewById(R.id.vegBadge);

        datalist = findViewById(R.id.datalist);
        itemlist = findViewById(R.id.categoryItemlist);

        txtItemCount = findViewById(R.id.itemsCount);

        nameCountList = new HashMap<>();
        namePriceList = new HashMap<>();

        categoryNamesList = new ArrayList<>();

        txtRestaurantName = findViewById(R.id.restaurant_name);
        txtRestaurantLocation = findViewById(R.id.restaurant_location);


        txtTotalCost = findViewById(R.id.itemsCost);

        itemNameList = new ArrayList<>();
        itemPriceList = new ArrayList<>();
        itemCountList = new ArrayList<>();

        aSwitch = findViewById(R.id.toggleswitch);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String restaurantID = bundle.getString("rest_ID");
        String operationStatus = bundle.getString("operationStatus");
        String restaurantName = bundle.getString("name");
        String restaurantLocation = bundle.getString("location");

        // method to load Items
        next(restaurantID, operationStatus);

        txtRestaurantName.setText(restaurantName);
        txtRestaurantLocation.setText(restaurantLocation);
        linearLayout = findViewById(R.id.linearLayout);

        cartLayout = findViewById(R.id.cartLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // method to load Items
                next(restaurantID, operationStatus);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });


       cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RestaurantProfileActivity.this,OrderedSummaryActivity.class);

                Bundle bundle1 = new Bundle();
                bundle1.putString("restaurantName",restaurantName);
                bundle1.putString("restaurantAddress",restaurantLocation);
                bundle1.putString("restaurantID",restaurantID);
                bundle1.putString("operationStatus", operationStatus);
                //bundle1.putString("grandTotal",String.valueOf(grandTotal));
                //bundle1.putString("totalCount",String.valueOf(quantity));
                intent.putExtras(bundle1);
                intent.putExtra("recommendedItemsList", (Serializable) productArrayList);
                intent.putExtra("normal_itemsList",(Serializable) itemsArrayList);
                startActivity(intent);
            }
        });

        restaurantToolBar = findViewById(R.id.restaurantToolBar);
        setSupportActionBar(restaurantToolBar);


        final ActionBar restaurantActionBar = getSupportActionBar();
        restaurantActionBar.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        restaurantActionBar.setDisplayShowTitleEnabled(false);

        backbutton = (ImageView) findViewById(R.id.back_button);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantProfileActivity.this, UserDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        calculateCartTotal();
    }

    public void next(String restaurantID, String operationStatus) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("restaurant_ID",restaurantID);

        ApiInterface apiInterface = RetrofitClient.getClient(this).create(ApiInterface.class);

        apiInterface.processAllItems(jsonObject).enqueue(new Callback<CategoryItemsModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CategoryItemsModel> call, Response<CategoryItemsModel> response) {
                if(response.isSuccessful()) {
                    CategoryItemsModel categoryItemsModel = response.body();
                    assert categoryItemsModel!=null;

                    if(categoryItemsModel.isStatus()) {
                        categoryItemsList = categoryItemsModel.getData();

                        loadRestaurantItems(categoryItemsList);

                        recommendedRecycler(productArrayList,cartProductClickListener,operationStatus);
                        itemsRecycler(categoryNamesList,itemsArrayList,cartItemClickListener,operationStatus);

                        // Checking whole array list whether it contains Vegetarian
                       Boolean recommendedVeg =  productArrayList.stream().allMatch(item -> item.getType().equals("Vegetarian"));
                       Boolean itemsVeg = itemsArrayList.stream().allMatch(item -> item.getType().equals("Vegetarian"));

                       if(recommendedVeg && itemsVeg) {
                           // showing Veg Badge

                           txtVegBadge.setVisibility(View.VISIBLE);
                       } else {
                           // showing Toggle Button
                           aSwitch.setVisibility(View.VISIBLE);
                           aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                   if (isChecked) {


                                       // loading Vegetarian Items Only for Recommended
                                       productArrayList.clear();
                                       itemsArrayList.clear();

                                       for(int i=0;i<categoryItemsList.size();i++) {

                                           if(categoryItemsList.get(i).getType().equals("Vegetarian")) {
                                               if(categoryItemsList.get(i).getRecommended().equals("Yes")) {
                                                   productArrayList.add(new Product(Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), 0, categoryItemsList.get(i).getType(),categoryItemsList.get(i).getItem_Name(), categoryItemsList.get(i).getImgUrl(), categoryItemsList.get(i).getCategory()));
                                               } else {
                                                   itemsArrayList.add(new Product(Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), 0, categoryItemsList.get(i).getType(),categoryItemsList.get(i).getItem_Name(), categoryItemsList.get(i).getImgUrl(), categoryItemsList.get(i).getCategory()));
                                               }
                                           }
                                       }

                                       // WE ARE AGAIN INITIALISING THE ADAPTER BECAUSE THE listener has BLOCK LEVEL SCOPE
                                       // initialising Adapter again by passing Array list
                                       recommendedRecycler(productArrayList,cartProductClickListener,operationStatus);

                                       // Loading Vegetarian Items for below Items Adapter
                                       itemsRecycler(categoryNamesList,itemsArrayList,cartItemClickListener,operationStatus);

                                   }
                                   if (!isChecked) {

                                       productArrayList.clear();
                                       itemsArrayList.clear();
                                       categoryNamesList.clear();
                                       loadRestaurantItems(categoryItemsList);

                                       recommendedRecycler(productArrayList,cartProductClickListener,operationStatus);
                                       itemsRecycler(categoryNamesList,itemsArrayList,cartItemClickListener,operationStatus);

                                   }
                               }
                           });

                       }

                       swipeRefreshLayout.setRefreshing(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<CategoryItemsModel> call, Throwable t) {

            }
        });

    }

    public void loadRestaurantItems(List<CategoryItemsModel.DataBean> categoryItemsList) {

        categoryNamesList.clear();
        productArrayList.clear();
        itemsArrayList.clear();


        for(int i=0; i<categoryItemsList.size();i++) {

            if(!categoryNamesList.contains(RestaurantProfileActivity.this.categoryItemsList.get(i).getCategory())) {
                categoryNamesList.add(RestaurantProfileActivity.this.categoryItemsList.get(i).getCategory());
            }
            // adding all the items from Master List to Product class

            if(categoryItemsList.get(i).getRecommended().equals("Yes")) {
                // adding items to Recommended Category
                productArrayList.add(new Product(Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), 0, categoryItemsList.get(i).getType(),categoryItemsList.get(i).getItem_Name(), categoryItemsList.get(i).getImgUrl(), categoryItemsList.get(i).getCategory()));
            } else {
                itemsArrayList.add(new Product(Integer.parseInt(categoryItemsList.get(i).getFinal_Price()), Integer.parseInt(categoryItemsList.get(i).getFinal_Price()),0, categoryItemsList.get(i).getType(),categoryItemsList.get(i).getItem_Name(),categoryItemsList.get(i).getImgUrl(),categoryItemsList.get(i).getCategory()));
            }

        }

    }

    public void recommendedRecycler(List<Product> productArrayList, CartProductClickListener cartProductClickListener, String operationStatus) {

        adapter = new RecycleGridAdapter1(RestaurantProfileActivity.this, productArrayList,cartProductClickListener,operationStatus);

        recommendedshimmerFrameLayout.stopShimmer();
        recommendedshimmerFrameLayout.setVisibility(View.GONE);
        datalist.setVisibility(View.VISIBLE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(RestaurantProfileActivity.this, 2, GridLayoutManager.VERTICAL, false);
        datalist.setLayoutManager(gridLayoutManager);
        datalist.setAdapter((RecyclerView.Adapter) adapter);

        adapter.notifyDataSetChanged();

    }

    public void itemsRecycler(List<String> categoryNamesList,List<Product> itemsArrayList, CartItemClickListener cartItemClickListener, String operationStatus) {

        itemAdapter = new RestaurantItemAdapter(RestaurantProfileActivity.this, categoryNamesList,itemsArrayList,cartItemClickListener,operationStatus);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestaurantProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        itemlist.setLayoutManager(linearLayoutManager);
        itemlist.setAdapter((RecyclerView.Adapter) itemAdapter);

        itemAdapter.notifyDataSetChanged();


    }


    @Override
    public void onMinusClick(Product cartItemsBean) {

        int i = productArrayList.indexOf(cartItemsBean);
        if (cartItemsBean.getQuantity() > 0) {
            int itemTotal = cartItemsBean.getUnitPrice() * cartItemsBean.getQuantity();

            Product updatedProduct = new Product(cartItemsBean.getUnitPrice(),itemTotal, (cartItemsBean.getQuantity() -1), cartItemsBean.getType(), cartItemsBean.getTitles(),
                    cartItemsBean.getImages(),cartItemsBean.getItemCategory());

            productArrayList.remove(cartItemsBean);
            productArrayList.add(i, updatedProduct);

            adapter.notifyDataSetChanged();

            calculateCartTotal();
        }
    }

    @Override
    public void onPlusClick(Product cartItemsBean) {
        int i = productArrayList.indexOf(cartItemsBean);

        int quantity = cartItemsBean.getQuantity() + 1;
        int itemTotal = cartItemsBean.getUnitPrice() * quantity;
        Log.d("TAG", "onMinusClick: "+quantity);


        Product updatedProduct = new Product(cartItemsBean.getUnitPrice(),itemTotal, quantity, cartItemsBean.getType(),cartItemsBean.getTitles(),
                cartItemsBean.getImages(),cartItemsBean.getItemCategory());

        productArrayList.remove(cartItemsBean);
        productArrayList.add(i, updatedProduct);

        Log.e("QUNATITY", "" + updatedProduct.getQuantity());
       // updateQuantity(updatedProduct);

        adapter.notifyDataSetChanged();


        calculateCartTotal();
    }

    @Override
    public void onAddClick(int position, Product cartItemsBean) {

        int i = productArrayList.indexOf(cartItemsBean);
        Product updatedProduct = new Product(cartItemsBean.getUnitPrice(),cartItemsBean.getUnitPrice(), 1, cartItemsBean.getType(),cartItemsBean.getTitles(),
                cartItemsBean.getImages(),cartItemsBean.getItemCategory());

        productArrayList.remove(cartItemsBean);
        productArrayList.add(i, updatedProduct);

        Log.e("QUNATITY", "" + updatedProduct.getQuantity());
        // updateQuantity(updatedProduct);

        adapter.notifyDataSetChanged();


        calculateCartTotal();
    }

    @Override
    public void onItemMinusClick(Product productItemsBean) {

        int i = itemsArrayList.indexOf(productItemsBean);

        if (productItemsBean.getQuantity() > 0) {

            int itemTotal = productItemsBean.getUnitPrice() * productItemsBean.getQuantity();

            //CategoryItemsModel.DataBean updatedItem = new CategoryItemsModel.DataBean(productItemsBean.getCategory(),productItemsBean.getItem_ID(),productItemsBean.getItem_Name(),productItemsBean.getType(),productItemsBean.getItem_Price(), productItemsBean.getAvailability(),productItemsBean.getDiscount(),productItemsBean.getFinal_Price(),productItemsBean.getImgUrl(),productItemsBean.getRecommended(),Integer.toString(itemTotal),(productItemsBean.getQuantity() - 1));

            NormalProducts updatedItem = new NormalProducts(productItemsBean.getUnitPrice(),itemTotal, (productItemsBean.getQuantity() -1 ) , productItemsBean.getType(),productItemsBean.getTitles(),productItemsBean.getImages(),productItemsBean.getItemCategory());

            itemsArrayList.remove(productItemsBean);
            itemsArrayList.add(i, updatedItem);

            itemAdapter.notifyDataSetChanged();

            calculateCartTotal();
        }

    }

    @Override
    public void onItemPlusClick(Product productItemsBean) {

        int i = itemsArrayList.indexOf(productItemsBean);

        int quantity = productItemsBean.getQuantity() + 1;
        int itemTotal = productItemsBean.getUnitPrice()* quantity;
        Log.d("TAG", "onMinusClick: "+quantity);


        //CategoryItemsModel.DataBean updatedItem = new CategoryItemsModel.DataBean(productItemsBean.getCategory(),productItemsBean.getItem_ID(),productItemsBean.getItem_Name(),productItemsBean.getType(),productItemsBean.getItem_Price(), productItemsBean.getAvailability(),productItemsBean.getDiscount(),productItemsBean.getFinal_Price(),productItemsBean.getImgUrl(),productItemsBean.getRecommended(),Integer.toString(itemTotal),(productItemsBean.getQuantity() + 1));

        NormalProducts updatedItem = new NormalProducts(productItemsBean.getUnitPrice(),itemTotal, quantity, productItemsBean.getType(),productItemsBean.getTitles(),productItemsBean.getImages(),productItemsBean.getItemCategory());

        itemsArrayList.remove(productItemsBean);
        itemsArrayList.add(i, updatedItem);

        Log.e("QUNATITY", "" + updatedItem.getQuantity());
        // updateQuantity(updatedProduct);

        itemAdapter.notifyDataSetChanged();


        calculateCartTotal();


    }

    @Override
    public void onAddItemClick(int position, Product productItemsBean) {

        int i = itemsArrayList.indexOf(productItemsBean);

        NormalProducts updatedItem = new NormalProducts(productItemsBean.getUnitPrice(),productItemsBean.getUnitPrice(), 1, productItemsBean.getType(), productItemsBean.getTitles(),productItemsBean.getImages(),productItemsBean.getItemCategory());

       // CategoryItemsModel.DataBean updatedItem = new CategoryItemsModel.DataBean(productItemsBean.getCategory(),productItemsBean.getItem_ID(),productItemsBean.getItem_Name(),productItemsBean.getType(),productItemsBean.getItem_Price(), productItemsBean.getAvailability(),productItemsBean.getDiscount(),productItemsBean.getFinal_Price(),productItemsBean.getImgUrl(),productItemsBean.getRecommended(),productItemsBean.getItemTotalPrice(),1);

        itemsArrayList.remove(productItemsBean);
        itemsArrayList.add(i, updatedItem);

        Log.e("QUNATITY", "" + updatedItem.getQuantity());
        // updateQuantity(updatedProduct);

        itemAdapter.notifyDataSetChanged();

        calculateCartTotal();

    }

    // total Amount
    public void calculateCartTotal() {

        int grandTotal=0,quantity=0;


            cartLayout.setVisibility(View.VISIBLE);
           for (Product order : productArrayList) {

                grandTotal += (ParseDouble(String.valueOf(order.getUnitPrice())) * order.getQuantity());
                quantity += (ParseDouble(String.valueOf(order.getQuantity())));
            }

            for (Product order : itemsArrayList) {

                grandTotal += (ParseDouble(String.valueOf(order.getUnitPrice())) * order.getQuantity());
                quantity += (ParseDouble(String.valueOf(order.getQuantity())));
            }

            if(grandTotal == 0) {
                cartLayout.setVisibility(View.GONE);
            } else {

                Log.e("TOTAL", "" + productArrayList.size() + "Items | " + "  DISCOUNT : " + grandTotal);

                cartLayout.setVisibility(View.VISIBLE);


                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                String formattedValue = decimalFormat.format(grandTotal);
                //txtSubTotal.setText(productArrayList.size() + " Items | Rs " + grandTotal);
                txtTotalCost.setText("₹ " + formattedValue);
                txtItemCount.setText("" + String.valueOf(quantity));
            }



    }


    private double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recommendedshimmerFrameLayout.startShimmer();
        //allRestaurantsshimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recommendedshimmerFrameLayout.stopShimmer();
        //allRestaurantsshimmerFrameLayout.stopShimmer();
    }

}

