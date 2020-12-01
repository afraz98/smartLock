package com.example.safeboxv20;
import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class Cognito {
    private String userPoolId = "us-west-2_PTBuogrDY";
    private String clientId = "5sodoburchn7kccl19raggb8sb";
    private String clientSecret = "1civolcbi8i9tun7krib26ks3qrjff1665a5f4jkcqsopcnd1e49";
    private Context context;

    public Cognito(Context context){
        this.context = context;
    }

    public CognitoUserPool getUserPool(){
        return new CognitoUserPool(context, userPoolId, clientId, clientSecret, Regions.US_WEST_2);
    }
}
