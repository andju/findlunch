package edu.hm.cs.projektstudium.findlunch.androidapp.measure;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * Independent class for measurement.
 * Separated from live operation!
 *
 * Builds JSON based POST request for RTT measures.
 * Extracts received data from push notification and sends received data back to REST webserver.
 *
 * For performing measure set MEASURE_ACTIVE true.
 * Adjust host and REST interface adjustable for custom measure.
 *
 *
 * Created by Maximilian Haag on 22.12.2016.
 *
 */

public class MeasureProcessing {
    private static final String TAG = "MeasureProcessing";

    //measurement #####################
    private static final boolean MEASURE_ACTIVE = false;
    private static final String host = "findlunch.de";
    private static final String url = "https://" + host + ":22001/api/transmit_measure";
    //measurement #####################

    //FCM process
    public void storeMeasureData(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String pushTitle = data.get("title").toString();
        String timeStamp = data.get("timestamp").toString();
        new SendData().execute(url, pushTitle, timeStamp);
    }

    //ADM process
    public void processMeasureData(Bundle data) {
        String pushTitle = data.get("title").toString();
        String timeStamp = data.get("timestamp").toString();
        new SendData().execute(url, pushTitle, timeStamp);
    }
	
	public static boolean isMeasureActive() {
		return MEASURE_ACTIVE;
    }
}

/**
 *
 * Sends data back to webserver REST interface as POST.
 *
 * Created by Maximilian Haag on 22.12.2016.
 *
 */
class SendData extends AsyncTask<String, String, String> {

    private static final String TAG = "SendData (AsyncTask)";

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        String payload_t = params[1];
        String payload_ts = params[2];

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        headers.setAll(map);

        //Payload data
        Map req_payload = new HashMap();
        req_payload.put("title", payload_t);
        req_payload.put("timestamp", payload_ts);

        HttpEntity<?> request = new HttpEntity<>(req_payload, headers);

        try {
            // Make the HTTP DELETE request to the Basic Auth protected URL
            ResponseEntity<?> response = new RestTemplate().postForEntity(url, request, String.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                Log.i(TAG, "Success, sending measure message back to spring server.");
            }
        } catch (HttpClientErrorException e) {
            Log.i(TAG, "Error at sending measure message back to spring server.");
            Log.e(getClass().getName(), e.getMessage());
        } catch (RestClientException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
        return url;

    }
}


