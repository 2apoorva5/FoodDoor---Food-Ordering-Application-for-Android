package com.hospitality.fooddoor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.textfield.TextInputLayout;
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
import com.hospitality.fooddoor.model.MyResponse;
import com.hospitality.fooddoor.model.Notification;
import com.hospitality.fooddoor.model.OrderRequests;
import com.hospitality.fooddoor.model.Orders;
import com.hospitality.fooddoor.model.Sender;
import com.hospitality.fooddoor.model.Token;
import com.hospitality.fooddoor.remote.APIService;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerCart;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference request;

    APIService mService;

    public TextView total_amount, delivery_charges, net_payable_amount;
    public Button keepAdding, proceed;

    List<Orders> cart = new ArrayList<>();
    public CartAdapter adapter;

    private Dialog dialog;
    private ImageView arrowBack;
    private TextView txtEmail;
    private TextInputLayout txtInputName, txtInputFlat, txtInputLocality, txtInputLandmark, txtInputPin, txtInputPhone;
    private EditText txtName, txtFlat, txtLocality, txtLandmark, txtPin, txtPhone;
    private Spinner states, cities;
    private Button saveAddress;

    ArrayAdapter<CharSequence> statelist;
    ArrayAdapter<CharSequence> citylist;
    ArrayAdapter<CharSequence> punjablist;
    ArrayAdapter<CharSequence> jharkhandlist;
    ArrayAdapter<CharSequence> chhattisgarhlist;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.cart_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ForCartTitle);
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

        mService = Common.getFCMService();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerCart = findViewById(R.id.listCart);
        recyclerCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);

        total_amount = findViewById(R.id.total_amount);
        delivery_charges = findViewById(R.id.delivery_charges);
        net_payable_amount = findViewById(R.id.net_payable);
        keepAdding = findViewById(R.id.keepAdding);
        proceed = findViewById(R.id.cartProceed);

        View view = getLayoutInflater().inflate(R.layout.activity_address, null);
        dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(view);
        arrowBack = view.findViewById(R.id.arrow_back);

        txtEmail = view.findViewById(R.id.txtEmail);

        txtInputName = view.findViewById(R.id.txt_input_name);
        txtInputFlat = view.findViewById(R.id.txt_input_flat);
        txtInputLocality = view.findViewById(R.id.txt_input_locality);
        txtInputLandmark = view.findViewById(R.id.txt_input_landmark);
        txtInputPin = view.findViewById(R.id.txt_input_pin);
        txtInputPhone = view.findViewById(R.id.txt_input_mobile);
        txtName = view.findViewById(R.id.txt_name);
        txtFlat = view.findViewById(R.id.txt_flat);
        txtLocality = view.findViewById(R.id.txt_locality);
        txtLandmark = view.findViewById(R.id.txt_landmark);
        txtPin = view.findViewById(R.id.txt_pin);
        txtPhone = view.findViewById(R.id.txt_mobile);
        states = view.findViewById(R.id.states);
        cities = view.findViewById(R.id.cities);
        saveAddress = view.findViewById(R.id.saveAddress);

        txtPhone.addTextChangedListener(AddressTextWatcher);

        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());
        txtEmail.setVisibility(View.GONE);

        statelist = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        citylist = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        punjablist = ArrayAdapter.createFromResource(this, R.array.punjab_cities, android.R.layout.simple_spinner_item);
        jharkhandlist = ArrayAdapter.createFromResource(this, R.array.jharkhand_cities, android.R.layout.simple_spinner_item);
        chhattisgarhlist = ArrayAdapter.createFromResource(this, R.array.chhattisgarh_cities, android.R.layout.simple_spinner_item);

        statelist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citylist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        punjablist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jharkhandlist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chhattisgarhlist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        states.setAdapter(statelist);

        states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choosestate = parent.getItemAtPosition(position).toString();
                if(choosestate.equals("Select State*"))
                {
                    cities.setAdapter(citylist);
                }
                else if(choosestate.equals("Punjab"))
                {
                    cities.setAdapter(punjablist);
                }
                else if(choosestate.equals("Jharkhand"))
                {
                    cities.setAdapter(jharkhandlist);
                }
                else if(choosestate.equals("Chhattisgarh"))
                {
                    cities.setAdapter(chhattisgarhlist);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        keepAdding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    List<Orders> order = new ArrayList<>();
                    order = new Database(CartActivity.this).getCarts(user.getEmail());
                    OrderRequests orderRequests = new OrderRequests(
                            txtName.getText().toString().trim(),
                            user.getEmail(),
                            txtFlat.getText().toString().trim() + ", " +
                                    txtLocality.getText().toString().trim() + ", " +
                                    txtLandmark.getText().toString().trim() + ", " +
                                    txtPin.getText().toString().trim() + ", " +
                                    cities.getSelectedItem().toString().trim() + ", " +
                                    states.getSelectedItem().toString().trim(),
                            txtPhone.getText().toString().trim(),
                            net_payable_amount.getText().toString().trim(),
                            order
                    );

                    String order_number = String.valueOf(System.currentTimeMillis());
                    request.child(order_number)
                            .setValue(orderRequests);
                    new Database(getBaseContext()).cleanCart(user.getEmail());
                    sendNotificationOrder(order_number);
                    finish();
                }
                else {
                    Toast.makeText(CartActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.black);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadCart();
                }
                else {
                    total_amount.setVisibility(View.INVISIBLE);
                    delivery_charges.setVisibility(View.INVISIBLE);
                    net_payable_amount.setVisibility(View.INVISIBLE);
                    proceed.setEnabled(false);
                    proceed.setBackgroundResource(R.drawable.round_layout6);
                    Toast.makeText(CartActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                {
                    loadCart();

                    swipeRefreshLayout.setRefreshing(false);
                    total_amount.setVisibility(View.VISIBLE);
                    delivery_charges.setVisibility(View.VISIBLE);
                    net_payable_amount.setVisibility(View.VISIBLE);
                    proceed.setEnabled(true);
                    proceed.setBackgroundResource(R.drawable.round_layout1);

                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.show();
                        }
                    });
                }
                else
                {
                    recyclerCart.setVisibility(View.GONE);
                    total_amount.setVisibility(View.INVISIBLE);
                    delivery_charges.setVisibility(View.INVISIBLE);
                    net_payable_amount.setVisibility(View.INVISIBLE);
                    proceed.setEnabled(false);
                    proceed.setBackgroundResource(R.drawable.round_layout6);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(CartActivity.this, "Please check your Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);               //gets all node with isServerToken true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    //create raw payload to send
                    Notification notification = new Notification("Food@Door", "There's a New Order : " + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success == 1)
                                        {
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(CartActivity.this, "Action Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private TextWatcher AddressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String flat = txtFlat.getText().toString().trim();
            String locality = txtLocality.getText().toString().trim();
            String state = states.getSelectedItem().toString().trim();
            String city = cities.getSelectedItem().toString().trim();
            String pin = txtPin.getText().toString().trim();
            String phone = txtPhone.getText().toString().trim();

            if(!name.isEmpty() && !email.isEmpty() && !flat.isEmpty() && !locality.isEmpty() && !pin.isEmpty() && !phone.isEmpty()){
                saveAddress.setEnabled(true);
                saveAddress.setBackgroundResource(R.drawable.round_layout1);
            }
            else {
                saveAddress.setEnabled(false);
                saveAddress.setBackgroundResource(R.drawable.round_layout6);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void loadCart() {
        swipeRefreshLayout.setRefreshing(false);
        cart = new Database(this).getCarts(user.getEmail());
        adapter = new CartAdapter(cart, this);
        recyclerCart.setAdapter(adapter);
        recyclerCart.setVisibility(View.VISIBLE);

        int total = 0;
        for(Orders orders : cart){
            total += (Integer.parseInt(orders.getPrice()))*(Integer.parseInt(orders.getQuantity()));
        }

        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total_amount.setText(fmt.format(total));

        int delivery1 = 80;
        int delivery2 = 60;
        int delivery3 = 40;
        int delivery4 = 0;
        String free_delivery = "FREE DELIVERY";

        if(total == 0){
            delivery_charges.setText(fmt.format(delivery4));
            net_payable_amount.setText(fmt.format(total));
            proceed.setEnabled(false);
            proceed.setBackgroundResource(R.drawable.round_layout6);
        }
        if(total > 0 && total <= 100){
            delivery_charges.setText(fmt.format(delivery1));
            net_payable_amount.setText(fmt.format(total + delivery1));
            proceed.setEnabled(true);
            proceed.setBackgroundResource(R.drawable.round_layout1);
        }
        if(total > 100 && total <= 250){
            delivery_charges.setText(fmt.format(delivery2));
            net_payable_amount.setText(fmt.format(total + delivery2));
            proceed.setEnabled(true);
            proceed.setBackgroundResource(R.drawable.round_layout1);
        }

        if(total > 200 && total <= 500){
            delivery_charges.setText(fmt.format(delivery3));
            net_payable_amount.setText(fmt.format(total + delivery3));
            proceed.setEnabled(true);
            proceed.setBackgroundResource(R.drawable.round_layout1);
        }

        if(total > 500){
            delivery_charges.setText(free_delivery);
            net_payable_amount.setText(fmt.format(total));
            proceed.setEnabled(true);
            proceed.setBackgroundResource(R.drawable.round_layout1);
        }

        adapter.notifyDataSetChanged();
    }

    public interface ItemClickListener{
        void onClick(View view, int position, boolean isLongClick);
    }


    public static class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;

        TextView cartFoodName, cartPrice, cartTotalPrice, cartDiscount, quantity, deleteItem;
        ElegantNumberButton cartQuantity;
        ImageView cartImage, cartMealType;

        private ItemClickListener itemClickListener;

        public void setCartFoodName(TextView cartFoodName) {
            this.cartFoodName = cartFoodName;
        }

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            cartFoodName = view.findViewById(R.id.cart_food_name);
            cartDiscount = view.findViewById(R.id.cart_discount);
            cartQuantity = view.findViewById(R.id.cart_quantity);
            quantity = view.findViewById(R.id.quantity);
            cartPrice = view.findViewById(R.id.cart_price);
            cartTotalPrice = view.findViewById(R.id.cart_total_price);
            deleteItem = view.findViewById(R.id.deleteCartItem);

            cartImage = view.findViewById(R.id.cart_image);
            cartMealType = view.findViewById(R.id.cart_meal_type);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

        private List<Orders> listData = new ArrayList<>();
        private CartActivity cartActivity;

        public CartAdapter(List<Orders> listData, CartActivity cartActivity) {
            this.listData = listData;
            this.cartActivity = cartActivity;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(cartActivity);
            View itemView = inflater.inflate(R.layout.cart_layout, viewGroup, false);
            return new CartViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int i) {

            Picasso.get().load(listData.get(i).getFoodImg())
                    .resize(130, 130)
                    .centerCrop()
                    .into(cartViewHolder.cartImage);

            Picasso.get().load(listData.get(i).getMealType())
                    .resize(20, 20)
                    .centerCrop()
                    .into(cartViewHolder.cartMealType);

            String cart_discount = listData.get(i).getDiscount();
            cartViewHolder.cartDiscount.setText(cart_discount);

            cartViewHolder.cartQuantity.setNumber(listData.get(i).getQuantity());
            cartViewHolder.cartQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    Orders orders = listData.get(i);
                    orders.setQuantity(String.valueOf(newValue));
                    new Database(cartActivity).updateCart(orders);

                    int total = 0;
                    List<Orders> order = new Database(cartActivity).getCarts(user.getEmail());
                    for(Orders item : order){
                        total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    }

                    Locale locale = new Locale("en", "IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    cartActivity.total_amount.setText(fmt.format(total));

                    int delivery1 = 80;
                    int delivery2 = 60;
                    int delivery3 = 40;
                    int delivery4 = 0;
                    String free_delivery = "FREE DELIVERY";

                    if(total == 0){
                        cartActivity.delivery_charges.setText(fmt.format(delivery4));
                        cartActivity.net_payable_amount.setText(fmt.format(total));
                        cartActivity.proceed.setEnabled(false);
                        cartActivity.proceed.setBackgroundResource(R.drawable.round_layout6);
                    }
                    if(total > 0 && total <= 100){
                        cartActivity.delivery_charges.setText(fmt.format(delivery1));
                        cartActivity.net_payable_amount.setText(fmt.format(total + delivery1));
                        cartActivity.proceed.setEnabled(true);
                        cartActivity.proceed.setBackgroundResource(R.drawable.round_layout1);
                    }
                    if(total > 100 && total <= 250){
                        cartActivity.delivery_charges.setText(fmt.format(delivery2));
                        cartActivity.net_payable_amount.setText(fmt.format(total + delivery2));
                        cartActivity.proceed.setEnabled(true);
                        cartActivity.proceed.setBackgroundResource(R.drawable.round_layout1);
                    }

                    if(total > 200 && total <= 500){
                        cartActivity.delivery_charges.setText(fmt.format(delivery3));
                        cartActivity.net_payable_amount.setText(fmt.format(total + delivery3));
                        cartActivity.proceed.setEnabled(true);
                        cartActivity.proceed.setBackgroundResource(R.drawable.round_layout1);
                    }

                    if(total > 500){
                        cartActivity.delivery_charges.setText(free_delivery);
                        cartActivity.net_payable_amount.setText(fmt.format(total));
                        cartActivity.proceed.setEnabled(true);
                        cartActivity.proceed.setBackgroundResource(R.drawable.round_layout1);
                    }

                    adapter.notifyDataSetChanged();

                    String count = cartViewHolder.cartQuantity.getNumber().toString().trim();
                    cartViewHolder.quantity.setText(count);

                    Locale locale1 = new Locale("en", "IN");
                    NumberFormat fmt1 = NumberFormat.getCurrencyInstance(locale1);
                    int price = Integer.parseInt(listData.get(i).getPrice());
                    int total_price = (Integer.parseInt(listData.get(i).getPrice()))*(Integer.parseInt(listData.get(i).getQuantity()));
                    cartViewHolder.cartPrice.setText(fmt1.format(price));
                    cartViewHolder.cartTotalPrice.setText(fmt1.format(total_price));
                }
            });

            String count = cartViewHolder.cartQuantity.getNumber().toString().trim();
            cartViewHolder.quantity.setText(count);

            Locale locale1 = new Locale("en", "IN");
            NumberFormat fmt1 = NumberFormat.getCurrencyInstance(locale1);
            int price = Integer.parseInt(listData.get(i).getPrice());
            int total_price = (Integer.parseInt(listData.get(i).getPrice()))*(Integer.parseInt(listData.get(i).getQuantity()));
            cartViewHolder.cartPrice.setText(fmt1.format(price));
            cartViewHolder.cartTotalPrice.setText(fmt1.format(total_price));
            cartViewHolder.cartFoodName.setText(listData.get(i).getFoodName());
            cartViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCart(cartViewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }
    }

    private void deleteCart(final int adapterPosition) {
        android.app.AlertDialog.Builder sure = new android.app.AlertDialog.Builder(CartActivity.this);
        sure.setMessage("Are you sure you want to Remove this Item?");
        sure.setCancelable(false);
        sure.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cart.remove(adapterPosition);
                new Database(CartActivity.this).cleanCart(user.getEmail());
                for(Orders item : cart)
                {
                    new Database(CartActivity.this).addToCart(item);
                }

                loadCart();
            }
        });

        sure.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = sure.create();
        alertDialog.show();
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
        if(dialog.isShowing())
        {
            dialog.dismiss();
        }
        else {
            super.onBackPressed();
        }
    }
}
