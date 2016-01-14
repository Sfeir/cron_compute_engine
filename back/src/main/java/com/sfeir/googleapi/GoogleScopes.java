package com.sfeir.googleapi;

import java.util.HashSet;
import java.util.Set;
import com.google.api.services.compute.ComputeScopes;

public class GoogleScopes {
    
    public static final Set<String> COMPUTE_SCOPES_ALL = ComputeScopes.all();

    public static final Set<String> SPREADSHEET_SCOPES;

    static {
        SPREADSHEET_SCOPES = new HashSet<String>();
        SPREADSHEET_SCOPES.add("https://spreadsheets.google.com/feeds");
    }
}
