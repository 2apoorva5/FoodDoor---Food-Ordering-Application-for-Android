package com.hospitality.fooddoor.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.hospitality.fooddoor.model.Orders;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "fooddoorlocal.db";
    private static final int DATABASE_VERSION = 1;

    private static final String FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS Favorites" + "(FoodId TEXT NOT NULL, UserEmail TEXT NOT NULL, PRIMARY KEY(FoodId, UserEmail));";

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL(FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.setForcedUpgrade(newVersion);
        super.onUpgrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public boolean isInCarts(String foodId, String userEmail)
    {
        boolean flag = false;
        SQLiteDatabase DB = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetails WHERE UserEmail='%s' AND FoodId='%s'", userEmail, foodId);
        cursor = DB.rawQuery(SQLQuery, null);
        if(cursor.getCount()>0)
        {
            flag = true;
        }
        else
        {
            flag = false;
        }

        cursor.close();
        return flag;
    }

    public List<Orders> getCarts(String userEmail){
        SQLiteDatabase DB = getReadableDatabase();
        SQLiteQueryBuilder QB = new SQLiteQueryBuilder();

        String[] SQL_SELECT = { "UserEmail", "FoodId", "FoodName", "Quantity", "Price", "Discount", "FoodImg", "MealType" };
        String SQL_TABLE = "OrderDetails";

        QB.setTables(SQL_TABLE);
        Cursor cursor = QB.query(DB, SQL_SELECT, "UserEmail=?", new String[]{userEmail}, null, null, null);

        final List<Orders> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(new Orders(
                        cursor.getString(cursor.getColumnIndex("UserEmail")),
                        cursor.getString(cursor.getColumnIndex("FoodId")),
                        cursor.getString(cursor.getColumnIndex("FoodName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount")),
                        cursor.getString(cursor.getColumnIndex("FoodImg")),
                        cursor.getString(cursor.getColumnIndex("MealType"))));
            }while (cursor.moveToNext());
        }
        return result;
    }

    public void addToCart(Orders orders)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetails(UserEmail, FoodId, FoodName, Quantity, Price, Discount, FoodImg, MealType) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                orders.getUserEmail(),
                orders.getFoodId(),
                orders.getFoodName(),
                orders.getQuantity(),
                orders.getPrice(),
                orders.getDiscount(),
                orders.getFoodImg(),
                orders.getMealType());
        DB.execSQL(query);
    }

    public void cleanCart(String userEmail)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetails WHERE UserEmail='%s'", userEmail);
        DB.execSQL(query);
    }

    public int getCountCart(String userEmail)
    {
        int count = 0;

        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetails WHERE UserEmail='%s'", userEmail);
        Cursor cursor = DB.rawQuery(query, null);
        if(cursor.moveToFirst())
        {
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Orders orders) {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("UPDATE OrderDetails SET Quantity= '%s' WHERE UserEmail = '%s' AND FoodId='%s'", orders.getQuantity(), orders.getUserEmail(), orders.getFoodId());
        DB.execSQL(query);
    }

    public void increaseCart(String userEmail, String foodId)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("UPDATE OrderDetails SET Quantity= Quantity+1 WHERE UserEmail = '%s' AND FoodId='%s'", userEmail, foodId);
        DB.execSQL(query);
    }

    public void addToFavourites(String foodId, String userEmail)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId, UserEmail) VALUES('%s','%s');", foodId, userEmail);
        DB.execSQL(query);
    }

    public void removeFromFavourites(String foodId, String userEmail)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s' and UserEmail='%s';", foodId, userEmail);
        DB.execSQL(query);
    }

    public boolean isInFavourites(String foodId, String userEmail)
    {
        SQLiteDatabase DB = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s' and UserEmail='%s';", foodId, userEmail);
        Cursor cursor = DB.rawQuery(query, null);
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
