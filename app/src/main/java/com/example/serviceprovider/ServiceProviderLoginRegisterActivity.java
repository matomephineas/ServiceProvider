package com.example.serviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServiceProviderLoginRegisterActivity extends AppCompatActivity {

    RelativeLayout relative;
    Button btnDriverLogin,btnDriverRegister;
    TextView tvCreateAccount,tvLogin,tvLoginDriver;
    EditText editTextServiceProviderEmailAddress,editServiceProviderTextPassword;
    private String email,password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private DatabaseReference serviceProviderDatabaseRef;
    private String onlineServiceProviderID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_login_register);
        btnDriverLogin =findViewById(R.id.btnDriverLogin);
        btnDriverRegister =findViewById(R.id.btnDriverRegister);
        tvCreateAccount=findViewById(R.id.tvCreateAccount);
        tvLoginDriver =findViewById(R.id.tvLoginDriver);
        relative =findViewById(R.id.relative);
        editServiceProviderTextPassword=findViewById(R.id.editServiceProviderTextPassword);
        editTextServiceProviderEmailAddress=findViewById(R.id.editTextServiceProviderEmailAddress);
        tvLogin =findViewById(R.id.tvLogin);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);


    }


    public void ServiceProviderRegisterLink(View view) {
        btnDriverLogin.setVisibility(View.INVISIBLE);
        tvCreateAccount.setVisibility(View.INVISIBLE);
        btnDriverRegister.setVisibility(View.VISIBLE);
        tvLoginDriver.setVisibility(View.VISIBLE);
        tvLogin.setText("Register Service Provider");
        btnDriverRegister.setEnabled(true);

    }
    public void ServiceProviderSendDriverToLogin(View view)
    {
        btnDriverLogin.setVisibility(View.VISIBLE);
        tvCreateAccount.setVisibility(View.VISIBLE);
        btnDriverRegister.setVisibility(View.INVISIBLE);
        tvLoginDriver.setVisibility(View.INVISIBLE);
        tvLogin.setText("Login Driver");
        btnDriverLogin.setEnabled(true);
    }

    public void RegisterServiceProvider(View view)
    {
        email = editTextServiceProviderEmailAddress.getText().toString().trim();
        password =editServiceProviderTextPassword.getText().toString().trim();

        Register(email,password);
    }

    private void Register(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            editTextServiceProviderEmailAddress.setError("Field cannot be empty");
            editTextServiceProviderEmailAddress.requestFocus();
        }
        if(TextUtils.isEmpty(password))
        {
            editTextServiceProviderEmailAddress.setError("Field cannot be empty");
            editTextServiceProviderEmailAddress.requestFocus();
        }
        else
        {
            dialog.setTitle("Register Service provider");
            dialog.setMessage("Please wait while checking credentials.......");
             dialog.show();
          mAuth.createUserWithEmailAndPassword(email,password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task)
                      {
                        if(task.isSuccessful())
                        {
                            onlineServiceProviderID =mAuth.getCurrentUser().getUid();
                            serviceProviderDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProviders").child(onlineServiceProviderID);

                            serviceProviderDatabaseRef.setValue(true);
                            Intent driverIntent = new Intent(getApplicationContext(), ServiceProviderMainActivity.class);
                            startActivity(driverIntent);
                            Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                          dialog.dismiss();
                      }
                  });
        }
    }
    private boolean validateEmail() {
        email = editTextServiceProviderEmailAddress.getText().toString().trim();
        if (email.isEmpty()) {
            editTextServiceProviderEmailAddress.setError("filed must not be empty");
            editTextServiceProviderEmailAddress.requestFocus();
            return false;
        } else {
            editTextServiceProviderEmailAddress.setError(null);
            editTextServiceProviderEmailAddress.requestFocus();
            return true;
        }
    }
    private boolean validatePassword() {
        password = editServiceProviderTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            editServiceProviderTextPassword.setError("filed must not be empty");
            editServiceProviderTextPassword.requestFocus();
            return false;
        } else {
            editServiceProviderTextPassword.setError(null);
            editServiceProviderTextPassword.requestFocus();
            return true;
        }
    }
    public void LoginDriver(View view)
    {
        if(!validateEmail() | !validatePassword()){
            return;
        }
        else
        {
            Login(email,password);
        }

    }

    private void Login(String email, String password)
    {
        dialog.setTitle("Login");
        dialog.setMessage("Please wait while checking credentials....");
        dialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {

                            Snackbar.make(relative,"Login Successful",Snackbar.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ServiceProviderMainActivity.class);

                            startActivity(intent);
                            dialog.dismiss();
                        }
                        else
                        {
                            Snackbar.make(relative,"Login failed: "+task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                    }
                });
    }

}