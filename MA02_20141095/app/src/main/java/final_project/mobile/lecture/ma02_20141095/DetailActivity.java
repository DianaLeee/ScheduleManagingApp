package final_project.mobile.lecture.ma02_20141095;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivFace;
    private TextView tvName;
    private Button btStart;
    private TextView tvDate;
    private Button btSms;
    private Button btAlarm;

    private double latitude;
    private double longitude;
    private String title;

    public final static int DEFAULT_ZOOM_LEVEL = 16;

    LatLng currentLoc;

    private PendingIntent pendingIntent;
    private LocationManager locManager;
    private GoogleMap googleMap;
    private MarkerOptions poiMarkerOptions;
    private Marker marker;

    private MarkerOptions centerMarkerOptions;
    private Marker centerMarker;

    private PolylineOptions pOptions;

    Intent intent;
    Appoint sData;

    AlarmManager am;
    PendingIntent sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //location manager
        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //google map
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(mapReadyCallBack);

        //marker option
        poiMarkerOptions = new MarkerOptions();
        poiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.green_marker));


        ivFace = (ImageView) findViewById(R.id.ivFace);
        tvName = (TextView) findViewById(R.id.tvName);
        btStart = (Button) findViewById(R.id.btStart);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btSms = (Button) findViewById(R.id.btSms);
        btAlarm = (Button) findViewById(R.id.btAlarm);

        intent = getIntent();
        sData = (Appoint)intent.getSerializableExtra("appoint");

//        Toast.makeText(DetailActivity.this, sData.getPlace_name(), Toast.LENGTH_SHORT).show();

        tvName.setText(sData.getsName());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm"); // HH=24h, hh=12h
        String str = df.format(sData.getTime());
        tvDate.setText(str);

//      When you click the picture, you can call him/her.
        Drawable d = new BitmapDrawable(getFacebookPhoto(sData.getsId()));
        ivFace.setImageDrawable(d);
        ivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DetailActivity.this);
                alert_confirm.setMessage("Do you call " + sData.getsName() + "?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+sData.getsNumber()));
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });


        latitude = sData.getLatitude();
        longitude = sData.getLongitude();
        title = sData.getTitle();

        //another marker option for my location
        centerMarkerOptions = new MarkerOptions();
        centerMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_fixed_black_24dp));
        centerMarkerOptions.title("My location");

//        button for finding my location
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 0, locationListener);

            }
        });

//        send message including my location -> unused
        btSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = "";
                if(currentLoc == null) {
                    place = "";
                } else {
                    Geocoder geocoder = new Geocoder(DetailActivity.this, Locale.US);
                    List<android.location.Address> address = null;
                    try {
                        if(geocoder != null) {
                            address = geocoder.getFromLocation(currentLoc.latitude, currentLoc.longitude, 1);
                        }
                        if(address != null && address.size() > 0) {
                            place = address.get(0).getAddressLine(0).toString();
                        }
                    } catch (IOException e) {
                        place = "unknown";
                    }
                }

                String num = sData.getsNumber();
                String text = "I'm going to "+ sData.getPlace_name() + "now! \n" + place;

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + num));
                intent.putExtra("sms_body", text);
                startActivity(intent);

            }
        });

        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

//        alarm function
        btAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** AlertDialog **/
                final EditText etEdit = new EditText(DetailActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("Set your alarm ... minutes before your schedule");
                dialog.setView(etEdit);
                // OK
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = etEdit.getText().toString();
//                        Toast.makeText(DetailActivity.this, inputValue, Toast.LENGTH_SHORT).show();

                        int time = Integer.parseInt(inputValue);

                        Intent intent = new Intent(DetailActivity.this, LocationBR.class);
                        intent.putExtra("sData", sData);
                        sender = PendingIntent.getBroadcast(DetailActivity.this, sData.get_id(), intent, 0);
                        am.set(AlarmManager.RTC_WAKEUP, sData.getTime() - (time * 60000), sender);

                        Toast.makeText(DetailActivity.this, "Alarm was set successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                // Cancel
                dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*LocationListener*/
    android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("DetailActivity - ", "Current Location : " + location.getLatitude() + ", " + location.getLongitude());

            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, DEFAULT_ZOOM_LEVEL));

            centerMarker.setPosition(currentLoc);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {

        }
    };

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            googleMap = map;


            LatLng lastLatLng;
            lastLatLng = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, DEFAULT_ZOOM_LEVEL));

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                }
            });

            poiMarkerOptions.title(sData.getPlace_name());
            poiMarkerOptions.snippet(title);
            poiMarkerOptions.position(new LatLng(latitude, longitude));
            googleMap.addMarker(poiMarkerOptions);


//            last location
            Location lastLocation = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            LatLng lastLatLngForMe;
            if (lastLocation != null) {
                lastLatLngForMe = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                lastLatLngForMe = new LatLng(41.837938, -87.627314);
            }
            centerMarkerOptions.position(lastLatLngForMe);
            centerMarker = googleMap.addMarker(centerMarkerOptions);
            centerMarker.showInfoWindow();


        }

    };

//    get photo from address book
    public Bitmap getFacebookPhoto (long userId) {
        ContentResolver cr = getContentResolver();
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.human);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.human);
        return defaultPhoto;
    }
}
