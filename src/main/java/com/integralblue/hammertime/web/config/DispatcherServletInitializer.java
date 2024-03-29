/*
 * Copyright 2002-2012 the original author or authors.
 *
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

package com.integralblue.hammertime.web.config;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
        		new ShallowEtagHeaderFilter(),
        		new HiddenHttpMethodFilter(),
        		new CharacterEncodingFilter()
        		};
    }

	@Override
   protected Class<?>[] getRootConfigClasses() {
           return null;
   }

    @Override
   protected Class<?>[] getServletConfigClasses() {
           return new Class<?>[] { WebMvcConfig.class };
   }

   @Override
   protected String[] getServletMappings() {
           return new String[] { "/*" };
   }

   @Override
   protected void customizeRegistration(Dynamic registration) {
           registration.setAsyncSupported(true);
    }

}
