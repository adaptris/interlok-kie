package com.adaptris.holiday.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Holiday {

  public enum TransportType {
    Plane,
    Train,
    Automobile
  };

  private TransportType transportType;
  private List<String> stopOffs;
  private String destination;

  public Holiday() {
    setDestination("");
    setStopOffs(new ArrayList<>());
  }

  public TransportType getTransportType() {
    return transportType;
  }

  public void setTransportType(TransportType t) {
    this.transportType = t;
  }

  public Holiday withTransportType(TransportType g) {
    setTransportType(g);
    return this;
  }

  public List<String> getStopOffs() {
    return stopOffs;
  }

  public Holiday withStopOffs(String... s) {
    setStopOffs(new ArrayList<>(Arrays.asList(s)));
    return this;
  }

  public void setStopOffs(List<String> s) {
    this.stopOffs = s;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String s) {
    this.destination = s;
  }

  public Holiday withDestination(String g) {
    setDestination(g);
    return this;
  }

}


