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
package br.octahedron.cotopaxi.model.attribute;

/**
 * Indicates that a {@link ModelAttribute} is invalid.
 * 
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 * 
 */
public class InvalidAttributeException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidAttributeException() {
	}

	public InvalidAttributeException(String message) {
		super(message);
	}

	public InvalidAttributeException(Throwable cause) {
		super(cause);
	}

	public InvalidAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

}
