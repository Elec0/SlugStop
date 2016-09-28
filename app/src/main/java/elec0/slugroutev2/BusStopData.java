package elec0.slugroutev2;

import com.google.android.gms.maps.model.LatLng;

public class BusStopData
{
    private int id;
    private LatLng loc;
    private String name;

    BusStopData(int id, LatLng loc, String name)
    {
        this.id = id;
        this.loc = loc;
        this.name = name;
    }
    BusStopData(int id, double lat, double lon, String name)
    {
        this(id, new LatLng(lat, lon), name);
    }

    public int getID()
    {
        return id;
    }

    public void setID(int id)
    {
        this.id = id;
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
