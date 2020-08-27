package com.example.starvelater.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.starvelater.R;
import com.example.starvelater.SharedPref.MyAppPrefsManager;
import com.example.starvelater.activities.helpandsupport.Feedback;
import com.example.starvelater.activities.helpandsupport.HelpandSupportActivity;
import com.example.starvelater.activities.helpandsupport.TermsandConditionsActivity;
import com.example.starvelater.activities.restaurant.All_Restaurants;
import com.example.starvelater.activities.loginsignup.StartUpScreen;
import com.example.starvelater.adapters.userdashboard_adapters.MostPopularAdapter;
import com.example.starvelater.adapters.userdashboard_adapters.UserAllRestaurantsAdapter;
import com.example.starvelater.adapters.userdashboard_adapters.UtilityAdapter;
import com.example.starvelater.adapters.userdashboard_adapters.UtilityHelperClass;
import com.example.starvelater.api.ApiInterface;
import com.example.starvelater.api.RetrofitClient;
import com.example.starvelater.jsonmodels.LocationsModel;
import com.example.starvelater.jsonmodels.PopularRestaurantsModel;
import com.example.starvelater.jsonmodels.RestaurantsModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ShimmerFrameLayout mostPopularshimmerFrameLayout, allRestaurantsshimmerFrameLayout;
    static final float END_SCALE = 0.7f;
    RecyclerView mostPopularRecyclerView;
    RecyclerView allRestaurantsRecyclerView;
    RecyclerView utilityRecycler;
    RecyclerView.Adapter adapter;

    TextView loading_anim;

    //SwipeRefreshLayout swipeRefreshLayout;

    MyAppPrefsManager myAppPrefsManager;

    Spinner spinCity,spinArea;



    List<LocationsModel.DataBean> locationList;
    List<RestaurantsModel.DataBean> restaurantList,popularList,allRestList;

    private GradientDrawable gradient1, gradient2, gradient3;

    private Dialog setLocationDialog;

    private LinearLayout setLocationLayout;

    TextView txtLocation;

    LinearLayout progressBar, dialogProgressBar;

    String TAG="RESPONSE_DATA";
    String city, area;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    ImageView qr_icon;
    TextView txtViewAll;

    Animation loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        mostPopularshimmerFrameLayout = findViewById(R.id.most_popular_shimmer);
        allRestaurantsshimmerFrameLayout = findViewById(R.id.all_restaurants_shimmer);
        setLocationLayout = findViewById(R.id.locationLayout);

        txtLocation = findViewById(R.id.location);

        setLocationLayout.setEnabled(false);

        loading_anim = findViewById(R.id.loading_anim);



        loadingAnimation = AnimationUtils.loadAnimation(UserDashboard.this,R.anim.blink_anim);

        txtLocation.setVisibility(View.GONE);

        // Starting Blink action
        loading_anim.setVisibility(View.VISIBLE);
        loading_anim.startAnimation(loadingAnimation);




      //  swipeRefreshLayout = findViewById(R.id.refreshLayout);

       // swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        progressBar = findViewById(R.id.progressBar);
        dialogProgressBar = findViewById(R.id.progressBar);


        popularList = new ArrayList<>();
        allRestList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);

        myAppPrefsManager = new MyAppPrefsManager(UserDashboard.this);

        //Shared Preferences
        if(myAppPrefsManager.getUserName() != null) {
            Toast.makeText(this, "Welcome back " + myAppPrefsManager.getUserName(), Toast.LENGTH_SHORT).show();
        }


        if(myAppPrefsManager.getCity() == null){
            txtLocation.setText("Kakinada, Jayendra Nagar");
            city = "Kakinada";
            area = "Jayendra Nagar";
            allRestaurantsRecycler(city, area);
        }else{
            city = myAppPrefsManager.getCity();
            area = myAppPrefsManager.getArea();
            allRestaurantsRecycler(city, area);

            txtLocation.setText(city + ", " + area);
        }

        //Hooks
        mostPopularRecyclerView = findViewById(R.id.most_popular_recycler);

        allRestaurantsRecyclerView = findViewById(R.id.all_restaurants);
        utilityRecycler = findViewById(R.id.utility_recycler);

        txtViewAll = (TextView) findViewById(R.id.restaurantsViewAll);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        qr_icon = findViewById(R.id.qr_icon);
        qr_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(UserDashboard.this, "Sorry! Not Yet Implemented", Toast.LENGTH_SHORT).show();*/
                Intent intent2 = new Intent(UserDashboard.this, Qr_scan.class);
                startActivity(intent2);

            }
        });

        txtViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(UserDashboard.this, All_Restaurants.class);
                startActivity(intent1);
            }
        });

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                next();
            }
        });
*/

        next();


        setLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProgressBar.setVisibility(View.VISIBLE);
                if(locationList.size() == 0){
                    Toast.makeText(UserDashboard.this, "Low Internet Connectivity", Toast.LENGTH_SHORT).show();
                }else {
                    openSetLocationDialog(locationList);
                }
            }
        });



        navigationDrawer();
        utilityRecycler();
    }

    private void next() {
        // Loading All Locations, calling Service
        ApiInterface apiInterface = RetrofitClient.getClient(UserDashboard.this).create(ApiInterface.class);
        apiInterface.processLocationData().enqueue(new Callback<LocationsModel>() {
            @Override
            public void onResponse(Call<LocationsModel> call, Response<LocationsModel> response) {
                if(response.isSuccessful()){
                    LocationsModel locationsModel = response.body();
                    assert locationsModel!= null;
                    if(locationsModel.isStatus()){
                        locationList = locationsModel.getData();
                        dialogProgressBar.setVisibility(View.GONE);

                        setLocationLayout.setEnabled(true);
                        loading_anim.setVisibility(View.GONE);
                        loading_anim.clearAnimation();
                        txtLocation.setVisibility(View.VISIBLE);
                    }else{
                        dialogProgressBar.setVisibility(View.GONE);
                        Toast.makeText(UserDashboard.this, "No Locations Found", Toast.LENGTH_SHORT).show();
                    }

                  //  swipeRefreshLayout.setRefreshing(false);
                }else{
                    dialogProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserDashboard.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                   // swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<LocationsModel> call, Throwable t) {
                dialogProgressBar.setVisibility(View.GONE);
                Toast.makeText(UserDashboard.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
              //  swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void openSetLocationDialog(List<LocationsModel.DataBean> locationList) {
        setLocationDialog = new Dialog(UserDashboard.this);

        setLocationDialog.setContentView(R.layout.location_dialog);

        spinCity = setLocationDialog.findViewById(R.id.spinCity);
        spinArea = setLocationDialog.findViewById(R.id.spinArea);

        ArrayList<String> cityList = new ArrayList<>();

        // Creating City List
        for(int i = 0; i< UserDashboard.this.locationList.size(); i++) {
            if(!cityList.contains(UserDashboard.this.locationList.get(i).getCity_Name())) {
                cityList.add(UserDashboard.this.locationList.get(i).getCity_Name());
            }
        }

        // Setting City List to spinCity spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserDashboard.this, R.layout.support_simple_spinner_dropdown_item, cityList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinCity.setAdapter(arrayAdapter);

        dialogProgressBar.setVisibility(View.GONE);

        // Creating Areas List
        ArrayList<String> areaList = new ArrayList<>();

        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Extracting the Selected City
                String cityName = spinCity.getSelectedItem().toString();
                areaList.clear();
                //progressBar.setVisibility(View.VISIBLE);

                // Creating the Areas List based on city selected from the called Service
                for(int i = 0; i< UserDashboard.this.locationList.size(); i++) {
                    if(UserDashboard.this.locationList.get(i).getCity_Name().equals(cityName)){
                        areaList.add(UserDashboard.this.locationList.get(i).getArea_Name());
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserDashboard.this, R.layout.support_simple_spinner_dropdown_item, areaList);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinArea.setAdapter(arrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                progressBar.setVisibility(View.GONE);
            }
        });


        ImageView btnClose = setLocationDialog.findViewById(R.id.btnClose);

        Button btnSubmit = (Button) setLocationDialog.findViewById(R.id.btnSubmit);


          btnSubmit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  String areaName = spinArea.getSelectedItem().toString();
                  String cityName = spinCity.getSelectedItem().toString();

                  if (!areaName.isEmpty() && !cityName.isEmpty()) {
                      // Initialising New Shared Preferences
                      txtLocation.setText(cityName + ", " + areaName);
                      myAppPrefsManager.setCity(cityName);
                      myAppPrefsManager.setArea(areaName);
                      mostPopularRecyclerView.setVisibility(View.GONE);
                      mostPopularshimmerFrameLayout.setVisibility(View.VISIBLE);
                      mostPopularshimmerFrameLayout.startShimmer();
                      allRestaurantsRecyclerView.setVisibility(View.GONE);
                      allRestaurantsshimmerFrameLayout.setVisibility(View.VISIBLE);
                      allRestaurantsshimmerFrameLayout.startShimmer();
                      allRestaurantsRecycler(cityName, areaName);

                      //Toast.makeText(UserDashboard.this, "Changed Location Successfully !", Toast.LENGTH_SHORT).show();
                      setLocationDialog.dismiss();
                  }
              }
          });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationDialog.dismiss();
            }
        });


        Window window = setLocationDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        setLocationDialog.show();

    }



    private void allRestaurantsRecycler(String city, String area) {

        progressBar.setVisibility(View.GONE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("city",city);
        jsonObject.addProperty("area",area);

        ApiInterface apiInterface = RetrofitClient.getClient(this).create(ApiInterface.class);

        apiInterface.processRestaurants(jsonObject).enqueue(new Callback<RestaurantsModel>() {
            @Override
            public void onResponse(@NonNull Call<RestaurantsModel> call, @NonNull Response<RestaurantsModel> response) {

                if(response.isSuccessful()) {
                    RestaurantsModel restaurantsModel = response.body();
                    assert restaurantsModel!=null;
                    if(restaurantsModel.isStatus()) {

                        // getting all Restaurants list based on Areas and Cities
                        restaurantList = restaurantsModel.getData();
                        
                        popularList.clear();
                        allRestList.clear();
                        for(int i=0;i<restaurantList.size(); i++) {

                            if(restaurantList.get(i).getType().equals("Most Popular")) {
                                popularList.add(restaurantList.get(i));
                            } else  {
                                allRestList.add(restaurantList.get(i));
                            }
                        }

                            mostPopularRecyclerView.setHasFixedSize(true);
                            mostPopularRecyclerView.setLayoutManager(new LinearLayoutManager(UserDashboard.this, LinearLayoutManager.HORIZONTAL, false));
                            adapter = new MostPopularAdapter(UserDashboard.this,popularList);

                            mostPopularshimmerFrameLayout.stopShimmer();
                            mostPopularshimmerFrameLayout.setVisibility(View.GONE);
                            mostPopularRecyclerView.setVisibility(View.VISIBLE);

                            mostPopularRecyclerView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();


                            allRestaurantsRecyclerView.setHasFixedSize(true);
                            allRestaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(UserDashboard.this, LinearLayoutManager.HORIZONTAL, false));

                            // NEED TO CHANGE THE ARRAY LIST TO allRestList
                            adapter = new UserAllRestaurantsAdapter(UserDashboard.this, allRestList);



                            allRestaurantsshimmerFrameLayout.stopShimmer();
                            allRestaurantsshimmerFrameLayout.setVisibility(View.GONE);
                            allRestaurantsRecyclerView.setVisibility(View.VISIBLE);
                            allRestaurantsRecyclerView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();


                    } else {
                        Toast.makeText(UserDashboard.this, "Sorry No Restaurants Found !", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UserDashboard.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestaurantsModel> call, @NonNull Throwable t) {

                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(UserDashboard.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        });



        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffeff400, 0xffaff600});
    }


    //Navigation Drawer Functions
    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(UserDashboard.this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_home:
                Intent intent2 = new Intent(UserDashboard.this, UserDashboard.class);
                startActivity(intent2);
                break;
            case R.id.nav_all_categories:
                Intent intent = new Intent(UserDashboard.this, AllCategories.class);
                startActivity(intent);
                break;
            case R.id.nav_get_started:
                Intent intent1 = new Intent(UserDashboard.this, StartUpScreen.class);
                startActivity(intent1);
                break;
            case R.id.nav_terms_and_conditions:
                Intent intent3 = new Intent(UserDashboard.this, TermsandConditionsActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_support:
                Intent intent4= new Intent(UserDashboard.this, HelpandSupportActivity.class);
                startActivity(intent4);
                break;
            case R.id.feedback:
                Intent intent6 = new Intent(UserDashboard.this, Feedback.class);
                startActivity(intent6);
                break;

       }
        return true;
    }

    private void utilityRecycler() {

        progressBar.setVisibility(View.GONE);

        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffeff400, 0xffaff600});
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});

        ArrayList<UtilityHelperClass> utility = new ArrayList<>();
        utility.add(new UtilityHelperClass("Restaurant", R.drawable.restaurants, gradient1));
        utility.add(new UtilityHelperClass("College Canteen", R.drawable.college, gradient2));
        utility.add(new UtilityHelperClass("Corporate Cafe", R.drawable.corporate, gradient3));

        utilityRecycler.setHasFixedSize(true);
        adapter = new UtilityAdapter(utility);
        utilityRecycler.setLayoutManager(new LinearLayoutManager(UserDashboard.this, LinearLayoutManager.HORIZONTAL, false));
        utilityRecycler.setAdapter(adapter);



    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostPopularshimmerFrameLayout.startShimmer();
        allRestaurantsshimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mostPopularshimmerFrameLayout.stopShimmer();
        allRestaurantsshimmerFrameLayout.stopShimmer();
    }
}
