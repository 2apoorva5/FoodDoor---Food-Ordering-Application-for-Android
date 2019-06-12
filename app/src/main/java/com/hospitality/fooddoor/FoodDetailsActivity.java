package com.hospitality.fooddoor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.database.Database;
import com.hospitality.fooddoor.model.Food;
import com.hospitality.fooddoor.model.Orders;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class FoodDetailsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    TextView foodName, foodDiscount, foodPrice, foodPrePrice, foodDesc, copyright;
    ImageView foodImg, mealType, favourites, fbShare;
    Button addToCart;
    ElegantNumberButton elegantNumberButton;

    String foodId = "";

    Database localDB;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference foodDetails;

    Food currentFood;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        Toolbar toolbar = findViewById(R.id.foodDetails_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForFoodDetailsTitle);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(FoodDetailsActivity.this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        foodDetails = database.getReference("Foods");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        foodName = findViewById(R.id.name_food);
        foodDiscount = findViewById(R.id.discount);
        foodPrice = findViewById(R.id.price);
        foodPrePrice = findViewById(R.id.priceEarlier);
        foodDesc = findViewById(R.id.desc_food);
        copyright = findViewById(R.id.copyright);

        foodImg = findViewById(R.id.img_food);
        mealType = findViewById(R.id.meal_type);
        favourites = findViewById(R.id.favourite);
        fbShare = findViewById(R.id.fb_share);

        addToCart = findViewById(R.id.addToCart);

        elegantNumberButton = findViewById(R.id.elegantNumberButton);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null){
                    foodId = getIntent().getStringExtra("FoodId");
                }
                if(!foodId.isEmpty()){
                    if(Common.isConnectedToInternet(getBaseContext()))
                    {
                        if(getIntent() != null)
                        {
                            foodId = getIntent().getStringExtra("FoodId");
                            localDB = new Database(FoodDetailsActivity.this);
                            if(localDB.isInFavourites(foodId, user.getEmail()))
                            {
                                favourites.setImageResource(R.drawable.favourite2);
                            }
                        }

                        getFoodDetails(foodId);

                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Common.isConnectedToInternet(getBaseContext())) {
                                    final boolean isExists = new Database(getBaseContext()).isInCarts(foodId, user.getEmail());
                                    if(!isExists) {
                                        new Database(getBaseContext()).addToCart(new Orders(
                                                user.getEmail(),
                                                foodId,
                                                currentFood.getName(),
                                                elegantNumberButton.getNumber(),
                                                currentFood.getPrice(),
                                                currentFood.getDiscount(),
                                                currentFood.getImage(),
                                                currentFood.getType()
                                        ));
                                    }
                                    else
                                    {
                                        new Database(getBaseContext()).increaseCart(user.getEmail(), foodId);
                                    }

                                    Alerter.create(FoodDetailsActivity.this)
                                            .setTitle("ADDED TO CART!")
                                            .setTextAppearance(R.style.Alerter1TextAppearance)
                                            .setBackgroundColorRes(R.color.Alerter1)
                                            .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                            .setDuration(3000)
                                            .enableSwipeToDismiss()
                                            .enableIconPulse(true)
                                            .enableVibration(true)
                                            .show();

                                    addToCart.setBackgroundResource(R.drawable.round_layout1);
                                    addToCart.setText("GO TO CART");
                                    addToCart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(FoodDetailsActivity.this, CartActivity.class));
                                            finish();
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

                        favourites.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getIntent() != null)
                                {
                                    foodId = getIntent().getStringExtra("FoodId");
                                }
                                if(!foodId.isEmpty())
                                {
                                    if(Common.isConnectedToInternet(FoodDetailsActivity.this))
                                    {
                                        if(!localDB.isInFavourites(foodId, user.getEmail()))
                                        {
                                            localDB.addToFavourites(foodId, user.getEmail());
                                            favourites.setImageResource(R.drawable.favourite2);
                                            Alerter.create(FoodDetailsActivity.this)
                                                    .setTitle("ADDED TO FAVOURITES!")
                                                    .setTextAppearance(R.style.Alerter1TextAppearance)
                                                    .setBackgroundColorRes(R.color.Alerter1)
                                                    .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                        }
                                        else
                                        {
                                            localDB.removeFromFavourites(foodId, user.getEmail());
                                            favourites.setImageResource(R.drawable.favourite1);
                                            Alerter.create(FoodDetailsActivity.this)
                                                    .setTitle("REMOVED FROM FAVOURITES!")
                                                    .setTextAppearance(R.style.Alerter1TextAppearance)
                                                    .setBackgroundColorRes(R.color.Alerter1)
                                                    .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        });

                        fbShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Common.isConnectedToInternet(FoodDetailsActivity.this))
                                {
                                    Picasso.get().load(currentFood.getImage()).into(target);
                                }
                                else
                                {
                                    Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                    else
                    {
                        foodImg.setVisibility(View.INVISIBLE);
                        mealType.setVisibility(View.INVISIBLE);
                        foodDiscount.setVisibility(View.INVISIBLE);
                        foodName.setVisibility(View.INVISIBLE);
                        foodPrice.setVisibility(View.INVISIBLE);
                        foodPrePrice.setVisibility(View.INVISIBLE);
                        favourites.setVisibility(View.INVISIBLE);
                        fbShare.setVisibility(View.INVISIBLE);
                        elegantNumberButton.setEnabled(false);
                        elegantNumberButton.setVisibility(View.INVISIBLE);
                        foodDesc.setVisibility(View.INVISIBLE);
                        addToCart.setEnabled(false);
                        favourites.setEnabled(false);
                        fbShare.setEnabled(false);
                        addToCart.setBackgroundResource(R.drawable.round_layout6);
                        Toast.makeText(FoodDetailsActivity.this, "Please check your Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                {
                    getFoodDetails(foodId);
                    swipeRefreshLayout.setRefreshing(false);
                    foodImg.setVisibility(View.VISIBLE);
                    mealType.setVisibility(View.VISIBLE);
                    favourites.setVisibility(View.VISIBLE);
                    fbShare.setVisibility(View.VISIBLE);
                    foodDiscount.setVisibility(View.VISIBLE);
                    foodName.setVisibility(View.VISIBLE);
                    foodPrice.setVisibility(View.VISIBLE);
                    foodPrePrice.setVisibility(View.VISIBLE);
                    elegantNumberButton.setVisibility(View.VISIBLE);
                    elegantNumberButton.setEnabled(true);
                    foodDesc.setVisibility(View.VISIBLE);
                    addToCart.setEnabled(true);
                    favourites.setEnabled(true);
                    fbShare.setEnabled(true);
                    addToCart.setText("ADD TO CART");
                    addToCart.setBackgroundResource(R.drawable.round_layout2);

                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Common.isConnectedToInternet(getBaseContext())) {
                                final boolean isExists = new Database(getBaseContext()).isInCarts(foodId, user.getEmail());
                                if(!isExists) {
                                    new Database(getBaseContext()).addToCart(new Orders(
                                            user.getEmail(),
                                            foodId,
                                            currentFood.getName(),
                                            elegantNumberButton.getNumber(),
                                            currentFood.getPrice(),
                                            currentFood.getDiscount(),
                                            currentFood.getImage(),
                                            currentFood.getType()
                                    ));
                                }
                                else
                                {
                                    new Database(getBaseContext()).increaseCart(user.getEmail(), foodId);
                                }

                                Alerter.create(FoodDetailsActivity.this)
                                        .setTitle("ADDED TO CART!")
                                        .setTextAppearance(R.style.Alerter1TextAppearance)
                                        .setBackgroundColorRes(R.color.Alerter1)
                                        .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                        .setDuration(3000)
                                        .enableSwipeToDismiss()
                                        .enableIconPulse(true)
                                        .enableVibration(true)
                                        .show();

                                addToCart.setBackgroundResource(R.drawable.round_layout1);
                                addToCart.setText("GO TO CART");
                                addToCart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(FoodDetailsActivity.this, CartActivity.class));
                                        finish();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

                    favourites.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getIntent() != null)
                            {
                                foodId = getIntent().getStringExtra("FoodId");
                            }
                            if(!foodId.isEmpty())
                            {
                                if(Common.isConnectedToInternet(FoodDetailsActivity.this))
                                {
                                    if(!localDB.isInFavourites(foodId, user.getEmail()))
                                    {
                                        localDB.addToFavourites(foodId, user.getEmail());
                                        favourites.setImageResource(R.drawable.favourite2);
                                        Alerter.create(FoodDetailsActivity.this)
                                                .setTitle("ADDED TO FAVOURITES!")
                                                .setTextAppearance(R.style.Alerter1TextAppearance)
                                                .setBackgroundColorRes(R.color.Alerter1)
                                                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .show();
                                    }
                                    else
                                    {
                                        localDB.removeFromFavourites(foodId, user.getEmail());
                                        favourites.setImageResource(R.drawable.favourite1);
                                        Alerter.create(FoodDetailsActivity.this)
                                                .setTitle("REMOVED FROM FAVOURITES!")
                                                .setTextAppearance(R.style.Alerter1TextAppearance)
                                                .setBackgroundColorRes(R.color.Alerter1)
                                                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    });

                    fbShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Common.isConnectedToInternet(FoodDetailsActivity.this))
                            {
                                Picasso.get().load(currentFood.getImage()).into(target);
                            }
                            else
                            {
                                Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
                else
                {
                    foodImg.setVisibility(View.INVISIBLE);
                    mealType.setVisibility(View.INVISIBLE);
                    foodDiscount.setVisibility(View.INVISIBLE);
                    foodName.setVisibility(View.INVISIBLE);
                    foodPrice.setVisibility(View.INVISIBLE);
                    foodPrePrice.setVisibility(View.INVISIBLE);
                    favourites.setVisibility(View.INVISIBLE);
                    fbShare.setVisibility(View.INVISIBLE);
                    elegantNumberButton.setEnabled(false);
                    elegantNumberButton.setVisibility(View.INVISIBLE);
                    foodDesc.setVisibility(View.INVISIBLE);
                    favourites.setEnabled(false);
                    fbShare.setEnabled(false);
                    addToCart.setEnabled(false);
                    addToCart.setText("ADD TO CART");
                    addToCart.setBackgroundResource(R.drawable.round_layout6);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(FoodDetailsActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFoodDetails(String foodId) {
        swipeRefreshLayout.setRefreshing(false);
        foodDetails.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(foodImg);
                foodName.setText(currentFood.getName());
                foodDiscount.setText(currentFood.getDiscount());
                foodPrice.setText(String.format("₹ %s", currentFood.getPrice()));
                foodPrePrice.setText(String.format("₹ %s", currentFood.getPreviousPrice()));
                foodPrePrice.setPaintFlags(foodPrePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                foodDesc.setText(currentFood.getDescription());
                Picasso.get().load(currentFood.getType()).into(mealType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
