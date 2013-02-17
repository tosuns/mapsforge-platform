package com.viniciusfortuna.transit.gtfs;

/**
 * GTFS route, as defined at
 * https://developers.google.com/transit/gtfs/reference#routes_fields
 *
 * @author Vinicius Fortuna
 */
public class Route {
  public String id;
  public String shortName;
  public String longName;
  public int type;

  /** Creates a Route with its required fields. */
  public Route(String id, String shortName, String longName, int type) {
    this.id = id;
    this.shortName = shortName;
    this.longName = longName;
    this.type = type;
  }
}
