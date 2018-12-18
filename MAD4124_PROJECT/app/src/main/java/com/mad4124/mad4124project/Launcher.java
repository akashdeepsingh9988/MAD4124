package com.mad4124.mad4124project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Loading loading = new Loading();
        loading.start();
    }

    class Loading extends Thread
    {
        public void run()
        {
            try
            {
                Thread.sleep(2000);

                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), UserHome.class));
                }

                else
                {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginScreen.class));
                }

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

    }
}
