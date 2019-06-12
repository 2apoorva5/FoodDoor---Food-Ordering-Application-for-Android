package com.hospitality.fooddoor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.model.HomeFromHouse;
import com.hospitality.fooddoor.model.HomeMeals;
import com.hospitality.fooddoor.model.HomePopular;
import com.hospitality.fooddoor.model.HomeRecommended;
import com.hospitality.fooddoor.model.HomeSlider;
import com.hospitality.fooddoor.model.HomeToday;
import com.hospitality.fooddoor.model.Token;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout culprit, noInternet;
    private SliderLayout sliderLayout;
    private CircleImageView profilePic, homeProfilePic;
    private TextView username, homeUser, homeGreetings, viewAllToday, viewAllMeals, viewAllRecommended, viewAllFromHouse, viewAllPopular;
    private CardView category1, viewAll1, veg1, nonVeg1, breakfast1, lunch1, dinner1, offer201, offer301, offer401, offer501;
    private ImageView category, viewAll, veg, nonVeg, breakfast, lunch, dinner, offer20, offer30, offer40, offer50;
    private RecyclerView recyclerToday, recyclerMeals, recyclerRecommended, recyclerFromHouse, recyclerPopular;
    private LinearLayoutManager layoutManager1, layoutManager2, layoutManager3, layoutManager4, layoutManager5;
    FirebaseRecyclerAdapter<HomeToday, TodayViewHolder> todayAdapter;
    FirebaseRecyclerAdapter<HomeMeals, MealsViewHolder> mealsAdapter;
    FirebaseRecyclerAdapter<HomeFromHouse, FromHouseViewHolder> fromAdapter;
    FirebaseRecyclerAdapter<HomeRecommended, RecommendViewHolder> recommendAdapter;
    FirebaseRecyclerAdapter<HomePopular, PopularViewHolder> popularAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference Banners, Today, Meals, FromHouse, Recommended, Popular;
    String name, email;

    HashMap<String, String> image_list;
    String url1 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fcategory.jpg?alt=media&token=07f5b30a-42ec-48f9-8ee7-7c689e833004";
    String url2 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fviewall.jpg?alt=media&token=491e66f6-4c99-47a2-87bc-58eded16ab9f";
    String url3 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fvegfood.jpg?alt=media&token=fac1830b-517b-4cbc-992d-fb62a21db6b9";
    String url4 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fnonvegfood.jpg?alt=media&token=da5d5cbf-41b8-4ff3-ab4f-97de30a5300b";
    String url5 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fbreakfast.jpg?alt=media&token=87f99e51-a6bf-4275-b1ab-59a9a98e4ddc";
    String url6 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Flunch.png?alt=media&token=ac4cce09-9b27-4f7b-adf6-2c4021f9c1f5";
    String url7 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Fdinner.jpg?alt=media&token=319f94ad-b642-4616-9da3-382ee10da3ec";
    String url8 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Foffer20.jpg?alt=media&token=e2652c9e-f44a-4a12-b36f-d853c8b86a31";
    String url9 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Foffer30.jpg?alt=media&token=a7607f9c-db54-4558-b612-922ed2d2b3e3";
    String url10 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Foffer40.jpg?alt=media&token=322906ca-a502-4eeb-9369-6c843157f9da";
    String url11 = "https://firebasestorage.googleapis.com/v0/b/fooddoor-42765.appspot.com/o/HomeImages%2Foffer50.jpg?alt=media&token=fd0fe4e7-07a9-4cf4-aaa6-cd3f5a1b188b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fooddoor_home);

        final Toolbar toolbar = findViewById(R.id.home_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForHomeTitle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        profilePic = navigationView.getHeaderView(0).findViewById(R.id.pro_pic);
        username = navigationView.getHeaderView(0).findViewById(R.id.user_name);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        Banners = database.getReference("Banners");
        Banners.keepSynced(true);
        Today = database.getReference("Home Today");
        Today.keepSynced(true);
        Meals = database.getReference("Home Meals");
        Meals.keepSynced(true);
        FromHouse = database.getReference("Home From House");
        FromHouse.keepSynced(true);
        Recommended = database.getReference("Home Recommended");
        Recommended.keepSynced(true);
        Popular = database.getReference("Home Popular");
        Popular.keepSynced(true);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        culprit = findViewById(R.id.culprit);
        noInternet = findViewById(R.id.no_internet);
        sliderLayout = findViewById(R.id.banner_layout);

        homeUser = findViewById(R.id.homeUser);
        homeGreetings = findViewById(R.id.homeGreetings);
        homeProfilePic = findViewById(R.id.homeProfilePic);

        viewAllToday = findViewById(R.id.viewAllToday);
        viewAllMeals = findViewById(R.id.viewAllMeals);
        viewAllFromHouse = findViewById(R.id.viewAllFromHouse);
        viewAllRecommended = findViewById(R.id.viewAllRecommended);
        viewAllPopular = findViewById(R.id.viewAllPopular);

        category = findViewById(R.id.categoryImg);
        viewAll = findViewById(R.id.seeAllImg);
        veg = findViewById(R.id.vegImg);
        nonVeg = findViewById(R.id.nonVegImg);
        breakfast = findViewById(R.id.breakfastImg);
        lunch = findViewById(R.id.lunchImg);
        dinner = findViewById(R.id.dinnerImg);
        offer20 = findViewById(R.id.offer20Img);
        offer30 = findViewById(R.id.offer30Img);
        offer40 = findViewById(R.id.offer40Img);
        offer50 = findViewById(R.id.offer50Img);

        category1 = findViewById(R.id.categoryBtn);
        viewAll1 = findViewById(R.id.seeAllBtn);
        veg1 = findViewById(R.id.vegBtn);
        nonVeg1 = findViewById(R.id.nonVegBtn);
        breakfast1 = findViewById(R.id.breakfastBtn);
        lunch1 = findViewById(R.id.lunchBtn);
        dinner1 = findViewById(R.id.dinnerBtn);
        offer201 = findViewById(R.id.offer20Btn);
        offer301 = findViewById(R.id.offer30Btn);
        offer401 = findViewById(R.id.offer40Btn);
        offer501 = findViewById(R.id.offer50Btn);

        recyclerToday = findViewById(R.id.recyclerToday);
        recyclerMeals = findViewById(R.id.recyclerMeals);
        recyclerFromHouse = findViewById(R.id.recyclerFromHouse);
        recyclerRecommended = findViewById(R.id.recyclerRecommended);
        recyclerPopular = findViewById(R.id.recyclerPopular);

        if(user == null){
            username.setText("LOGIN/SIGNUP");
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            for(UserInfo profile : user.getProviderData()){
                String providerId = profile.getProviderId();

                String uid = profile.getUid();

                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();

                Glide.with(this)
                        .load(photoUrl)
                        .into(profilePic);
                Glide.with(this)
                        .load(photoUrl)
                        .into(homeProfilePic);

                assert name != null;
                String[] split2 = name.split(" ", 2);
                username.setText(String.format("Hi, %s!", split2[0]));
                homeUser.setText(String.format("Hey, %s!", split2[0]));
            }
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(HomeActivity.this))
                {
                    if(swipeRefreshLayout.isRefreshing() && swipeRefreshLayout.isNestedScrollingEnabled())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    sliderLayout.startAutoCycle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
                else
                {
                    if(swipeRefreshLayout.isRefreshing() && swipeRefreshLayout.isNestedScrollingEnabled())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    //make translucent statusBar on kitkat devices
                    if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                        setWindowFlag(HomeActivity.this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
                    }
                    if (Build.VERSION.SDK_INT >= 19) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                    //make fully Android Transparent Status bar
                    if (Build.VERSION.SDK_INT >= 21) {
                        setWindowFlag(HomeActivity.this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                    }
                    culprit.setVisibility(View.GONE);
                    noInternet.setVisibility(View.VISIBLE);
                    Toast.makeText(HomeActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(HomeActivity.this))
                {
                    if(swipeRefreshLayout.isRefreshing() && swipeRefreshLayout.isNestedScrollingEnabled())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    sliderLayout.startAutoCycle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                    }
                    culprit.setVisibility(View.VISIBLE);
                    noInternet.setVisibility(View.GONE);
                }
                else
                {
                    if(swipeRefreshLayout.isRefreshing() && swipeRefreshLayout.isNestedScrollingEnabled())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    //make translucent statusBar on kitkat devices
                    if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                        setWindowFlag(HomeActivity.this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
                    }
                    if (Build.VERSION.SDK_INT >= 19) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                    //make fully Android Transparent Status bar
                    if (Build.VERSION.SDK_INT >= 21) {
                        setWindowFlag(HomeActivity.this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                    }
                    culprit.setVisibility(View.GONE);
                    noInternet.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });

        Glide.with(this).load(url1).into(category);
        Glide.with(this).load(url2).into(viewAll);
        Glide.with(this).load(url3).into(veg);
        Glide.with(this).load(url4).into(nonVeg);
        Glide.with(this).load(url5).into(breakfast);
        Glide.with(this).load(url6).into(lunch);
        Glide.with(this).load(url7).into(dinner);
        Glide.with(this).load(url8).into(offer20);
        Glide.with(this).load(url9).into(offer30);
        Glide.with(this).load(url10).into(offer40);
        Glide.with(this).load(url11).into(offer50);

        layoutManager1 = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2 = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager4 = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager5 = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerToday.setHasFixedSize(true);
        recyclerToday.setLayoutManager(layoutManager1);
        recyclerMeals.setHasFixedSize(true);
        recyclerMeals.setLayoutManager(layoutManager2);
        recyclerFromHouse.setHasFixedSize(true);
        recyclerFromHouse.setLayoutManager(layoutManager3);
        recyclerRecommended.setHasFixedSize(true);
        recyclerRecommended.setLayoutManager(layoutManager4);
        recyclerPopular.setHasFixedSize(true);
        recyclerPopular.setLayoutManager(layoutManager5);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        setupSlider();

        todayLoader();
        mealsLoader();
        fromHouseLoader();
        recommendLoader();
        popularLoader();
    }

    private void setupSlider() {
        image_list = new HashMap<>();
        Banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    HomeSlider slider = postSnapshot.getValue(HomeSlider.class);
                    image_list.put(slider.getName(), slider.getImage());
                }

                for(String key : image_list.keySet())
                {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.centerCrop();

                    TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .image(image_list.get(key))
                            .setRequestOption(requestOptions)
                            .setProgressBarVisible(true)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView baseSliderView) {
                                    return;
                                }
                            });

                    sliderLayout.addSlider(textSliderView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setDuration(4000);
        sliderLayout.startAutoCycle();
    }

    private void todayLoader() {
        List<HomeToday> homeToday;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<HomeToday> todayOptions = new FirebaseRecyclerOptions.Builder<HomeToday>()
                .setQuery(Today, HomeToday.class)
                .build();

        todayAdapter = new FirebaseRecyclerAdapter<HomeToday, TodayViewHolder>(todayOptions) {
            @Override
            protected void onBindViewHolder(@NonNull TodayViewHolder holder, int position, @NonNull HomeToday model) {
                holder.todayName.setText(model.getName());
                holder.todayDiscount.setText(model.getDiscount());
                Picasso.get().load(model.getImage()).into(holder.todayImg);
                Picasso.get().load(model.getMealType()).into(holder.todayType);
                holder.setItemClickListener(new TodayItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement to avoid errors
                    }
                });
            }

            @NonNull
            @Override
            public TodayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_today, viewGroup, false);
                return new TodayViewHolder(itemView);
            }
        };

        todayAdapter.startListening();
        todayAdapter.notifyDataSetChanged();  //show updated data
        recyclerToday.setAdapter(todayAdapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface TodayItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class TodayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView todayName, todayDiscount;
        ImageView todayImg, todayType;

        private TodayItemClickListener todayItemClickListener;

        public TodayViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            todayName = view.findViewById(R.id.home_today_name);
            todayDiscount = view.findViewById(R.id.home_today_discount);
            todayImg = view.findViewById(R.id.home_today_img);
            todayType = view.findViewById(R.id.home_today_meal_type);
        }

        public void setItemClickListener(TodayItemClickListener todayItemClickListener) {
            this.todayItemClickListener = todayItemClickListener;
        }

        @Override
        public void onClick(View v) {
            todayItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private void mealsLoader(){
        List<HomeMeals> homeMeals;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<HomeMeals> mealsOptions = new FirebaseRecyclerOptions.Builder<HomeMeals>()
                .setQuery(Meals, HomeMeals.class)
                .build();

        mealsAdapter = new FirebaseRecyclerAdapter<HomeMeals, MealsViewHolder>(mealsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MealsViewHolder holder, int position, @NonNull HomeMeals model) {
                holder.mealsName.setText(model.getName());
                holder.mealsDiscount.setText(model.getDiscount());
                Picasso.get().load(model.getImage()).into(holder.mealsImg);
                Picasso.get().load(model.getMealType()).into(holder.mealsType);
                holder.setItemClickListener(new MealsItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement to avoid errors
                    }
                });
            }

            @NonNull
            @Override
            public MealsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_meals, viewGroup, false);
                return new MealsViewHolder(itemView);
            }
        };

        mealsAdapter.startListening();
        mealsAdapter.notifyDataSetChanged();  //show updated data
        recyclerMeals.setAdapter(mealsAdapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface MealsItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class MealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView mealsName, mealsDiscount;
        ImageView mealsImg, mealsType;

        private MealsItemClickListener mealsItemClickListener;

        public MealsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            mealsName = view.findViewById(R.id.home_meals_name);
            mealsDiscount = view.findViewById(R.id.home_meals_discount);
            mealsImg = view.findViewById(R.id.home_meals_img);
            mealsType = view.findViewById(R.id.home_meals_type);
        }

        public void setItemClickListener(MealsItemClickListener mealsItemClickListener) {
            this.mealsItemClickListener = mealsItemClickListener;
        }

        @Override
        public void onClick(View v) {
            mealsItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private void fromHouseLoader(){
        List<HomeFromHouse> homeFromHouses;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<HomeFromHouse> fromOptions = new FirebaseRecyclerOptions.Builder<HomeFromHouse>()
                .setQuery(FromHouse, HomeFromHouse.class)
                .build();

        fromAdapter = new FirebaseRecyclerAdapter<HomeFromHouse, FromHouseViewHolder>(fromOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FromHouseViewHolder holder, int position, @NonNull HomeFromHouse model) {
                holder.fromName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.fromImg);
                Picasso.get().load(model.getMealType()).into(holder.fromType);
                holder.setItemClickListener(new FromHouseItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement to avoid errors
                    }
                });
            }

            @NonNull
            @Override
            public FromHouseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_from, viewGroup, false);
                return new FromHouseViewHolder(itemView);
            }
        };

        fromAdapter.startListening();
        fromAdapter.notifyDataSetChanged();  //show updated data
        recyclerFromHouse.setAdapter(fromAdapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface FromHouseItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class FromHouseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView fromName;
        ImageView fromImg, fromType;

        private FromHouseItemClickListener fromHouseItemClickListener;

        public FromHouseViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            fromName = view.findViewById(R.id.home_from_name);
            fromImg = view.findViewById(R.id.home_from_img);
            fromType = view.findViewById(R.id.home_from_meal_type);
        }

        public void setItemClickListener(FromHouseItemClickListener fromHouseItemClickListener) {
            this.fromHouseItemClickListener = fromHouseItemClickListener;
        }

        @Override
        public void onClick(View v) {
            fromHouseItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private void recommendLoader(){
        List<HomeRecommended> homeRecommendeds;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<HomeRecommended> recoOptions = new FirebaseRecyclerOptions.Builder<HomeRecommended>()
                .setQuery(Recommended, HomeRecommended.class)
                .build();

        recommendAdapter = new FirebaseRecyclerAdapter<HomeRecommended, RecommendViewHolder>(recoOptions) {
            @Override
            protected void onBindViewHolder(@NonNull RecommendViewHolder holder, int position, @NonNull HomeRecommended model) {
                holder.recoName.setText(model.getName());
                holder.recoDiscount.setText(model.getDiscount());
                Picasso.get().load(model.getImage()).into(holder.recoImg);
                Picasso.get().load(model.getMealType()).into(holder.recoType);
                holder.setItemClickListener(new RecommendItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement to avoid errors
                    }
                });
            }

            @NonNull
            @Override
            public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_recommended, viewGroup, false);
                return new RecommendViewHolder(itemView);
            }
        };

        recommendAdapter.startListening();
        recommendAdapter.notifyDataSetChanged();  //show updated data
        recyclerRecommended.setAdapter(recommendAdapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface RecommendItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class RecommendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView recoName, recoDiscount;
        ImageView recoImg, recoType;

        private RecommendItemClickListener recommendItemClickListener;

        public RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            recoName = view.findViewById(R.id.home_recommend_name);
            recoDiscount = view.findViewById(R.id.home_recommend_discount);
            recoImg = view.findViewById(R.id.home_recommend_img);
            recoType = view.findViewById(R.id.home_recommend_type);
        }

        public void setItemClickListener(RecommendItemClickListener recommendItemClickListener) {
            this.recommendItemClickListener = recommendItemClickListener;
        }

        @Override
        public void onClick(View v) {
            recommendItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private void popularLoader(){
        List<HomePopular> homePopulars;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<HomePopular> popularOptions = new FirebaseRecyclerOptions.Builder<HomePopular>()
                .setQuery(Popular, HomePopular.class)
                .build();

        popularAdapter = new FirebaseRecyclerAdapter<HomePopular, PopularViewHolder>(popularOptions) {
            @Override
            protected void onBindViewHolder(@NonNull PopularViewHolder holder, int position, @NonNull HomePopular model) {
                holder.popName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.popImg);
                Picasso.get().load(model.getMealType()).into(holder.popType);
                holder.setItemClickListener(new PopularItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement to avoid errors
                    }
                });
            }

            @NonNull
            @Override
            public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_popular, viewGroup, false);
                return new PopularViewHolder(itemView);
            }
        };

        popularAdapter.startListening();
        popularAdapter.notifyDataSetChanged();  //show updated data
        recyclerPopular.setAdapter(popularAdapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface PopularItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView popName;
        ImageView popImg, popType;

        private PopularItemClickListener popularItemClickListener;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            popName = view.findViewById(R.id.home_popular_name);
            popImg = view.findViewById(R.id.home_popular_img);
            popType = view.findViewById(R.id.home_popular_type);
        }

        public void setItemClickListener(PopularItemClickListener popularItemClickListener) {
            this.popularItemClickListener = popularItemClickListener;
        }

        @Override
        public void onClick(View v) {
            popularItemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderLayout.startAutoCycle();
        if(todayAdapter != null)
        {
            todayAdapter.startListening();
        }
        if(mealsAdapter != null)
        {
            mealsAdapter.startListening();
        }
        if(fromAdapter != null)
        {
            fromAdapter.startListening();
        }
        if(recommendAdapter != null)
        {
            recommendAdapter.startListening();
        }
        if(popularAdapter != null)
        {
            popularAdapter.startListening();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sliderLayout.startAutoCycle();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sliderLayout.startAutoCycle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sliderLayout.startAutoCycle();
    }

    private void updateToken(String token) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token data = new Token(token, false);       //false because this token has been sent by client app
        tokens.child(user.getUid()).setValue(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.nav_home){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_menu){
            startActivity(new Intent(HomeActivity.this, MenuActivity.class));
        }

        if(id == R.id.nav_cart){
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        }

        if(id == R.id.nav_about){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_orders){
            startActivity(new Intent(HomeActivity.this, OrderStatusActivity.class));
        }

        if(id == R.id.nav_rate){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_share){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_contact){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_track){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_writeUs){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_profile){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_notifications){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_pay){
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(id == R.id.nav_logout){
            if(Common.isConnectedToInternet(HomeActivity.this))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Are you sure you want to Sign Out?");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance()
                                .signOut(HomeActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else
            {
                Toast.makeText(HomeActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setWindowFlag(HomeActivity homeActivity, final int bits, boolean on) {
        Window window = homeActivity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        if(on){
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Are you sure you want to Exit?");
            builder.setCancelable(false);
            builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeActivity.super.onBackPressed();
                    HomeActivity.this.finish();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
