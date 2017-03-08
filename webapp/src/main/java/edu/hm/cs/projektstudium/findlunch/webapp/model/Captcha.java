package edu.hm.cs.projektstudium.findlunch.webapp.model;

/**
 * The class Captcha.
 */
public class Captcha {

    /**
     * The answer to the Captcha of the User.
     */
    private String answer;

    /**
     * The token of the Captcha.
     */
    private String imageToken;

    /**
     * Instantiates a new Captcha.
     *
     * @param answerParam        the username
     * @param imageTokenParam        the password
     */
    public Captcha(final String answerParam, final String imageTokenParam) {
        this.answer = answerParam;
        this.imageToken = imageTokenParam;
    }

    /**
     * Empty constructor for a new Captcha.
     */
    public Captcha() {
    }

    /**
     * Gets answer.
     *
     * @return the anser
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Gets the image token.
     *
     * @return the imageToken
     */
    public String getImageToken() {
        return imageToken;
    }

}