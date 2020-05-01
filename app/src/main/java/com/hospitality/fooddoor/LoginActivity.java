package com.hospitality.fooddoor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hospitality.fooddoor.common.Common;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference users;

    //extra variables
    private static final int RC_SIGN_IN = 123;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_signup);

        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

//        printkeyhash();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        users = FirebaseDatabase.getInstance().getReference("Users");

        View view = getLayoutInflater().inflate(R.layout.no_internet_connection1, null);
        dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(view);

        if(Common.isConnectedToInternet(LoginActivity.this)){
            if(firebaseUser != null){
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else {
                Authenticate();
            }
        }
        else {
            dialog.show();
            return;
        }
    }
//
//    private void printkeyhash() {
//        try{
//            PackageInfo info = getPackageManager().getPackageInfo("com.hospitality.fooddoor", PackageManager.GET_SIGNATURES);
//            for(Signature signature:info.signatures)
//            {
//                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
//                messageDigest.update(signature.toByteArray());
//                Log.e("KEYHASH", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }

    private void Authenticate(){

        AuthMethodPickerLayout methodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.signin_methods)
                .setEmailButtonId(R.id.emailLogin)
                .setGoogleButtonId(R.id.googleLogin)
                .setFacebookButtonId(R.id.facebookLogin)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false)
                        .setAuthMethodPickerLayout(methodPickerLayout)
                        .setTheme(R.style.ForLogin)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                Intent intent2 = new Intent(LoginActivity.this, HomeActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                finish();
            }
            else {
                if(response == null){
                    onBackPressed();
                    return;
                }

                if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    Toast.makeText(LoginActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void setWindowFlag(LoginActivity loginActivity, final int bits, boolean on) {
        Window window = loginActivity.getWindow();
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
            LoginActivity.super.onBackPressed();
            LoginActivity.this.finish();
    }
}
