package com.sfeir.gce.operation;

import java.io.IOException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;

public class DeleteInstanceOperation extends InstanceOperation {


    public DeleteInstanceOperation(String GOOGLE_PROJECT_NAME, String ZONE, String INSTANCE_NAME, Compute compute) {
        super(GOOGLE_PROJECT_NAME, ZONE, INSTANCE_NAME, compute);
    }

    public void execute() throws IOException {
        log.info(String.format("Deleting: %s %s %s", GOOGLE_PROJECT_NAME, ZONE, INSTANCE_NAME));
        Compute.Instances.Delete delete = compute.instances().delete(GOOGLE_PROJECT_NAME, ZONE, INSTANCE_NAME);
        Operation operation = delete.execute();
        log.info(operation.getStatus());
    }


}