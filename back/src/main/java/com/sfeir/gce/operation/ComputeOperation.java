package com.sfeir.gce.operation;

import java.io.IOException;
import java.util.logging.Logger;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.Operation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class ComputeOperation {

    protected Logger log = Logger.getLogger(ComputeOperation.class.getName());

    protected String GOOGLE_PROJECT_NAME;
    protected String ZONE;
    protected Compute compute;

    public ComputeOperation(String GOOGLE_PROJECT_NAME, String ZONE, Compute compute) {
        this.GOOGLE_PROJECT_NAME = GOOGLE_PROJECT_NAME;
        this.ZONE = ZONE;
        this.compute = compute;
    }

    public void execute() throws IOException {
        String instanceName = "testinstance";
        Instance content = createPayload_instance(instanceName, "testdisk", "f1-micro", "23.251.130.148");

/*
        Compute.Instances.Delete delete = compute.instances().delete(GOOGLE_PROJECT_NAME, ZONE, instanceName);
        Operation operation = delete.execute();
        System.out.println(operation.getStatus());
*/
        Compute.Instances.Insert insert = compute.instances().insert(GOOGLE_PROJECT_NAME, ZONE, content);
        System.out.println("INSERT");

        Operation operation = insert.execute();
        System.out.println("OK " + operation.getStatus());

        int i = 10;
        String instanceStatus = "";
        do {
            i--;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Compute.Instances.Get get = compute.instances().get(GOOGLE_PROJECT_NAME, ZONE, instanceName);
            Instance instance = get.execute();
            instanceStatus = instance.getStatus();
            System.out.println(i);
            System.out.println(instance.getStatus());
            System.out.println("operation:" + operation.getStatus());

        } while(!"RUNNING".equals(instanceStatus) && i>0);


        Compute.Instances.List request = compute.instances().list(GOOGLE_PROJECT_NAME, ZONE);

        GenericJson result = request.execute();
        InstanceList list = (InstanceList) result;
        for (Instance instance : list.getItems()) {
            System.out.println("*****************");
            System.out.println(instance.getName());
            System.out.println(instance.getCreationTimestamp());
            print("kind", instance);
            print("machineType", instance);
            System.out.println("*****************");

            //System.out.println(instance.toString());
            //System.out.println("*****************");
            //System.out.println(instance.toPrettyString());
        }

        System.out.printf(result.toPrettyString());

    }

    private void print(String name, Instance instance) {
        System.out.printf("%s:%s\n", name, instance.get(name));
    }

    /**
     * Makes the payload for creating an instance.
     *
     * @param natIP
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
