function dosmth(){
	alert("hello");
}
function setUpMenuEvents() {
	// next for home
    $('#settings').click(function() {
        menuHandler.nextView("settings");
    }); 
    $('#toTrace').click(function() {
        menuHandler.nextView("toTrace");
    }); 
    $('#trace').click(function() {
        menuHandler.nextView("trace");
    });    
    $('#editPoint').click(function() {
        menuHandler.nextView("editPoint");
    });
    $('#editTrace').click(function() {
        menuHandler.nextView("editTrace");
    });
    $('#address').click(function() {
    	menuHandler.nextView("address");
    				  });
    $('#geo').click(function() {
    	menuHandler.nextView("geo");
    				  });
    $('#removePoint').click(function() {
    	menuHandler.nextView("removePoint");
    				  });
    $('#addPoint').click(function() {
    	menuHandler.nextView("addPoint");
    				  });
    $('#fakeStart').click(function() {
    	menuHandler.nextView("fakeStart");
    				  });
    $('#mapType').click(function() {
    	menuHandler.nextView("mapType");
    				  });
    $('#info').click(function() {
    	menuHandler.nextView("info");
    				  });
    // home, map, points, addWaypoints for geo
    $('#home').click(function() {
        menuHandler.nextView("home");
                      });
    $('#map').click(function() {        
    	menuHandler.nextView("map");
                      }); 
    $('#points').click(function() {        
    	menuHandler.nextView("points");
                      });
    $('#savePoint').click(function() {        
    	menuHandler.nextView("savePoint");
                      });
    $('#addWaypoints').click(function() {        
    	menuHandler.nextView("addWaypoints");
    				  });
    $('#offline').click(function() {
    	menuHandler.nextView("offline");
    				  });
    // home, trace, geo for latLng ...
    // home, geo,saveTrace, trace, goPosition for map     
    $('#saveTrace').click(function() {
        menuHandler.nextView("saveTrace");
                      });
    // home, resetTrace, geoAddPoint, maps for trace 
    $('#resetTrace').click(function() {
        menuHandler.nextView("resetTrace");
                      });
    $('#geoAddPoint').click(function() {
        menuHandler.nextView("geoAddPoint");
                      });
    $('#goPosition').click(function() {
        menuHandler.nextView("goPosition");
                      });
    // home, rename, remove for edit
    $('#rename').click(function() {
        menuHandler.nextView("rename");
                      });
    $('#remove').click(function() {
        menuHandler.nextView("remove");
                      });
    // address
    $('#coordinateGo').click(function() {
        	menuHandler.nextView("coordinateGo");
            });
    $('#addressGo').click(function() {
        	menuHandler.nextView("addressGo");
            });
    $('#clearAddress').click(function() {
        	menuHandler.nextView("clearAddress");
            });
    // home, addWaypoints for waypoints 
    // ...
}