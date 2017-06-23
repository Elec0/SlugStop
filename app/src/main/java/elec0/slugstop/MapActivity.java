package elec0.slugstop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean pauseTimer = false;
    private List<MarkerData> busList;
    private List<MarkerData> markerList;
    private List<BusStopData> innerLoopList, outerLoopList, nightOwlList, miscList;
    private boolean firstRun = true;
    private SharedPreferences prefs = null;
    private Marker lastOpened = null;
    private int busIDHighlighted = -1, lastBusIDHighlighted = -1;

    // Runs without a timer by reposting this handler at the end of the runnable
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
        nightOwlList = new ArrayList<>();
        miscList = new ArrayList<>();

        prefs = getSharedPreferences("elec0.slugstop", MODE_PRIVATE);
        
        // Populate the inner loop stops
        innerLoopList.add(new BusStopData(new LatLng( 36.9999313354492, -122.062049865723), "McLaughlin & Science Hill"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9967041015625, -122.063583374023), "Heller & Kerr Hall"));
        innerLoopList.add(new BusStopData(new LatLng( 36.999210357666, -122.064338684082), "Heller & Kresge College"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9999313354492, -122.062049865723), "McLaughlin & Science Hill"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9997062683105, -122.05834197998), "McLaughlin & College 9 & 10 - Health Center"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9966621398926, -122.055480957031), "Hagar & Bookstore"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9912567138672, -122.054962158203), "Hagar & East Remote"));
        innerLoopList.add(new BusStopData(new LatLng( 36.985523223877, -122.053588867188), "Hagar & Lower Quarry Rd"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9815368652344, -122.052131652832), "Coolidge & Hagar"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9787902832031, -122.057762145996), "High & Western Dr"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9773025512695, -122.054328918457), "High & Barn Theater"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9826698303223, -122.062492370605), "Empire Grade & Arboretum"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9905776977539, -122.066116333008), "Heller & Oakes College"));
        innerLoopList.add(new BusStopData(new LatLng( 36.9927787780762, -122.064880371094), "Heller & College 8 & Porter"));
        // Remote bus stops. Should actually add a new category called upper campus
        innerLoopList.add(new BusStopData(new LatLng(36.988537, -122.064799), "West Remote Parking Lot"));
        innerLoopList.add(new BusStopData(new LatLng(36.990786, -122.052190), "East Remote Parking Lot"));

        // Outer loop stops
        outerLoopList.add(new BusStopData(new LatLng( 36.9992790222168, -122.064552307129), "Heller & Kresge College"));
        outerLoopList.add(new BusStopData(new LatLng( 37.0000228881836, -122.062339782715), "McLaughlin & Science Hill"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9999389648438, -122.058349609375), "McLaughlin & College 9 & 10 - Health Center"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9990234375, -122.055229187012), "McLaughlin & Crown College"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9974822998047, -122.055030822754), "Hagar & Bookstore-Stevenson College"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9942474365234, -122.055511474609), "Hagar & Field House East"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9912986755371, -122.054656982422), "Hagar & East Remote"));
        outerLoopList.add(new BusStopData(new LatLng( 36.985912322998, -122.053520202637), "Hagar & Lower Quarry Rd"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9813537597656, -122.051971435547), "Coolidge & Hagar"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9776763916016, -122.053558349609), "Coolidge & Main Entrance"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9786148071289, -122.05785369873), "High & Western Dr"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9798469543457, -122.059257507324), "Empire Grade & Tosca Terrace"));
        outerLoopList.add(new BusStopData(new LatLng( 36.9836616516113, -122.064964294434), "Empire Grade & Arboretum"));
        outerLoopList.add(new BusStopData(new LatLng( 36.989917755127, -122.067230224609), "Heller & Oakes College"));
        outerLoopList.add(new BusStopData(new LatLng( 36.991828918457, -122.066833496094), "Heller & Family Student Housing"));
        outerLoopList.add(new BusStopData(new LatLng( 36.992977142334, -122.065223693848), "Heller & College 8 & Porter"));

        // Night Owl Stops
        nightOwlList.add(new BusStopData(new LatLng(36.966213, -122.039845), "Mission & Bay Out"));
        nightOwlList.add(new BusStopData(new LatLng(36.966923, -122.040621), "Mission & Bay In"));
        nightOwlList.add(new BusStopData(new LatLng(36.971551, -122.026006), "Pacific & Cathcart"));

        // Tutorial Stop
        miscList.add(new BusStopData(new LatLng(37.003311, -122.059843), "Tutorial Stop"));
    }

    private void updateBusGPS()
    {
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
                    MarkerData bus = new MarkerData(jsonObj.getInt("id"), jsonObj.getDouble("lon"), jsonObj.getDouble("lat"), jsonObj.getString("type"));
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
            // If no busses are running, which is possible late at night, we want to show a message saying that, but we only want to do it once per times the app is launched
            if(firstRun)
            {
                String msg = "It appears no busses are running at this time.\nCheck slugroute.com if you think SlugStop isn't working correctly.";
                showDialog(msg, "OK");
                firstRun = false;
            }

            if(markerList.size() != 0)
            {
                for (MarkerData m : markerList)
                {
                    m.getMarker().remove();
                    m.getDirectionMarker().remove();
                }
                markerList.clear();
            }
            return;
        }
        for(MarkerData b : busList)
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
                if (!selectedMarker.getType().equals(b.getType()) || lastBusIDHighlighted == b.getID())
                {
                    markerList.remove(selectedMarker);
                    selectedMarker.getMarker().remove();
                    selectedMarker.getDirectionMarker().remove();
                    selectedMarker = null;
                    lastBusIDHighlighted = -1; // Clear this once we've reset the icon
                }
            }

            if(selectedMarker != null) // Move the marker
            {
                selectedMarker.setLoc(b.getLoc());
                animateMarker(selectedMarker, b.getLoc(), false);
            }
            else // Create the marker
            {
                int icon = 0;
                String snippet = "";
                boolean h = !(busIDHighlighted == b.getID());
                switch(b.getType().toUpperCase())
                {
                    case "LOOP":
                        icon = (h ? R.drawable.slugroute_loop : R.drawable.slugroute_loop_highlight);
                        break;
                    case "UPPER CAMPUS":
                        icon = (h ? R.drawable.slugroute_upper : R.drawable.slugroute_upper_highlight);
                        break;
                    case "NIGHT OWL":
                        icon = (h ? R.drawable.slugrotue_nightowl : R.drawable.slugroute_nightowl_highlight);
                        snippet = "Stops at all metro stops";
                        break;
                    case "LOOP OUT OF SERVICE AT BARN THEATER":
                    case "OUT OF SERVICE/SORRY":
                        icon = (h ? R.drawable.slugroute_out : R.drawable.slugroute_out_highlight);
                        break;
                    default:
                        icon = (h ? R.drawable.slugroute_special : R.drawable.slugroute_special_highlight);
                        break;
                }

                Marker busMarker = mMap.addMarker(new MarkerOptions()
                            .position(b.getLoc())
                            .title(b.getType())
                            .zIndex(0.5f)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(icon)));
                if(!snippet.equals(""))
                    busMarker.setSnippet(snippet);

                Marker directionMarker = mMap.addMarker(new MarkerOptions()
                        .position(b.getLoc())
                        .zIndex(0.1f)
                        .anchor(0.5f, 0.5f)
                        .rotation(0)
                        .visible(false) // Start not visible, since we have no direction data
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_arrow)));

                MarkerData md = new MarkerData(b.getID(), busMarker, b.getLoc(), b.getType());
                md.setDirectionMarker(directionMarker);
                markerList.add(md);
            }

            // If markerList contains any bus IDs that aren't in busList, remove them
            List<MarkerData> toRemove = new ArrayList<>();

            for(MarkerData mar : markerList)
            {
                boolean remove = true;
                for(MarkerData bus : busList)
                {
                    if(mar.getID() == bus.getID()) // It's in the list, don't remove it
                    {
                        remove = false;
                    }
                }
                if(remove) // At this point it's not in the bus list, so add it to the list to be removed from the main list
                {
                    toRemove.add(mar);
                }
            }
            for(MarkerData markerRemove : toRemove)
            {
                markerRemove.getMarker().remove();
                markerRemove.getDirectionMarker().remove();
                markerList.remove(markerRemove);
                Log.d("Elec0", "Remove marker id " + markerRemove.getID());
            }
        }
    }

    private void loadBusStops()
    {
        int height = 50, width = 50;
        createMarker(innerLoopList, R.drawable.slugroute_gold, "Inner Loop", width, height);
        createMarker(outerLoopList, R.drawable.slugroute_blue, "Outer Loop", width, height);
        createMarker(nightOwlList, R.drawable.slugroute_purple, "Night Owl", width, height);
        createMarker(miscList, R.drawable.slugroute_question, "Click here to view message", (int)(width*1.5f), (int)(height*1.5f));
    }

    /***
     * Create markers associated with a list of BusStopDatas.
     * @param list List of BusStopData
     * @param icon Drawable resource
     * @param snippet Text value for snippet
     * @param width Width of icon
     * @param height Height of icon
     */
    private void createMarker(List<BusStopData> list, int icon, String snippet, int width, int height)
    {
        for(BusStopData b : list)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(icon, getTheme());
            Bitmap bit = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bit, width, height, false);

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(b.getLoc())
                    .title(b.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(!pauseTimer)
            pauseTimer = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(pauseTimer)
            pauseTimer = false;

        if (prefs.getBoolean("firstrun", true))
        {
            // First time ever run tutorial
            showDialog("The circles are busses, the diamonds are bus stops.", "OK");
            showDialog("Tap on any marker to see more information about it.", "OK");

            prefs.edit().putBoolean("firstrun", false).apply(); // if this runs more than once, use .commit()
        }
        if(prefs.getBoolean("secondrun", true))
        {
            // Add more info, for after they saw it the first time.
            showDialog("Tap on a bus, then tap on the info window for that bus to highlight it so it can be easily located.", "OK");
            showDialog("Tap on the window again to un-highlight it.", "OK");

            prefs.edit().putBoolean("secondrun", false).apply();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // Permission granted, turn on location. This is only required for the first run
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            try
            {
                mMap.setMyLocationEnabled(true);
            }
            catch(SecurityException e)
            {
                // This should never happen.
            }
        }
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

                // Should check here if the marker clicked is a direction one, if so don't do anything

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

        // This is the white window that shows up once you click on a marker
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                if(marker.getTitle().equals("Tutorial Stop"))
                {
                    // The windows all show up at once, so display them last-to-first so they show up in the correct order.
                    // I'm sure there's a way to wait for each one to finish displaying, but this works.
                    showDialog("Tap on the window again to un-highlight it.", "OK");
                    showDialog("Tap on a bus, then tap on the info window for that bus to highlight it so it can be easily located.", "OK");
                    showDialog("The circles are busses, the diamonds are bus stops.", "OK");
                    showDialog("Tap on any marker to see more information about it.", "OK");
                    return;
                }
                // This is O(2m+b), which isn't that bad, I guess...
                // It could absolutely get better since I merged BusData and MarkerData, though
                for(MarkerData m : markerList)
                {
                    if(m.getMarker().getId().equals(marker.getId()))
                    {
                        Log.d("Elec0", "Found marker " + marker.getId());
                        MarkerData linkedBus = null, oldHiBus = null;
                        for(MarkerData b : busList)
                        {
                            // Find the bus we're trying to highlight
                            if(b.getID() == m.getID())
                            {
                                linkedBus = b;
                                break;
                            }
                        }

                        // If there was a bus that was highlighted, find the linked MarkerData and remove it so the marker itself updates

                        for(MarkerData oldM : markerList)
                        {
                            if(oldM.getID() == linkedBus.getID())
                            {
                                markerList.remove(oldM);
                                oldM.getMarker().remove();
                                oldM.getDirectionMarker().remove();
                                break;
                            }
                        }

                        /*if(busIDHighlighted == linkedBus.getID())
                            busIDHighlighted = -1;
                        else*/
                        if(busIDHighlighted != linkedBus.getID())
                        {
                            lastBusIDHighlighted = busIDHighlighted;
                            busIDHighlighted = linkedBus.getID();
                        }
                        else // The user tapped the same bus a second time to de-highlighgt it
                        {
                            lastBusIDHighlighted = -1;
                            busIDHighlighted = -1;
                        }
                        busUpdateMarker();
                        break;
                    }
                }
                // Check if marker is a bus, if so update icon with a change in hue or something.


            }
        });

        loadBusStops();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    // GENERAL METHODS BELOW

    public void showDialog(String msg, String confirmMsg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(confirmMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        try
        {
            //if (isFinishing()) {
                alert.show();
            //}
        }
        catch(WindowManager.BadTokenException e)
        {
            Log.e("Elec0", e.getStackTrace().toString());
            Toast.makeText(getApplicationContext(), "There has been an error showing a dialog.", Toast.LENGTH_SHORT);
        }
    }

    /***
     * Method to animate marker movement. Got this off of StackOverflow, I think.
     * Meant to grab the url but it's too late now
     * @param markerData The MarkerData class that contains the Marker object
     * @param toPosition Position to move the marker to
     * @param hideMarker If the marker should be hidden or not
     */
    public void animateMarker(final MarkerData markerData, final LatLng toPosition, final boolean hideMarker)
    {
        final Marker marker = markerData.getMarker();
        final Marker markerD = markerData.getDirectionMarker(); // Handle moving the direction arrow too

        Location from = new Location("");
        Location to = new Location("");
        from.setLatitude(marker.getPosition().latitude);
        from.setLongitude(marker.getPosition().longitude);
        to.setLatitude(toPosition.latitude);
        to.setLongitude(toPosition.longitude);

        double dist = from.distanceTo(to);
        // If the app has been paused for a while it's possible a bus has moved really far, and we don't want to use that movement for the direction arrow
        boolean movedFar = false;
        if(dist > 200) // I think this is suitably large
            movedFar = true;

        // Don't move if there's nowhere to move. Prevents the shaking when busses aren't moving
        // Also don't update if the bus is moving a really small amount. This usually comes from errors in the GPS unit on the bus
        if(marker.getPosition().equals(toPosition) || dist < 2)
        {
            return;
        }


        final Handler handler = new Handler();
        Projection proj = mMap.getProjection();

        final long start = SystemClock.uptimeMillis();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        // Calculate the new rotation angle based on the difference between the two marker positions
        final float endRotation = from.bearingTo(to);
        markerD.setVisible(!movedFar); // If it's moved far, then don't show it this loop
        if(!movedFar)
            markerD.setRotation(endRotation);

        final Interpolator interpolator = new LinearInterpolator();
        final long duration = 500; // Time in ms for the animation to take

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));
                markerD.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    marker.setVisible(!hideMarker);
                    markerD.setVisible(!hideMarker);
                }
            }
        });
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
                    return readStream(in);

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
                //return "[{\"id\":\"90\",\"lon\":-122.0542,\"lat\":36.99102,\"type\":\"UPPER CAMPUS\"},{\"id\":\"80\",\"lon\":-122.054436,\"lat\":36.977352,\"type\":\"LOOP\"},{\"id\":\"83\",\"lon\":-122.05556,\"lat\":36.995377,\"type\":\"LOOP\"},{\"id\":\"79\",\"lon\":-122.05759,\"lat\":36.978577,\"type\":\"LOOP\"},{\"id\":\"96\",\"lon\":-122.06477,\"lat\":36.988667,\"type\":\"UPPER CAMPUS\"},{\"id\":\"93\",\"lon\":-122.053154,\"lat\":36.979633,\"type\":\"SPECIAL\"},{\"id\":\"86\",\"lon\":-122.05362,\"lat\":36.977722,\"type\":\"LOOP\"},{\"id\":\"92\",\"lon\":-122.05113,\"lat\":36.990925,\"type\":\"OUT OF SERVICE/SORRY\"},{\"id\":\"95\",\"lon\":-122.062,\"lat\":36.999996,\"type\":\"LOOP\"},{\"id\":\"82\",\"lon\":-122.05331,\"lat\":36.979767,\"type\":\"OUT OF SERVICE/SORRY\"}]";
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            busGPSCallback(result);
        }
    }

    // ***** General Functions *****

    /**
     * Gets width of drawable
     * @param resources
     * @param id
     * @return
     */
    private int getDrawableWidth(Resources resources, int id){
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        return bitmap.getWidth();
    }
    /**
     * Gets height of drawable
     * @param resources
     * @param id
     * @return
     */
    private int getDrawableHeight(Resources resources, int id){
        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
        return bitmap.getHeight();
    }
}
