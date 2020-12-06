package com.application.safebox;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

public class LoginActivity extends Activity {
    private Button submit;                              //Submit button
    private EditText email, password;                   //Email, password text fields
    private String useremail, userpassword = "";        //Email and password parameters
    private ImageView img;
    private boolean validUser;

    private void switchContext(){
        validUser = true;
        final Intent intent = new Intent(this, DeviceScanActivity.class);
        img.setImageResource(R.drawable.unlock_icon);
        startActivity(intent);
    }

    //Print text to user
    private void prompt(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        submit = findViewById(R.id.submit);             //Submit button
        email = findViewById(R.id.emailinput);          //Email text field
        password = findViewById(R.id.passwordinput);    //Password text field
        img = findViewById(R.id.loginicon);
        img.setImageResource(R.drawable.login_icon);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validUser = true;
                useremail = email.getText().toString();
                userpassword = password.getText().toString();

                //Initialize Amplify application and Cognito Auth plugin
                try {
                    Amplify.addPlugin(new AWSCognitoAuthPlugin());
                    Amplify.configure(getApplicationContext());
                    Log.i("MyAmplifyApp", "Initialized Amplify");
                } catch (AmplifyException error){
                    error.printStackTrace();
                }


                Amplify.Auth.signIn(
                        useremail,
                        userpassword,
                        result -> switchContext(),
                        error -> img.setImageResource(R.drawable.locked_icon)
                );
            }
        });
    }
}
