package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class could be used for a generic Captcha handling process.
 * For example Captchas or responses for Captchas from different providers could be obtained through the methods of
 * this class.
 */
@RestController
public class GenericCaptchaRestController {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GenericCaptchaRestController.class);

    /**
     * Gets the Captcha or a Captcha-response of the requested Captcha provider.
     *
     * @param request the HttpServletRequest
     * @return the log file.
     */
    @CrossOrigin
    @RequestMapping(path = "/api/captcha", method = RequestMethod.GET, params = {"provider"})
    public final ResponseEntity<String> getRemoteContent(final HttpServletRequest request) throws IOException {
        LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

        Response response = null;
        try {
            response = new OkHttpClient().newCall(new Request.Builder()
                    .url(request.getParameterValues("provider")[0])
                    .get()
                    .build()).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return new ResponseEntity<>(response != null ? response.body().string() : "No body", HttpStatus.OK);
    }

}
