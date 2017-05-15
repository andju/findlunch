package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class makes it easier for administrators to get log files.
 */
@RestController
public class LogRestController {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);

    /**
     * Gets the requested logfile.
     *
     * @param request the HttpServletRequest
     *
     * @return the log file
     */
    @CrossOrigin
    @RequestMapping(path = "/api/logs", method = RequestMethod.GET, params = {"file"})
    public final String getLogfile(final HttpServletRequest request) throws IOException {
        LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

        String logfile = "";
        final File file = new File(request.getParameterValues("file")[0]);
        final FileReader fileReader = new FileReader(file);
        int position = fileReader.read();
        while (position != -1) {
            logfile += (char) position;
            position = fileReader.read();
        }
        fileReader.close();
        return logfile
                + "\n"
                + file.getAbsolutePath();

    }

}
