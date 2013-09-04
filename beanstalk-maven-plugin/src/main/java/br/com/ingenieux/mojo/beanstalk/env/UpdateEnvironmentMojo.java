package br.com.ingenieux.mojo.beanstalk.env;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import br.com.ingenieux.mojo.beanstalk.AbstractNeedsEnvironmentMojo;
import br.com.ingenieux.mojo.beanstalk.cmd.env.update.UpdateEnvironmentCommand;
import br.com.ingenieux.mojo.beanstalk.cmd.env.update.UpdateEnvironmentContext;
import br.com.ingenieux.mojo.beanstalk.cmd.env.update.UpdateEnvironmentContextBuilder;

import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;

/**
 * Updates the environment versionLabel for a given environmentName
 * 
 * See the <a href=
 * "http://docs.amazonwebservices.com/elasticbeanstalk/latest/api/API_UpdateEnvironment.html"
 * >UpdateEnvironment API</a> call.
 * 
 * @since 0.2.0
 */
@Mojo(name="update-environment")
public class UpdateEnvironmentMojo extends AbstractNeedsEnvironmentMojo {
	/**
	 * Version Label to use. Defaults to Project Version
	 */
	@Parameter(property="beanstalk.versionLabel", defaultValue="${project.version}")
	String versionLabel;

	/**
	 * Application Description
	 */
	@Parameter(property="beanstalk.environmentDescription")
	String environmentDescription;

	/**
	 * Configuration Option Settings
	 */
	@Parameter
	ConfigurationOptionSetting[] optionSettings;

	/**
	 * <p>Template Name.</p>
	 * 
	 * <p>Could be either literal or a glob, like, <pre>ingenieux-services-prod-*</pre>. If a glob, there will
	 * be a lookup involved, and the first one in reverse ASCIIbetical order
	 * will be picked upon.
	 * </p>
	 */
	@Parameter(property="beanstalk.templateName")
	String templateName;
	
	/**
	 * Use Latest Version Label?
	 */
	@Parameter(property="beanstalk.useLatestVersionLabel")
	boolean useLatestVersionLabel = true;

	protected Object executeInternal() throws AbstractMojoExecutionException {
		if (null == optionSettings) {
			optionSettings = super.introspectOptionSettings();
		}
		
		UpdateEnvironmentContext context = UpdateEnvironmentContextBuilder
		    .updateEnvironmentContext().withEnvironmentId(curEnv.getEnvironmentId())//
		    .withEnvironmentDescription(environmentDescription)//
		    .withEnvironmentName(curEnv.getEnvironmentName())//
		    .withOptionSettings(optionSettings)//
		    .withTemplateName(lookupTemplateName(applicationName, templateName))//
		    .withVersionLabel(versionLabel)//
		    .withUseLatestVersionLabel(useLatestVersionLabel)//
		    .withLatestVersionLabel(curEnv.getVersionLabel())//
		    .build();
		UpdateEnvironmentCommand command = new UpdateEnvironmentCommand(this);

		return command.execute(context);
	}
}
