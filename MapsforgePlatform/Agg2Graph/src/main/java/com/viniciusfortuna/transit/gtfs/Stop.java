package com.viniciusfortuna.transit.gtfs;

/**
 * GTFS stop, as defined at
 * https://developers.google.com/transit/gtfs/reference#stops_fields
 *
 * @author Vinicius Fortuna
 */
public class Stop {
  public String id;
  public String name;
  public double latitude;
  public double longitude;

  /** Creates a Stop with its required fields. */
  public Stop(String id, String name, double latitude, double longitude) {
    this.id = id;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
