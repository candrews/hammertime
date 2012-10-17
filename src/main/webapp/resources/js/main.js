/* Author: Dimitri Kennedy

*/

HAMMERTIME = {


	common: {
      init: function(){
    	  this.fbInit();
  			var hoverOptions = {placement: 'top', trigger: 'hover'};
  			$('.homeSubNav a').popover(hoverOptions);
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
		    (function(d){
		        var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
		        if (d.getElementById(id)) {return;}
		        js = d.createElement('script'); js.id = id; js.async = true;
		        js.src = "//connect.facebook.net/en_US/all.js";
		        ref.parentNode.insertBefore(js, ref);
		      }(document));
	   },
	   
	   /*
	    * binds FB login and logout functionality
	    * */
	   fbConnect: function(){
			
		   $('#auth-loginlink').fadeIn();
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
		      FB.Event.subscribe('auth.login', function () {
		          window.location = "/web/project/create/";
		      });
	          FB.login();
	        });

	        
	        $('#auth-logoutlink').on('click', function(){	
	          FB.logout();
		      FB.Event.subscribe('auth.logout', function () {
		          window.location = "/web/";
		      });
	          
	        });
	        
		   
	   }
    },

    home:{
    	init: function(){

    	}
    },
    
    project: {
    	init: function(){
    		var THIS = this;
    		$('#pinSubmit').on('click', function(){
    			$('#feed li').remove();
    			THIS.pintrestFeed();
    			console.log('clicked');
    		});
    	},
		  
    	pintrestFeed: function(global){
    			console.log('init');
    			var pinUser = $('#pinUser').val();
    		    var Pinterest = (function() {

    		        var options = {
    		            username: pinUser,
    		            numEntries: 25,
    		            container: '#feed'
    		        };

    		        var feed;
    		        var container;

    		        var loadFeed = function() {
    		            google.load("feeds", "1", {callback:function(){
    		                container = $(options.container);
    		                feed = new google.feeds.Feed('http://pinterest.com/'+options.username+'/feed.rss');
    		                feed.setNumEntries(options.numEntries);
    		                feed.load(buildGrid);
    		            }});
    		        };

    		        var buildGrid = function(result) {
    		            if (!result.error) {
    		                container.html('');
    		                for (var i = 0; i < result.feed.entries.length; i++) {
    		                    var entry = result.feed.entries[i];
    		                    var html = $('<li class="item"><div>'+entry.content+'</div></li>');
    		                    html.find('a').attr('href', entry.link);
    		                    html.find('a').attr('target', '_blank');
    		                    html.find('p:nth-child(2)').remove();
    		                    container.append(html);
    		                }
    		                $('.flexslider').flexslider({
    		                    animation: "slide",
    		                    animationLoop: false,
    		                    itemWidth: 200,
    		                    itemMargin: 5
    		                  }).fadeIn();
    		            }else{
    		            	alert('error!');
    		            }
    		        };

    		        return {
    		            init: function(params) {
    		                params = params || {};
    		                for(var i in params) {
    		                    options[i] = params[i];
    		                }
    		                loadFeed();
    		            }
    		        };

    		    // expose our module to the global object
    		    global.Pinterest = Pinterest;
    		})( this );


    		Pinterest.init({
    		    username: pinUser,
    		    numEntries: 25,
    		    container: '#feed',
    		});
    		
    	},      	
    	
    	
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



















