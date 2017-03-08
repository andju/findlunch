package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Captcha;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is used for validating received Captcha input.
 */
public final class CaptchaController {

    private CaptchaController() {}

    /**
     * The logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    /**
     * The secret key for reCaptcha.
     */
    private static final String PRIVATE_KEY = "0000000000000000000000000000000000000000";

    /**
     * This method verifies a received Captcha and is valid for the RESTful-Webservice of the application "FindLunch".
     * It uses the old version v1 of reCaptcha.
     *
     * @param captcha  the Captcha object
     * @param clientIp the client's IP-address
     *
     * @return whether the captcha was valid or not
     */
    public static boolean verifyCaptcha(final Captcha captcha, final String clientIp) {
        try {
            final Response response = new OkHttpClient().newCall(new Request.Builder()
                    .url("https://www.google.com/recaptcha/api/verify")
                    .addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8")
                    .post(new FormBody.Builder()
                            .add("privatekey", PRIVATE_KEY)
                            .add("remoteip", clientIp)
                            .add("response", captcha.getAnswer())
                            .add("challenge", captcha.getImageToken())
                            .build()).build()).execute();

            final String responseBody = response.body().string();
            response.close();
            return responseBody.startsWith("true");

        } catch (IOException e) {
            LOGGER.error("IOException", "Unerwarteter Fehler aufgetreten.");
        }
        return false;
    }

    /**
     * This method verifies a received Captcha and is valid for the internet site of the application "FindLunch"
     * It uses the new version v2 of reCaptcha.
     *
     * @param captchaResponse  the Captcha response
     * @param clientIp the client's IP-address
     *
     * @return whether the captcha was valid or not
     */
    static boolean verifyCaptchaV2(final String captchaResponse, final String clientIp) {
        try {
            final Response response = new OkHttpClient().newCall(new Request.Builder()
                    .url("https://www.google.com/recaptcha/api/siteverify")
                    .addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8")
                    .post(new FormBody.Builder()
                            .add("secret", PRIVATE_KEY)
                            .add("remoteip", clientIp)
                            .add("response", captchaResponse)
                            .build()).build()).execute();

            final String responseBody = response.body().string();
            response.close();
            final JsonParser jsonParser = new JsonParser();
            final JsonObject jsonObject = jsonParser.parse(responseBody).getAsJsonObject();
            final String successMessage = jsonObject.get("success").getAsString();
            return "true".equals(successMessage);

        } catch (IOException e) {
            LOGGER.error("IOException", "Unerwarteter Fehler aufgetreten.");
        }
        return false;
    }

}
