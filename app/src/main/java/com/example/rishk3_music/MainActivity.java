package com.example.rishk3_music;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txtViewLatGPS;
    private TextView txtViewLongGPS;
    private TextView txtViewAltGPS;

    private TextView txtViewLatNetwork;
    private TextView txtViewLongNetwork;
    private TextView txtViewAltNetwork;

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;

    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private Button btnGPS;
    private Button btnNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtViewLatGPS = findViewById(R.id.txtViewLatGPS);
        txtViewLongGPS = findViewById(R.id.txtViewLonGPS);
        txtViewAltGPS = findViewById(R.id.txtViewAltGPS);

        txtViewLatNetwork = findViewById(R.id.txtViewLatNetwork);
        txtViewLongNetwork = findViewById(R.id.txtViewLonNetwork);
        txtViewAltNetwork = findViewById(R.id.txtViewAltNetwork);

        btnGPS = findViewById(R.id.btnGPSLoc);
        btnNetwork = findViewById(R.id.btnNetworkLoc);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPositionGPS();
            }
        });

        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPositionNetwork();
            }
        });
    }

    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1
                    );
                    txtViewAltGPS.setText("Country:" + addresses.get(0).getCountryName()+"  Locality:" + addresses.get(0).getLocality()
                            +"  Postalcode:" + addresses.get(0).getPostalCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                txtViewLatGPS.setText("GpsLatitude"+Double.toString(location.getLatitude()));
                txtViewLongGPS.setText("GpsLongitude"+Double.toString(location.getLongitude()));

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.GPS_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                btnGPS.setEnabled(false);
                mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, mLocationListenerGPS);
            }
        }
    }

    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                txtViewLatNetwork.setText("NetworkLatitude"+Double.toString(location.getLatitude()));
                txtViewLongNetwork.setText("NetworkLongitude"+Double.toString(location.getLongitude()));
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1
                    );
                    txtViewAltNetwork.setText("Country:" + addresses.get(0).getCountryName()+"  Locality:" + addresses.get(0).getLocality()
                            +"  Postalcode:" + addresses.get(0).getPostalCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                btnNetwork.setEnabled(false);
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, mLocationListenerNetwork);
            }
        }
    }

    private void showAlert(int messageId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_watch_permissions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                }
            }).setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManagerGPS != null) {
            mLocationManagerGPS.removeUpdates(mLocationListenerGPS);
        }

        if (mLocationManagerNetwork != null) {
            mLocationManagerNetwork.removeUpdates(mLocationListenerNetwork);
        }

        txtViewLatGPS.setText(null);
        txtViewLongGPS.setText(null);
        txtViewAltGPS.setText(null);

        txtViewLatNetwork.setText(null);
        txtViewLongNetwork.setText(null);
        txtViewAltNetwork.setText(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!btnGPS.isEnabled()) {
            btnGPS.setEnabled(true);
        }

        if (!btnNetwork.isEnabled()) {
            btnNetwork.setEnabled(true);
        }

    }

}