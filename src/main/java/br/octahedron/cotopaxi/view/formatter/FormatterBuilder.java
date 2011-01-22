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

import static br.octahedron.cotopaxi.CotopaxiConfigView.HTML_FORMAT;
import static br.octahedron.cotopaxi.CotopaxiConfigView.JSON_FORMAT;

import java.util.HashMap;
import java.util.Map;

import br.octahedron.cotopaxi.CotopaxiConfigView;
import br.octahedron.cotopaxi.inject.Inject;

/**
 * A builder for formatters. It creates the right {@link Formatter} for a given format. It looks at
 * the {@link CotopaxiConfigView} to check which {@link Formatter} should be used for each format.
 * 
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 */
public class FormatterBuilder {
	
	@Inject
	private CotopaxiConfigView cotopaxiConfigView;
	private Map<String, Class<? extends Formatter>> defaultFormatters = new HashMap<String, Class<? extends Formatter>>();
	
	public FormatterBuilder() {
		this.registerDefaultFormatter(HTML_FORMAT, VelocityFormatter.class);
		this.registerDefaultFormatter(JSON_FORMAT, SimpleJSONFormatter.class);
	}
	
	/**
	 * @param cotopaxiConfigView Sets the {@link CotopaxiConfigView}
	 */
	public void setCotopaxiConfigView(CotopaxiConfigView cotopaxiConfigView) {
		this.cotopaxiConfigView = cotopaxiConfigView;
	}

	protected void registerDefaultFormatter(String format, Class<? extends Formatter> formatterClass) {
		this.defaultFormatters.put(format, formatterClass);
	}

	/**
	 * Gets the {@link Formatter} for the given format. If there's no {@link Formatter} registered
	 * for the given format, it returns null.
	 * 
	 * @throws FormatterNotFoundException
	 */
	public Formatter getFormatter(String format) throws FormatterNotFoundException {
		try {
			Class<? extends Formatter> formatterClass = this.cotopaxiConfigView.getFormatter(format);
			if (formatterClass == null && this.defaultFormatters.containsKey(format)) {
				formatterClass = this.defaultFormatters.get(format);
			}
			if (formatterClass != null) {
				return formatterClass.newInstance();
			} else {
				throw new FormatterNotFoundException("No Formatter fegistered for format " + format);
			}
		} catch (InstantiationException e) {
			throw new FormatterNotFoundException("Can't instanciate Formatter for format" + format, e);
		} catch (IllegalAccessException e) {
			throw new FormatterNotFoundException("Can't instanciate Formatter for format" + format, e);
		}
	}
}
