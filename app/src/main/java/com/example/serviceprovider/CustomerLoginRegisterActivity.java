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

public class CustomerLoginRegisterActivity extends AppCompatActivity {
    Button btnCustomerLogin,btnCustomerRegister;
    TextView tvCreateAccount,tvLogin,tvLoginDriver;
    EditText editTextCustomerEmailAddress,editCustomerTextPassword;
    private String email,password;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private RelativeLayout relative;
    private DatabaseReference customerDatabaseRef;
    private String onlineCustomerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);
        btnCustomerLogin =findViewById(R.id.btnCustomerLogin);
        btnCustomerRegister =findViewById(R.id.btnCustomerRegister);
        tvCreateAccount=findViewById(R.id.tvCustomerCreateAccount);
        tvLoginDriver =findViewById(R.id.tvLoginCustomer);
        tvLogin =findViewById(R.id.tvCustomerLogin);
        editCustomerTextPassword=findViewById(R.id.editCustomerTextPassword);
        editTextCustomerEmailAddress =findViewById(R.id.editTextCustomerEmailAddress);
        relative =findViewById(R.id.relative);
        dialog =new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

    }
    private boolean validateEmail() {
        email = editTextCustomerEmailAddress.getText().toString().trim();
        if (email.isEmpty()) {
            editTextCustomerEmailAddress.setError("filed must not be empty");
            editTextCustomerEmailAddress.requestFocus();
            return false;
        } else {
            editTextCustomerEmailAddress.setError(null);
            editTextCustomerEmailAddress.requestFocus();
            return true;
        }
    }
    private boolean validatePassword() {
        password = editCustomerTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            editCustomerTextPassword.setError("filed must not be empty");
            editCustomerTextPassword.requestFocus();
            return false;
        } else {
            editCustomerTextPassword.setError(null);
            editCustomerTextPassword.requestFocus();
            return true;
        }
    }
    public void CustomerLogin(View view)
    {
        if(!validateEmail() | !validatePassword())
        {
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
                            Intent intent = new Intent(getApplicationContext(), CustomersMainActivity.class);
                            startActivity(intent);
                            Snackbar.make(relative,"Login Successful",Snackbar.LENGTH_LONG).show();

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
    public void CusterRegister(View view)
    {
        btnCustomerLogin.setVisibility(View.INVISIBLE);
        tvCreateAccount.setVisibility(View.INVISIBLE);
        btnCustomerRegister.setVisibility(View.VISIBLE);
        tvLoginDriver.setVisibility(View.VISIBLE);
        tvLogin.setText("Register Customer");
        btnCustomerRegister.setEnabled(true);
    }


    public void CusterSendCusterToLogin(View view)
    {
        btnCustomerLogin.setVisibility(View.VISIBLE);
        tvCreateAccount.setVisibility(View.VISIBLE);
        btnCustomerRegister.setVisibility(View.INVISIBLE);
        tvLoginDriver.setVisibility(View.INVISIBLE);
        tvLogin.setText("Login Customer");
        btnCustomerLogin.setEnabled(true);
    }

    public void RegisterCusterDetails(View view)
    {
        email = editTextCustomerEmailAddress.getText().toString().trim();
        password =editCustomerTextPassword.getText().toString().trim();

        Register(email,password);
    }

    private void Register(String email, String password)
    {
        if(TextUtils.isEmpty(email))
        {
            editTextCustomerEmailAddress.setError("Field cannot be empty");
            editTextCustomerEmailAddress.requestFocus();
        }
        if(TextUtils.isEmpty(password))
        {
            editCustomerTextPassword.setError("Field cannot be empty");
            editCustomerTextPassword.requestFocus();
        }
        else
        {
            dialog.setTitle("Register Service provider");
            dialog.setMessage("Loading.......");
            dialog.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                onlineCustomerID =mAuth.getCurrentUser().getUid();
                                customerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID);


                                customerDatabaseRef.setValue(true);
                                Intent driverIntent = new Intent(getApplicationContext(), CustomersMainActivity.class);
                                startActivity(driverIntent);
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }
                    });
        }
    }
}