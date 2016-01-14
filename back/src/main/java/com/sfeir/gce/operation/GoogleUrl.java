package com.sfeir.gce.operation;

import com.google.api.services.compute.Compute;

public class GoogleUrl {

    private String project;
    private String zone;

    private String urlPrefixWithProject;
    private String urlPrefixWithProjectAndZone;
    private String source;

    public GoogleUrl(String project, String zone) {
        this.project = project;
        this.zone = zone;

        urlPrefixWithProject = Compute.DEFAULT_BASE_URL + project;
        urlPrefixWithProjectAndZone = urlPrefixWithProject + "/zones/" + zone;
        source = urlPrefixWithProjectAndZone + "/disks/";
    }

    public String getMachineTypeUrl(String machineType){
        return urlPrefixWithProjectAndZone + "/machineTypes/"  + machineType;
    }

    public String getUrlPrefixWithProject() {
        return urlPrefixWithProject;
    }

    public String getUrlPrefixWithProjectAndZone() {
        return urlPrefixWithProjectAndZone;
    }

    public String getSource(String bootDiskName) {
        return source + bootDiskName;
    }

    public String getDefaultNetwork() {
        return urlPrefixWithProject + "/global/networks/default";
    }


}
