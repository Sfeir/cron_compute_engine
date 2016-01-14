package com.sfeir.gce.operation;

import java.io.IOException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceList;

public class ListInstanceOperation extends ComputeOperation {

    public ListInstanceOperation(String GOOGLE_PROJECT_NAME, String ZONE, Compute compute) {
        super(GOOGLE_PROJECT_NAME, ZONE, compute);
    }

    public InstanceList getResult() {
        return result;
    }

    InstanceList result;

    public void execute() throws IOException {

        Compute.Instances.List request = compute.instances().list(GOOGLE_PROJECT_NAME, ZONE);

        InstanceList instanceList = request.execute();

        result = instanceList;

    }


}
