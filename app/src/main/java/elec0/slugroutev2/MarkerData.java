package elec0.slugroutev2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerData
{
    private int id;
    private Marker marker;
    private LatLng loc;

    public String getType()
    {return type;}

    public void setType(String type)
    {this.type = type;}

    private String type;

    public MarkerData(int id, Marker marker, LatLng loc, String type)
    {
        this.id = id;
        this.marker = marker;
        this.loc = loc;
        this.type = type;
    }
    public MarkerData(int id, Marker marker, double lat, double lon, String type)
    {
        this(id, marker, new LatLng(lat, lon), type);
    }

    public void updatePosition()
    {
        marker.setPosition(loc);
    }


    public int getID()
    {return id;}

    public void setID(int id)
    {this.id = id;}

    public Marker getMarker()
    {return marker;}

    public void setMarker(Marker marker)
    {this.marker = marker;}

    public LatLng getLoc()
    {return loc;}

    public void setLoc(LatLng loc)
    {this.loc = loc;}

    public double getLat()
    {return loc.latitude;}

    public double getLon()
    {return loc.longitude;}

    public void setLon(double lon)
    { loc = new LatLng(loc.latitude, lon); }

    public void setLat(double lat)
    { loc = new LatLng(lat, loc.longitude); }
}
