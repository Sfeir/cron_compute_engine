package com.sfeir.googleapi;

import java.io.IOException;
import java.util.Set;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;



import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AppEngineAppIdentityServiceProvider extends ServiceProvider {


    public AppEngineAppIdentityServiceProvider(String applicationName) {
        super(applicationName);
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
        return null;
     }


    @Override
    protected String getJsonCredentialFileName() {
        return null;
    }


    public Credential getCredential() throws IOException{
        Set<String> scopes = getScopes();

        AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
        AppIdentityService.GetAccessTokenResult accessToken = appIdentity.getAccessToken(scopes);

        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        credential.setAccessToken(accessToken.getAccessToken());
        return credential;
    }

}
