package com.sydtrip.android.sydtrip.query;

import com.sydtrip.android.sydtrip.model.Stop;

public class Query {

    private Stop from;

    private Stop to;

    private long time;

    public static Builder create() {
        return new Builder();
    }

    private Query(Stop from, Stop to, long time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }

    public Stop getFrom() {
        return from;
    }

    public Stop getTo() {
        return to;
    }

    public long getTime() {
        return time;
    }

    public boolean isValid() {
        return from != null && to != null && time > 0;
    }

    public boolean isNotValid() {
        return !isValid();
    }

    public static class Builder {
        private Stop from;

        private Stop to;

        private long time;

        public Builder from(Stop from) {
            this.from = from;
            return this;
        }

        public Builder to(Stop to) {
            this.to = to;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Query build() {
            return new Query(from, to, time);
        }
    }
}
