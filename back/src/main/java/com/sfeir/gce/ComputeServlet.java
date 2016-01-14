package com.sfeir.gce;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.sfeir.gce.operation.DeleteInstanceOperation;
import com.sfeir.gce.operation.ListInstanceOperation;
import com.sfeir.gce.operation.StartInstanceOperation;
import com.sfeir.googleapi.AppEngineAppIdentityServiceProvider;
import com.sfeir.googleapi.ServiceProvider;

public class ComputeServlet extends HttpServlet {

    public static Logger log = Logger.getLogger(ComputeServlet.class.getName());

    public final static String ACTION_KEY = "action";
    private static final String PROJECT_NAME = "sfeirjenkins";
    private static final String ZONE = "europe-west1-a";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ACTION action = getAction(req);
        action.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ACTION action = getAction(req);
        action.doPost(req, resp);
    }



    private ACTION getAction(HttpServletRequest req) {
        String action_key = req.getParameter(ACTION_KEY);

        ACTION action = ACTION.DEFAULT;
        try {
            action = ACTION.valueOf(action_key);
        } catch (IllegalArgumentException | NullPointerException ex) {
            // On ignore les mauvaises demandes d'actions
        }
        log.log(Level.FINER, "ACTION:" + action);
        return action;
    }

    enum ACTION {
        DEFAULT {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                log.info("Default");

                Compute computeService = getComputeService();

                // Add the code to make an API call here.

                ListInstanceOperation listInstanceOperation = new ListInstanceOperation(PROJECT_NAME, ZONE, computeService);
                listInstanceOperation.execute();

                // Send the results as the response
                StringBuilder buffer = new StringBuilder();
                InstanceList list = listInstanceOperation.getResult();
                if(list != null && list.getItems() != null) {
                    for (Instance instance : list.getItems()) {
                        buffer.append(instance.toPrettyString());
                    }
                } else {
                    buffer.append("No instance");
                }

                resp.setStatus(200);
                resp.setContentType("application/json");

                PrintWriter writer = resp.getWriter();
                writer.print(buffer.toString());
            }
        },

        INSERT {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                log.info("Insert");
                Compute computeService = getComputeService();

                StartInstanceOperation startOperation = new StartInstanceOperation(PROJECT_NAME, ZONE, "jenkins", "jenkins", "g1-small", computeService);
                startOperation.setNatIP("23.251.130.148").execute();


                ACTION.DEFAULT.doGet(req, resp);
            }
        },

        DELETE {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                log.info("Delete");

                Compute computeService = getComputeService();

                DeleteInstanceOperation deleteOperation = new DeleteInstanceOperation(PROJECT_NAME, ZONE, "jenkins", computeService);
                deleteOperation.execute();

                ACTION.DEFAULT.doGet(req, resp);
            }
        };


        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        };

        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        };
    }

    private static Compute getComputeService() throws IOException {
        ServiceProvider serviceProvider = new AppEngineAppIdentityServiceProvider(PROJECT_NAME);

        return serviceProvider.getComputeService();
    }
}
