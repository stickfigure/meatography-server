package com.voodoodyne.meatography;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Our goal is to collect a lot of data ponts as cheaply and efficently as possible. GAE is great with coarse-grained
 * objects, so we aggregate all the data points for a day into a single entity. This has the advantage that no indexes
 * will be updated with each write, so pay only the one 1 write op per point (not counting the first write). This also
 * means we can load a whole day for one read op.</p>
 *
 * <p>Days have a natural key - the ISO-8601 formatted date (in UTC).</p>
 *
 * <p>Normally we would use a List<Measurement> to hold the measurement data. However, we want this to be
 * super efficient in terms of storage and memory because this demo app is running in GAE's free tier. So
 * instead of proper structures, we'll store measurements as three arrays (temp, humidity, datetime).
 * This is generally an antipattern but we're going to store ~1400 of these per day and I want to be able
 * to efficiently load months at a time.</p>
 */
@Entity
@NoArgsConstructor
public class Day {
	/** @return the releavnt Day id for a given moment in time */
	public static String idFor(DateTime whenever) {
		return whenever.withZone(DateTimeZone.UTC).toLocalDate().toString();
	}

	/** ISO-6801 format, eg 2015-04-27 */
	@Id
	@Getter
	private String date;

	/**
	 * A Measurement is the temp, humidity, when at the same index. All floats are Doubles in the datastore
	 * so using Double here just means we have one less conversion to do.
	 */
	private List<Double> temperature = new ArrayList<>();
	private List<Double> humidity = new ArrayList<>();
	private List<DateTime> when = new ArrayList<>();

	/**
	 * Construct a Day with a key appropriate for the given date
	 */
	public Day(DateTime when) {
		date = idFor(when);
	}

	/**
	 * Add a measurement to this Day
	 */
	public void add(Measurement measurement) {
		assert idFor(measurement.getWhen()).equals(date);

		temperature.add((double)measurement.getTemperature());
		humidity.add((double)measurement.getHumidity());
		when.add(measurement.getWhen());
	}

	/**
	 * @return the measurement data in friendly form
	 */
	public List<Measurement> getMeasurements() {
		List<Measurement> measurements = new ArrayList<>(temperature.size());

		for (int i = 0; i < temperature.size(); i++) {
			measurements.add(new Measurement(temperature.get(i).floatValue(), humidity.get(i).floatValue(), when.get(i)));
		}

		return measurements;
	}
}
