/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.onebusaway.csv_entities.schema.DefaultEntitySchemaFactory;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsEntitySchemaFactory;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import org.onebusaway.gtfs_transformer.factory.EntityRetentionGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AFROGENIUS
 */
public class roadConverter {
    
  private static final Logger _log = LoggerFactory.getLogger(roadParser.class);

  private static final int MINUTES_IN_DAY = 24 * 60;

  private File _inputPath;

  private File _outputPath;

  private String _agencyId = "DTA";

  private String _agencyName = "Demo Transmit Authority";

  private String _agencyTimezone = "America/Los_Angeles";

  private String _agencyLang = "en";

  private String _agencyUrl = "http://google.com";

  private String _agencyPhone = "";

  private int _vehicleType = -1;

  private Map<AgencyAndId, List<roadHeader>> _journeysById = new HashMap<AgencyAndId, List<roadHeader>>();

  private Map<String, roadLocation> _locationById = new HashMap<String, roadLocation>();

  private Map<String, roadAdditional> _additionalLocationById = new HashMap<String, roadAdditional>();

  private Map<String, roadVehicleType> _vehicleTypesById = new HashMap<String, roadVehicleType>();

  private Map<AgencyAndId, Map<String, roadRouteDescription>> _routeDescriptionsByIdAndDirection = new HashMap<AgencyAndId, Map<String, roadRouteDescription>>();

  private Map<String, roadOperator> _operatorsById = new HashMap<String, roadOperator>();

  private Map<String, String> _serviceDateModificationSuffixByKey = new HashMap<String, String>();

  private GtfsRelationalDaoImpl _dao = new GtfsRelationalDaoImpl();

  private boolean _pruneStopsWithNoLocationInfo = false;

  private Set<String> _pruneStopsWithPrefixes = Collections.emptySet();

  private Set<String> _stopsWithNoLocationInfo = new HashSet<String>();

  private int _prunedStopsWithNoLocationInfoCount = 0;

  private int _prunedStopTimesCount = 0;

  private int _prunedTripsCount = 0;
  
  
  //
  public void setInputPath(File inputPath) {
    _inputPath = inputPath;
  }

  public void setOutputPath(File outputPath) {
    _outputPath = outputPath;
  }

  public void setAgencyId(String agencyId) {
    _agencyId = agencyId;
  }

  public void setAgencyName(String agencyName) {
    _agencyName = agencyName;
  }

  public void setAgencyUrl(String agencyUrl) {
    _agencyUrl = agencyUrl;
  }

  public void setAgencyPhone(String agencyPhone) {
    _agencyPhone = agencyPhone;
  }

  public void setAgencyTimezone(String agencyTimezone) {
    _agencyTimezone = agencyTimezone;
  }

  public void setAgencyLang(String agencyLang) {
    _agencyLang = agencyLang;
  }

  public void setVehicleType(int vehicleType) {
    _vehicleType = vehicleType;
  }

  public void setPruneStopsWithNoLocationInfo(
      boolean pruneStopsWithNoLocationInfo) {
    _pruneStopsWithNoLocationInfo = pruneStopsWithNoLocationInfo;
  }

  public void setPruneStopsWithPrefixes(Set<String> pruneStopsWithPrefixes) {
    _pruneStopsWithPrefixes = pruneStopsWithPrefixes;
  }

  public void run() throws IOException {

    _log.info("Input path: " + _inputPath);
    _log.info("Output path: " + _outputPath);

    List<File> paths = new ArrayList<>();
    getApplicableFiles(_inputPath, paths);

    if (paths.isEmpty()) {
      _log.error("No applicable input files were found!");
      System.exit(-1);
    }

    roadParser parser = new roadParser();
    HandlerImpl handler = new HandlerImpl();
    for (File path : paths) {
      _log.info("parsing file: " + path);
      parser.parse(path, handler);
    }

    constructGtfs();
    writeGtfs();

    if (_prunedStopsWithNoLocationInfoCount > 0) {
      _log.info(String.format(
          "pruned stops with no location info: %d stops used by %d stop times in %d trips",
          _prunedStopsWithNoLocationInfoCount, _prunedStopTimesCount,
          _prunedTripsCount));
    }
  }

  private void constructGtfs() {
    constructTrips();
    pruneTrips();
  }
  
  //
  private void constructTrips() {
      _journeysById.values().forEach(journies -> {
          for (int i = 0; i < journies.size(); ++i) {
              roadHeader journey = journies.get(i);
              if (journey.getOperatorId().equals("EU")) {
                  continue;
              }
              Trip trip = new Trip();
              String id = journey.getOperatorId() + "-"
                      + journey.getJourneyIdentifier();
              if (journies.size() > 1) {
                  id += "-" + i;
              }
              trip.setId(new AgencyAndId(journey.getOperatorId(), id));
              trip.setRoute(getRouteForJourney(journey));
              trip.setServiceId(getServiceIdForJourney(journey));
              
              AgencyAndId routeId = trip.getRoute().getId();
              Map<String, roadRouteDescription> routeDescriptions = _routeDescriptionsByIdAndDirection.get(routeId);
              if (routeDescriptions != null) {
                  roadRouteDescription routeDescription = routeDescriptions.get(journey.getRouteDirection());
                  if (routeDescription != null) {
                      trip.setTripHeadsign(routeDescription.getRouteDescription());
                  }
              }
              
              constructTimepoints(journey, trip);
              _dao.saveEntity(trip);
          } });
  }

  @SuppressWarnings("unchecked")
  private void pruneTrips() {
    EntityRetentionGraph graph = new EntityRetentionGraph(_dao);
    _dao.getAllTrips().forEach(trip -> {
        List<StopTime> stopTimes = _dao.getStopTimesForTrip(trip);
            if (stopTimes.size() > 1) {
                graph.retainUp(trip);
            }
        });
    GtfsEntitySchemaFactory.getEntityClasses().stream().map(entityClass -> {
        List<Object> objectsToRemove = new ArrayList<>();
        _dao.getAllEntitiesForType(entityClass).stream().filter(entity -> (!graph.isRetained(entity))).forEachOrdered(entity -> {
            objectsToRemove.add(entity);
        }); return objectsToRemove;
        }).forEachOrdered(objectsToRemove -> {
            objectsToRemove.forEach(toRemove -> {
                _dao.removeEntity((IdentityBean<Serializable>) toRemove);
            });
        });
  }

  private Route getRouteForJourney(roadHeader journey) {
    String operatorId = journey.getOperatorId();
    AgencyAndId routeId = new AgencyAndId(operatorId, operatorId + "-"
        + journey.getRouteIdentifier());
    Route route = _dao.getRouteForId(routeId);
    if (route == null) {
      route = new Route();
      route.setAgency(getAgencyForId(operatorId));
      route.setId(routeId);
      route.setShortName(routeId.getId());
      route.setType(getRouteTypeForJourney(journey));
      _dao.saveEntity(route);
    }
    return route;
  }

  private Agency getAgencyForId(String id) {
    Agency agency = _dao.getAgencyForId(id);
    if (agency == null) {
      agency = new Agency();
      agency.setId(id);

      roadOperator operator = _operatorsById.get(id);
      agency.setTimezone(_agencyTimezone);
      agency.setUrl(_agencyUrl);
      agency.setLang(_agencyLang);

      if (operator != null) {
        agency.setName(operator.getShortFormName());
        agency.setPhone(operator.getEnquiryPhone());
      } else {
        agency.setName(_agencyName);
        agency.setPhone(_agencyPhone);
      }
      _dao.saveEntity(agency);
    }
    return agency;
  }

  private int getRouteTypeForJourney(roadHeader journey) {   
    roadVehicleType vehicleType = _vehicleTypesById.get(journey.getVehicleType());
    if (vehicleType == null) {
        return (this._vehicleType);
//        throw new AtcoCifException("unknown vehicle type: " + vehicleType);
    }
    String desc = vehicleType.getDescription().toLowerCase();
    if (desc.equals("bus") || desc.equals("coach")) {
      return 3;
    } else if (desc.equals("heavy rail")) {
      return 2;
    } else if (_vehicleType != -1) {
      return _vehicleType;
    } else {
      throw new roadException(
          "no default VehicleType specified and could not determine GTFS route vehicle type from ATCO-CIF vehicle type description: "
              + desc);
    }
  }
  
  
  //
  private AgencyAndId getServiceIdForJourney(roadHeader journey) {
    AgencyAndId serviceId = constructServiceIdForJourney(journey);
    ServiceCalendar calendar = _dao.getCalendarForServiceId(serviceId);
    if (calendar == null) {
      calendar = new ServiceCalendar();
      calendar.setServiceId(serviceId);
      calendar.setStartDate(journey.getStartDate());
      calendar.setEndDate(journey.getEndDate());
      calendar.setMonday(journey.getMonday());
      calendar.setTuesday(journey.getTuesday());
      calendar.setWednesday(journey.getWednesday());
      calendar.setThursday(journey.getThursday());
      calendar.setFriday(journey.getFriday());
      calendar.setSaturday(journey.getSaturday());
      calendar.setSunday(journey.getSunday());
      _dao.saveEntity(calendar);

      for (roadDateRun modification : journey.getCalendarModifications()) {
        Date startDate = modification.getStartDate().getAsDate();
        Date endDate = modification.getEndDate().getAsDate();

        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(startDate);

        int exceptionType = modification.getOperationCode() == 1 ? 1 : 2;

        while (true) {
          Date date = c.getTime();
          if (date.after(endDate))
            break;

          ServiceCalendarDate calendarDate = new ServiceCalendarDate();
          calendarDate.setServiceId(serviceId);
          calendarDate.setDate(new ServiceDate(date));
          calendarDate.setExceptionType(exceptionType);
          _dao.saveEntity(calendarDate);

          c.add(Calendar.DAY_OF_YEAR, 1);
        }
      }
      _dao.clearAllCaches();
    }
    return serviceId;
  }

  private AgencyAndId constructServiceIdForJourney(roadHeader journey) {
    StringBuilder b = new StringBuilder();
    b.append(journey.getStartDate().getAsString());
    b.append('-');
    b.append(journey.getEndDate().getAsString());
    b.append('-');
    b.append(journey.getSunday() == 1 ? "S" : "_");
    b.append(journey.getMonday() == 1 ? "M" : "_");
    b.append(journey.getTuesday() == 1 ? "T" : "_");
    b.append(journey.getWednesday() == 1 ? "W" : "_");
    b.append(journey.getThursday() == 1 ? "H" : "_");
    b.append(journey.getFriday() == 1 ? "F" : "_");
    b.append(journey.getSaturday() == 1 ? "S" : "_");
    b.append('-');
    b.append(getServiceDateModificationsSuffix(journey));
    return id(b.toString());
  }

  private String getServiceDateModificationsSuffix(roadHeader journey) {
    List<roadDateRun> modifications = journey.getCalendarModifications();
    if (modifications.isEmpty()) {
      return "00";
    }

    StringBuilder b = new StringBuilder();
    Collections.sort(modifications);
    modifications.stream().map(modification -> {
        b.append('|');
        b.append(modification.getStartDate().getAsString());
            return modification;
        }).map(modification -> {
            b.append('-');
            b.append(modification.getEndDate().getAsString());
            return modification;
        }).forEachOrdered(modification -> {
            b.append('-');
            b.append(modification.getOperationCode());
        });
    String key = b.toString();
    String suffix = _serviceDateModificationSuffixByKey.get(key);
    if (suffix == null) {
      DecimalFormat format = new DecimalFormat("00");
      suffix = format.format(_serviceDateModificationSuffixByKey.size() + 1);
      _serviceDateModificationSuffixByKey.put(key, suffix);
    }
    return suffix;
  }

  private void constructTimepoints(roadHeader journey, Trip trip) {

    normalizeTimes(journey);

    boolean first = true;
    boolean prunedStopWithoutLocationInfo = false;

    for (roadTimePoint timePoint : journey.getTimePoints()) {
      String stopId = timePoint.getLocationId();
      Stop stop = findStop(stopId);

      /**
       * A NULL stop indicates a stop that has been pruned because it doesn't
       * have location information. We do not produce stop times for these
       * stops.
       */
      if (stop == null) {
        prunedStopWithoutLocationInfo = true;
        _prunedStopTimesCount++;
        continue;
      }

      StopTime stopTime = new StopTime();
      stopTime.setTrip(trip);
      stopTime.setStop(stop);
      if (timePoint.getArrivalTime() != 0 || timePoint.getDepartureTime() != 0
          || first) {
        stopTime.setArrivalTime(timePoint.getArrivalTime() * 60);
        stopTime.setDepartureTime(timePoint.getDepartureTime() * 60);
      }

      stopTime.setStopSequence(_dao.getAllStopTimes().size());
      _dao.saveEntity(stopTime);
      first = false;
    }

    if (prunedStopWithoutLocationInfo) {
      _prunedTripsCount++;
    }
  }

  private void normalizeTimes(roadHeader journey) {
    List<roadTimePoint> timepoints = journey.getTimePoints();
    if (timepoints.isEmpty()) {
      return;
    }

    int prevDepartureTime = -1;
    int dayOffset = 0;

    for (int i = 0; i < timepoints.size(); ++i) {
      roadTimePoint timepoint = timepoints.get(i);
      int arrivalTime = timepoint.getArrivalTime();
      int departureTime = timepoint.getDepartureTime();

      arrivalTime += dayOffset * MINUTES_IN_DAY;
      while (arrivalTime < prevDepartureTime) {
        arrivalTime += MINUTES_IN_DAY;
        dayOffset++;
      }

      departureTime += dayOffset * MINUTES_IN_DAY;
      while (departureTime < arrivalTime) {
        departureTime += MINUTES_IN_DAY;
        dayOffset++;
      }

      timepoint.setArrivalTime(arrivalTime);
      timepoint.setDepartureTime(departureTime);
      prevDepartureTime = departureTime;
    }
  }

  private Stop findStop(String stopId) {

    for (String prefix : _pruneStopsWithPrefixes) {
      if (stopId.startsWith(prefix)) {
        return null;
      }
    }
    roadLocation location = getLocationForId(stopId);
    if (location == null) {
      throw new roadException("no stop found with id " + stopId);
    }

    String locationId = location.getLocationId();
    AgencyAndId id = id(locationId);
    Stop stop = _dao.getStopForId(id);
    if (stop == null) {
      roadAdditional additionalLocation = _additionalLocationById.get(locationId);
      if (additionalLocation == null) {
        throw new roadException("found location with id=" + locationId
            + " but no additional location information found");
      }

      if (additionalLocation.getLat() == 0.0
          || additionalLocation.getLon() == 0.0) {
        if (_stopsWithNoLocationInfo.add(stopId)) {
          _log.info("stop with no location: " + locationId);
          _prunedStopsWithNoLocationInfoCount++;
        }
        if (_pruneStopsWithNoLocationInfo) {
          return null;
        }
      }

      stop = new Stop();
      stop.setId(id(locationId));
      stop.setName(location.getName());
      stop.setLat(additionalLocation.getLat());
      stop.setLon(additionalLocation.getLon());

      _dao.saveEntity(stop);
    }
    return stop;
  }

  private roadLocation getLocationForId(String stopId) {
    roadLocation location = _locationById.get(stopId);
    /**
     * I've noticed a strange case where a journey references a stop with an id
     * "blahX" when only a stop with id "blah" exists.
     */
    if (location == null) {
      if (stopId.length() > 1) {
        stopId = stopId.substring(0, stopId.length() - 1);
        location = _locationById.get(stopId);
      }
    }
    return location;
  }

  private void writeGtfs() throws IOException {
    GtfsWriter writer = new GtfsWriter();
    writer.setOutputLocation(_outputPath);
    DefaultEntitySchemaFactory schemaFactory = new DefaultEntitySchemaFactory();
    schemaFactory.addFactory(GtfsEntitySchemaFactory.createEntitySchemaFactory());
    writer.setEntitySchemaFactory(schemaFactory);
    writer.run(_dao);
  }

  private void getApplicableFiles(File path, List<File> applicableFiles) {
    _log.info("Scanning path: " + path);
    if (path.isDirectory()) {
      _log.info("Directory found...");
      for (File subPath : path.listFiles()) {
        getApplicableFiles(subPath, applicableFiles);
      }
    } else if (path.getName().toLowerCase().endsWith(".cif")) {
      _log.info("CIF File found!");
      applicableFiles.add(path);
    }
  }

  private AgencyAndId id(String id) {
    return new AgencyAndId(_agencyId, id);
  }

  private class HandlerImpl implements roadContentHandler {

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {

    }

    @Override
    public void startElement(roadElement element) {
      if (element instanceof roadHeader) {
        roadHeader journey = (roadHeader) element;
        AgencyAndId journeyId = new AgencyAndId(journey.getOperatorId(),
            journey.getJourneyIdentifier());
        List<roadHeader> journies = _journeysById.get(journeyId);
        if (journies == null) {
          journies = new ArrayList<>();
          _journeysById.put(journeyId, journies);
        }
        journies.add(journey);
      } else if (element instanceof roadLocation) {
        roadLocation location = (roadLocation) element;
        _locationById.put(location.getLocationId(), location);
      } else if (element instanceof roadAdditional) {
        roadAdditional location = (roadAdditional) element;
        _additionalLocationById.put(location.getLocationId(), location);
      } else if (element instanceof roadVehicleType) {
        roadVehicleType vehicle = (roadVehicleType) element;
        _vehicleTypesById.put(vehicle.getId(), vehicle);
      } else if (element instanceof roadRouteDescription) {
        roadRouteDescription route = (roadRouteDescription) element;
        AgencyAndId id = new AgencyAndId(route.getOperatorId(),
            route.getRouteNumber());
        Map<String, roadRouteDescription> byDirection = _routeDescriptionsByIdAndDirection.get(id);
        if (byDirection == null) {
          byDirection = new HashMap<>();
          _routeDescriptionsByIdAndDirection.put(id, byDirection);
        }
        roadRouteDescription existing = byDirection.put(
            route.getRouteDirection(), route);
        if (existing != null) {
          System.out.println(existing);
        }
      } else if (element instanceof roadOperator) {
        roadOperator operator = (roadOperator) element;
        roadOperator existing = _operatorsById.put(operator.getOperatorId(),
            operator);
        if (existing != null) {
          _log.info("!");
        }
      }

    }

    @Override
    public void endElement(roadElement element) {

    }
  }
}
