package com.dgsd.sydtrip.transformer;

import com.dgsd.sydtrip.transformer.exception.DatabaseCreationException;
import com.dgsd.sydtrip.transformer.exception.DatabaseOperationException;
import com.dgsd.sydtrip.transformer.gtfs.model.target.Route;
import com.dgsd.sydtrip.transformer.gtfs.model.target.Stop;
import com.dgsd.sydtrip.transformer.gtfs.model.target.Trip;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

public class Database implements AutoCloseable {

    private final static Logger LOG = Logger.getLogger(Database.class.getName());

    private static final String STOP_INSERT_TEMPLATE
            = "INSERT OR REPLACE INTO stops VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String TRIP_INSERT_TEMPLATE
            = "INSERT OR REPLACE INTO trips VALUES(?, ?, ?, ?, ?, ?)";
    private static final String ROUTE_INSERT_TEMPLATE
            = "INSERT OR REPLACE INTO routes VALUES(?, ?, ?, ?, ?, ?)";

    private final Connection connection;

    public Database(String fileName) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseCreationException(e);
        }
    }

    public void create() {
        try (final Statement statement = connection.createStatement()) {

            LOG.info("Creating stops table");
            statement.execute(loadResource("sql/table_stops.sql"));

            LOG.info("Creating stop times table");
            statement.execute(loadResource("sql/table_stop_times.sql"));

            LOG.info("Creating trips table");
            statement.execute(loadResource("sql/table_trips.sql"));

            LOG.info("Creating routes table");
            statement.execute(loadResource("sql/table_routes.sql"));

            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public void persist(List<Trip> trips, List<Stop> stops) {
        try {
            persistStops(stops);
            persistTrips(trips);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    private void persistTrips(List<Trip> trips) throws SQLException {
        try (final PreparedStatement tripStatement = connection.prepareStatement(TRIP_INSERT_TEMPLATE)) {
            try (final PreparedStatement routeStatement = connection.prepareStatement(ROUTE_INSERT_TEMPLATE)) {
                for (Trip trip : trips) {
                    int tripIdx = 1;
                    setInt(tripStatement, tripIdx++, trip.getId());
                    tripStatement.setString(tripIdx++, trip.getHeadSign());
                    setInt(tripStatement, tripIdx++, trip.getDirection());
                    tripStatement.setString(tripIdx++, trip.getBlockId());
                    setInt(tripStatement, tripIdx++, trip.isWheelchairAccessible() ? 1 : 0);

                    final Route route = trip.getRoute();
                    if (route == null) {
                        setInt(tripStatement, tripIdx++, 0);
                    } else {
                        setInt(tripStatement, tripIdx++, route.getId());

                        int routeIdx = 1;
                        setInt(routeStatement, routeIdx++, route.getId());
                        setInt(routeStatement, routeIdx++, route.getAgencyId());
                        routeStatement.setString(routeIdx++, route.getShortName());
                        routeStatement.setString(routeIdx++, route.getLongName());
                        setInt(routeStatement, routeIdx++, route.getRouteType());
                        setInt(routeStatement, routeIdx++, route.getColor());
                        routeStatement.addBatch();
                    }

                    tripStatement.addBatch();
                }

                routeStatement.executeBatch();
                tripStatement.executeBatch();
            }
        }
    }

    private void persistStops(List<Stop> stops) throws SQLException {
        LOG.info("Persisting stops...");
        try (final PreparedStatement statement = connection.prepareStatement(STOP_INSERT_TEMPLATE)) {
            for (Stop stop : stops) {
                int idx = 1;
                setInt(statement, idx++, stop.getId());
                statement.setString(idx++, stop.getCode());
                statement.setString(idx++, stop.getName());
                statement.setFloat(idx++, stop.getLat());
                statement.setFloat(idx++, stop.getLng());
                setInt(statement, idx++, stop.getType());
                statement.setString(idx++, stop.getPlatformCode());

                statement.addBatch();
            }

            statement.executeBatch();
        }
        LOG.info("Finished persisting stops...");
    }

    private static String loadResource(final String path) {
        try (InputStream stream
                     = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            return IOUtils.toString(stream);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setInt(PreparedStatement pStatement, int pos, int value) throws SQLException {
        if(value == 0) {
            pStatement.setNull(pos, Types.INTEGER);
        } else {
            pStatement.setInt(pos, value);
        }
    }
}
