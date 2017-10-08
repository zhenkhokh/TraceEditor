function dosmth(){
	alert("hello");
}
function setUpMenuEvents() {
	// next for home
    $('#settings').click(function() {
    	//alert("hello settings");
        menuHandler.nextView("settings");
    }); 
    $('#toTrace').click(function() {
    	//alert("hello toTrace");
        menuHandler.nextView("toTrace");
    }); 
    $('#trace').click(function() {
    	//alert("hello trace");
        menuHandler.nextView("trace");
    });    
    $('#editPoint').click(function() {
    	//alert("hello editPoint");
        menuHandler.nextView("editPoint");
    });
    $('#editTrace').click(function() {
    	//alert("hello editTrace");
        menuHandler.nextView("editTrace");
    });
    $('#coordinateGo').click(function() {        
    	//alert("hello coordinateGo");
    	menuHandler.nextView("coordinateGo");
    				  }); 
    $('#geo').click(function() {        
    	//alert("hello geo");
    	menuHandler.nextView("geo");
    				  });   
    // home, map, points, addWaypoints for geo
    $('#home').click(function() {
    	//alert("hello home");
        menuHandler.nextView("home");
                      });
    $('#map').click(function() {        
    	//alert("hello map");
    	menuHandler.nextView("map");
                      }); 
    $('#points').click(function() {        
    	//alert("hello points");
    	menuHandler.nextView("points");
                      });
    $('#savePoint').click(function() {        
    	//alert("hello savePoint");
    	menuHandler.nextView("savePoint");
                      });
    $('#addWaypoints').click(function() {        
    	//alert("hello addWaypoints");
    	menuHandler.nextView("addWaypoints");
    				  });
    // home, trace, geo for latLng ...
    // home, geo,saveTrace, trace, goPosition for map     
    $('#saveTrace').click(function() {
    	//alert("hello saveTrace");
        menuHandler.nextView("saveTrace");
                      });
    // home, resetTrace, geoAddPoint, maps for trace 
    $('#resetTrace').click(function() {
    	//alert("hello resetTrace");
        menuHandler.nextView("resetTrace");
                      });
    $('#geoAddPoint').click(function() {
    	//alert("hello geoAddPoint");
        menuHandler.nextView("geoAddPoint");
                      });
    $('#goPosition').click(function() {
    	//alert("hello goPosition");
        menuHandler.nextView("goPosition");
                      });
    // home, rename, remove for edit
    $('#rename').click(function() {
    	//alert("hello rename");
        menuHandler.nextView("rename");
                      });
    $('#remove').click(function() {
    	//alert("hello remove");
        menuHandler.nextView("remove");
                      });
    // home, addWaypoints for waypoints 
    // ...
}