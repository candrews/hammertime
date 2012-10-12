/* Author: Dimitri Kennedy

*/

HAMMERTIME = {


	common: {
      init: function(){
    	  this.fbInit();
		  },

	  fbInit: function(){
		  window.fbAsyncInit = function(){
			  FB.init({
			      appId      : '110725189085402', // App ID
			      status     : true, // check login status
			      cookie     : true, // enable cookies to allow the server to access the session
			      xfbml      : true,  // parse XFBML
			      oauth 	 : true
			    });
			  HAMMERTIME.common.fbConnect();
		  	};
	   },
	   
	   /*
	    * binds FB login and logout functionality
	    * */
	   fbConnect: function(){
	        FB.Event.subscribe('auth.statusChange', function(response) {
	          if (response.authResponse) {
	            // user has auth'd your app and is logged into Facebook
	            FB.api('/me', function(me){
	              if (me.name) {
	                document.getElementById('auth-displayname').innerHTML = me.name;
	                $('#auth-loggedin img').attr('src', 'https://graph.facebook.com/' + me.id + '/picture').fadeIn();
	              }
	            });
	            $('#loggedOutScreen').hide();
	            $('.nav-collapse').show();
	            $('#auth-loggedin').show();
	          } else {
	            // user has not auth'd your app, or is not logged into Facebook
	            $('#loggedOutScreen').show();
	            $('.nav-collapse').hide();
	            $('#auth-loggedin').hide();
	          }
	        });

	        // respond to clicks on the login and logout links
	        $('#auth-loginlink').on('click', function(){
	          FB.login();
	        });

	        
	        $('#auth-logoutlink').on('click', function(){	
	          FB.logout();
	          
	        }); 		   
		   
	   }
    },

    home:{
    	init: function(){
    		var hoverOptions = {placement: 'top', trigger: 'hover'};
    		$('.homeSubNav a').popover(hoverOptions);
    	}
    },

}, 

UTIL = {
	fire : function(func,funcname, args){
		var namespace = HAMMERTIME;  // indicate your obj literal namespace here
		funcname = (funcname === undefined) ? 'init' : funcname;
		if (func !== '' && namespace[func] && typeof namespace[func][funcname] == 'function'){
			namespace[func][funcname](args);
		} 
	}, 
	loadEvents : function(){
		var bodyId = document.body.id;
		// hit up common first.
		UTIL.fire('common');
		// do all the classes too.
		$.each(document.getElementsByTagName('div')[0].className.split(/\s+/),function(i,classnm){
			UTIL.fire(classnm);
			UTIL.fire(bodyId);

		});
		UTIL.fire('common','finalize');
	} 
}; 
// kick it all off here 
$(document).ready(UTIL.loadEvents);



















