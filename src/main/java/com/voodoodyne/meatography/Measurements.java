package com.voodoodyne.meatography;

import com.googlecode.objectify.VoidWork;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 */
@Path("/measurements")
@Slf4j
public class Measurements {

	/**
	 * @return all measurements, for now
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Measurement> get() {
		List<Measurement> measurements = new ArrayList<>();

		for (Day day : ofy().load().type(Day.class)) {
			measurements.addAll(day.getMeasurements());
		}

		return measurements;
	}

	/**
	 * @return all measurements in a funky format that makes dygraphs happy and just happens to be
	 * close to the native storage representation. We're trying to make this as efficient as possible
	 * so we can display tens of thousands of points.
	 */
	@GET
	@Path("/funky")
	@Produces(MediaType.APPLICATION_JSON)
	public ChartData getFunky() {
		ChartData data = new ChartData();

		for (Day day : ofy().load().type(Day.class)) {
			day.addTo(data);
		}

		return data;
	}

	/**
	 * Add one measurement to our database. Date is the date posted, not the date submitted (which is ignored).
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String post(final Measurement measurement) {
		log.debug("Received " + measurement);

		// Let's be untrustworthy of clients and substitute our own date for all taken measurements. That is,
		// the date of a measurement is the date it arrived.
		measurement.setWhen(DateTime.now());

		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				Day day = ofy().load().type(Day.class).id(Day.idFor(measurement.getWhen())).now();
				if (day == null)
					day = new Day(measurement.getWhen());

				day.add(measurement);

				ofy().save().entity(day);
			}
		});

		return "success";
	}
}
