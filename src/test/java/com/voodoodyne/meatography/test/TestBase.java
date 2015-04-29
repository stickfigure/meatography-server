/*
 */

package com.voodoodyne.meatography.test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.voodoodyne.meatography.GuiceConfig.MeatographyModule;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * All tests should extend this class to set up our test environment. This includes GAE, Mockito, Objectify,
 * and Guice.
 *
 * @see <a href="http://code.google.com/appengine/docs/java/howto/unittesting.html">Unit Testing in Appengine</a>
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class TestBase
{
	/** */
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(
					// Our tests assume strong consistency
					new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(),
					new LocalTaskQueueTestConfig());

	private Injector injector;

	/** Set up and torn down with every method */
	private Closeable rootService;

	/** */
	@BeforeMethod
	public void setUp() {
		this.helper.setUp();
		MockitoAnnotations.initMocks(this);

		injector = Guice.createInjector(Modules.override(new MeatographyModule()).with(setUpTestModule()));

		assert rootService == null;
		rootService = ObjectifyService.begin();
	}

	/** */
	@AfterMethod
	public void tearDown() {
		// This is normally done in ObjectifyFilter but that doesn't exist for tests
		rootService.close();
		rootService = null;

		injector = null;

		this.helper.tearDown();
	}

	/** Override this to add a custom override module to the guice environment */
	protected Module setUpTestModule() {
		return new EmptyModule();
	}

	/**
	 * Get an instance of something out of Guice
	 */
	public <T> T instance(Class<T> type) {
		return injector.getInstance(type);
	}
}
