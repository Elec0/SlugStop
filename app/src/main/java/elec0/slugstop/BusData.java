package elec0.slugstop;

import com.google.android.gms.maps.model.LatLng;

public class BusData
{
    private int id;
    private LatLng loc;
    private String type;

    public BusData(int id, double lon, double lat, String type)
    {
        this.id = id;
        loc = new LatLng(lat, lon);
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Bus id: " + id + ", lon: " + loc.longitude + ", lat: " + loc.latitude + ", type: " + type;
    }

    public int getID()
    { return id; }
    public void setID(int id)
    { this.id = id;}
    public double getLon()
    { return loc.longitude; }
    public double getLat()
    { return loc.latitude; }
    public void setLon(double lon)
    { loc = new LatLng(loc.latitude, lon); }
    public void setLat(double lat)
    { loc = new LatLng(lat, loc.longitude); }
    public String getType()
    { return type; }
    public void setType(String type)
    { this.type = type; }
    public LatLng getLoc()
    { return loc; }
}
