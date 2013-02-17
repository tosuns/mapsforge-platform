package com.viniciusfortuna.transit.gtfs;

import java.net.URL;
import java.util.TimeZone;

/**
 * GTFS agency, as defined at
 * https://developers.google.com/transit/gtfs/reference#agency_fields
 *
 * @author Vinicius Fortuna
 */
public class Agency {
  public String id;
  public String name;
  public URL url;
  public TimeZone timezone;

  /** Creates an Agency with its required fields. */
  public Agency(String id, String name, URL url, TimeZone timezone) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.timezone = timezone;
  }
}
