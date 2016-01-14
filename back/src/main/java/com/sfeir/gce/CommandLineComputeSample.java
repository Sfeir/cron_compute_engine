package com.sfeir.gce;

import java.io.IOException;
import com.sfeir.gce.operation.DeleteInstanceOperation;
import com.sfeir.gce.operation.StartInstanceOperation;
import com.google.api.services.compute.Compute;
import com.sfeir.googleapi.CommandLineServiceProvider;
import com.sfeir.googleapi.ServiceProvider;

public class CommandLineComputeSample {

   /**
    * Be sure to specify the name of your application. If the application name
    * is {@code null} or
    * blank, the application will log a warning. Suggested format is
    * "MyCompany-ProductName/1.0".
    */
   private static final String PROJECT_NAME = "sfeirjenkins";
   private static final String ZONE = "europe-west1-b";

    public static void main(String[] args) throws IOException {

      ServiceProvider serviceProvider = new CommandLineServiceProvider(PROJECT_NAME);

      Compute computeService = serviceProvider.getComputeService();

      StartInstanceOperation startOperation = new StartInstanceOperation(PROJECT_NAME, ZONE, "jenkins", "dockerjenkins", "g1-small", computeService);
      startOperation.execute();

      DeleteInstanceOperation deleteOperation = new DeleteInstanceOperation(PROJECT_NAME, ZONE, "jenkins", computeService);
      deleteOperation.execute();

   }
}
