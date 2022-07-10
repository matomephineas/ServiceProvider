package com.example.serviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

public class AvailableServicesActivity extends AppCompatActivity {

    Animation smalltobig, fromlefttoright, fleft,fhelper;
    EditText txtYourAddressLocation;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_services);
        txtYourAddressLocation = findViewById(R.id.txtYourAddressLocation);
        address =  getIntent().getStringExtra("address");

        smalltobig = AnimationUtils.loadAnimation(this,R.anim.smalltobig);
        fromlefttoright = AnimationUtils.loadAnimation(this,R.anim.fromlefttoright);
        fleft = AnimationUtils.loadAnimation(this,R.anim.fleft);
        fhelper = AnimationUtils.loadAnimation(this,R.anim.fhelper);

        txtYourAddressLocation.setText(address);
    }
}