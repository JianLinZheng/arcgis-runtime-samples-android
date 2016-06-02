package com.example.changeviewpoint;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int SCALE = 5000;
    private static String TAG = "ChangeViewPoint";
    private MapView mMapView;
    private SpatialReference spatialReference;
    private Button mGeometryButton, mCenterScaleButton, mAnimateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inflate MapView from layout
        mMapView = (MapView) findViewById(R.id.mapView);
        // create a map with the BasemapType topographic
        ArcGISMap mMap = new ArcGISMap(Basemap.createImageryWithLabels());
        // set the map to be displayed in this view
        mMapView.setMap(mMap);
        // create spatial reference for all points
        spatialReference = SpatialReference.create(2229);
        // create point for starting location - London
        Point startPoint = new Point(28677947.756181,22987445.6186465, spatialReference);

        //set viewpoint of map view to starting point and scaled
        mMapView.setViewpointCenterWithScaleAsync(startPoint, SCALE);

        // inflate the Buttons from the layout
        mGeometryButton = (Button) findViewById(R.id.geometryButton);
        mCenterScaleButton = (Button) findViewById(R.id.centerScaleButton);
        mAnimateButton = (Button) findViewById(R.id.animateButton);

        // create geometry of Griffith Park from JSON raw file, add graphics and set viewpoint of the map view to Griffith Park
        mGeometryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an input stream for the raw text file containing JSON of Griffith Park
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier("griffithparkjson",
                                "raw", getPackageName()));

                InputStreamReader inputReader = new InputStreamReader(ins);

                BufferedReader bufferReader = new BufferedReader(inputReader);
                String line;
                StringBuilder text = new StringBuilder();

                // read the text file
                try {
                    while (( line = bufferReader.readLine()) != null) {
                        text.append(line);
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
                String JsonString = text.toString();

                // create Geometry from JSON
                Geometry geometry = Geometry.fromJson(JsonString, spatialReference);
                GraphicsOverlay overlay = new GraphicsOverlay();
                // add graphics overlay on map view
                mMapView.getGraphicsOverlays().add(overlay);
                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, Color.GREEN, null);
                // add graphic of Griffith Park
                overlay.getGraphics().add(new Graphic(geometry, fillSymbol));

                // set viewpoint of map view to Geometry - Griffith Park
                mMapView.setViewpointGeometryAsync(geometry);

                mGeometryButton.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                mAnimateButton.setBackgroundColor(getResources().getColor(R.color.primary));
                mCenterScaleButton.setBackgroundColor(getResources().getColor(R.color.primary));
            }
        });

        mAnimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the London location point
                Point londonPoint = new Point(28677947.756181,22987445.6186465, spatialReference);
                // create the viewpoint with the London point and scale
                Viewpoint viewpoint = new Viewpoint(londonPoint, SCALE);
                // set the map views's viewpoint to London with a ten second duration
                mMapView.setViewpointWithDurationAsync(viewpoint, 10);

                mGeometryButton.setBackgroundColor(getResources().getColor(R.color.primary));
                mAnimateButton.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                mCenterScaleButton.setBackgroundColor(getResources().getColor(R.color.primary));
            }
        });

        mCenterScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the Waterloo location point
                Point waterlooPoint = new Point(28681235.9843606, 22990575.7224154, spatialReference);
                // set the map views's viewpoint centered on Waterloo and scaled
                mMapView.setViewpointCenterWithScaleAsync(waterlooPoint, SCALE);

                mGeometryButton.setBackgroundColor(getResources().getColor(R.color.primary));
                mAnimateButton.setBackgroundColor(getResources().getColor(R.color.primary));
                mCenterScaleButton.setBackgroundColor(getResources().getColor(R.color.primary_dark));
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }
}
