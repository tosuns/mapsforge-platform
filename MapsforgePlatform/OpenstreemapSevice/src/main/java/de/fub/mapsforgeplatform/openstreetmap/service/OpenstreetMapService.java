/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.service;

import java.text.MessageFormat;
import javax.ws.rs.core.MediaType;

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
    private com.sun.jersey.api.client.WebResource webOverpassResource;
    private com.sun.jersey.api.client.Client overpassClient;
    private static final String BASE_URI = "http://api.openstreetmap.org/";
    private static final String OVERPASS_BASE_URI = "http://overpass-api.de/";

    public OpenstreetMapService() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = com.sun.jersey.api.client.Client.create(config);
        webResource = client.resource(BASE_URI).path("api/0.6/");
        overpassClient = com.sun.jersey.api.client.Client.create(config);
        webOverpassResource = overpassClient.resource(OVERPASS_BASE_URI).path("api/");
    }

    /**
     * Fetches all gpx data that are within the specified bounding box and page
     * number.
     *
     * @param responseType Class representing the response
     * @param String leftLong
     * @param String bottomLat
     * @param String rightLong
     * @param String topLat
     * @param String page
     * @return response object (instance of responseType class)
     */
    public <T> T getGpsTracks(Class<T> responseType,
            String leftLong,
            String bottomLat,
            String rightLong,
            String topLat,
            String page) throws com.sun.jersey.api.client.UniformInterfaceException {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        qParams.add("bbox", MessageFormat.format("{0},{1},{2},{3}", leftLong, bottomLat, rightLong, topLat));
        qParams.add("page", page);
        return webResource.path("trackpoints").queryParams(qParams).accept(MediaType.TEXT_XML, MediaType.APPLICATION_XML).get(responseType);
    }

    /**
     *
     * @param <T>
     * @param responseType
     * @param leftLong
     * @param bottomLat
     * @param rightLong
     * @param topLat
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getOSMHighwayMap(Class<T> responseType,
            String leftLong,
            String bottomLat,
            String rightLong,
            String topLat) {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder = stringBuilder.append("(node({0},{1},{2},{3});rel(bn)->.x;")
                .append("way")
                .append("[\"highway\"=\"rail\"]")
                .append("[\"highway\"=\"preserved\"]")
                .append("[\"highway\"=\"narrow_gauge\"]")
                .append("[\"highway\"=\"monorail\"]")
                .append("[\"highway\"=\"miniature\"]")
                .append("[\"highway\"=\"light_rail\"]")
                .append("[\"highway\"=\"funicular\"]")
                .append("[\"highway\"=\"abandoned\"]")
                .append("[\"highway\"=\"disused\"]")
                .append("[\"highway\"=\"construction\"]")
                .append("({0},{1},{2},{3});node(w)->.x;);out meta;");

        String parameter = MessageFormat.format(stringBuilder.toString(),
                bottomLat, leftLong, topLat, rightLong);
        qParams.add("data", parameter);
        T post = webOverpassResource.path("interpreter")
                .accept(MediaType.TEXT_XML, MediaType.APPLICATION_XML, "application/osm3s+xml")
                .post(responseType, qParams);
        return post;
    }

    /**
     *
     * @param <T>
     * @param responseType
     * @param leftLong
     * @param bottomLat
     * @param rightLong
     * @param topLat
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getOSMTrainMap(Class<T> responseType,
            String leftLong,
            String bottomLat,
            String rightLong,
            String topLat) {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        String parameter = MessageFormat.format("(node({0},{1},{2},{3});rel(bn)->.x;way[\"railway\"=\"rail\"]({0},{1},{2},{3});node(w)->.x;);out meta;",
                bottomLat, leftLong, topLat, rightLong);
        qParams.add("data", parameter);
        T post = webOverpassResource.path("interpreter")
                .accept(MediaType.TEXT_XML, MediaType.APPLICATION_XML, "application/osm3s+xml")
                .post(responseType, qParams);
        return post;
    }

    /**
     *
     * @param <T>
     * @param responseType
     * @param leftLong
     * @param bottomLat
     * @param rightLong
     * @param topLat
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getOSMTramMap(Class<T> responseType,
            String leftLong,
            String bottomLat,
            String rightLong,
            String topLat) {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        String parameter = MessageFormat.format("(node({0},{1},{2},{3});rel(bn)->.x;way[\"railway\"=\"tram\"]({0},{1},{2},{3});node(w)->.x;);out meta;",
                bottomLat, leftLong, topLat, rightLong);
        qParams.add("data", parameter);
        T post = webOverpassResource.path("interpreter")
                .accept(MediaType.TEXT_XML, MediaType.APPLICATION_XML, "application/osm3s+xml")
                .post(responseType, qParams);
        return post;
    }

    /**
     *
     * @param <T>
     * @param responseType
     * @param leftLong
     * @param bottomLat
     * @param rightLong
     * @param topLat
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getOSMSubwayMap(Class<T> responseType,
            String leftLong,
            String bottomLat,
            String rightLong,
            String topLat) {
        javax.ws.rs.core.MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        String parameter = MessageFormat.format("(node({0},{1},{2},{3});rel(bn)->.x;way[\"railway\"=\"subway\"]({0},{1},{2},{3});node(w)->.x;);out meta;",
                bottomLat, leftLong, topLat, rightLong);
        qParams.add("data", parameter);
        T post = webOverpassResource.path("interpreter")
                .accept(MediaType.TEXT_XML, MediaType.APPLICATION_XML, "application/osm3s+xml")
                .post(responseType, qParams);
        return post;
    }

    public void close() {
        client.destroy();
        overpassClient.destroy();
    }
}
