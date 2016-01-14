package com.sfeir.googleapi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Logger;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.compute.Compute;

public abstract class ServiceProvider {


    public static Logger log = Logger.getLogger(ServiceProvider.class.getName());

    protected String accessType;


    protected String projectName;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    protected static HttpTransport httpTransport;

    private static GoogleClientSecrets clientSecrets = null;

    private Set<String> scopes;

    protected ServiceProvider(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Global instance of the {@link com.google.api.client.util.store.DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    protected static DataStoreFactory dataStoreFactory;

    public abstract HttpTransport getHttpTransport() throws IOException;

    protected abstract String getJsonCredentialFileName();

    protected abstract DataStoreFactory getDataStoreFactory() throws IOException;

    protected abstract Credential authorize(AuthorizationCodeFlow authFlow) throws IOException;

    public Credential getCredential() throws IOException {

        Set<String> scopes = getScopes();

        AuthorizationCodeFlow authFlow = new GoogleAuthorizationCodeFlow.Builder(
                getHttpTransport(), JSON_FACTORY, getGoogleClientSecrets(), scopes)
                .setDataStoreFactory(getDataStoreFactory())
                .setAccessType(accessType)
                .build();

        return authorize(authFlow);
    }


    private GoogleClientSecrets getGoogleClientSecrets() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(ServiceProvider.class.getResourceAsStream(getJsonCredentialFileName())));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
                clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Overwrite the src/main/resources/client_secrets.json file with the client secrets file "
                            + "you downloaded from the Quickstart tool or manually enter your Client ID and Secret "
                            + "from https://code.google.com/apis/console/?api=compute#project:898068729280 "
                            + "into src/main/resources/client_secrets.json");
            return null;
        }
        return clientSecrets;
    }


    protected Set<String> getScopes() {
        return scopes;
    }


    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public Compute getComputeService() throws IOException {

        setScopesIfNull(GoogleScopes.COMPUTE_SCOPES_ALL);

        Compute.Builder builder = new Compute.Builder(httpTransport, JSON_FACTORY, getCredential());
        builder.setApplicationName(projectName);
        return builder.build();
    }

    private void setScopesIfNull(Set<String> defaultScope) {
        if(scopes == null) {
            log.warning("No scopes set, using default for this services\n" +
                    "        Ask for only the permissions you need. Asking for more permissions will\n" +
                    "        reduce the number of users who finish the process for giving you access\n" +
                    "        to their accounts. It will also increase the amount of effort you will\n" +
                    "        have to spend explaining to users what you are doing with their data.\n" +
                    "        Here we are listing all of the available scopes. You should remove scopes\n" +
                    "        that you are not actually using.");

            for (String scope : defaultScope) {
                log.warning(scope);
            }

            setScopes(defaultScope);
        }
    }

    public HttpRequestFactory getSpreadsheetHttpRequestFactory() throws IOException {

        setScopesIfNull(GoogleScopes.SPREADSHEET_SCOPES);
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory(getCredential());

        return requestFactory;
    }


    /*
        Set up authorization code flow.
        Ask for only the permissions you need. Asking for more permissions will
        reduce the number of users who finish the process for giving you access
        to their accounts. It will also increase the amount of effort you will
        have to spend explaining to users what you are doing with their data.
        Here we are listing all of the available scopes. You should remove scopes
        that you are not actually using.
     */
    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

}
