/*
 * Copyright (c) 2013 Google Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under
 * the License.
 */

package com.google.api.services.samples.compute.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sfeir.gce.operation.ListInstanceOperation;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.services.compute.Compute;
import com.sfeir.googleapi.AppEngineServiceProvider;
import com.sfeir.googleapi.ServiceProvider;

/**
 * Entry servlet for the Compute Engine API App Engine Sample.
 * Demonstrates how to make an authenticated API call using OAuth 2 helper
 * classes.
 */
public class ComputeSampleServlet extends AbstractAppEngineAuthorizationCodeServlet {

   /**
    * Be sure to specify the name of your application. If the application name
    * is {@code null} or
    * blank, the application will log a warning. Suggested format is
    * "MyCompany-ProductName/1.0".
    */
   private static final String PROJECT_NAME = "sfeirjenkins";
    private static final String ZONE = "europe-west1-b";
   private static final long   serialVersionUID = 1L;

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

      ServiceProvider serviceProvider = new AppEngineServiceProvider(PROJECT_NAME);


      Compute compute = serviceProvider.getComputeService();
      // Add the code to make an API call here.

       ListInstanceOperation listInstanceOperation = new ListInstanceOperation(PROJECT_NAME, ZONE, compute);
       listInstanceOperation.execute();

      // Send the results as the response
      resp.setStatus(200);
      resp.setContentType("application/json");

      PrintWriter writer = resp.getWriter();

      writer.println(listInstanceOperation.getResult().toPrettyString());

   }

   @Override
   protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
      return Utils.initializeFlow();
   }

   @Override
   protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
      return Utils.getRedirectUri(req);
   }
}
