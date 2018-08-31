package ru.android.zheka.gmapexample1;

import java.util.ArrayList;
import java.util.List;

import ru.android.zheka.route.Route;
import ru.android.zheka.route.Segment;

public class ResultRouteHandler {
    List<Route> routes = new ArrayList <Route> ();// no need synch it is done by minitor
    int routeSize = -1;//extraPointsNum + 1

    public ResultRouteHandler(int routeSize){
        this.routeSize = routeSize;
    }

    public void addRouteIgnoreNull(Route route) { if (route!=null) routes.add(route);}
    public void addAllRoutesIgnoreNull(List<Route> routes) { if (routes!=null) this.routes.addAll (routes);}

    public List <Route> getRoutes() {
        return routes;
    }

    public boolean isAvailable() {
        return routeSize == routes.size ()?true:false;
    }

    public int getLength(){
        int length=0;
        for (Route route:routes){
            for (Segment segment:route.getSegments ()){
                length += segment.getLength ();
            }
        }
        return length;
    }
}
