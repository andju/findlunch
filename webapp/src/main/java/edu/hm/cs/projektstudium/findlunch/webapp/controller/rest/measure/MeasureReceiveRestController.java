package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest.measure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.hm.cs.projektstudium.findlunch.webapp.measurement.MeasureUnit;
import edu.hm.cs.projektstudium.findlunch.webapp.measurement.PushMeasureBase;


/**
*
* The Class MeasureReceiveRestController.
* 
* Separated usage from live-operation.
* Rest controller class for receiving measure data.
* 
* **Live operation has to be disabled at package "scheduling", class "PushNotificationScheduledTask".
* **Measurement has to be enabled at package "measurement", class "PushMeasureBase".
* 
* Receives at class "PushMeasureBase" specified number of pushes at rest interface.
* Measure-Pushes contain:
* 
* **Title, with current number for sequency check and transmission check, integrated in title to reduce overhead of json file.
* **ID of current user of current push for scaled measurement, integrated in title to reduce overhead of json file.
* **Timestamp of sent time for calculating round-trip time.
* 
* If all data was transmitted, the average time is calculated.
* (If not all data was transmitted average calculation is not meaningful, all received pushes are stored to log file)
* 
* Result can be sorted by ID at result file for scaled device data.
*  
* Created by Maxmilian Haag on 06.02.2017.
* @author Maximilian Haag
* 
*/
@RestController
public class MeasureReceiveRestController {

	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(MeasureReceiveRestController.class);

	/**
	 * Expecting set up number of pushes for each device
	 * Number of pushes and number of devices set up at "PushMeasureBase" class.
	 */
	private static int EXPECTED_NR_OF_PUSHES = PushMeasureBase.getNumberOfPushes() * PushMeasureBase.getUserDevices();

	/**
	 * Current number of received pushes.
	 */
	private int pushCount = 0;
	
	/**
	 * Stored measure data for further average and sequency calculation.
	 */
	private List<MeasureUnit> measure = new ArrayList<>();
	
	/**
	 * Writing to external log file for further usage of measure data.
	 */
	private PrintWriter pw;

	
	/**
	 * Initialization of rest controller with output file writer.
	 */
	public MeasureReceiveRestController() {
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File ("MeasureLog.txt"))), true);
		} catch (FileNotFoundException e) {
			LOGGER.error("Not possible to create file");
		}
	}

	/**
	 * Rest method, receiving measure data as Json-POST on /api/transmit_measure.
	 * Extraction of title (push number, user id) and timestamp of push measure.
	 * 
	 * @param msg The push measure as a map.
	 * @param request The HttpServletRequest.
	 * @return The response entity representing a status code.
	 */
	@CrossOrigin
	@RequestMapping(value = "/api/transmit_measure", method = RequestMethod.POST, headers = "Accept=application/json")
	public ResponseEntity<Integer> transmitMeasure(@RequestBody HashMap<String,String> msg, HttpServletRequest request) {

		String pushMessageTitle = msg.get("title");
		String pushMessageTimeStamp = msg.get("timestamp");

		//Creating local measure unit from push measure data.
		MeasureUnit oneMessage = new MeasureUnit();
		oneMessage.setTitle(pushMessageTitle);
		oneMessage.setTimeStamp(pushMessageTimeStamp);
		oneMessage.setReceiveTime(String.valueOf(System.currentTimeMillis()));
		oneMessage.setPushNumber(extractPushNumber(pushMessageTitle));
		oneMessage.setUserId(extractUserId(pushMessageTitle));

		computeFileRecord(oneMessage);
		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}

	
	/**
	 * Processing Round-Trip-Time of one measure.
	 * Logging to console and file of every received measure (for case of loss of some measures)
	 * @param oneMessage One measure unit to compute.
	 */
	private void computeFileRecord(MeasureUnit oneMessage) {

		measure.add(oneMessage);
		pushCount++;

		//Calculating taken round-trip-time
		long takenTimeInMs =  oneMessage.getReceiveTimeL() - oneMessage.getTimeStampL();
		long userId = oneMessage.getUserId();

		//Console log
		LOGGER.info("Received Push-Message Measure Answer: " + userId);
		LOGGER.info("Received Push-Message Measure Answer: " + takenTimeInMs);
		
		//Writing to measure file, time and id
		pw.println(takenTimeInMs + "-" + userId);
		pw.flush();

		//Check if last 
		checkIfLastForAvgTime();

	}


	/**
	 * Calculating overall average, if transmission completed successfully.
	 * Does average time calculation, sequency check and sort if selected for scaled measure.
	 * Condition not reached, if loss of messages.
	 * 
	 */
	private void checkIfLastForAvgTime() {

		//Last one of estimated pushes (OVERALL), launch sequency evaluation (performance measure).
		if(pushCount == EXPECTED_NR_OF_PUSHES) {

			//One iteration completed
			LOGGER.info("=============================");
			pw.println("=============================");
			double avgTime = calcAvgTime();

			boolean seqCorrect = orderSequencyCorrect();
			boolean allTransmitted = allTransmitted();

			//Console log
			LOGGER.info("Measure done, average time: " + avgTime);
			LOGGER.info("Sequency corrcect? " + seqCorrect);
			LOGGER.info("All data transmitted? " + allTransmitted);

			//Log output file
			pw.println("Measure done, average time: " + avgTime);
			pw.println("Sequency corrcect? " + seqCorrect);
			pw.println("All data transmitted? " + allTransmitted);
			pw.flush();
			pw.close();


			//Reset
			pushCount = 0;
			measure = new ArrayList<>();

			//Sorting measure result if activated
			if(PushMeasureBase.SORT_SCALED_MEASURE_WHEN_READY) {
				sortScaledMeasure();
			}


		}
	}


	/**
	 * Checks all recorded measure data for correct sequency.
	 * @return Sequency correct or not.
	 */
	private boolean orderSequencyCorrect(){
		int lastNumber = -1;
		for(int i = 0; i < measure.size(); i++) {
			String title = measure.get(i).getTitle();
			int numberOfCurrentPush = extractPushNumber(title);
			if((lastNumber + 1) != numberOfCurrentPush) {
				return false;
			}
			lastNumber = i;
		}
		return true;
	}


	/**
	 * Calculates an average round-trip-time of all received measure data.
	 * @return The average time of the whole measure.
	 */
	private double calcAvgTime() {
		double avgTime = 0;
		long takenTimeInMs = 0;
		for(int i = 0; i < measure.size(); i++) {
			MeasureUnit mu = measure.get(i);
			takenTimeInMs = mu.getReceiveTimeL() - mu.getTimeStampL();
			avgTime += takenTimeInMs;
		}
		avgTime /= EXPECTED_NR_OF_PUSHES;
		return avgTime;
	}


	/**
	 * Checks if all pushes were transmitted successfully.
	 * If the number of recorded pushes matches the expected number of pushes.
	 * @return All transmitted or not.
	 */
	private boolean allTransmitted() {
		return measure.size() == EXPECTED_NR_OF_PUSHES;
	}


	/**
	 * Extracts the number of the push from the title string from the received map.
	 * @param title The title containing name, push number and user id.
	 * @return The extracted number of the current push.
	 */
	private int extractPushNumber(String title) {
		//extract number from "testpushX time" -> time
		String num = title.substring(10, title.length());
		int numberOfCurrentPush = Integer.parseInt(num);
		return numberOfCurrentPush;
	}


	/**
	 * Extracts the user id (max. 0-9) of the title for processing the scaled measure. 
	 * Userful for scaled test, possible up to 10 devices.
	 * 
	 * @param title The title containing name, push number and user id.
	 * @return The user id which belongs to the current push.
	 */
	private int extractUserId(String title) {
		//extract number from "testpushX time" -> X (ID)
		String num = title.substring(8, 9);
		int userId = Integer.parseInt(num);
		return userId;
	}

	
	
	/**
	 * Sorting scaled mixed measure data in format "time-userId".
	 * Data of more than one device/user is mixed, must be sorted for further average and median etc. calculation.
	 * Reading and re-writing to log output file.
	 */
	private void sortScaledMeasure() {
		HashMap <String, ArrayList<String>> sortedData = new HashMap<>();
		BufferedReader br = null;
		File logF = null;
		try {
			logF = new File("MeasureLog.txt");
			br = new BufferedReader(new FileReader(logF));

			String oneLine = br.readLine();
			boolean finishR = false;
			while(!finishR) {

				String [] split = oneLine.split("-");
				String timeStr = split[0];
				String userId = split[1];

				if(!sortedData.containsKey(userId)){
					sortedData.put(userId, new ArrayList<>());
				}

				ArrayList<String> valData = sortedData.get(userId);
				valData.add(timeStr);
				sortedData.put(userId, valData);

				oneLine = br.readLine();
				if(oneLine == null || oneLine.startsWith("=")) {
					finishR = true;
				}
			}

			try {
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logF)), true);

				for(Entry<String, ArrayList<String>> entry : sortedData.entrySet()) {
					pw.println(entry.getKey());
					ArrayList<String> values = entry.getValue();
					for(int i = 0; i < values.size(); i++) {
						pw.println(values.get(i));
					}
					pw.flush();
				}
				pw.close();

			} catch (FileNotFoundException e) {
				LOGGER.error("Not possible to create file");			}

		} catch (FileNotFoundException e) {
			LOGGER.error("Not possible to read/write file");
		} catch (IOException e) {
			LOGGER.error("I/O Error");
		}

	}

}
