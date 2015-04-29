/*
 */

package com.voodoodyne.meatography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;
import com.voodoodyne.meatography.util.GuiceResteasyFilterDispatcher;
import com.voodoodyne.meatography.util.ObjectMapperContextResolver;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Singleton;
import static com.googlecode.objectify.ObjectifyService.factory;

/**
 * Creates our Guice module
 *
 * @author Jeff Schnitzer
 */
@Slf4j
public class GuiceConfig extends GuiceServletContextListener {

	public static class MeatographyServletModule extends ServletModule {
		@Override
		protected void configureServlets() {
			bind(ObjectifyFilter.class).in(Singleton.class);
			filter("/*").through(ObjectifyFilter.class);

			// this needs to be last
			filter("/*").through(GuiceResteasyFilterDispatcher.class);
		}
	}

	public static class MeatographyModule extends AbstractModule {
		/** This is a great place to set up Objectify */
		static {
			JodaTimeTranslators.add(factory());
			factory().register(Day.class);
		}

		@Override
		protected void configure() {
			bind(Measurements.class);

			// Make sure RESTEasy picks this up so we get our ObjectMapper from guice
			bind(ObjectMapperContextResolver.class);
		}

		@Provides
		@Singleton
		ObjectMapper objectMapper() {
			return new ObjectMapper().registerModule(new JodaModule());
		}
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new MeatographyServletModule(), new MeatographyModule());
	}
}