package com.viniciusfortuna.transit.gtfs;

import java.util.List;

/**
 * Holds an entire GTFS specification.
 * See https://developers.google.com/transit/gtfs/reference for details.
 * Original Version by Vinicius Fortuna
 * 
 * @author Sebastian Müller
 */
public class GtfsSpec {

	List<Agency> agencies;
	List<Stop> stops;
	List<Route> routes;


	public List<Agency> getAgencies() {
		return agencies;
	}

	public void setAgencies(List<Agency> agencies) {
		this.agencies = agencies;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

  GtfsSpec(List<Agency> agencies, List<Stop> stops, List<Route> routes) {
    this.agencies = agencies;
    this.stops = stops;
    this.routes = routes;
  }
}
