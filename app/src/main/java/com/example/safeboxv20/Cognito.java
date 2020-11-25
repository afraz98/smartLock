package com.example.safeboxv20;
import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class Cognito {
    private static String userPoolId = "us-west-2_PTBuogrDY";
    private static String clientId = "5sodoburchn7kccl19raggb8sb";
    private static String clientSecret = "1civolcbi8i9tun7krib26ks3qrjff1665a5f4jkcqsopcnd1e49";
    private static Regions cognitoRegion = Regions.US_WEST_2;
    private static Context cxt;

    public static CognitoUserPool getUserPool(){
        return new CognitoUserPool(cxt, userPoolId, clientId, clientSecret, cognitoRegion);
    }
}
