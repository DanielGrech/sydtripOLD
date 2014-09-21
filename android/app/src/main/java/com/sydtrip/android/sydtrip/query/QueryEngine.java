package com.sydtrip.android.sydtrip.query;

import com.sydtrip.android.sydtrip.data.RoutingEngine;
import com.sydtrip.android.sydtrip.model.StopTimeList;

import timber.log.Timber;

public class QueryEngine implements IQueryEngine {

    private final RoutingEngine mRoutingEngine;

    public QueryEngine(RoutingEngine routingEngine) {
        mRoutingEngine = routingEngine;
    }

    @Override
    public QueryResult execute(Query query) {
        if (query.isNotValid()) {
            return QueryResult.NO_RESULTS;
        }

        final StopTimeList stopTimesForFromStop = mRoutingEngine.getStopTimesAt(query.getFrom());
        final StopTimeList stopTimesForToStop = mRoutingEngine.getStopTimesAt(query.getTo());

        Timber.w("Stops for from: %s", stopTimesForFromStop);
        Timber.w("Stops for to: %s", stopTimesForToStop);

        return QueryResult.NO_RESULTS;
    }
}
