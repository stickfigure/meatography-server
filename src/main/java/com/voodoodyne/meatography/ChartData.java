package com.voodoodyne.meatography;

import lombok.Data;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Format very palatable to dygraphs
 */
@Data
public class ChartData {
	/** These are going to be big structures, cut down on the reallocs */
	private static final int START_CAPACITY = 1024 * 48;

	List<Double> temperature = new ArrayList<>(START_CAPACITY);
	List<Double> humidity = new ArrayList<>(START_CAPACITY);
	List<DateTime> when = new ArrayList<>(START_CAPACITY);
}
