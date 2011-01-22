/*
 *  This file is part of Cotopaxi.
 *
 *  Cotopaxi is free software: you can redistribute it and/or modify
 *  it under the terms of the Lesser GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Cotopaxi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the Lesser GNU General Public License
 *  along with Cotopaxi. If not, see <http://www.gnu.org/licenses/>.
 */
package br.octahedron.cotopaxi;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.octahedron.cotopaxi.controller.FacadeThree;
import br.octahedron.cotopaxi.controller.auth.AuthManager;
import br.octahedron.cotopaxi.controller.auth.SessionUserLookupStrategy;
import br.octahedron.cotopaxi.controller.auth.UserInfo;
import br.octahedron.cotopaxi.controller.auth.UserLookupStrategy;
import br.octahedron.cotopaxi.controller.auth.UserNotAuthorizedException;
import br.octahedron.cotopaxi.controller.auth.UserNotLoggedException;
import br.octahedron.cotopaxi.controller.filter.FilterException;
import br.octahedron.cotopaxi.inject.InjectionManager;
import br.octahedron.cotopaxi.inject.InstanceHandler;
import br.octahedron.cotopaxi.metadata.MetadataMapper;
import br.octahedron.cotopaxi.metadata.PageNotFoundExeption;
import br.octahedron.cotopaxi.metadata.annotation.Action.HTTPMethod;
import br.octahedron.cotopaxi.metadata.annotation.LoginRequired.LoginRequiredMetadata;

/**
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 * 
 */
public class AuthTest {

	private InstanceHandler instanceHandler = new InstanceHandler();
	private UserLookupStrategy userStrategy;
	private AuthManager auth;
	private MetadataMapper mapper;

	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		this.userStrategy = createMock(UserLookupStrategy.class);
		CotopaxiConfigView config = instanceHandler.getInstance(CotopaxiConfigView.class);
		config.getCotopaxiConfig().addModelFacade(FacadeThree.class);
		
		InjectionManager.registerImplementation(UserLookupStrategy.class, this.userStrategy);
		
		this.auth = instanceHandler.getInstance(AuthManager.class);
		this.mapper = new MetadataMapper(config);
	}
	
	@After
	public void tearDown() {
		this.userStrategy = null;
		InjectionManager.removeImplementation(AuthManager.class);
	}

	@Test
	public void authenticationTest1() throws IllegalArgumentException, FilterException, IllegalAccessException, PageNotFoundExeption,
			UserNotAuthorizedException {
		/*
		 * This test checks no logged user
		 */
		// Prepare test
		RequestWrapper request = createMock(RequestWrapper.class);
		expect(request.getURL()).andReturn("/restricted1").atLeastOnce();
		expect(request.getHTTPMethod()).andReturn(HTTPMethod.GET).atLeastOnce();
		expect(request.getFormat()).andReturn(null);
		replay(request);
		expect(this.userStrategy.getCurrentUser(request)).andReturn(null);
		expect(this.userStrategy.getLoginURL("/restricted1")).andReturn("/login");
		replay(this.userStrategy);

		try {
			// invoking the auth mechanism
			LoginRequiredMetadata login = this.mapper.getMapping(request).getLoginMetadata();
			this.auth.authorizeUser(request, login);
			fail();
		} catch (UserNotLoggedException e) {
			assertEquals("/login", e.getRedirectURL());
		} finally {
			// check test results
			verify(request);
			verify(this.userStrategy);
		}
	}

	@Test
	public void authenticationTest2() throws PageNotFoundExeption, UserNotLoggedException, UserNotAuthorizedException {
		/*
		 * This test an logged user
		 */
		// Prepare test
		RequestWrapper request = createMock(RequestWrapper.class);
		expect(request.getURL()).andReturn("/restricted1").atLeastOnce();
		expect(request.getHTTPMethod()).andReturn(HTTPMethod.GET).atLeastOnce();
		expect(request.getFormat()).andReturn(null);
		replay(request);
		expect(this.userStrategy.getCurrentUser(request)).andReturn(new UserInfo("danilo"));
		replay(this.userStrategy);

		// invoking the auth mechanism
		LoginRequiredMetadata login = this.mapper.getMapping(request).getLoginMetadata();
		this.auth.authorizeUser(request, login);

		// check test results
		verify(request);
		verify(this.userStrategy);
	}

	@Test(expected = UserNotAuthorizedException.class)
	public void authenticationTest3() throws PageNotFoundExeption, UserNotLoggedException, UserNotAuthorizedException {
		/*
		 * This test an logged user but with wrong role
		 */
		// Prepare test
		RequestWrapper request = createMock(RequestWrapper.class);
		expect(request.getURL()).andReturn("/restricted3").atLeastOnce();
		expect(request.getHTTPMethod()).andReturn(HTTPMethod.GET).atLeastOnce();
		expect(request.getFormat()).andReturn(null);
		replay(request);
		expect(this.userStrategy.getCurrentUser(request)).andReturn(new UserInfo("danilo", "tester"));
		replay(this.userStrategy);

		try {
			// invoking the auth mechanism
			LoginRequiredMetadata login = this.mapper.getMapping(request).getLoginMetadata();
			this.auth.authorizeUser(request, login);
		} finally {
			assertTrue(this.userStrategy == this.instanceHandler.getInstance(UserLookupStrategy.class));
			assertEquals(this.userStrategy, this.instanceHandler.getInstance(UserLookupStrategy.class));
			// check test results
			verify(request);
			verify(this.userStrategy);
		}
	}

	@Test
	public void authenticationTest4() throws PageNotFoundExeption, UserNotLoggedException, UserNotAuthorizedException {
		/*
		 * This test an logged with required role
		 */
		// Prepare test
		RequestWrapper request = createMock(RequestWrapper.class);
		expect(request.getURL()).andReturn("/restricted3").atLeastOnce();
		expect(request.getHTTPMethod()).andReturn(HTTPMethod.GET).atLeastOnce();
		expect(request.getFormat()).andReturn(null);
		replay(request);
		expect(this.userStrategy.getCurrentUser(request)).andReturn(new UserInfo("danilo", "admin"));
		replay(this.userStrategy);

		// invoking the auth mechanism
		LoginRequiredMetadata login = this.mapper.getMapping(request).getLoginMetadata();
		this.auth.authorizeUser(request, login);

		// check test results
		verify(request);
		verify(this.userStrategy);
	}
	
	@Test
	public void authenticationTest5() throws PageNotFoundExeption, UserNotLoggedException, UserNotAuthorizedException {
		/*
		 * This test an logged with required role
		 */
		// Prepare test
		RequestWrapper request = createMock(RequestWrapper.class);
		expect(request.getURL()).andReturn("/restricted3").atLeastOnce();
		expect(request.getHTTPMethod()).andReturn(HTTPMethod.GET).atLeastOnce();
		expect(request.getFormat()).andReturn(null);
		expect(request.getSessionAttribute(SessionUserLookupStrategy.USER_SESSION_ATTRIBUTE)).andReturn(new UserInfo("danilo","admin"));
		replay(request);

		InjectionManager.registerImplementation(UserLookupStrategy.class, new SessionUserLookupStrategy());
		InjectionManager.removeImplementation(AuthManager.class);
		this.auth = instanceHandler.getInstance(AuthManager.class);
		
		// invoking the auth mechanism
		LoginRequiredMetadata login = this.mapper.getMapping(request).getLoginMetadata();
		this.auth.authorizeUser(request, login);

		// check test results
		verify(request);
	}

}
