package com.example.safeboxv20;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
    private Button submit;              //Submit button
    private EditText email, password;   //Email, password parameters
    private String useremail = "", userpassword = "";
    public void authenticateUser(){
        // ...
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final Intent intent = new Intent(this, DeviceScanActivity.class);

        submit = findViewById(R.id.submit);
        email = findViewById(R.id.emailinput);
        password = findViewById(R.id.passwordinput);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                useremail = email.getText().toString();
                userpassword = password.getText().toString();
                    startActivity(intent);
            }
        });
    }
}
