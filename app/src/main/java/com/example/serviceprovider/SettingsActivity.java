package com.example.serviceprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText nameEditText,phoneEditText,serviceName,service_amount;
    private ImageView closeButton, saveButton;
    private TextView profileChangeBtn;
    private String getType,checker="";
    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference storageProfileRef;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getType = getIntent().getStringExtra("type");
        Toast.makeText(getApplicationContext(), getType, Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        databaseReference =FirebaseDatabase.getInstance().getReference().child("Users").child(getType);
        storageProfileRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView =findViewById(R.id.profile);
        nameEditText =findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone_number);
        serviceName = findViewById(R.id.service_provider_service);
        service_amount=findViewById(R.id.service_amount);

        if(getType.equals("ServiceProviders"))
        {
            serviceName.setVisibility(View.VISIBLE);
            service_amount.setVisibility(View.VISIBLE);
        }

        profileChangeBtn = findViewById(R.id.change_picture_btn);
        saveButton = findViewById(R.id.save_button);
        closeButton =findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getType.equals("ServiceProviders"))
                {
                 startActivity(new Intent(getApplicationContext(),ServiceProviderMainActivity.class));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(), CustomersMainActivity.class));
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checker.equals("clicked"))
                {
                  validateControllers();
                }
                else
                {
                 validateAndSaveOnlyInformation();
                }
            }
        });
       profileChangeBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checker = "clicked";
               CropImage.activity()
                       .setAspectRatio(1,1)
                       .start(SettingsActivity.this);
           }
       });
       getUserInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data !=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            if(getType.equals("ServiceProviders"))
            {
                startActivity(new Intent(getApplicationContext(), ServiceProviderMainActivity.class));
            }
            else {
                startActivity(new Intent(getApplicationContext(), CustomersMainActivity.class));
            }
            Toast.makeText(getApplicationContext(), "Error, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    private void validateControllers()
    {
        if(TextUtils.isEmpty(nameEditText.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your phone", Toast.LENGTH_SHORT).show();
        }
        else if(getType.equals("ServiceProviders") && TextUtils.isEmpty(serviceName.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your services", Toast.LENGTH_SHORT).show();
        }
        else if(getType.equals("ServiceProviders") && TextUtils.isEmpty(service_amount.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide amount", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
          uploadProfilePicture();
        }
    }

    private void uploadProfilePicture()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Settings Account Information");
        progressDialog.setMessage("Please wait,while updating settings account information");
        progressDialog.show();

        if(imageUri !=null)
        {
            final StorageReference fileRef= storageProfileRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
            uploadTask= fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                   if(!task.isSuccessful())
                   {
                       throw  task.getException();
                   }
                   return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("name",nameEditText.getText().toString());
                        userMap.put("phone",phoneEditText.getText().toString());
                        userMap.put("image",myUri);

                        if(getType.equals("ServiceProviders"))
                        {
                           userMap.put("service",serviceName.getText().toString());
                           userMap.put("amount",Integer.parseInt(service_amount.getText().toString()));
                        }
                       databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        progressDialog.dismiss();
                        if(getType.equals("ServiceProviders"))
                        {
                            startActivity(new Intent(getApplicationContext(), ServiceProviderMainActivity.class));

                        }
                        else
                        {
                            startActivity(new Intent(getApplicationContext(), CustomersMainActivity.class));
                        }

                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void validateAndSaveOnlyInformation()
    {
        if(TextUtils.isEmpty(nameEditText.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your phone", Toast.LENGTH_SHORT).show();
        }
        else if(getType.equals("ServiceProvider") && TextUtils.isEmpty(serviceName.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your services", Toast.LENGTH_SHORT).show();
        }
        else if(getType.equals("ServiceProvider") && TextUtils.isEmpty(service_amount.getText().toString().trim()))
        {
            Toast.makeText(this, "Please provide your service amount", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> userMap = new HashMap<>();
            userMap.put("uid",mAuth.getCurrentUser().getUid());
            userMap.put("name",nameEditText.getText().toString());
            userMap.put("phone",phoneEditText.getText().toString());

            if(getType.equals("ServiceProviders"))
            {
                userMap.put("service",serviceName.getText().toString());
                userMap.put("amount",Integer.parseInt(service_amount.getText().toString()));
            }
            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

            if(getType.equals("ServiceProviders"))
            {
                startActivity(new Intent(getApplicationContext(), ServiceProviderMainActivity.class));
            }
            else
            {
                startActivity(new Intent(getApplicationContext(), CustomersMainActivity.class));
            }
        }
    }
    private void getUserInformation()
    {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();

                    nameEditText.setText(name);
                    phoneEditText.setText(phone);

                    if(getType.equals("ServiceProviders"))
                    {
                        String service = snapshot.child("service").getValue().toString();
                        int amount = Integer.parseInt(snapshot.child("amount").getValue().toString());
                        serviceName.setText(service);
                        service_amount.setText(String.valueOf(amount));
                    }
                   if(snapshot.hasChild("image"))
                   {
                       String image = snapshot.child("image").getValue().toString();
                       Picasso.get().load(image).into(profileImageView);
                   }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}