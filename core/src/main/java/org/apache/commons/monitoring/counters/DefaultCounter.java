/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.monitoring.counters;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.monitoring.store.DataStore;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultCounter implements Counter {
    private final AtomicInteger concurrency = new AtomicInteger(0);
    private final Key key;
    private final DataStore dataStore;
    private volatile int maxConcurrency = 0;
    protected SummaryStatistics statistics;
    protected Lock lock = new ReentrantLock();

    public DefaultCounter(final Key key, final DataStore store) {
        this.key = key;
        this.statistics = new SummaryStatistics();
        this.dataStore = store;
    }

    public void addInternal(final double delta) { // should be called from a thread safe environment
        statistics.addValue(delta);
    }

    @Override
    public void updateConcurrency(final int concurrency) {
        if (concurrency > maxConcurrency) {
            maxConcurrency = concurrency;
        }
    }

    @Override
    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    @Override
    public AtomicInteger currentConcurrency() {
        return concurrency;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void reset() {
        statistics.clear();
    }

    @Override
    public void add(final double delta) {
        dataStore.addToCounter(this, delta);
    }

    @Override
    public void add(final double delta, final Unit deltaUnit) {
        add(key.getRole().getUnit().convert(delta, deltaUnit));
    }

    @Override
    public double getMax() {
        return statistics.getMax();
    }

    @Override
    public double getMin() {
        return statistics.getMin();
    }

    @Override
    public double getSum() {
        return statistics.getSum();
    }

    @Override
    public double getStandardDeviation() {
        return statistics.getStandardDeviation();
    }

    @Override
    public double getVariance() {
        return statistics.getVariance();
    }

    @Override
    public double getMean() {
        return statistics.getMean();
    }

    @Override
    public double getGeometricMean() {
        return statistics.getGeometricMean();
    }

    @Override
    public double getSumOfLogs() {
        return statistics.getSumOfLogs();
    }

    @Override
    public double getSumOfSquares() {
        return statistics.getSumOfLogs();
    }

    @Override
    public long getHits() {
        return statistics.getN();
    }

    public Lock getLock() {
        return lock;
    }
}