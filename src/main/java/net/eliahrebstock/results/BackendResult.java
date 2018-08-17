package net.eliahrebstock.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.eliahrebstock.HAProxyRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Result generated for each backend on HAProxy
 */
public class BackendResult {

    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(BackendResult.class);

    /**
     * Name of the backend in the proxy.
     */
    @JsonProperty
    private String name;

    /**
     * Weight associated with the backend.
     */
    @JsonProperty
    private int weight;

    /**
     * If the backend is up for HAProxy.
     */
    @JsonProperty
    private Boolean status;

    /**
     * Detailed HAProxy backend status information
     */
    @JsonProperty(value = "check_status")
    private String pingStatus;

    public BackendResult(HAProxyRecord record) {
        HAProxyRecord.HCStatus recordCheckStatus = record.getCheckStatus();
        boolean recordStatus = false;
        String recordStatusString = "";
        if (recordCheckStatus != null) {
            recordStatusString = recordCheckStatus.toString();
            recordStatus = recordCheckStatus.getStatus();
        }
        this.name = record.getServiceName();
        this.weight = record.getWeight();
        this.status = recordStatus;
        this.pingStatus = recordStatusString;
    }

    /**
     * Get backend result as a JSON string.
     * @return String JSON backend result
     */
    @Nullable
    @JsonIgnore
    public String getAsJSON() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
