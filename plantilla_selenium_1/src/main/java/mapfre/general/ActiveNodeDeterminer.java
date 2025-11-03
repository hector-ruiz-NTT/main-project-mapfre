package mapfre.general;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*//import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
//import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpRequest;
//import org.apache.hc.core5.http.HttpEntity;
import org.apache.http.HttpEntity;
//import org.apache.hc.core5.http.HttpHost;
import org.apache.http.HttpHost;
//import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;*/




import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import org.openqa.selenium.remote.SessionId;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActiveNodeDeterminer {
    private String gridHostName;
    private int gridPort;

    private static final Logger LOGGER = Logger.getLogger(ActiveNodeDeterminer.class.getCanonicalName());

    /**
     *
     * @param gridHostName - The host where the Grid Hub is running.
     * @param gridPort - The port on which the Grid port is listening to.
     */
    public ActiveNodeDeterminer(String gridHostName, int gridPort) {
        this.gridHostName = gridHostName;
        this.gridPort = gridPort;
    }

    /**
     * @param sessionId - A {@link SessionId} object that represents a valid session.
     * @return - A {@link GridNode} object that represents the node to which the session was routed to.
     */
    public GridNode getNodeInfoForSession(SessionId sessionId) {
        GridNode node;
        //CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
            URL url = new URL("http://" + gridHostName + ":" + gridPort + "?session=" + sessionId);
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", url.toExternalForm());
            response = client.execute(new HttpHost(gridHostName,gridPort),r);
            JsonObject object = extractJson(response.getEntity());
            URL tempUrl = new URL(object.get("proxyId").getAsString());
            node = new GridNode(tempUrl.getHost(), tempUrl.getPort());
        } catch (Exception e) {
            String errorMsg = "Failed to acquire remote webdriver node and port info. Root cause: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        LOGGER.info("Session " + sessionId + " was routed to " + node.toString());
        return node;
    }

    private JsonObject extractJson(HttpEntity entity) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return new JsonParser().parse(builder.toString()).getAsJsonObject();
        }
    }

    /**
     * A simple POJO (Plain Old Java Object) class that represents a Node in the
     * Selenium Grid environment.
     */
    public static class GridNode {
        private String nodeIp;
        private int nodePort;


        public GridNode(String nodeIp, int nodePort) {
            this.nodeIp = nodeIp;
            this.nodePort = nodePort;
        }

        /**
         * @return - A String that represents the IP Address of the node.
         */
        public String getNodeIp() {
            return this.nodeIp;
        }

        /**
         * @return - An int that represents the port number of the node.
         */
        public int getNodePort() {
            return this.nodePort;
        }

        @Override
        public String toString() {
            return "GridNode [IP='" + nodeIp + "', Port=" + nodePort + "]";
        }
    }
}
