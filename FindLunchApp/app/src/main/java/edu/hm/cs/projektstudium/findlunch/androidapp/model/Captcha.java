package edu.hm.cs.projektstudium.findlunch.androidapp.model;

/**
 * The type Captcha.
 */
public class Captcha {

    /**
     * The answer to the Captcha given by the user.
     */
    private String answer;

    /**
     * The imageToken for the Captcha.
     */
    private String imageToken;

    /**
     * Instantiates a new Captcha.
     *
     * @param answerParam        the answer to the Captcha given by the user
     * @param imageTokenParam        the imageToken
     */
    public Captcha(final String answerParam, final String imageTokenParam) {
        this.answer = answerParam;
        this.imageToken = imageTokenParam;
    }

    /**
     * Empty Constructor a new Captcha.
     */
    @SuppressWarnings("unused")
    public Captcha() {
    }

    /**
     * Gets the answer given by the user.
     *
     * @return the answer given by the user
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Gets the imageToken for a Captcha.
     *
     * @return the imageToken
     */
    public String getImageToken() {
        return imageToken;
    }
}
