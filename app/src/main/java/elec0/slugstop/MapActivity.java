package elec0.slugstop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean pauseTimer = false;
    private List<BusData> busList;
    private List<MarkerData> markerList;
    private List<BusStopData> innerLoopList, outerLoopList;
    private boolean firstRun = true;

    private Marker lastOpened = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        busList = new ArrayList<>();
        markerList = new ArrayList<>();
        innerLoopList = new ArrayList<>();
        outerLoopList = new ArrayList<>();
        
        // Populate the inner loop stops
        innerLoopList.add(new BusStopData(5, new LatLng( 36.9999313354492, -122.062049865723), "McLaughlin & Science Hill"));
        innerLoopList.add(new BusStopData(2, new LatLng( 36.9967041015625, -122.063583374023), "Heller & Kerr Hall"));
        innerLoopList.add(new BusStopData(3, new LatLng( 36.999210357666, -122.064338684082), "Heller & Kresge College"));
        innerLoopList.add(new BusStopData(5, new LatLng( 36.9999313354492, -122.062049865723), "McLaughlin & Science Hill"));
        innerLoopList.add(new BusStopData(6, new LatLng( 36.9997062683105, -122.05834197998), "McLaughlin & College 9 & 10 - Health Center"));
        innerLoopList.add(new BusStopData(10, new LatLng( 36.9966621398926, -122.055480957031), "Hagar & Bookstore"));
        innerLoopList.add(new BusStopData(13, new LatLng( 36.9912567138672, -122.054962158203), "Hagar & East Remote"));
        innerLoopList.add(new BusStopData(15, new LatLng( 36.985523223877, -122.053588867188), "Hagar & Lower Quarry Rd"));
        innerLoopList.add(new BusStopData(17, new LatLng( 36.9815368652344, -122.052131652832), "Coolidge & Hagar"));
        innerLoopList.add(new BusStopData(18, new LatLng( 36.9787902832031, -122.057762145996), "High & Western Dr"));
        innerLoopList.add(new BusStopData(20, new LatLng( 36.9773025512695, -122.054328918457), "High & Barn Theater"));
        innerLoopList.add(new BusStopData(23, new LatLng( 36.9826698303223, -122.062492370605), "Empire Grade & Arboretum"));
        innerLoopList.add(new BusStopData(26, new LatLng( 36.9905776977539, -122.066116333008), "Heller & Oakes College"));
        innerLoopList.add(new BusStopData(29, new LatLng( 36.9927787780762, -122.064880371094), "Heller & College 8 & Porter"));

        // Outer loop stops
        outerLoopList.add(new BusStopData(1, new LatLng( 36.9992790222168, -122.064552307129), "Heller & Kresge College"));
        outerLoopList.add(new BusStopData(4, new LatLng( 37.0000228881836, -122.062339782715), "McLaughlin & Science Hill"));
        outerLoopList.add(new BusStopData(7, new LatLng( 36.9999389648438, -122.058349609375), "McLaughlin & College 9 & 10 - Health Center"));
        outerLoopList.add(new BusStopData(8, new LatLng( 36.9990234375, -122.055229187012), "McLaughlin & Crown College"));
        outerLoopList.add(new BusStopData(9, new LatLng( 36.9974822998047, -122.055030822754), "Hagar & Bookstore-Stevenson College"));
        outerLoopList.add(new BusStopData(11, new LatLng( 36.9942474365234, -122.055511474609), "Hagar & Field House East"));
        outerLoopList.add(new BusStopData(12, new LatLng( 36.9912986755371, -122.054656982422), "Hagar & East Remote"));
        outerLoopList.add(new BusStopData(14, new LatLng( 36.985912322998, -122.053520202637), "Hagar & Lower Quarry Rd"));
        outerLoopList.add(new BusStopData(16, new LatLng( 36.9813537597656, -122.051971435547), "Coolidge & Hagar"));
        outerLoopList.add(new BusStopData(19, new LatLng( 36.9776763916016, -122.053558349609), "Coolidge & Main Entrance"));
        outerLoopList.add(new BusStopData(21, new LatLng( 36.9786148071289, -122.05785369873), "High & Western Dr"));
        outerLoopList.add(new BusStopData(22, new LatLng( 36.9798469543457, -122.059257507324), "Empire Grade & Tosca Terrace"));
        outerLoopList.add(new BusStopData(24, new LatLng( 36.9836616516113, -122.064964294434), "Empire Grade & Arboretum"));
        outerLoopList.add(new BusStopData(25, new LatLng( 36.989917755127, -122.067230224609), "Heller & Oakes College"));
        outerLoopList.add(new BusStopData(27, new LatLng( 36.991828918457, -122.066833496094), "Heller & Family Student Housing"));
        outerLoopList.add(new BusStopData(28, new LatLng( 36.992977142334, -122.065223693848), "Heller & College 8 & Porter"));
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run()
        {
            if(!pauseTimer)
            {
                updateBusGPS();
            }
            timerHandler.postDelayed(this, 1000);
        }
    };


    private void updateBusGPS()
    {
        // http://bts.ucsc.edu:8081/location/get
        new BusUpdateAsync().execute("");
    }

    public void busGPSCallback(String result)
    {
        busList.clear();
        if(result != null)
        {
            try
            {
                JSONArray jsonArr = new JSONArray(result);
                for (int i = 0; i < jsonArr.length(); ++i)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    BusData bus = new BusData(jsonObj.getInt("id"), jsonObj.getDouble("lon"), jsonObj.getDouble("lat"), jsonObj.getString("type"));
                    busList.add(bus);
                }
            }
            catch (Exception e)
            {
                Log.e("Elec0", "Bus GPS Callback Error", e);
            }
        }
        busUpdateMarker();
    }

    private void busUpdateMarker()
    {
        // If a bus turns its transmitter off, we want to stop displaying the bus on the map.
        if(busList.size() == 0)
        {
            if(firstRun == true)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("It appears no buses are running at this time")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                firstRun = false;
            }

            if(markerList.size() != 0)
            {
                for (MarkerData m : markerList)
                {
                    m.getMarker().remove();
                }
                markerList.clear();
            }
            return;
        }
        for(BusData b : busList)
        {
            // We don't want to add a marker, we want to move the marker that has the same id as the bus
            // If no marker exists, create it.

            MarkerData selectedMarker = null;

            for(MarkerData m : markerList)
            {
                if(m.getID() == b.getID())
                {
                    selectedMarker = m;
                    break;
                }
            }

            if(selectedMarker != null)
            {
                // If the type is different, we need to remove the marker and put in a new one to handle the change in icon
                if (!selectedMarker.getType().equals(b.getType()))
                {
                    markerList.remove(selectedMarker);
                    selectedMarker.getMarker().remove();
                    selectedMarker = null;
                }
            }

            if(selectedMarker != null) // Move the marker
            {
                selectedMarker.setLoc(b.getLoc());
                animateMarker(selectedMarker.getMarker(), b.getLoc(), false);
            }
            else // Create the marker
            {
                int icon = 0;
                switch(b.getType().toUpperCase())
                {
                    case "LOOP":
                        icon = R.drawable.slugroute_loop;
                        break;
                    case "UPPER CAMPUS":
                        icon = R.drawable.slugroute_upper;
                        break;
                    case "NIGHT OWL":
                        icon = R.drawable.slugrotue_nightowl;
                        break;
                    case "LOOP OUT OF SERVICE AT BARN THEATER":
                    case "OUT OF SERVICE/SORRY":
                        icon = R.drawable.slugroute_out;
                        break;
                    default:
                        icon = R.drawable.slugroute_special;
                        break;
                }
                Marker m = mMap.addMarker(new MarkerOptions()
                            .position(b.getLoc())
                            .title(b.getType())
                            .zIndex(0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(icon)));
                markerList.add(new MarkerData(b.getID(), m, b.getLoc(), b.getType()));
            }

            // If markerList contains any bus IDs that aren't in busList, remove them
            List<MarkerData> toRemove = new ArrayList<>();

            for(MarkerData mar : markerList)
            {
                boolean remove = true;
                for(BusData bus : busList)
                {
                    if(mar.getID() == bus.getID()) // It's in the list, don't remove it
                    {
                        remove = false;
                    }
                }
                if(remove == true) // At this point it's not in the bus list, so add it to the list to be removed from the main list
                {
                    toRemove.add(mar);
                }
            }
            for(MarkerData markerRemove : toRemove)
            {
                markerRemove.getMarker().remove();
                markerList.remove(markerRemove);
                Log.d("Elec0", "Remove marker id " + markerRemove.getID());
            }
        }
    }

    /***
     * Method to animate marker movement. Got this off of StackOverflow, I think.
     * Meant to grab the url but it's too late now
     * @param marker
     * @param toPosition
     * @param hideMarker
     */
    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker)
    {
        // Don't move if there's nowhere to move. Prevents the shaking when busses aren't moving
        if(marker.getPosition().equals(toPosition))
        {
            return;
        }

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("Elec0", "onPause");
        pauseTimer = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("Elec0", "onResume");
        pauseTimer = false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // We should probably handle people clicking never ask again for this.
        int permCoarse = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permFine = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permCoarse != PackageManager.PERMISSION_GRANTED || permFine != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 13); // the 13 is arbitrary
        }

        try
        {
            mMap.setMyLocationEnabled(true); // Turn showing device location on
        }
        catch(SecurityException e)
        {
            Log.e("Elec0", "Location error security exception", e);
            // Do nothing because we can't see the device's location data, and that isn't critical for the app to work.
        }

        LatLngBounds bounds = new LatLngBounds(new LatLng(36.976343, -122.072109), new LatLng(37.004803, -122.041124));


        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0));

        // Preventing the map from switching to the marker clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpened != null) {
                    // Close the info window
                    lastOpened.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpened.equals(marker)) {
                        // Nullify the lastOpened object
                        lastOpened = null;
                        // Return so that the info window isn't opened again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last opened such that we can close it later
                lastOpened = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

        loadBusStops();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void loadBusStops()
    {
        int height = 50, width = 50;
        for(BusStopData b : innerLoopList)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.slugroute_gold, getTheme());
            Bitmap bit = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bit, width, height, false);

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(b.getLoc())
                    .title(b.getName())
                    .snippet("Inner Loop")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
        for(BusStopData b : outerLoopList)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.slugroute_blue, getTheme());
            Bitmap bit = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bit, width, height, false);

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(b.getLoc())
                    .title(b.getName())
                    .snippet("Outer Loop")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }



    class BusUpdateAsync extends AsyncTask<String, String, String>
    {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            try
            {
                URL url = new URL("http://bts.ucsc.edu:8081/location/get");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try
                {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String stream = readStream(in);
                    return stream;

                }
                catch(Exception e)
                {
                    Log.e("Elec0", "BusUpdate Error 1", e);
                }
                finally
                {
                    urlConnection.disconnect();
                }
            }
            catch(Exception e)
            {
                Log.e("Elec0", "BusUpdate Error 2", e);
            }
            return null;
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            busGPSCallback(result);
        }
    }
}
