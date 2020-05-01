package com.hospitality.fooddoor;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.model.OrderRequests;

public class OrderStatusActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerOrder;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference request;

    FirebaseRecyclerAdapter<OrderRequests, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        Toolbar toolbar = findViewById(R.id.order_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForOrdersTitle);
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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Order Requests");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerOrder = findViewById(R.id.recycler_orders);
        recyclerOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerOrder.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadOrdersList(user.getEmail());
                }
                else {
                    Toast.makeText(OrderStatusActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                {
                    swipeRefreshLayout.setRefreshing(false);
                    loadOrdersList(user.getEmail());
                }
                else
                {
                    recyclerOrder.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(OrderStatusActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadOrdersList(String useremail) {

        swipeRefreshLayout.setRefreshing(false);
        Query getOrderByUser = request.orderByChild("email").equalTo(useremail);
        FirebaseRecyclerOptions<OrderRequests> orderOptions = new FirebaseRecyclerOptions.Builder<OrderRequests>()
                .setQuery(getOrderByUser, OrderRequests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<OrderRequests, OrderViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull OrderRequests model) {
                viewHolder.setOrderID(adapter.getRef(position).getKey());
                viewHolder.setOrderStatus(convertCodeToStatus(model.getStatus()));
                viewHolder.setOrderPrice(model.getTotal());
                viewHolder.setOrderMobile(model.getPhone());
                viewHolder.setOrderAddress(model.getAddress());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.orders_layout, viewGroup, false);
                return new OrderViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerOrder.setAdapter(adapter);
        recyclerOrder.setVisibility(View.VISIBLE);
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "PLACED";
        }
        else if(status.equals("1")){
            return "COOKING";
        }
        else if(status.equals("2")) {
            return "ON THE WAY";
        }
        else {
            return "DELIVERED";
        }

    }

    public interface ItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        TextView orderID, orderStatus, orderPrice, orderMobile, orderAddress;
        private ItemClickListener itemClickListener;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            orderID = view.findViewById(R.id.orderID);
            orderStatus = view.findViewById(R.id.order_status);
            orderPrice = view.findViewById(R.id.order_price);
            orderMobile = view.findViewById(R.id.order_mobile);
            orderAddress = view.findViewById(R.id.order_address);
        }

        public void setOrderID(String ID){
            orderID.setText(ID);
        }

        public void setOrderStatus(String status){
            orderStatus.setText(status);
        }

        public void setOrderPrice(String price){
            orderPrice.setText(price);
        }

        public void setOrderMobile(String mobile){
            orderMobile.setText(mobile);
        }

        public void setOrderAddress(String address){
            orderAddress.setText(address);
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
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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
