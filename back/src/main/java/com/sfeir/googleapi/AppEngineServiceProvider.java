package com.sfeir.googleapi;

import java.io.IOException;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.appengine.api.users.UserServiceFactory;

public class AppEngineServiceProvider extends ServiceProvider {


    public AppEngineServiceProvider(String applicationName) {
        super(applicationName);
        this.accessType = "online";
        this.httpTransport = new UrlFetchTransport();
        this.dataStoreFactory = AppEngineDataStoreFactory.getDefaultInstance();

    }

    @Override
    public HttpTransport getHttpTransport() {
        return httpTransport;
    }

    @Override
    protected DataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    @Override
    protected Credential authorize(AuthorizationCodeFlow authFlow) throws IOException {
        Credential credential = authFlow.loadCredential(getUserId());

        return credential;
    }

    private String getUserId() {
        return UserServiceFactory.getUserService().getCurrentUser().getUserId();
    }

    @Override
    protected String getJsonCredentialFileName() {
        return "/client_secrets.json";
    }
}
