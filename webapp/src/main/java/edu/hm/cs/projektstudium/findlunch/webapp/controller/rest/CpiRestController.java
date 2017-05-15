package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is used in combination with the Content-Security-Policy-Header and receives any violations of the set
 * policy. This class is used as the csp-report-uri.
 */
@RestController
class CpiRestController {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(CpiRestController.class);

    /**
     * This method handles the received Content-Security-Policy-violations and forwards them to a mobile device
     * through the messenger Telegram.
     *
     * @param request the HttpServletRequest
     */
    @CrossOrigin
    @RequestMapping(path = "/api/csp-report-uri", method = RequestMethod.POST)
    public void getCspViolations(final HttpServletRequest request) throws IOException {
    	
        LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        final StringBuilder stringBuilder = new StringBuilder();
        String input;
        while ((input = bufferedReader.readLine()) != null) {
            stringBuilder.append(input);
        }
        bufferedReader.close();

        final JsonElement jsonElement = new JsonParser().parse(stringBuilder.toString());
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        // Get the csp-report explicitly from the originally JSON-object.
        final JsonObject cspReport = jsonObject.getAsJsonObject("csp-report");
        // NotificationController.sendMessageToTelegram("Content-Security-Policy-Report:\n" + cspReport.toString());
        System.out.println("Content-Security-Policy-Report:\n" + cspReport.toString());
    }

}
