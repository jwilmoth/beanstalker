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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import br.com.ingenieux.mojo.beanstalk.AbstractNeedsEnvironmentMojo;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentCommand;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentContext;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentContextBuilder;

import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsResult;

/**
 * Creates and Launches an Elastic Beanstalk Environment
 * 
 * See the docs for the <a href=
 * "http://docs.amazonwebservices.com/elasticbeanstalk/latest/api/API_CreateEnvironment.html"
 * >CreateEnvironment API</a> call.
 * 
 * @since 0.1.0
 */
@Mojo(name="create-environment")
public class CreateEnvironmentMojo extends AbstractNeedsEnvironmentMojo {
	/**
	 * Application Description
	 */
	@Parameter(property="beanstalk.applicationDescription", defaultValue="${project.name}")
	String applicationDescription;

	/**
	 * Configuration Option Settings
	 */
	@Parameter
	ConfigurationOptionSetting[] optionSettings;

	/**
	 * Version Label to use
	 */
	@Parameter(property="beanstalk.versionLabel")
	String versionLabel;

	/**
	 * Solution Stack Name
	 */
	@Parameter(property="beanstalk.solutionStack", defaultValue="32bit Amazon Linux running Tomcat 7")
	String solutionStack;

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
	 * Overrides parent in order to avoid a thrown exception as there's not an environment to lookup
	 */
	@Override
	protected void configure() {
		// Disable parent lookup - We're CREATING, mind that!
	}
	
	@Override
	protected Object executeInternal() throws AbstractMojoExecutionException {
		CreateEnvironmentResult result = createEnvironment(cnamePrefix, this.environmentName);

		return result;
	}

	protected CreateEnvironmentResult createEnvironment(String cnameToCreate, String newEnvironmentName)
	    throws AbstractMojoExecutionException {
		/*
		 * Hey Aldrin, have you ever noticed we're getting pedantic on those validations?
		 */
		Validate.isTrue(isNotBlank(newEnvironmentName), "No New Environment Name Supplied");
		
		if (null == optionSettings) {
			optionSettings = introspectOptionSettings();
		}
		
		if (StringUtils.isBlank(versionLabel)) {
			DescribeApplicationVersionsResult appVersionsResult = getService().describeApplicationVersions(new DescribeApplicationVersionsRequest().withApplicationName(applicationName));

			List<ApplicationVersionDescription> appVersionList = new ArrayList<ApplicationVersionDescription>(appVersionsResult.getApplicationVersions());
			
			Collections.sort(appVersionList, new Comparator<ApplicationVersionDescription>() {
				@Override
				public int compare(ApplicationVersionDescription o1,
						ApplicationVersionDescription o2) {
					return new CompareToBuilder().append(o2.getDateUpdated(), o1.getDateUpdated()).append(o2.getDateCreated(), o1.getDateUpdated()).toComparison();
				}
			});
			
			if (appVersionList.isEmpty()) {
				String message = "No version label supplied **AND** no app versions available.";
				
				getLog().info(message);
				
				throw new IllegalStateException(message);
			} else {
				versionLabel = appVersionList.get(0).getVersionLabel();
				
				getLog().info("Using latest available application version " + versionLabel);
			}
		}
		
		CreateEnvironmentContextBuilder builder = CreateEnvironmentContextBuilder
		    .createEnvironmentContext() //
		    .withApplicationName(applicationName)//
		    .withApplicationDescription(applicationDescription)//
		    .withCnamePrefix(cnameToCreate)//
		    .withSolutionStack(solutionStack)//
		    .withTemplateName(templateName)//
		    .withEnvironmentName(newEnvironmentName)//
		    .withOptionSettings(optionSettings)//
		    .withVersionLabel(versionLabel);//
		
		CreateEnvironmentContext context = builder.build();

		CreateEnvironmentCommand command = new CreateEnvironmentCommand(this);

		return command.execute(context);
	}
}
