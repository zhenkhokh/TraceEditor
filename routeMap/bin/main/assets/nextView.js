function dosmth(){
	alert("hello");
}
function setUpMenuEvents() {
	// next 3 for home
    $('#toTrace').click(function() {
    	//alert("hello toTrace");
        menuHandler.nextView("toTrace");
    }); 
    $('#trace').click(function() {
    	//alert("hello trace");
        menuHandler.nextView("trace");
    });    
    $('#geo').click(function() {
    	//alert("hello geo");
        menuHandler.nextView("geo");
    });    
    $('#map').click(function() {        
    	//alert("hello map");
    	menuHandler.nextView("map");
                      });    
    // home, map, points for geo
    $('#home').click(function() {
    	//alert("hello home");
        menuHandler.nextView("home");
                      });
    $('#points').click(function() {        
    	//alert("hello points");
    	menuHandler.nextView("points");
                      });
    $('#savePoint').click(function() {        
    	//alert("hello savePoint");
    	menuHandler.nextView("savePoint");
                      });
    // home, trace, geo for latLng ...
    // home, geo,saveTrace, trace for map     
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
}