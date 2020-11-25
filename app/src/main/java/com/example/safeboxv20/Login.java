package com.example.safeboxv20;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class Login extends Activity {
    private Button submit;              //Submit button
    private EditText email, password;   //Email, password parameters
    private String useremail = "", userpassword = "";
    private ImageView img;
    private boolean validUser;

    final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession session, CognitoDevice device){ validUser = true; }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            AuthenticationDetails d = new AuthenticationDetails(userId, userpassword, null);
            authenticationContinuation.setAuthenticationDetails(d);
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) { }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) { }

        @Override
        public void onFailure(Exception exception) { prompt("Invalid password or email. Please try again."); }
    };

    private void authenticateUser(){
        CognitoUser u = Cognito.getUserPool().getUser(useremail);
        u.getSessionInBackground(authenticationHandler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final Intent intent = new Intent(this, DeviceScanActivity.class);

        submit = findViewById(R.id.submit);
        email = findViewById(R.id.emailinput);
        password = findViewById(R.id.passwordinput);
        img = findViewById(R.id.loginicon);
        img.setImageResource(R.drawable.login_icon);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validUser = false;
                useremail = email.getText().toString();
                userpassword = password.getText().toString();

                authenticateUser();
                if (validUser) {
                    img.setImageResource(R.drawable.unlock_icon);
                    startActivity(intent);
                }
            }
        });
    }

    private void prompt(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
