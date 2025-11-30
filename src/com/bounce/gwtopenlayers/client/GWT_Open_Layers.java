package com.bounce.gwtopenlayers.client;

import java.util.Date;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.LayerLoadEndListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.JsonLayerCreator;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;

//import com.bounce.heregwt.client.GreetingService;
//import com.bounce.heregwt.client.GreetingServiceAsync;
//import com.bounce.heregwt.client.HereMapJnsi;
//import com.bounce.heregwt.shared.HereRouteInfoBean;
//import com.bounce.heregwt.shared.HereRouteLegBean;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Open_Layers implements EntryPoint {

    private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
    private Map map;
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    public void onModuleLoad() {
		
		MapOptions mapOptions = new MapOptions();
		mapOptions.setNumZoomLevels(16);
		MapWidget mapWidget = new MapWidget("700px", "600px", mapOptions);
		
		// Create a map layer that will be the street map.
		OSM osmMapnik = OSM.Mapnik("Mapnik");
		osmMapnik.setIsBaseLayer(true);
			
		// Create a map layer that will be the Colorado counties. This layer will overlay the street map.
        // Read polygons for each CO county from a file (colorado-counties.geojson).
		// Each county(polygon) is a Feature. Once the entire file is loaded, these Features can accessed and their fill color changed.
		// When the layer is redrawn, the counties will be drawn in the new fill colors.
	    final Vector polyLayer = JsonLayerCreator.createLayerFromJson("CO Counties Layer", "data/colorado-counties.geojson");
	    	        	    
        map = mapWidget.getMap();
        
        //First create a select control and make sure it is activated
        SelectFeature selectFeature = new SelectFeature(polyLayer);
        selectFeature.setAutoActivate(true);
        selectFeature.setToggle(true);
        selectFeature.setClickOut(true);
        selectFeature.setSelectStopDown(false);
                
        map.addControl(selectFeature);
        
        // Display the popup for a Feature when that Feature is selected
        polyLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
            public void onFeatureSelected(FeatureSelectedEvent eventObject) {
            	
            	map.addPopup(eventObject.getVectorFeature().getPopup());
            }
        });

        //And add a VectorFeatureUnselectedListener which removes the popup.
        polyLayer.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener()
        {
            public void onFeatureUnselected(FeatureUnselectedEvent eventObject)
            {
            	map.removePopup(eventObject.getVectorFeature().getPopup());
            }
        });
      
        // Add a listener that detects when the layer has been completely loaded from the file so we know that all
        // of the features are now accessible.
        polyLayer.addLayerLoadEndListener(new LayerLoadEndListener() {

			@Override
			public void onLoadEnd(LoadEndEvent eventObject) {
				
				// Define style for the push pin		        
		        Style pinStyle = new Style();
		        pinStyle.setLabelXOffset(10);
		        pinStyle.setLabelYOffset(10);
		        pinStyle.setLabelAlign("lb");
		        pinStyle.setFontColor("#0000FF");
		        pinStyle.setGraphicSize(16, 16);
		        pinStyle.setExternalGraphic("http://www.kyreneinternalmedicine.com/images/location.png");
		        pinStyle.setFillOpacity(1.0);
		
			    // Define red, yellow, and green fill styles
			    Style redFillStyle = new Style();
			    redFillStyle.setFillColor("#FA0000"); // RGB
			    
			    Style greenFillStyle = new Style();
			    greenFillStyle.setFillColor("#00FA00");
			    
			    Style yellowFillStyle = new Style();
			    yellowFillStyle.setFillColor("#F4E868");

			    for (VectorFeature county : polyLayer.getFeatures()) {
			    	
				    // Set the fill style for each county based on it's name
			    	String countyName = county.getAttributes().getAttributeAsString("name");
		    	
			    	if (countyName.compareToIgnoreCase("JJ") < 1) {
			    		county.setStyle(redFillStyle);
			    	} else if (countyName.compareToIgnoreCase("SS") < 1) {
			    		county.setStyle(greenFillStyle);
			    	} else {
			    		county.setStyle(yellowFillStyle);
			    	}
			    	
			    	// Add a popup for each county that will be displayed when the user
			    	// hovers over it
			        final Popup popup = new FramedCloud(countyName + "_popup", county.getCenterLonLat(), null,
			                countyName, null, false);
			        popup.setPanMapIfOutOfView(true);
			        popup.setAutoSize(true);
			        county.setPopup(popup);
			        
			        // Add a selectable pin to the center of each county that will 
			        // have its own popup when selected.
			        LonLat centerLonLat = county.getCenterLonLat();
			        Point p = new Point(centerLonLat.lon(), centerLonLat.lat());		        
			    	
			        final VectorFeature pointFeature = new VectorFeature(p, pinStyle);
			        pointFeature.setFeatureId(countyName + "_Pin");
			        polyLayer.addFeature(pointFeature);
			        
	                final Popup pinPopup = new FramedCloud(countyName + "_Pin_Popup", pointFeature.getCenterLonLat(), null, "<h1>"+countyName+"</H1><BR/>Pin Popup", null, false);
	                popup.setPanMapIfOutOfView(true);
	                popup.setAutoSize(true);
	                pointFeature.setPopup(pinPopup);   
			    }
			    polyLayer.redraw();				
			}	
        });
        
        	
        map.addLayer(osmMapnik);
	    map.addLayer(polyLayer);
	     
	    // Center the map over Colorado, and zoom to an appropriate level
	    LonLat lonLat = new LonLat(-105.57, 38.69);
	    lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), mapWidget.getMap().getProjection());
	    map.setCenter(lonLat, 7);
		RootPanel.get("gwtroot").add(mapWidget);
		
		Button exportButton = new Button("Export Map");
		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				JavascriptInterface.exportMap(map.getJSObject());
				
			}
		});
		RootPanel.get("gwtroot").add(exportButton);
		
		Date now = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm zzzz");
		Label dateLabel = new Label("Sending Date: " + dtf.format(now));
		RootPanel.get("gwtroot").add(dateLabel);
		
		greetingService.sendDate(now, new AsyncCallback<Long>() {
			
			public void onFailure(Throwable caught) {
				DialogBox dialogBox = new DialogBox();
				dialogBox.setText("RPC Failure: " + caught.getMessage());
				dialogBox.center();
			}

			public void onSuccess(Long returnVal) {
			}
				
		});	

		
	}
}

