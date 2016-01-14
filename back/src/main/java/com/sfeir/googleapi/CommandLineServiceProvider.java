package com.sfeir.googleapi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class CommandLineServiceProvider extends ServiceProvider {

    public CommandLineServiceProvider(String applicationName) throws IOException {
        super(applicationName);
        this.accessType = "offline";
        try {
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Can't init HttpTransport", e);
        }
        this.dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

    }

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/compute_sample");

    @Override
    public HttpTransport getHttpTransport() throws IOException {
        return httpTransport;
    }

    @Override
    protected DataStoreFactory getDataStoreFactory() throws IOException {
        return dataStoreFactory;
    }

    @Override
    protected Credential authorize(AuthorizationCodeFlow authFlow) throws IOException {
        AuthorizationCodeInstalledApp authorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(authFlow, new LocalServerReceiver());
        Credential credential = authorizationCodeInstalledApp.authorize("user");
        return credential;
    }


    @Override
    protected String getJsonCredentialFileName() {
        return "/clientApp_secrets.json";
    }
}
