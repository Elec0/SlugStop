package elec0.slugstop;

import com.google.android.gms.maps.model.LatLng;

public class BusStopData
{
    private static int IDS = 0;
    private int id;
    private LatLng loc;
    private String name;

    BusStopData(LatLng loc, String name)
    {
        this.id = ++IDS;
        this.loc = loc;
        this.name = name;
    }
    BusStopData(double lat, double lon, String name)
    {
        this(new LatLng(lat, lon), name);
    }

    public int getID()
    {
        return id;
    }

    public LatLng getLoc()
    {
        return loc;
    }

    public void setLoc(LatLng loc)
    {
        this.loc = loc;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
