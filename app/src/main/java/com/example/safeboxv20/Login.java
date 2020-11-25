package com.example.safeboxv20;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends Activity {
    private Button submit;              //Submit button
    private EditText email, password;   //Email, password parameters
    private String useremail = "", userpassword = "";
    private ImageView img;

    public boolean authenticateUser(String user, String password){
        return true;
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
                boolean validUser = false;
                useremail = email.getText().toString();
                userpassword = password.getText().toString();

                validUser = authenticateUser(useremail, userpassword);
                if (validUser) {
                    img.setImageResource(R.drawable.unlock_icon);
                    startActivity(intent);
                } else prompt("Invalid password or email. Please try again.");
            }
        });
    }

    private void prompt(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
