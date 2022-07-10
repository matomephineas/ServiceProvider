package com.example.serviceprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.serviceprovider.Models.Requests;
import com.example.serviceprovider.Models.Services;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomersMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback
, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLoggedOutDriverStatus = false;
    private Button btnRequestService, requestServiceBtn, confirmService;
    private EditText txtYourLocation, YourLocation, enteredServiceLayout;
    private String customerID;
    private TextView  txtYourServices,distance, tvServiceDoesNotExist;
    private LatLng CustomerPickupLocation;
    private DatabaseReference customerDatabaseRef;
    private DatabaseReference ServiceProviderAvailableLocationRef, DriverRef, ServiceProvidersWorkingRef;
    private int radius = 1;
    private Boolean driverFound = false, requestType = false;
    public String driverFoundID,referenceNumber;
    GeoQuery geoQuery;
    Marker DriverMarker, PickupMarker;
    private ValueEventListener DriverLocationRefListener;
    String requestedService, Address, type, Amount, pickUpDate, pickUpTime, userPhone, userName, lan, lon, address1;
    Geocoder geocoder;
    List<Address> myAddress;


    private TextView txtPhone, txtService, txtName, amount_driver;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout, rel_service;
    Animation smalltobig, fromlefttoright, fleft, fhelper, slide_in_button;
    private RelativeLayout linear_with_hint_service;
    private RecyclerView recyclerView;

    private ArrayList<Services> mList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_main);
        Toolbar toolbar = findViewById(R.id.custToolbar);
        btnRequestService = findViewById(R.id.btnRequestService);
        distance = findViewById(R.id.distance);

       progressDialog = new ProgressDialog(this);

        enteredServiceLayout = findViewById(R.id.YourServices);
        YourLocation = findViewById(R.id.YourLocation);
        confirmService = findViewById(R.id.confirmService);
        recyclerView = findViewById(R.id.recyclerview1);

        txtName = findViewById(R.id.provider_name);
        txtService = findViewById(R.id.service_driver);
        txtPhone = findViewById(R.id.phone);

        profilePic = findViewById(R.id.service_provider_picture);
        relativeLayout = findViewById(R.id.rel1);
        amount_driver = findViewById(R.id.amount_driver);
        rel_service = findViewById(R.id.rel_service);

        linear_with_hint_service = findViewById(R.id.linear_with_hint_service);

        txtYourLocation = findViewById(R.id.txtYourLocation);
        txtYourServices = findViewById(R.id.txtYourServices);


        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        fromlefttoright = AnimationUtils.loadAnimation(this, R.anim.fromlefttoright);
        fleft = AnimationUtils.loadAnimation(this, R.anim.fleft);
        fhelper = AnimationUtils.loadAnimation(this, R.anim.fhelper);
        slide_in_button = AnimationUtils.loadAnimation(this, R.anim.slide_in_button);


        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        customerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        ServiceProviderAvailableLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        ServiceProvidersWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");

        DrawerLayout drawer = findViewById(R.id.customer_drawer_layout);
        NavigationView navigationView = findViewById(R.id.cust_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.cust_map);
        mapFragment.getMapAsync(this);

        btnRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDetails();
            }
        });
        confirmService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEnteredService();
            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
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
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


        lan = "" + location.getLatitude();
        lon = "" + location.getLongitude();

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            myAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address1 = myAddress.get(0).getAddressLine(0);
        //address.setText(address1);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("type", "Customers");
                startActivity(intent);
                finish();
                return true;
            case  R.id.navlogout:
                mAuth.signOut();
                Intent intent1 = new Intent(CustomersMainActivity.this,WelcomeActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("type", "Customers");
                startActivity(intent);
                finish();
                return true;
            case R.id.navHistory:
                Intent intent1 = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                break;
        }
        return false;
    }

    private void GetClosestServiceDriver() {
        GeoFire geoFire = new GeoFire(ServiceProviderAvailableLocationRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPickupLocation.latitude, CustomerPickupLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //anytime the driver is called this method will be called
                //key =driverID and the location
                if (!driverFound && requestType) {
                    driverFound = true;
                    driverFoundID = key;
                    DriverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("ServiceProviders").child(driverFoundID);
                    HashMap driverMap = new HashMap();
                    driverMap.put("CusterServiceID", customerID);
                    DriverRef.updateChildren(driverMap);
                    GettingServiceProviderLocation();
                    requestServiceBtn.setText("Looking for Service Provider Location");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius = radius + 1;
                    GetClosestServiceDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void GettingServiceProviderLocation() {
        DriverLocationRefListener = ServiceProvidersWorkingRef.child(driverFoundID).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && requestType) {
                            List<Object> driverLocationMap = (List<Object>) snapshot.getValue();

                            double LocationLat = 0;
                            double LocationLon = 0;
                            requestServiceBtn.setText("Service Provider found");

                            relativeLayout.setVisibility(View.VISIBLE);
                            getAssignServiceProviderInformation();
                            if (driverLocationMap.get(0) != null) {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if (driverLocationMap.get(1) != null) {
                                LocationLon = Double.parseDouble(driverLocationMap.get(1).toString());
                            }
                            LatLng DriverLatLng = new LatLng(LocationLat, LocationLon);
                            if (DriverMarker != null) {
                                DriverMarker.remove();
                            }
                            Location location1 = new Location("");
                            location1.setLatitude(CustomerPickupLocation.latitude);
                            location1.setLongitude(CustomerPickupLocation.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            float Distance = location1.distanceTo(location2);

                            if (Distance < 90) {
                                requestServiceBtn.setText("Service Provider Reached");
                            } else {
                                requestServiceBtn.setText("Service Provider Found: " + String.valueOf(Distance));
                                distance.setText(String.valueOf(Distance));

                            }
                            DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("your service provider is here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkLayout() {
        linear_with_hint_service.setVisibility(View.VISIBLE);
        rel_service.setVisibility(View.INVISIBLE);
        YourLocation.setText(address1);
    }
    private void getAssignServiceProviderInformation()
    {
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference()
                .child("Requests").child(currentUser.getUid().toString());
        requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String id = snapshot.child("uid").getValue().toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("ServiceProviders").child(id);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                                String name = snapshot.child("name").getValue().toString();
                                String phone = snapshot.child("phone").getValue().toString();
                                String service = snapshot.child("service").getValue().toString();
                                int amount = Integer.parseInt(snapshot.child("amount").getValue().toString());
                                txtName.setText(name);
                                txtPhone.setText(phone);
                                txtService.setText(service);
                                amount_driver.setText(String.valueOf(amount));

                                if (snapshot.hasChild("image")) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    private void viewDetails() {
        rel_service.setVisibility(View.VISIBLE);
        rel_service.startAnimation(slide_in_button);
        btnRequestService.setVisibility(View.INVISIBLE);
        txtYourLocation.setText(address1);
        txtYourServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLayout();
            }
        });
    }
    private boolean validateService() {
        requestedService = enteredServiceLayout.getText().toString().trim();
        if (requestedService.isEmpty()) {
            enteredServiceLayout.setError("filed must not be empty");
            enteredServiceLayout.requestFocus();
            return false;
        } else {
            enteredServiceLayout.setError(null);
            enteredServiceLayout.requestFocus();
            return true;
        }
    }
    private void checkEnteredService() {
        if (!validateService()) {
            return;
        } else {
            viwAvailableServiceProviders(requestedService);
        }
    }
    private void viwAvailableServiceProviders(String urService) {
        AvailableServiceProviderAdapter adapter;
        ArrayList<Services> mList;

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mList = new ArrayList<>();
        adapter = new AvailableServiceProviderAdapter(mList, getApplicationContext());
        recyclerView.setAdapter(adapter);
        Query reference = FirebaseDatabase.getInstance().getReference("Users").child("ServiceProviders").orderByChild("service").equalTo(urService);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Services services = dataSnapshot.getValue(Services.class);
                    mList.add(services);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class AvailableServiceProviderAdapter extends RecyclerView.Adapter<AvailableServiceProviderAdapter.viewHolder> {
        private List<Services> Items;
        private Context context;
        public AvailableServiceProviderAdapter(List<Services> items, Context context) {
            Items = items;
            this.context = context;
        }
        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_provider, parent, false);
            return new viewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.job.setText(Items.get(position).getService());
            holder.amount.setText(String.valueOf(Items.get(position).getAmount()));
            holder.phone.setText(Items.get(position).getPhone());
            holder.provider_name.setText(Items.get(position).getName());
            Picasso.get().load(Items.get(position).getImage()).into(holder.service_provider_picture);
            String id = Items.get(position).getUid();

            holder.accepServiceProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String timeRequested,dateRequested;

                    Calendar calendar = Calendar.getInstance();

                    final SimpleDateFormat CurrentDate = new SimpleDateFormat("yyyy/MM/dd");
                    dateRequested = CurrentDate.format(calendar.getTime());

                    final SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:MM");
                    timeRequested = CurrentTime.format(calendar.getTime());
                 AlertDialog.Builder alertdialog = new AlertDialog.Builder(view.getRootView().getContext());
                 alertdialog.setTitle("Emergency");
                 alertdialog.setMessage("Is your request emergency?");
                 alertdialog.setCancelable(true);
                 alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i)
                     {


                         if (requestType) {
                             requestType = false;
                             geoQuery.removeAllListeners();
                             ServiceProvidersWorkingRef.removeEventListener(DriverLocationRefListener);
                             if (driverFound != null) {
                                 DriverRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                         .child("ServiceProviders").child(driverFoundID).child("CusterServiceID");
                                 DriverRef.removeValue();
                                 driverFoundID = null;
                                 Toast.makeText(getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                             }
                             driverFound = false;
                             radius = 1;
                             customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                             GeoFire geoFire = new GeoFire(customerDatabaseRef);
                             geoFire.removeLocation(customerID, new GeoFire.CompletionListener() {
                                 @Override
                                 public void onComplete(String key, DatabaseError error) {

                                 }
                             });
                             if (PickupMarker != null) {
                                 PickupMarker.remove();
                             }
                             if (DriverMarker != null) {
                                 DriverMarker.remove();
                             }
                             requestServiceBtn.setText("Request Service");
                             relativeLayout.setVisibility(View.GONE);
                         }
                         else
                         {
                             ProgressDialog progressDialog = new ProgressDialog(view.getRootView().getContext());
                             progressDialog.setTitle("Requesting Service");
                             progressDialog.setMessage("Please wait while requesting your service");
                             progressDialog.show();

                             Random r = new Random();

                             HashSet<Integer> set = new HashSet<Integer>();

                             while(set.size() <1)
                             {
                                 int random = r.nextInt(999999)+1000000;
                                 set.add(random);
                             }
                             for(int ra:set)
                             {
                                 //to generate student number
                                 DateFormat df = new SimpleDateFormat("yy");
                                 String fd = df.format(Calendar.getInstance().getTime());
                                 String sum = fd + ra;
                                 referenceNumber = sum;
                             }

                             requestType = true;
                             customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                             GeoFire geoFire = new GeoFire(customerDatabaseRef);
                             geoFire.setLocation(customerID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
                                 @Override
                                 public void onComplete(String key, DatabaseError error)
                                 {
                                     DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(currentUser.getUid());
                                     user.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             if(snapshot.exists())
                                             {
                                                 DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
                                                 Requests requests = new Requests(requestedService,String.valueOf(Items.get(position).getAmount()),
                                                         Items.get(position).getName(),"requested",id,key,Items.get(position).getPhone(),
                                                         snapshot.child("name").getValue().toString(),
                                                         snapshot.child("phone").getValue().toString(),address1,timeRequested,dateRequested,"yes",referenceNumber);
                                                         ref.child(key).setValue(requests)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task)
                                                             {
                                                                 if(task.isSuccessful())
                                                                 {
                                                                     Toast.makeText(context, "Service requested successfully", Toast.LENGTH_SHORT).show();
                                                                      progressDialog.dismiss();
                                                                 }
                                                                 else
                                                                 {
                                                                     Toast.makeText(context, "Request unsuccessful", Toast.LENGTH_SHORT).show();
                                                                     progressDialog.dismiss();
                                                                 }
                                                             }
                                                         });
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {

                                         }
                                     });

                                 }
                             });
                             CustomerPickupLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                             PickupMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickupLocation).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                             btnRequestService.setText("Getting service provider....");
                             linear_with_hint_service.setVisibility(View.INVISIBLE);
                             btnRequestService.setVisibility(View.VISIBLE);

                             GetClosestServiceDriver();
                         }
                     }
                 });
                 alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         if (requestType) {
                             requestType = false;
                             geoQuery.removeAllListeners();
                             ServiceProvidersWorkingRef.removeEventListener(DriverLocationRefListener);
                             if (driverFound != null) {
                                 DriverRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                         .child("ServiceProviders").child(driverFoundID).child("CusterServiceID");
                                 DriverRef.removeValue();
                                 driverFoundID = null;
                                 Toast.makeText(getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                             }
                             driverFound = false;
                             radius = 1;
                             customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                             GeoFire geoFire = new GeoFire(customerDatabaseRef);
                             geoFire.removeLocation(customerID, new GeoFire.CompletionListener() {
                                 @Override
                                 public void onComplete(String key, DatabaseError error) {

                                 }
                             });
                             if (PickupMarker != null) {
                                 PickupMarker.remove();
                             }
                             if (DriverMarker != null) {
                                 DriverMarker.remove();
                             }
                             requestServiceBtn.setText("Request Service");
                             relativeLayout.setVisibility(View.GONE);
                         }
                         else
                         {
                             Random r = new Random();
                             HashSet<Integer> set = new HashSet<Integer>();

                             while(set.size() <1)
                             {
                                 int random = r.nextInt(999999)+1000000;
                                 set.add(random);
                             }
                             for(int ra:set)
                             {
                                 //to generate student number
                                 DateFormat df = new SimpleDateFormat("yy");
                                 String fd = df.format(Calendar.getInstance().getTime());
                                 String sum = fd + ra;
                                 referenceNumber = sum;
                             }
                             ProgressDialog progressDialog = new ProgressDialog(view.getRootView().getContext());
                             progressDialog.setTitle("Requesting Service");
                             progressDialog.setMessage("Please wait while requesting your service");
                             progressDialog.show();

                             requestType = true;
                             customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                             GeoFire geoFire = new GeoFire(customerDatabaseRef);
                             geoFire.setLocation(customerID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
                                 @Override
                                 public void onComplete(String key, DatabaseError error)
                                 {
                                     DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(currentUser.getUid());
                                     user.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             if(snapshot.exists())
                                             {
                                                 DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
                                                 Requests requests = new Requests(requestedService,String.valueOf(Items.get(position).getAmount()),
                                                         Items.get(position).getName(),"requested",id,key,Items.get(position).getPhone(),
                                                         snapshot.child("name").getValue().toString(),
                                                         snapshot.child("phone").getValue().toString(),address1,timeRequested,dateRequested,"no",referenceNumber);
                                                 ref.child(key).setValue(requests)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task)
                                                             {
                                                                 if(task.isSuccessful())
                                                                 {
                                                                     Toast.makeText(context, "Service requested successfully", Toast.LENGTH_SHORT).show();
                                                                     progressDialog.dismiss();
                                                                 }
                                                                 else
                                                                 {
                                                                     Toast.makeText(context, "Request unsuccessful", Toast.LENGTH_SHORT).show();
                                                                     progressDialog.dismiss();
                                                                 }
                                                             }
                                                         });
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {

                                         }
                                     });

                                 }
                             });
                             CustomerPickupLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                             PickupMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickupLocation).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                             btnRequestService.setText("Getting service provider....");
                             linear_with_hint_service.setVisibility(View.INVISIBLE);
                             btnRequestService.setVisibility(View.VISIBLE);

                             GetClosestServiceDriver();
                         }
                     }
                 });
                 alertdialog.create().show();


                }
            });
        }
        @Override
        public int getItemCount() {
            return Items.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            private TextView provider_name, phone, job, amount;
            private CircleImageView service_provider_picture;
            private RelativeLayout relativeLayout;
            private Button accepServiceProvider;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                relativeLayout = itemView.findViewById(R.id.single_provider);
                service_provider_picture = itemView.findViewById(R.id.service_provider_picture);
                amount = itemView.findViewById(R.id.amount);
                provider_name = itemView.findViewById(R.id.provider_name);
                phone = itemView.findViewById(R.id.phone);
                job = itemView.findViewById(R.id.job);
                accepServiceProvider = itemView.findViewById(R.id.accepServiceProvider);

            }
        }
    }



}