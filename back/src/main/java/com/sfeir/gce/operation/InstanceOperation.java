package com.sfeir.gce.operation;

import com.google.api.services.compute.Compute;

public abstract class InstanceOperation extends ComputeOperation {

    protected InstanceOperation(String GOOGLE_PROJECT_NAME, String ZONE, String INSTANCE_NAME, Compute compute) {
        super(GOOGLE_PROJECT_NAME, ZONE, compute);
        this.INSTANCE_NAME = INSTANCE_NAME;
    }

    protected String INSTANCE_NAME;


}
