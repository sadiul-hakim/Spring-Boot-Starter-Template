package xyz.sadiulhakim.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Map;

public class ResponseUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtility.class);

    public static void commitResponse(HttpServletResponse response, Map<String, String> map, int status) {

        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status);
            new ObjectMapper().writeValue(response.getOutputStream(), map);
        } catch (Exception ex) {
            LOGGER.error("Error Occurred in ResponseUtility. Cause {}", ex.getMessage());
        }
    }
}
