package com.example.starvelater.activities.college_canteen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.starvelater.R;
import com.example.starvelater.SharedPref.MyAppPrefsManager;
import com.example.starvelater.activities.user.UserDashboard;
import com.example.starvelater.adapters.multiutility_adapters.AllCategoriesAdapter;
import com.example.starvelater.api.ApiInterface;
import com.example.starvelater.api.RetrofitClient;
import com.example.starvelater.jsonmodels.RestaurantsModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class All_CollegeCanteen extends AppCompatActivity {

    List<String> restaurantName;
    List<String> restaurantLocation;
    List<Integer> restaurantImages;

    private ShimmerFrameLayout collegeCanteenShimmerFrameLayout;

    RecyclerView restaurantsList;

    SwipeRefreshLayout swipeRefreshLayout;

    //LinearLayout progressBar;
    TextView emptyView;

    AllCategoriesAdapter restaurantsAdapter;

    ImageView backbutton;
    Toolbar restaurantToolBar;
    MyAppPrefsManager myAppPrefsManager;
    JsonObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_college_canteen);

        collegeCanteenShimmerFrameLayout = findViewById(R.id.college_canteen_shimmer);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));


        /* Need to change after Accessing Data Dynamically from Server */
        restaurantsList = findViewById(R.id.restaurant_List);

        //progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        restaurantToolBar = findViewById(R.id.restaurantToolBar);
        setSupportActionBar(restaurantToolBar);

        myAppPrefsManager = new MyAppPrefsManager(this);

        final ActionBar restaurantActionBar = getSupportActionBar();
        restaurantActionBar.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        restaurantActionBar.setDisplayShowTitleEnabled(false);

        backbutton = (ImageView) findViewById(R.id.back_button);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(All_CollegeCanteen.this, UserDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                next();
            }
        });


        next();
    }

    public void next() {

        //progressBar.setVisibility(View.VISIBLE);
        collegeCanteenShimmerFrameLayout.setVisibility(View.VISIBLE);

        if(myAppPrefsManager.getCity() == null){
            jsonObject = new JsonObject();
            jsonObject.addProperty("city","Kakinada");
            jsonObject.addProperty("area","Jayendra Nagar");
            jsonObject.addProperty("type","College Canteen");

        }else {
            jsonObject = new JsonObject();
            jsonObject.addProperty("city", myAppPrefsManager.getCity());
            jsonObject.addProperty("area", myAppPrefsManager.getArea());
            jsonObject.addProperty("type","College Canteen");
        }

        ApiInterface apiInterface = RetrofitClient.getClient(this).create(ApiInterface.class);

        apiInterface.processAllCollegeCanteens(jsonObject).enqueue(new Callback<RestaurantsModel>() {
            @Override
            public void onResponse(Call<RestaurantsModel> call, Response<RestaurantsModel> response) {


                if(response.isSuccessful()) {

                    //progressBar.setVisibility(View.GONE);

                    RestaurantsModel restaurantsModel = response.body();
                    assert restaurantsModel != null;

                    if (restaurantsModel.isStatus()) {

                        List<RestaurantsModel.DataBean> resultBeans = restaurantsModel.getData();

                        restaurantsList.setHasFixedSize(true);
                        restaurantsList.setLayoutManager(new LinearLayoutManager(All_CollegeCanteen.this, LinearLayoutManager.HORIZONTAL, false));
                        restaurantsAdapter = new AllCategoriesAdapter(All_CollegeCanteen.this, resultBeans);

                        collegeCanteenShimmerFrameLayout.stopShimmer();
                        collegeCanteenShimmerFrameLayout.setVisibility(View.GONE);
                        restaurantsList.setVisibility(VISIBLE);

                        restaurantsList.setAdapter(restaurantsAdapter);

                        restaurantsAdapter.notifyDataSetChanged();


                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(All_CollegeCanteen.this, LinearLayoutManager.VERTICAL, false);
                        restaurantsList.setLayoutManager(linearLayoutManager);
                        restaurantsList.setAdapter((RecyclerView.Adapter) restaurantsAdapter);

                    } else {

                        //progressBar.setVisibility(View.GONE);
                        collegeCanteenShimmerFrameLayout.stopShimmer();
                        collegeCanteenShimmerFrameLayout.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        restaurantsList.setVisibility(View.GONE);
                    }

                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<RestaurantsModel> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                collegeCanteenShimmerFrameLayout.stopShimmer();
                collegeCanteenShimmerFrameLayout.setVisibility(View.GONE);
                Toast.makeText(All_CollegeCanteen.this, "Please Try Again! College Canteens Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        collegeCanteenShimmerFrameLayout.startShimmer();

    }

    @Override
    protected void onPause() {
        super.onPause();
        collegeCanteenShimmerFrameLayout.stopShimmer();
    }

}