/*
 */

package com.voodoodyne.meatography.util;

import com.google.inject.Injector;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * Use this as the Resteasy FilterDispatcher and it will be properly integrated with Guice
 * in a way that is more "Guicy" than the standard Resteasy integration. Map this
 * as a filter in Guice.
 *
 * @author Jeff Schnitzer
 */
@Singleton
//@Slf4j
public class GuiceResteasyFilterDispatcher extends FilterDispatcher
{
	private Injector injector;

	@Inject
	public GuiceResteasyFilterDispatcher(Injector injector) {
		this.injector = injector;
	}

	/**
	 * Also initializes all Guice bindings for
	 */
	@Override
	public void init(FilterConfig cfg) throws ServletException {
		super.init(cfg);

		final ServletContext context = cfg.getServletContext();
		final Registry registry = (Registry) context.getAttribute(Registry.class.getName());
		final ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) context.getAttribute(ResteasyProviderFactory.class.getName());
		final ModuleProcessor processor = new ModuleProcessor(registry, providerFactory);

		processor.processInjector(injector);
	}
}