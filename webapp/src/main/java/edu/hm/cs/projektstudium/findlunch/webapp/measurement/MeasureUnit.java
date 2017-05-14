package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

/**
 * MeasureUnit helper class.
 * Stores title, sending timestamp, receiving timestamp and user id of one measure.
 * 
 * Created by Maxmilian Haag on 06.02.2017
 * @author Maximilian Haag
 *
 */
public class MeasureUnit {

    /**
     * Title of measure unit.
     */
    private String title;
    
    /**
     * Timestamp of measure unit.
     */
    private String timeStamp;
    
    /**
     * Receive timestamp of measure unit.
     */
    private String receiveTime;
    
    /**
     * Number of pushes.
     */
    private long pushNumber;
    
    /**
     * Measure user id.
     */
    private long userId;


	/**
	 * Initialization.
	 */
	public MeasureUnit() {
    }


    /**
     * Get title.
     * @return title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * Set title.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

	/**
	 * Get timestamp.
	 * @return timestamp.
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Get timestamp as long.
	 * @return timestamp_l
	 */
	public long getTimeStampL() {
		return Long.parseLong(timeStamp);
	}

	/**
	 * Set timestamp
	 * @param timeStamp
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
    /**
     * Get receive time.
     * @return receive time.
     */
    public String getReceiveTime() {
		return receiveTime;
	}
    
    /**
     * Get receive time as long. 
     * @return receive_time_l
     */
    public long getReceiveTimeL() {
		return Long.parseLong(receiveTime);
	}

	/**
	 * Set receive time.
	 * @param receiveTime
	 */
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	/**
	 * Get push number as long
	 * @return push number as long
	 */
	public long getPushNumber() {
		return pushNumber;
	}

	/**
	 * Set push number
	 * @param pushNumber
	 */
	public void setPushNumber(long pushNumber) {
		this.pushNumber = pushNumber;
	}

	/**
	 * Get user id.
	 * @return user id
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Set user id
	 * @param userId
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

}
