package es.udc.fic.csi.baserest.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

/**
 * Utils fot {@link TestRestTemplate}
 * 
 * @author anxo.pvila@udc.es
 */
public class TestRestTemplateUtils {

    private TestRestTemplateUtils() {
    }

    /**
     * Convenience method for retrieving a list of the given class as a response
     * 
     * @param <T>          the type of the elements of the list
     * @param restTemplate the rest template
     * @param url          the url to query
     * @param cls          the class of the elements of the list
     * @return the list retrieved from the url
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getForList(TestRestTemplate restTemplate, String url, Class<T> cls) {
        // Cast should be ok, but arrayType returns a Class<?> instead of a Class<T[]>
        var cls2 = (Class<T[]>) cls.arrayType();
        ResponseEntity<T[]> response = restTemplate.getForEntity(url, cls2);

        return Arrays.asList(response.getBody());
    }
}
