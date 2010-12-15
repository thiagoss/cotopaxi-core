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
package br.octahedron.cotopaxi.metadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import br.octahedron.cotopaxi.config.CotopaxiConfigView;
import br.octahedron.cotopaxi.config.CotopaxiConfigurator;
import br.octahedron.cotopaxi.model.SuccessActionResponse;

/**
 * Annotation to be used to specify a template to be used to present the action response.
 * 
 * You should remember that you shoudn't pass the templates root folder.
 * 
 * Eg.: if your templates are stored at folder "templates" and you template name is
 * "my_template.vm", thus the template path is "templates/my_template.vm", you should annotated as
 * "my_template.vm" only and set the root folder using a {@link CotopaxiConfigurator}.
 * 
 * If you didn't set the templates names, it uses the "<simple class name>_<method name>.vm" as
 * template name for SUCCESS {@link SuccessActionResponse},
 * "<simple class name>_<method name>_error.vm" for EXCEPTION {@link SuccessActionResponse} and
 * "<simple class name>_<method name>_invalid.vm" for VALIDATION_FAILED
 * {@link SuccessActionResponse}.
 * 
 * @see CotopaxiConfigView#getTemplateRoot
 * 
 * @author Danilo Penna de Queiroz - daniloqueiroz@octahedron.com.br
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Template {

	public static final String TEMPLATE_EXTENSION = ".vm";

	public static final String ERROR_TEMPLATE_SUFFIX = "_error";

	public static final String VALIDATION_FAILED_TEMPLATE_SUFFIX = "_invalid";

	/**
	 * @return The template to be used on model successful execution
	 */
	String onSuccess();

	/**
	 * @return The template to be used on model error execution.
	 */
	String onError();

	/**
	 * @return The template to be used on model input validation fails.
	 */
	String onValidationFails();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface TemplateOnError {
		String template();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface TemplateOnSuccess {
		String template();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface TemplateOnValidationFails {
		String template();
	}

	/**
	 * A wrapper for Template metatada
	 */
	public static class TemplateMetadata {
		private String onSuccess;
		private String onError;
		private String onValidationFail;

		public TemplateMetadata(Method method) {
			// try load from annotation
			Template template = method.getAnnotation(Template.class);
			String onSuccessTPL, onErrorTPL, onValidationFailTPL;
			onSuccessTPL = onErrorTPL = onValidationFailTPL = null;
			if (template != null) {
				onSuccessTPL = template.onSuccess();
				onErrorTPL = template.onError();
				onValidationFailTPL = template.onValidationFails();
			} else {
				TemplateOnSuccess success = method.getAnnotation(TemplateOnSuccess.class);
				if (success != null) {
					onSuccessTPL = success.template();
				}
				TemplateOnError error = method.getAnnotation(TemplateOnError.class);
				if (error != null) {
					onErrorTPL = error.template();
				}
				TemplateOnValidationFails validation = method.getAnnotation(TemplateOnValidationFails.class);
				if (validation != null) {
					onValidationFailTPL = validation.template();
				}
			}
			// check and set default, if needed
			String prefix = method.getDeclaringClass().getSimpleName() + "_" + method.getName();
			this.onSuccess = (onSuccessTPL != null) ? onSuccessTPL : prefix + TEMPLATE_EXTENSION;
			this.onError = (onErrorTPL != null) ? onErrorTPL : prefix + ERROR_TEMPLATE_SUFFIX + TEMPLATE_EXTENSION;
			this.onValidationFail = (onValidationFailTPL != null) ? onValidationFailTPL : prefix + VALIDATION_FAILED_TEMPLATE_SUFFIX
					+ TEMPLATE_EXTENSION;
		}

		public String getOnSuccess() {
			return this.onSuccess;
		}

		public String getOnError() {
			return this.onError;
		}

		public String getOnValidationFail() {
			return this.onValidationFail;
		}
	}
}