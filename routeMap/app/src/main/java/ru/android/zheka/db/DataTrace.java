package ru.android.zheka.db;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.android.zheka.route.BellmannFord;

public class DataTrace{
	public ArrayList<String> extraPoints = new ArrayList<String>();
	private Iterator<List<LatLng>> cursor = null;

	@Override
	public String toString() {
		return "DataTrace{" +
				"extraPoints=" + extraPoints +
				", cursor=" + cursor +
				", allPoints=" + allPoints +
				", segmentPoints=" + segmentPoints +
				", restPoints=" + restPoints +
				", segments=" + segments +
				'}';
	}

	private PolylineOptions allPoints;
	private List<LatLng> segmentPoints = null;
	private List<LatLng> restPoints = null;
	private ArrayList<List<LatLng>> segments = null;

	public DataTrace(){
		allPoints = configPolyOptions ();
	}
	static public PolylineOptions configPolyOptions(){
		PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        return polyoptions;
	}

	public DataTrace copy(boolean initialization){
		DataTrace dataTrace = new DataTrace ();
		if (this.extraPoints==null)
			return null;
		dataTrace.extraPoints  = new ArrayList <> (extraPoints);
		if (segments==null)
			if(!initSegments ())
				return null;
		dataTrace.segments = new ArrayList <> (segments);
		if (initialization){
			UtilePointSerializer util = new UtilePointSerializer ();
			Iterator<String> it = extraPoints.iterator ();
			for (List<LatLng> segment:segments) {
				dataTrace.addPoints (segment);
				if (it.hasNext ()) {
					LatLng point = (LatLng) util.deserialize (it.next ());
					dataTrace.addPoint (point);
				}
			}
		}
		return dataTrace;
	}

	public List<LatLng> removeFirstSegment(){
		if (segments==null)
			return null;
		return segments.remove (0);
	}

	public void addPoints(List<LatLng> points){
		addPoints (points.toArray (new LatLng[0]));
	}

	public void addPoints(LatLng[] points){
		allPoints.add (points);
	}

	public void addPoint(String sPoint){
		LatLng point = (LatLng) new UtilePointSerializer ().deserialize (sPoint);
		addPoint (point);
	}

	public void addPoint(LatLng point){
		if (point!=null)
			allPoints.add (point);
	}

	public PolylineOptions getAllPoints() {
		return allPoints;
	}

	public List<LatLng> nextSegment(){
		if (segments==null)
			throw new NullPointerException ();
		if (cursor==null)
			cursor = segments.iterator ();
		if (cursor.hasNext ()){
			return cursor.next ();
		}
		throw new IndexOutOfBoundsException ();
	}

	public LatLng[] nextSegmentArray(){
		return nextSegment ().toArray (new LatLng[0]);
	}

	public boolean hasNext(){
		if (segments==null)
			return false;
		if (cursor==null)
			cursor = segments.iterator ();
		return cursor.hasNext ();
	}

	public void resetSegmentCursor (){
		if (segments==null)
			throw new NullPointerException ();
		cursor = segments.iterator ();
	}

//	public float getLength(){
//		return (float) BellmannFord.getPathLength (allPoints.getPoints ().toArray (new LatLng[0]));
//	}

	public boolean removeHead(LatLng removedWayPoint){
		if (getHeadOrTail (removedWayPoint,true)){
			allPoints = configPolyOptions ();
			addPoints (segmentPoints);
			return true;
		}
		return false;
	}

	public boolean initSegments(){
		List<LatLng> allPoints = this.allPoints.getPoints ();
		UtilePointSerializer utile = new UtilePointSerializer ();
		segments = new ArrayList <> ();
		for(String sPoint:extraPoints) {
			LatLng point = (LatLng) utile.deserialize (sPoint);
			if (!initSegment (point)){
				this.allPoints = configPolyOptions ();
				addPoints (allPoints);
				return false;
			}
		}
		segments.add (this.allPoints.getPoints ());
		this.allPoints = configPolyOptions ();
		addPoints (allPoints);
		return true;
	}

	private boolean initSegment(LatLng point){
		if (getHeadOrTail ( point, false)) {
			//segmentPoints.add(point);//link segments
			segments.add (segmentPoints);
			this.allPoints = configPolyOptions ();
			LatLng last = (LatLng) ((LinkedList)segmentPoints).getLast ();
			LatLng removed = (LatLng) ((LinkedList)restPoints).removeFirst ();
			if (last!=null)
				((LinkedList)restPoints).addFirst (last);//link segments
			else if (!removed.equals (point)){
				((LinkedList)restPoints).addFirst (removed);
			}else if (removed.equals (point)){
				System.out.println ("removed waypoints from segment:"+point);
			}

			addPoints (restPoints);
			return true;
		}
		if (allPoints.getPoints ().size () == segmentPoints.size ()) {
			segments.add (segmentPoints);
			restPoints.add (point); // end point
			return true;
		}
		return false;
	}

	private boolean getHeadOrTail(LatLng wayPoint,boolean forTail){
		boolean pass = forTail?false:true;
		LatLng bound = BellmannFord.round (wayPoint);
		segmentPoints = new LinkedList <> ();
		restPoints = new LinkedList <> ();
		for (LatLng point:allPoints.getPoints ()){
			if ( pass!=forTail && point.equals (wayPoint))//BellmannFord.round (point)
				pass = pass?false:true;
			if (pass)
				segmentPoints.add (point);
			else
				restPoints.add (point);
		}
		return pass==forTail;
	}
}
