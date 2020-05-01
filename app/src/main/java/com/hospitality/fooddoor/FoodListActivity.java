package com.hospitality.fooddoor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.database.Database;
import com.hospitality.fooddoor.model.Food;
import com.hospitality.fooddoor.model.Orders;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

public class FoodListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    String categoryId = "";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference foodlist;
    private RecyclerView recycler_food;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    private TextView wait, tryCategory, foodListText;
    private ImageView sideLogo;
    private ProgressBar progressBar;

    TextView textCartItemCount;

    Database localDB;

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
        setContentView(R.layout.activity_food_list);

        Toolbar toolbar = findViewById(R.id.food_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForFoodListTitle);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(FoodListActivity.this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        foodlist = database.getReference("Foods");
        foodlist.keepSynced(true);

        localDB = new Database(FoodListActivity.this);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tryCategory = findViewById(R.id.tryCategory);
        foodListText = findViewById(R.id.foodListText);
        sideLogo = findViewById(R.id.sideLogo);
        recycler_food = findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);
        layoutManager.onSaveInstanceState();

        wait = findViewById(R.id.wait);
        progressBar = findViewById(R.id.WanderingCubes);
        WanderingCubes wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null)
                {
                    categoryId = getIntent().getStringExtra("CategoryId");
                    tryCategory.setText(String.format("Try %s", Common.currentCategory.getName()));
                }
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    if(Common.isConnectedToInternet(FoodListActivity.this))
                    {
                        foodlistloader(categoryId);
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        wait.setVisibility(View.GONE);
                        sideLogo.setVisibility(View.GONE);
                        tryCategory.setVisibility(View.GONE);
                        foodListText.setVisibility(View.GONE);
                        Toast.makeText(FoodListActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)
                {
                    categoryId = getIntent().getStringExtra("CategoryId");
                    tryCategory.setText(String.format("Try %s", Common.currentCategory.getName()));
                }
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    if(Common.isConnectedToInternet(FoodListActivity.this))
                    {
                        sideLogo.setVisibility(View.VISIBLE);
                        tryCategory.setVisibility(View.VISIBLE);
                        foodListText.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        foodlistloader(categoryId);
                    }
                    else
                    {
                        sideLogo.setVisibility(View.GONE);
                        tryCategory.setVisibility(View.GONE);
                        foodListText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        wait.setVisibility(View.GONE);
                        recycler_food.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(FoodListActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void foodlistloader(String categoryId) {
        swipeRefreshLayout.setRefreshing(false);
        Query searchByName = foodlist.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final Food model) {
                viewHolder.txtFoodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.FoodImage);
                Picasso.get().load(model.getType()).into(viewHolder.MealType);
                viewHolder.txtDiscount.setText(model.getDiscount());
                viewHolder.txtPrice.setText(String.format("₹ %s", model.getPrice()));
                viewHolder.txtPrePrice.setText(String.format("₹ %s", model.getPreviousPrice()));
                viewHolder.txtPrePrice.setPaintFlags(viewHolder.txtPrePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                viewHolder.setFoodClickListener(new FoodClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //just implement this to avoid error
                        return;
                    }
                });

                viewHolder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent foodDetails = new Intent(FoodListActivity.this, FoodDetailsActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(FoodListActivity.this, viewHolder.FoodImage, ViewCompat.getTransitionName(viewHolder.FoodImage));
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetails, optionsCompat.toBundle());
                    }
                });

                viewHolder.AddToWishList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Common.isConnectedToInternet(getBaseContext())) {
                                final boolean isExists = new Database(getBaseContext()).isInCarts(adapter.getRef(position).getKey(), user.getEmail());
                                if(!isExists) {
                                    new Database(getBaseContext()).addToCart(new Orders(
                                            user.getEmail(),
                                            adapter.getRef(position).getKey(),
                                            model.getName(),
                                            "1",
                                            model.getPrice(),
                                            model.getDiscount(),
                                            model.getImage(),
                                            model.getType()
                                    ));
                                }
                                else
                                {
                                    new Database(getBaseContext()).increaseCart(user.getEmail(), adapter.getRef(position).getKey());
                                }

                                Alerter.create(FoodListActivity.this)
                                        .setTitle("ADDED TO CART!")
                                        .setTextAppearance(R.style.Alerter1TextAppearance)
                                        .setBackgroundColorRes(R.color.Alerter1)
                                        .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                                        .setDuration(3000)
                                        .enableSwipeToDismiss()
                                        .enableIconPulse(true)
                                        .enableVibration(true)
                                        .show();

                                setupBadge();

                            } else {
                                Toast.makeText(FoodListActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

                viewHolder.fbShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Common.isConnectedToInternet(FoodListActivity.this))
                        {
                            Picasso.get().load(model.getImage()).into(target);
                        }
                        else
                        {
                            Toast.makeText(FoodListActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

                localDB = new Database(FoodListActivity.this);
                if(localDB.isInFavourites(adapter.getRef(position).getKey(), user.getEmail()))
                {
                    viewHolder.favourites.setImageResource(R.drawable.favourite2);
                }

                viewHolder.favourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Common.isConnectedToInternet(FoodListActivity.this))
                        {
                            if(!localDB.isInFavourites(adapter.getRef(position).getKey(), user.getEmail()))
                            {
                                localDB.addToFavourites(adapter.getRef(position).getKey(), user.getEmail());
                                viewHolder.favourites.setImageResource(R.drawable.favourite2);
                                Alerter.create(FoodListActivity.this)
                                        .setTitle(model.getName() + " has been added to Favorites!")
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
                                localDB.removeFromFavourites(adapter.getRef(position).getKey(), user.getEmail());
                                viewHolder.favourites.setImageResource(R.drawable.favourite1);
                                Alerter.create(FoodListActivity.this)
                                        .setTitle(model.getName() + " has been removed from Favorites!")
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
                            Toast.makeText(FoodListActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_list, viewGroup, false);
                return new FoodViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                if(progressBar != null && wait != null){
                    progressBar.setVisibility(View.GONE);
                    wait.setVisibility(View.GONE);
                }
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_food.setAdapter(adapter);
        recycler_food.setVisibility(View.VISIBLE);
    }

    public interface FoodClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        TextView txtFoodName, txtDiscount, txtPrice, txtPrePrice, details;
        ImageView FoodImage, MealType, favourites, fbShare;
        Button AddToWishList;

        private FoodClickListener foodClickListener;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            details = view.findViewById(R.id.details);
            txtFoodName = view.findViewById(R.id.food_name);
            txtDiscount = view.findViewById(R.id.discount);
            txtPrice = view.findViewById(R.id.present_price);
            txtPrePrice = view.findViewById(R.id.previous_price);
            favourites = view.findViewById(R.id.favourite);
            fbShare = view.findViewById(R.id.fb_share);

            AddToWishList = view.findViewById(R.id.add_to_wishlist);

            FoodImage = view.findViewById(R.id.food_img);
            MealType = view.findViewById(R.id.meal_type);

            itemView.setOnClickListener(this);
        }


        public void setFoodClickListener(FoodClickListener foodClickListener) {
            this.foodClickListener = foodClickListener;
        }

        @Override
        public void onClick(View v) {
            foodClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);

        final MenuItem menuItem = menu.findItem(R.id.menu_cart);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    private void setupBadge() {
        if(textCartItemCount != null)
        {
            if(new Database(FoodListActivity.this).getCountCart(user.getEmail()) == 0)
            {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            }
            else
            {
                textCartItemCount.setText(String.valueOf(new Database(FoodListActivity.this).getCountCart(user.getEmail())));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.menu_cart){
            startActivity(new Intent(FoodListActivity.this, CartActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
        if(adapter != null)
        {
            adapter.startListening();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
