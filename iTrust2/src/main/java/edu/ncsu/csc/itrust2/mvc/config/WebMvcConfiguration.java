/*
 * Copyright 2002-2016 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package edu.ncsu.csc.itrust2.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Maintains a variety of global configurations needed by the Spring web
 * application.
 *
 * @author Spring Security team
 * @author Kai Presler-Marshall
 *
 */
@EnableWebMvc
@ComponentScan ( { "edu.ncsu.csc.itrust2.controllers",
        "edu.ncsu.csc.itrust2.config" } ) /*
                                           * Controller packages. Update as
                                           * necessary
                                           */
@EnableGlobalMethodSecurity (
        prePostEnabled = true ) /*
                                 * Tell Spring to enforce the @PreAuthorize
                                 * annotations on Controller methods; this done
                                 * to ensure that only a user of the right type
                                 * has access to the page
                                 */
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * ApplicationContext used to maintain security
     */
    @Autowired
    private ApplicationContext          applicationContext;

    @Autowired
    private FormattingConversionService mvcConversionService;

    /**
     * Sets the /login path as the entry point for a user to login
     */
    @Override
    public void addViewControllers ( final ViewControllerRegistry registry ) {
        registry.addViewController( "/login" ).setViewName( "login" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }

    /**
     * Makes the Resources folder unprotected
     */
    @Override
    public void addResourceHandlers ( final ResourceHandlerRegistry registry ) {
        registry.addResourceHandler( "/resources/**" ).addResourceLocations( "classpath:/resources/" )
                .setCachePeriod( 31556926 );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }

    /**
     * ViewResolver used for Thymeleaf's parsing
     *
     * @return ViewResolver generated
     */
    @Bean
    public ViewResolver viewResolver () {
        final ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine( templateEngine() );
        resolver.setCharacterEncoding( "UTF-8" );
        return resolver;
    }

    /**
     * Templating engine for Spring and Thymeleaf
     *
     * @return TemplateEngine generated
     */
    @Bean
    public TemplateEngine templateEngine () {
        final SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setEnableSpringELCompiler( true );
        engine.setTemplateResolver( templateResolver() );
        return engine;
    }

    /**
     * Creates a TemplateResolver
     *
     * @return SpringResourceTemplateResolver generated
     */
    public SpringResourceTemplateResolver templateResolver () {

        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix( "classpath:/views/" );
        resolver.setSuffix( ".html" );
        resolver.setTemplateMode( TemplateMode.HTML );
        resolver.setApplicationContext( applicationContext );
        return resolver;
    }

    /**
     * Creates a DomainClassConverter from the mvcConversionService
     * 
     * @return DomainClassConverter
     */
    @Bean
    public DomainClassConverter< ? > domainClassConverter () {
        return new DomainClassConverter<FormattingConversionService>( mvcConversionService );
    }
}
