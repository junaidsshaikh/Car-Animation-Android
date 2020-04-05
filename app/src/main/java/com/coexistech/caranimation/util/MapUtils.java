package com.coexistech.caranimation.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.coexistech.caranimation.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static java.lang.Math.atan;

public class MapUtils {

    public static ArrayList<LatLng> getListOfLocations() {
        ArrayList<LatLng> locationList = new ArrayList<>();
        locationList.add(new LatLng(28.436970000000002, 77.11272000000001));
        locationList.add(new LatLng(28.43635, 77.11289000000001));
        locationList.add(new LatLng(28.4353, 77.11317000000001));
        locationList.add(new LatLng(28.435280000000002, 77.11332));
        locationList.add(new LatLng(28.435350000000003, 77.11368));
        locationList.add(new LatLng(28.4356, 77.11498));
        locationList.add(new LatLng(28.435660000000002, 77.11519000000001));
        locationList.add(new LatLng(28.43568, 77.11521));
        locationList.add(new LatLng(28.436580000000003, 77.11499));
        locationList.add(new LatLng(28.436590000000002, 77.11507));
        return locationList;
    }

    public static Bitmap getOriginDestinationMarkerBitmap() {
        int height = 20;
        int width = 20;
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(0F, 0F, width, height, paint);
        return  bitmap;
    }

    public static Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car);
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false);
    }

    public static Float getRotation(LatLng start, LatLng end) {
        Double latDifference = Math.abs(start.latitude - end.latitude);
        Double lngDifference = Math.abs(start.longitude - end.longitude);
        float rotation = -1F;
        if(start.latitude < end.latitude && start.longitude < end.longitude) {
            rotation = (float) Math.toDegrees(atan(lngDifference / latDifference));
        } else if(start.latitude >= end.latitude && start.longitude < end.longitude) {
            rotation = (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90);
        } else if(start.latitude >= end.latitude && start.longitude >= end.longitude) {
            rotation = (float) (Math.toDegrees(atan(lngDifference / latDifference)) + 180);
        } else if(start.latitude < end.latitude && start.longitude >= end.longitude) {
            rotation = (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270);
        }
        return rotation;
    }

}
