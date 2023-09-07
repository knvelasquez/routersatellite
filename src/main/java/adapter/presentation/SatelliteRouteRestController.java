package adapter.presentation;

import config.common.UrlBuild;
import domain.HttpConnector;
import domain.JsonService;
import model.SatelliteData;
import model.SatelliteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/router")
public class SatelliteRouteRestController {
    private static final Logger logger = LoggerFactory.getLogger(SatelliteRouteRestController.class);
    private final String hostSatellite;
    private final String messageServerUrlPath;
    private final int messageServerPort;
    private final HttpConnector httpConnector;
    private final JsonService jsonService;

    public SatelliteRouteRestController(@Value("${satellite.name}") String hostSatellite,
                                        @Value("${message.server.url.path}") String messageServerUrlPath,
                                        @Value("${message.server.port}") String messageServerPort,
                                        HttpConnector httpConnector,
                                        JsonService jsonService) {
        this.hostSatellite = hostSatellite;
        this.messageServerUrlPath = messageServerUrlPath;
        this.messageServerPort = Integer.parseInt(messageServerPort);
        this.httpConnector = httpConnector;
        this.jsonService = jsonService;
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "v2.0");
        response.put("status", "healthy");
        response.put("details", "Router satellite service is up and running.");
        response.put("date", new Date(System.currentTimeMillis()).toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/satellite")
    public String routerSatellite(@RequestBody SatelliteData satelliteData,
                                  @RequestHeader(value = "Authorization") String jwt) {
        Map<String, SatelliteModel> satellites = mapFrom(satelliteData);
        StringBuilder responseResult = new StringBuilder();
        if (satellites.containsKey(hostSatellite)) {
            responseResult.append(processInternallyInSameNetwork(satellites, jwt));
        }
        processExternallyInNextClientApiGatewayNetwork(satellites, jwt);
        return responseResult.toString();
    }

    private String processInternallyInSameNetwork(Map<String, SatelliteModel> satellites, String jwt) {
        String internalUrl = UrlBuild.from(messageServerPort, "coordinate-resolver", messageServerUrlPath);
        logger.info(internalLog());
        String bodyString = jsonService.mapToString(satellites.get(hostSatellite));
        HttpResponse<String> response = httpConnector.post(internalUrl, bodyString, jwt);
        satellites.remove(hostSatellite);
        return response.body().toString();
    }

    private void processExternallyInNextClientApiGatewayNetwork(Map<String, SatelliteModel> satellites, String jwt) {
        //Get the next remaining satellite to redirect request to your own network
        SatelliteModel firstRemainingSatellite = satellites.entrySet().iterator().next().getValue();
        String firstRemainingSatelliteName = firstRemainingSatellite.getName();
        StringBuilder firstRemainingSatelliteServerName = new StringBuilder(firstRemainingSatelliteName);
        firstRemainingSatelliteServerName.append(".api.gateway");

        String url = UrlBuild.fromCustom(firstRemainingSatelliteServerName.toString(), 80);
        logger.info(externalLog(url));
        List<SatelliteModel> remainingSatellites = satellites.values().stream().toList();
        String body = jsonService.mapToString(new SatelliteData(remainingSatellites));
        HttpResponse<String> response = httpConnector.post(url, body, jwt);
        //TODO implemenmt reintent logic
    }

    private Map<String, SatelliteModel> mapFrom(SatelliteData satellitesData) {
        Map<String, SatelliteModel> result = new HashMap<>();
        for (SatelliteModel satellite : satellitesData.getSatellites()) {
            result.put(satellite.getName(), satellite);
        }
        return result;
    }

    private String internalLog() {
        String internalUrl = UrlBuild.from(messageServerPort, "coordinate-resolver", messageServerUrlPath);
        return new StringBuilder("internally processed by ")
                .append(hostSatellite)
                .append(" ")
                .append(internalUrl).toString();
    }

    private String externalLog(String url) {
        return new StringBuilder("externally processed by ")
                .append(url).toString();
    }
}
