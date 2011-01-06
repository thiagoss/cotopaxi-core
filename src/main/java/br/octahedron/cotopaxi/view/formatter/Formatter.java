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
package br.octahedron.cotopaxi.view.formatter;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import br.octahedron.cotopaxi.view.ContentType;

/**
 * Formatters are responsible by formats the responses to be rendered to user.
 * 
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 * 
 */
public abstract class Formatter {
	
	private static final Logger logger = Logger.getLogger(Formatter.class.getName());

	private Map<String, Object> attributes;
	private ContentType contentType;
	private Locale locale;
	private String formatted;

	public Formatter(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * Checks if this {@link Formatter}formatter is ready to be used. Be ready means that the locale
	 * and attributes was already set.
	 * 
	 * @return <code>true</code> if it's ready, <code>false</code> otherwise.
	 */
	public boolean isReady() {
		return this.locale != null && this.attributes != null && !this.isTest();
	}

	/**
	 * Gets the attributes to be used by this {@link Formatter}.
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
	
	/**
	 * Gets the response's content type
	 * 
	 * @see ContentType
	 */
	public ContentType getContentType() throws IllegalStateException {
		return this.contentType;
	}

	/**
	 * Gets the response's locale
	 */
	public Locale getLocale() throws IllegalStateException {
		if (this.isReady()) {
			return this.locale;
		} else {
			throw new IllegalStateException("This formatter is not ready. You must set locale, before use it!");
		}
	}

	/**
	 * Sets this formatter Attributes
	 * 
	 * @param atts
	 *            The attributes to be formatted
	 */
	public void setAttributes(Map<String, Object> atts) {
		this.attributes = atts;
		this.format();
	}

	/**
	 * Sets this formatter locale.
	 * 
	 * @param lc
	 *            the response locale
	 */
	public void setLocale(Locale lc) {
		this.locale = lc;
		this.format();
	}

	public final String getFormatted() {
		return this.formatted;
	}

	/**
	 * Formats the response. It's a template method, subclass should implements the {@link Formatter#doFormat()} method.
	 */
	protected final void format() {
		if (this.isReady()) {
			logger.fine("Formatting...");
			this.formatted = this.doFormat();
		} 
	}
	
	/**
	 * Formats the content and return it as String. This method is called by the
	 * {@link Formatter#format()} template method, that check if this format is ready before call
	 * it, thus when this method be called, all the formatter properties, such locale and attributes
	 * will be already set.
	 * 
	 * @return
	 */
	protected abstract String doFormat();

	/**
	 * Checks if its running tests
	 */
	private boolean isTest() {
		return Boolean.parseBoolean(System.getProperty("cotopaxi.test", "false"));
	}
}
