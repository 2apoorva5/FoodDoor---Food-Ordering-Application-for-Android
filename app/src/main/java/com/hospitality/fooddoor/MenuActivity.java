package com.hospitality.fooddoor;

import android.content.Context;
import android.content.Intent;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
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

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout culprit;
    private TextView greetings, instruction;
    private ImageView sideLogo;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    private TextView wait1, wait2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForMenuTitle);
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

        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        recycler_menu.setLayoutManager(new GridLayoutManager(MenuActivity.this, 2));

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        category.keepSynced(true);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        culprit = findViewById(R.id.culprit);
        greetings = findViewById(R.id.category_greetings);
        instruction = findViewById(R.id.category_text);
        sideLogo = findViewById(R.id.sideLogo);
        wait1 = findViewById(R.id.wait1);
        wait2 = findViewById(R.id.wait2);
        progressBar = findViewById(R.id.WanderingCubes);
        WanderingCubes wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        if(user == null)
        {
            greetings.setText("Hi, Anonymous!");
        }
        else if(user != null)
        {
            for(UserInfo profile : user.getProviderData()){
                String providerId = profile.getProviderId();

                String uid = profile.getUid();

                String name = user.getDisplayName();
                String[] split2 = name.split(" ", 2);
                greetings.setText(String.format("Hey, %s!", split2[0]));
            }
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(MenuActivity.this))
                {
                    menuloader();
                }
                else
                {
                    culprit.setVisibility(View.GONE);
                    wait1.setVisibility(View.GONE);
                    wait2.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MenuActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(MenuActivity.this)){
                    swipeRefreshLayout.setRefreshing(false);
                    menuloader();
                }
                else {
                    culprit.setVisibility(View.GONE);
                    wait1.setVisibility(View.GONE);
                    wait2.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MenuActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void menuloader() {
        List<Category> categories;

        swipeRefreshLayout.setRefreshing(false);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder viewHolder, int position, @NonNull final Category model) {
                viewHolder.setMenuName(model.getName());
                viewHolder.setMenuImage(getApplicationContext(), model.getImage());
                viewHolder.setDiscount(model.getDiscount());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(MenuActivity.this, FoodListActivity.class);
                        Common.currentCategory = model;
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_category, viewGroup, false);
                return new MenuViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                if(progressBar != null && wait1 != null && wait2 != null){
                    progressBar.setVisibility(View.GONE);
                    wait1.setVisibility(View.GONE);
                    wait2.setVisibility(View.GONE);
                }
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();  //show updated data
        recycler_menu.setAdapter(adapter);
        culprit.setVisibility(View.VISIBLE);
    }

    public interface ItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;

        private ItemClickListener itemClickListener;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            itemView.setOnClickListener(this);
        }

        public void setMenuName(String menuName){
            TextView txtMenuName = view.findViewById(R.id.menu_name);
            txtMenuName.setText(menuName);
        }

        public void setMenuImage(Context context, String menuImage){
            ImageView MenuImage = view.findViewById(R.id.menu_img);
            Picasso.get().load(menuImage).into(MenuImage);
        }

        public void setDiscount(String discount){
            TextView Discount = view.findViewById(R.id.discount);
            Discount.setText(discount);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
        {
            adapter.startListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
