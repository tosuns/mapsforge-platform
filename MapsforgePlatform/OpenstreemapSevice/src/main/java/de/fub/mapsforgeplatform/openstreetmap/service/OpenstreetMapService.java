/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.service;

import java.text.MessageFormat;

/**
 * Jersey REST client generated for REST resource:Geocoding Service [geo]<br>
 * USAGE:
 * <pre>
 *        NewJerseyClient client = new NewJerseyClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Serdar
 */
public class OpenstreetMapService {

    private com.sun.jersey.api.client.WebResource webResource;
    private com.sun.jersey.api.client.Client client;
    private static final String BASE_URI = "http://api.openstreetmap.org/";

    public OpenstreetMapService() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = com.sun.jersey.api.client.Client.create(config);
        webResource = client.resource(BASE_URI).path("api/0.6/");
    }

    /**
     * @param responseType Class representing the response
     * @param q query parameter[REQUIRED]
     * @param key query parameter[REQUIRED]
     * @param optionalQueryParams List of optional query parameters in the form
     * of "param_name=param_value",...<br> List of optional query parameters:
     * <LI>output [OPTIONAL, DEFAULT VALUE: "xml"]
     * @return response object (instance of responseType class)
     */
    public <T> T getGpsTracks(Class<T> responseType, String leftLong, String bottomLat, String rightLong, String topLat, String page) throws com.sun.jersey.api.client.UniformInterfaceException {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        qParams.add("bbox", MessageFormat.format("{0},{1},{2},{3}", leftLong, bottomLat, rightLong, topLat));
        qParams.add("page", page);
        return webResource.path("trackpoints").queryParams(qParams).accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T getOSMMap(Class<T> responseType, String leftLong, String bottomLat, String rightLong, String topLat) {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        qParams.add("bbox", MessageFormat.format("{0},{1},{2},{3}", leftLong, bottomLat, rightLong, topLat));
        return webResource.path("map").queryParams(qParams).accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public void close() {
        client.destroy();
    }
}
