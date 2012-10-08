hammertime
==========

Facebook project for RAI

## Developing with Eclipse
1. Install Eclipse
1. Install m2e-wtp from the Eclipse Marketplace
1. Import this directory as an existing Maven project
1. "Run as" this project on a server (such as Tomcat)
1. You may have to edit your hosts file to map www.integralblue.com to 127.0.0.1, then use www.integralblue.com/hammertime as the URL in your browser to get the application to work due to how Facebook binds cookies to app domains

## Development notes
* You can use XFBML on the pages, see https://developers.facebook.com/docs/plugins/
* The template language is Thymeleaf: http://www.thymeleaf.org/
* All URLs use content negotiation to determine what response format to use. For example, when requested by a browser, the server returns HTML. When requested by an API client (with an accept header requesting application/json), the server returns json.
* The application gets the Facebook auth token from the cookie set by Facebook. But, if there isn't such a cookie included in the request, it will use the value of the HTTP Request Header "X-Facebook-Auth-Token" as the authentication token.
* If a URL requires authentication (aka a valid Facebook session), and there isn't one (in other words, no auth token could be found via either cookie or HTTP header), the server returns an HTTP 403 (Forbidden) response.
