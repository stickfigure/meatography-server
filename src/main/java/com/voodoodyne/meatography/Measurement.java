package com.voodoodyne.meatography;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
	/** In degrees C */
	@JsonProperty("t")
	private float temperature;

	/** In percent */
	@JsonProperty("h")
	private float humidity;

	/** */
	@JsonProperty("w")
	private DateTime when;

	/**
	 * @return true if the other measurement has the same values (ignoring date)
	 */
	public boolean sameMeasures(Measurement other) {
		return temperature == other.temperature && humidity == other.humidity;
	}
}
