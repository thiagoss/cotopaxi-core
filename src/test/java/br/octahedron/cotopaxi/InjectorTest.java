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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.octahedron.cotopaxi.cloudservice.DatastoreFacade;
import br.octahedron.cotopaxi.cloudservice.FakeDatastoreFacade;
import br.octahedron.cotopaxi.cloudservice.FakeMemcacheFacade;
import br.octahedron.cotopaxi.cloudservice.MemcacheFacade;
import br.octahedron.cotopaxi.inject.InjectionManager;
import br.octahedron.cotopaxi.inject.InstanceHandler;
import br.octahedron.cotopaxi.inject.UserDAO;
import br.octahedron.cotopaxi.inject.UserFacade;
import br.octahedron.cotopaxi.inject.UserService;

/**
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 */
public class InjectorTest {

	private InstanceHandler injector;

	@Before
	public void setUp() {
		InjectionManager.registerDependency(DatastoreFacade.class, FakeDatastoreFacade.class);
		InjectionManager.registerDependency(MemcacheFacade.class, FakeMemcacheFacade.class);
		this.injector = new InstanceHandler();
	}
	
	@Test
	public void testInjection() {
		UserFacade facade = (UserFacade) this.injector.getInstance(UserFacade.class);
		assertNotNull(facade);
		UserService service = facade.getUserService();
		assertNotNull(service);
		UserDAO userDAO = service.getUserDAO();
		assertNotNull(userDAO);
		DatastoreFacade ds = userDAO.getDatastoreFacade();
		assertTrue(ds instanceof FakeDatastoreFacade);
		MemcacheFacade mc = userDAO.getMemcacheFacade();
		assertTrue(mc instanceof FakeMemcacheFacade);
	}
}
