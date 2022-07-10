package com.example.serviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private EditText userMessageInput;
    private ImageButton sendMessage;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLoggedOutDriverStatus =false;
    private DatabaseReference AssignedCustomerRef, AssignedCustomerPickUpRef;
    private String driverID,customerID="";
    Marker PickupMarker;
    private ValueEventListener AssignedCustomerRefListner;
    private TextView txtPhone,txtService,txtName,customer_address;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;
    private String currentUserName,currentDate,currentTime;
    DatabaseReference usersRef,messageRef,groupMessageKeyRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        txtName = findViewById(R.id.name_customer);
        txtPhone = findViewById(R.id.phone_customer);
        profilePic =findViewById(R.id.profile_image_customer);
        relativeLayout=findViewById(R.id.rel2);
        customer_address = findViewById(R.id.customer_address);
       // GetAssignedCustomerRequest();

        toolbar.setTitle("Wefixit");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser =mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_logout:
                currentLoggedOutDriverStatus = true;
                DisconnectDriver();

                mAuth.signOut();
                Intent intent = new Intent(ServiceProviderMainActivity.this,WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.nav_service_provider_profile:
                Intent intent2 = new Intent(getApplicationContext(),SettingsActivity.class);
                intent2.putExtra("type","ServiceProviders");
                startActivity(intent2);
                finish();
                return true;
            case R.id.nav_customer_requests:
                Intent intent3 = new Intent(getApplicationContext(),CustomerRequests.class);
                startActivity(intent3);
                finish();
                return true;

            case R.id.nav_accepted_requests:
                Intent intent4 = new Intent(getApplicationContext(),ProviderHistoryActivity.class);
                startActivity(intent4);
                finish();
                return true;
            default:
                break;
        }
        return false;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this::onLocationChanged);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(@NonNull Location location)
    {
      if(getApplicationContext() !=null)
      {
          lastLocation = location;
          LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
          mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
          mMap.animateCamera(CameraUpdateFactory.zoomTo(13));



          DatabaseReference DriverAvailabilityRef =FirebaseDatabase.getInstance().getReference().child("Drivers Available");
          GeoFire geoFireAvailability = new GeoFire(DriverAvailabilityRef);

          DatabaseReference DriverWorkingRef =FirebaseDatabase.getInstance().getReference().child("Drivers Working");
          GeoFire geoFireWorking = new GeoFire(DriverWorkingRef);

         switch (customerID)
         {
             case "":
                 geoFireWorking.removeLocation(driverID, new GeoFire.CompletionListener() {
                     @Override
                     public void onComplete(String key, DatabaseError error) {

                     }
                 });
                 geoFireAvailability.setLocation(driverID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                     @Override
                     public void onComplete(String key, DatabaseError error) {
                         if (error != null) {
                             System.err.println("There was an error saving the location to GeoFire: " + error);
                         } else {
                             HashMap<String,Object> map = new HashMap<>();
                             map.put("id",driverID);
                             DriverAvailabilityRef.child(driverID).updateChildren(map);

                         }
                     }
                 });
                 break;
             default:
                 geoFireAvailability.removeLocation(driverID, new GeoFire.CompletionListener() {
                     @Override
                     public void onComplete(String key, DatabaseError error) {

                     }
                 });
                 geoFireWorking.setLocation(driverID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                     @Override
                     public void onComplete(String key, DatabaseError error) {
                         if (error != null) {
                             System.err.println("There was an error saving the location to GeoFire: " + error);
                         } else {
                             System.out.println("Location saved on server successfully!");
                         }
                     }
                 });
                 break;
         }
      }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(!currentLoggedOutDriverStatus)
        {
            DisconnectDriver();
        }
    }
    private void DisconnectDriver()
    {
        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailabilityRef =FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(DriverAvailabilityRef);
        geoFire.removeLocation(userID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error)
            {
                if (error != null) {
                    System.err.println("There was an error logging out: " + error);
                } else {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("uid",key);
                    DriverAvailabilityRef.child(userID).updateChildren(map);
                    System.out.println("Logged out successfully!");
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
    private void GetAssignedCustomerRequest()
     {
      AssignedCustomerRef = FirebaseDatabase.getInstance().getReference("Users")
              .child("ServiceProviders").child(driverID).child("CusterServiceID");
      AssignedCustomerRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot)
          {
            if(snapshot.exists())
            {
                customerID = snapshot.getValue().toString();
                GetAssignedCustomerPickupLocation();

                relativeLayout.setVisibility(View.VISIBLE);
                getAssignCustomerInformation();
            }
            else
            {
                customerID= "";
                if(PickupMarker !=null)
                {
                    PickupMarker.remove();
                }
                if(AssignedCustomerRefListner !=null)
                {
                    AssignedCustomerPickUpRef.removeEventListener(AssignedCustomerRefListner);
                }
                relativeLayout.setVisibility(View.GONE);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
    private void GetAssignedCustomerPickupLocation()
    {
        AssignedCustomerPickUpRef = FirebaseDatabase.getInstance().getReference().child("Customer Requests")
                .child(customerID).child("l");
        AssignedCustomerRefListner=AssignedCustomerPickUpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
              if(snapshot.exists())
              {
                  List<Object> customerLocationMap = (List<Object>) snapshot.getValue();
                  double LocationLat = 0;
                  double LocationLon =0;

                  if(customerLocationMap.get(0) !=null)
                  {
                      LocationLat =Double.parseDouble(customerLocationMap.get(0).toString());
                  }
                  if(customerLocationMap.get(1) !=null)
                  {
                      LocationLon =Double.parseDouble(customerLocationMap.get(1).toString());
                  }
                  LatLng DriverLatLng = new LatLng(LocationLat,LocationLon);
                  PickupMarker=mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Customer Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getAssignCustomerInformation()
    {
        Query reference =FirebaseDatabase.getInstance().getReference()
                .child("Requets").child(customerID).orderByChild("status")
                .equalTo("requested");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                    String name = snapshot.child("custName").getValue().toString();
                    String phone = snapshot.child("custPhone").getValue().toString();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Requests").child(customerID);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                             String address = snapshot.child("custAddress").getValue().toString();
                              customer_address.setText(address);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    txtName.setText(name);
                    txtPhone.setText(phone);

                    if(snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profilePic);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}