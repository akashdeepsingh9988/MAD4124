package com.mad4124.mad4124project;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    String TAG = "Akashdeep";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            Toast.makeText(this,"User already logged in!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), auth.getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();


        }
        else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, UserHome.class));
            }
            else {

                if (response == null) {
                    Log.d(TAG, "User cancelled the signin process");
                }
                else {
                    Log.d(TAG,"an error occurred during login");
                    Log.d(TAG, response.getError().getMessage());
                }
            }
        }
    }

    public void loginpressed(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            Toast.makeText(this,"User already logged in!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), auth.getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
          //  Toast.makeText(getApplicationContext(), auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();


        }
        else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

}
