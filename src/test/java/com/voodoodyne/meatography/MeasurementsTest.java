package com.voodoodyne.meatography;

import com.voodoodyne.meatography.test.TestBase;
import org.testng.annotations.Test;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 */
public class MeasurementsTest extends TestBase {

	@Test
	public void submittedMeasurementsShowUpInResults() {
		Measurements measurements = instance(Measurements.class);

		Measurement m0 = new Measurement(10, 20, null);
		Measurement m1 = new Measurement(30, 40, null);

		measurements.post(m0);
		measurements.post(m1);

		List<Measurement> found = measurements.get();

		assertThat(found, hasSize(2));
		assertThat(found.get(0).sameMeasures(m0), is(true));
		assertThat(found.get(1).sameMeasures(m1), is(true));
	}
}
