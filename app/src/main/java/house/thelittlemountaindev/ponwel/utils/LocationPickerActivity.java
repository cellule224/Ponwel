package house.thelittlemountaindev.ponwel.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import house.thelittlemountaindev.ponwel.R;

public class LocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback{

    private double markerLat;
    private double markerLon;
    private GoogleMap gMap;
    private String activityTitle;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private Polygon polygon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker);


        Button btnCloseMap = findViewById(R.id.btn_close_map);
        btnCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PolyUtil.containsLocation(markerLat, markerLon, polygon.getPoints(), false)){
                    returnResults();
                }else {
                    Toast.makeText(LocationPickerActivity.this, "Livraison non disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    private void returnResults() {
        Intent data = new Intent();
        data.putExtra("lat", markerLat);
        data.putExtra("lon", markerLon);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

         polygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(9.500711, -13.714073),
                        new LatLng(9.588430, -13.593270),
                        new LatLng(9.594493, -13.599647),
                        new LatLng(9.641220, -13.554468),
                        new LatLng(9.655611, -13.522944),
                        new LatLng(9.682830, -13.530876),
                        new LatLng(9.685602, -13.553450),
                        new LatLng(9.678429, -13.567459),
                        new LatLng(9.626606, -13.633322),
                        new LatLng(9.577308, -13.665825),
                        new LatLng(9.560872, -13.667728),
                        new LatLng(9.508491, -13.725358)));
        polygon.setTag("beta");
        //polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(COLOR_GREEN_ARGB);
        //polygon.setFillColor(COLOR_PURPLE_ARGB);


        gMap = googleMap;

        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        //gMap.setMyLocationEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(9.649, -13.578), 12f);
        gMap.moveCamera(cameraUpdate);
        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                markerLat = gMap.getCameraPosition().target.latitude;
                markerLon = gMap.getCameraPosition().target.longitude;

            }
        });
    }
}

