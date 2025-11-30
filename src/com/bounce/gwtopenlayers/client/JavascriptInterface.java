package com.bounce.gwtopenlayers.client;

import org.gwtopenmaps.openlayers.client.util.JSObject;

public class JavascriptInterface {
	

	public static native void exportMap(JSObject map) /*-{
		
	  	var mapCanvas = $doc.getElementById('staticmap');
	    
	    var size = map.getSize();
	    mapCanvas.width = size.w
	    mapCanvas.height = size.h;
	    
	    var mapContext = mapCanvas.getContext('2d');
	    
	    $wnd.alert("map = " + map);
	    
	    Array.prototype.forEach.call(map.getViewport().querySelectorAll('.ol-layer canvas, canvas.ol-layer'), function (canvas) {
	    
	    $wnd.alert("2222");
	        if (canvas.width > 0) {
	          var opacity = canvas.parentNode.style.opacity || canvas.style.opacity;
	          mapContext.globalAlpha = opacity === '' ? 1 : Number(opacity);
	
	          var backgroundColor = canvas.parentNode.style.backgroundColor;
	          if (backgroundColor) {
	            mapContext.fillStyle = backgroundColor;
	            mapContext.fillRect(0, 0, canvas.width, canvas.height);
	          }
	
	          var matrix;
	          var transform = canvas.style.transform;
	          
	          if (transform) {
	          
	            matrix = transform.match(/^matrix\(([^\(]*)\)$/)[1].split(',').map(Number);
	              
	          } else {
	          
	            matrix = [parseFloat(canvas.style.width) / canvas.width, 0, 0,
	              parseFloat(canvas.style.height) / canvas.height, 0, 0,];
	            
	          }
	    $wnd.alert("33");
	          
	          CanvasRenderingConterrrxt2D.prototype.setTransform.apply(mapContext, matrix);
	          mapContext.drawImage(canvas, 0, 0);
	          
	        }
	    }	    	    
	  );
			    	    	    $wnd.alert("444444");
		
	}-*/;
}
