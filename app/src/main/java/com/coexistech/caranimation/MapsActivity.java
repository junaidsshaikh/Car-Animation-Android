package com.coexistech.caranimation;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coexistech.caranimation.util.AnimationUtils;
import com.coexistech.caranimation.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LatLng defaultLocation;

    private Polyline grayPolyline;
    private Polyline blackPolyline;

    private Marker originMarker;
    private Marker destinationMarker;

    private Marker movingCabMarker;
    private LatLng previousLatLng;
    private LatLng currentLatLng;

    private Handler handler;
    private Runnable runnable;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        defaultLocation = new LatLng(28.435350000000003, 77.11368);
//        defaultLocation = new LatLng(20.0010565,73.7715935);
        showDefaultLocationOnMap(defaultLocation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPath(MapUtils.getListOfLocations());
                showMovingCab(MapUtils.getListOfLocations());
            }
        }, 3000);
    }

    private void showMovingCab(final ArrayList<LatLng> cabLatLngList) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (index < 10) {
                    updateCarLocation(cabLatLngList.get(index));
                    handler.postDelayed(runnable, 3000);
                    ++index;
                } else {
                    handler.removeCallbacks(runnable);
                    Toast.makeText(MapsActivity.this, "Trip Ends", Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.postDelayed(runnable, 5000);
    }


    private void moveCamera(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void animateCamera(LatLng latLng) {
        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(15.5f).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showDefaultLocationOnMap(LatLng latLng) {
        moveCamera(latLng);
        animateCamera(latLng);
    }

    public void showPath(ArrayList<LatLng> latLngList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5f);
        polylineOptions.addAll(latLngList);
        grayPolyline = googleMap.addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.color(Color.BLACK);
        blackPolylineOptions.width(5f);
        blackPolyline = googleMap.addPolyline(blackPolylineOptions);

        originMarker = addOriginDestinationMarkerAndGet(latLngList.get(0));
        originMarker.setAnchor(0.5f, 0.5f);
        destinationMarker  = addOriginDestinationMarkerAndGet(latLngList.get(latLngList.size()-1));
        destinationMarker.setAnchor(0.5f, 0.5f);

        ValueAnimator polylineAnimator = AnimationUtils.polylineAnimator();
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator ) {
                int percentValue = Integer.parseInt(valueAnimator.getAnimatedValue().toString());
                int index = grayPolyline.getPoints().size() * (int) (percentValue / 100.0f);
                blackPolyline.setPoints(grayPolyline.getPoints().subList(0, index));
            }
        });
        polylineAnimator.start();
    }

    private Marker addCarMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor =
                BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this));
        return googleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }

    private Marker addOriginDestinationMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor =
                BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap());
        return googleMap.addMarker(
                new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }

    private void updateCarLocation(LatLng latLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng);
        }
        if (previousLatLng == null) {
            currentLatLng = latLng;
            previousLatLng = currentLatLng;
            movingCabMarker.setPosition(currentLatLng);
            movingCabMarker.setAnchor(0.5f, 0.5f);
        } else {
            previousLatLng = currentLatLng;
            currentLatLng = latLng;
            ValueAnimator valueAnimator = AnimationUtils.carAnimator();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (currentLatLng != null && previousLatLng != null) {
                        float multiplier = animation.getAnimatedFraction();
                        LatLng nextLocation = new LatLng(
                                multiplier * currentLatLng.latitude + (1 - multiplier) * previousLatLng.latitude,
                                multiplier * currentLatLng.longitude + (1 - multiplier) * previousLatLng.longitude);
                        movingCabMarker.setPosition(nextLocation);
                        float rotation = MapUtils.getRotation(previousLatLng, nextLocation);
                        if (!Float.isNaN(rotation)) {
                            movingCabMarker.setRotation(rotation);
                        }
                        movingCabMarker.setAnchor(0.5f, 0.5f);
                        animateCamera(nextLocation);
                    }
                }
            });
            valueAnimator.start();
        }
    }
}
