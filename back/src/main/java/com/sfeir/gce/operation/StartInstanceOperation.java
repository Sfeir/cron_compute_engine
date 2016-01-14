package com.sfeir.gce.operation;

import java.io.IOException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.Operation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class StartInstanceOperation extends InstanceOperation {

    protected String DISK_NAME;
    protected String instanceType;


    private String natIP;

    public StartInstanceOperation(String GOOGLE_PROJECT_NAME, String ZONE, String INSTANCE_NAME, String diskName, String instanceType, Compute compute) {
        super(GOOGLE_PROJECT_NAME, ZONE, INSTANCE_NAME, compute);
        this.DISK_NAME = diskName;
        this.instanceType = instanceType;
    }

    public StartInstanceOperation setNatIP(String natIP) {
        this.natIP = natIP;
        return this;
    }

    public void execute() throws IOException {
        log.info(String.format("Creating: %s %s %s %s %s", GOOGLE_PROJECT_NAME, ZONE, INSTANCE_NAME, DISK_NAME, instanceType));

        natIP = "23.251.130.148";
        Instance content = createPayload_instance(INSTANCE_NAME, DISK_NAME, instanceType, natIP);
        Compute.Instances.Insert insert = compute.instances().insert(GOOGLE_PROJECT_NAME, ZONE, content);
        Operation operation = insert.execute();
        System.out.println("OK " + operation.getStatus());
    }

    /**
     * Makes the payload for creating an instance.
     *
     * @param instanceName the name of the instance.
     * @return the payload for the POST request to create a new instance.
     */
    public Instance createPayload_instance(
            String instanceName, String bootDiskName, String machineType) throws IOException {

        return createPayload_instance(instanceName, bootDiskName, machineType, null);
    }
    /**
     * Makes the payload for creating an instance.
     *
     * @param natIP external IP address
     * @param instanceName the name of the instance.
     * @return the payload for the POST request to create a new instance.
     */
    public Instance createPayload_instance(
            String instanceName, String bootDiskName, String machineType, String natIP) throws IOException {

        GoogleUrl googleUrl = new GoogleUrl(GOOGLE_PROJECT_NAME, ZONE);


        JsonObject json = new JsonObject();
        json.addProperty("kind", "compute#instance");
        json.addProperty("name", instanceName);
        json.addProperty("machineType", googleUrl.getMachineTypeUrl(machineType));

        JsonObject disksElem = new JsonObject();
        disksElem.addProperty("kind", "compute#attachedDisk");
        disksElem.addProperty("boot", true);
        disksElem.addProperty("type", "PERSISTENT");
        disksElem.addProperty("mode", "READ_WRITE");
        disksElem.addProperty("deviceName", bootDiskName);
        disksElem.addProperty("zone", googleUrl.getUrlPrefixWithProjectAndZone());
        disksElem.addProperty("source", googleUrl.getSource(bootDiskName));

        JsonArray jsonAr = new JsonArray();
        jsonAr.add(disksElem);
        json.add("disks", jsonAr);

        JsonObject networkInterfacesObj = new JsonObject();
        networkInterfacesObj.addProperty("kind", "compute#instanceNetworkInterface");
        networkInterfacesObj.addProperty("network", googleUrl.getDefaultNetwork());

        JsonObject accessConfigsObj = new JsonObject();
        accessConfigsObj.addProperty("name", "External NAT");
        accessConfigsObj.addProperty("type", "ONE_TO_ONE_NAT");
        if(natIP != null) {
            accessConfigsObj.addProperty("natIP", natIP);
        }

        JsonArray accessConfigsAr = new JsonArray();
        accessConfigsAr.add(accessConfigsObj);
        networkInterfacesObj.add("accessConfigs", accessConfigsAr);

        JsonArray networkInterfacesAr = new JsonArray();
        networkInterfacesAr.add(networkInterfacesObj);
        json.add("networkInterfaces", networkInterfacesAr);

        JsonObject serviceAccountsObj = new JsonObject();
        serviceAccountsObj.addProperty("kind", "compute#serviceAccount");
        serviceAccountsObj.addProperty("email", "default");
        JsonArray scopesAr = new JsonArray();
        scopesAr.add(new JsonPrimitive("https://www.googleapis.com/auth/userinfo.email"));
        scopesAr.add(new JsonPrimitive("https://www.googleapis.com/auth/compute"));
        scopesAr.add(new JsonPrimitive("https://www.googleapis.com/auth/devstorage.full_control"));
        serviceAccountsObj.add("scopes", scopesAr);
        JsonArray serviceAccountsAr = new JsonArray();
        serviceAccountsAr.add(serviceAccountsObj);
        json.add("serviceAccounts", serviceAccountsAr);

        JsonObject metadataObj = new JsonObject();

        JsonArray mdItemsAr = new JsonArray();
        JsonObject mdItemsObj = new JsonObject();
        mdItemsObj.addProperty("key", "startup-script-url");
        mdItemsObj.addProperty("value", "");
        mdItemsAr.add(mdItemsObj);
        metadataObj.add("items", mdItemsAr);
        json.add("metadata", metadataObj);
        String payload = json.toString();

        JsonFactory factory = JacksonFactory.getDefaultInstance();
        Instance instance = factory.fromString(payload, Instance.class);

        return instance;
    }
}
