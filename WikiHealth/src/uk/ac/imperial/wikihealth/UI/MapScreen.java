package uk.ac.imperial.wikihealth.UI;

import uk.ac.imperial.wikihealth.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity showing the current position of the user on Google Maps
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class MapScreen extends SherlockFragmentActivity {
 
    private GoogleMap mMap;	//Used to communicate with map view
    private LocationManager mLocationManager;	//Used for finding the users location
	private ActionbarBuilder mActionbarBuilder=new ActionbarBuilder("Map");	//Action Bar handler 
    	
	/**
	 * Called when activity is created
	 */
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 int resultCode =	 GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	 if (resultCode != ConnectionResult.SUCCESS) {
    		 Toast.makeText(this, "This device does not have Google Play support.", 
    		          Toast.LENGTH_LONG).show();
    		     finish();
    		     return;
    		}
    	 //Set View of the Activity
    	 setContentView(R.layout.map);
    	 
    	 //Add elements on the nap
    	 if (mMap == null) {
    		 	//Get map object to add elements to it
    		 	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

    		 	//Set GoogleMap to map view
    	        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	        
    	        //Get location information from device's location service
    	        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	        Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	        double latitude = lastLocation.getLatitude();
    	        double longitude = lastLocation.getLongitude();
    	        LatLng lastKnownLocation = new LatLng(latitude, longitude);
    	        
    	        //Show an icon to the user's last known location
    	        Marker userMarker  = mMap.addMarker(new MarkerOptions()
						    	        .position(lastKnownLocation)
						    	        .title("You are here")
						    	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
						    	        .snippet("This is your last recorded location"));
    	        
    	        
    	        //Animate camera to that point and zoom in to level 12(closest)
    	        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLocation));
    	        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 3000, null);
    	        userMarker.getPosition();
    	 }
    }

    //Initialize ActionBar 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu=mActionbarBuilder.addMenuItems(menu);
    	return super.onCreateOptionsMenu(menu);
    }
	
	//Handle ActionBar Events
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(mActionbarBuilder.handleMenuClicks(item, this)){
    		finish();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }

}
