package com.hospitality.fooddoor.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hospitality.fooddoor.common.Common;
import com.hospitality.fooddoor.model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if(user != null)
        {
            updateTokenToFirebase(tokenRefreshed);
        }
        else
        {
            return;
        }
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token token = new Token(tokenRefreshed, false);       //false because this token has been sent by client app
        tokens.child(user.getUid()).setValue(token);
    }
}
