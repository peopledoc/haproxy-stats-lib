package net.eliahrebstock;


import org.apache.commons.csv.CSVRecord;

import java.lang.reflect.Field;
import java.time.Duration;

public class HAProxyRecord {

    enum ProxyType {
        FRONTEND,
        BACKEND,
        SERVER,
        LISTENER
    }

    enum HCStatus {
        UNKNOWN,
        INITIALIZING,
        SOCKET_ERROR,
        LAYER_4_OK,
        LAYER_4_TIMEOUT,
        LAYER_4_NO_CONNECT,
        LAYER_6_OK,
        LAYER_6_TIMEOUT,
        LAYER_6_INVALID_RESPONSE,
        LAYER_7_OK,
        LAYER_7_OK_CONDITIONAL,
        LAYER_7_TIMEOUT,
        LAYER_7_INVALID_RESPONSE,
        LAYER_7_RESPONSE_ERROR;
        public boolean getStatus() {
            switch (this) {
                case LAYER_7_OK:
                case LAYER_4_OK:
                case LAYER_6_OK:
                    return true;
                default:
                    return false;
            }
        }
    }

    enum AgentStatus {
        UNKNOWN,
        INITIALIZING,
        SOCKET_ERROR,
        LAYER_4_OK,
        LAYER_4_TIMEOUT,
        LAYER_4_NO_CONNECT,
        LAYER_7_OK,
        LAYER_7_STOPPED
    }

    enum ProxyMode {
        UNKNOWN,
        TCP,
        HTTP,
        HEALTH
    }

    /* pxname */
    private String proxyName;

    /* svname */
    private String serviceName;

    /* qcur */
    private int currentQueuedRequests;

    /* qmax */
    private int maxQueuedRequests;

    /* scur */
    private int currentSessions;

    /* smax */
    private int maxSessions;

    /* slim */
    private int configuredSessionLimit;

    /* stot */
    private int cumulativeSessionNumber;

    /* bin */
    private int bytesIn;

    /* bout */
    private int bytesOut;

    /* dreq */
    private int deniedRequests;

    /* dresp */
    private int deniedResponses;

    /* ereq */
    private int requestErrors;

    /* econ */
    private int connectErrors;

    /* eresp */
    private int responseErrors;

    /* wretr */
    private int retriedServerConnectsInRequest;

    /* wredis */
    private int redispatchedConnectsInRequest;

    /* status */
    private String status;

    /* weight */
    private int weight;

    /* act */
    private int activeServers;

    /* bck */
    private int backupServers;

    /* chkfail */
    private int checkFailedNumber;

    /* chkdown */
    private int checkDownNumber;

    /* lastchg */
    private Duration lastStatusChange;

    /* downtime */
    private Duration downtime;

    /* qlimit */
    private int configuredMaxQueue;

    /* pid */
    private int processID;

    /* iid */
    private int proxyID;

    /* sid */
    private int serverID;

    /* throttle */
    private int throttlePercentage;

    /* lbtot */
    private int totalSelected;

    /* tracked */
    private int trackID;

    /* type */
    private ProxyType type;

    /* rate */
    private int sessionsInLastSecond;

    /* rate_lim */
    private int sessionsPerSecondLimit;

    /* rate_max */
    private int maxSessionsPerSecond;

    /* check_status */
    private HCStatus checkStatus;

    /* check_code */
    private int checkCode;

    /* check_duration */
    private Duration checkDuration;

    /* hrsp_1xx */
    private int HTTPResponse1xx;

    /* hrsp_2xx */
    private int HTTPResponse2xx;

    /* hrsp_3xx */
    private int HTTPResponse3xx;

    /* hrsp_4xx */
    private int HTTPResponse4xx;

    /* hrsp_5xx */
    private int HTTPResponse5xx;

    /* hrsp_other */
    private int HTTPResponseOther;

    /* hanafail */
    private String HCDetails;

    /* req_rate */
    private int requestsInLastSecond;

    /* req_rate_max */
    private int requestsPerSecondLimit;

    /* req_tot */
    private int maxRequestsPerSecond;

    /* cli_abrt */
    private int clientAborts;

    /* srv_abrt */
    private int serverAborts;

    /* comp_in */
    private int compressorBytesIn;

    /* comp_out */
    private int compressorBytesOut;

    /* comp_byp */
    private int compressorBytesBypassed;

    /* comp_rsp */
    private int compressedResponses;

    /* lastsess */
    private int timeSinceLastSession;

    /* last_chk */
    private String lastHCContents;

    /* last_agt */
    private String lastAgentCheckContents;

    /* qtime */
    private Duration averageQueueTime1024LastRequests;

    /* ctime */
    private Duration averageConnectTime1024LastRequests;

    /* rtime */
    private Duration averageResponseTime1024LastRequests;

    /* ttime */
    private Duration averageTotalSessionTime1024LastRequests;

    /* agent_status */
    private AgentStatus agentStatus;

    /* agent_code (unused per doc) */
    private int agentCode;

    /* agent_duration */
    private Duration agentCheckDuration;

    /* check_desc */
    private String HCDescription;

    /* agent_desc */
    private String AgentCheckDescription;

    /* check_rise */
    private int checkRiseParameter;

    /* check_fall */
    private int checkFallParameter;

    /* check_health */
    private int checkHealthResult;

    /* agent_rise */
    private int agentRiseParameter;

    /* agent_fall */
    private int agentFallParameter;

    /* agent_health */
    private int agentHealthResult;

    /* addr */
    private String address;

    /* cookie */
    private String cookie;

    /* mode */
    private ProxyMode mode;

    /* algo */
    private String loadBalancingAlgorithm;

    /* conn_rate */
    private int connectionsInLastSecond;

    /* conn_rate_max */
    private int maxConnectionPerSecond;

    /* conn_tot */
    private int totalConnections;

    /* intercepted */
    private int interceptedConnections;

    /* dcon */
    private int deniedRequestsTCPConnection;

    /* dses */
    private int deniedRequestsTCPSession;

    private ProxyType getProxyTypeFromString(String proxyType) throws IllegalArgumentException {
        int proxyTypeNumber = Integer.parseInt(proxyType);
        switch (proxyTypeNumber) {
            case 0:
                return ProxyType.FRONTEND;
            case 1:
                return ProxyType.BACKEND;
            case 2:
                return ProxyType.SERVER;
            case 3:
                return ProxyType.LISTENER;
            default:
                 throw new IllegalArgumentException(String.format("Unknown proxy type %d.", proxyTypeNumber));
        }
    }

    private HCStatus getHCStatusFromString(String checkStatus) throws IllegalArgumentException {
        if (checkStatus.contains("*")) {
            checkStatus = checkStatus.substring(2);
        }

        switch (checkStatus) {
            case "UNK":
                return HCStatus.UNKNOWN;
            case "INI":
                return HCStatus.INITIALIZING;
            case "SOCKERR":
                return HCStatus.SOCKET_ERROR;
            case "L4OK":
                return HCStatus.LAYER_4_OK;
            case "L4TOUT":
                return HCStatus.LAYER_4_TIMEOUT;
            case "L4CON":
                return HCStatus.LAYER_4_NO_CONNECT;
            case "L6OK":
                return HCStatus.LAYER_6_OK;
            case "L6TOUT":
                return HCStatus.LAYER_6_TIMEOUT;
            case "L6RSP":
                return HCStatus.LAYER_6_INVALID_RESPONSE;
            case "L7OK":
                return HCStatus.LAYER_7_OK;
            case "L7OKC":
                return HCStatus.LAYER_7_OK_CONDITIONAL;
            case "L7TOUT":
                return HCStatus.LAYER_7_TIMEOUT;
            case "L7RSP":
                return HCStatus.LAYER_7_INVALID_RESPONSE;
            case "L7STS":
                return HCStatus.LAYER_7_RESPONSE_ERROR;
            case "":
                return null;
            default:
                throw new IllegalArgumentException(String.format("Unknown healthcheck %s.", checkStatus));
        }
    }

    private AgentStatus getAgentStatusFromString(String agentStatus) throws IllegalArgumentException {
        switch (agentStatus) {
            case "UNK":
                return AgentStatus.UNKNOWN;
            case "INI":
                return AgentStatus.INITIALIZING;
            case "SOCKERR":
                return AgentStatus.SOCKET_ERROR;
            case "L4OK":
                return AgentStatus.LAYER_4_OK;
            case "L4TOUT":
                return AgentStatus.LAYER_4_TIMEOUT;
            case "L4CON":
                return AgentStatus.LAYER_4_NO_CONNECT;
            case "L7OK":
                return AgentStatus.LAYER_7_OK;
            case "L7STS":
                return AgentStatus.LAYER_7_STOPPED;
            case "":
                return null;
            default:
                throw new IllegalArgumentException(String.format("Unknown agent status %s.", agentStatus));
        }
    }

    private ProxyMode getProxyModeFromString(String proxyMode) throws IllegalArgumentException {
        switch (proxyMode) {
            case "http":
                return ProxyMode.HTTP;
            case "tcp":
                return ProxyMode.TCP;
            case "health":
                return ProxyMode.HEALTH;
            case "unknown":
                return ProxyMode.UNKNOWN;
            case "":
                return null;
            default:
                throw new IllegalArgumentException(String.format("Unknown proxy mode %s.", proxyMode));

        }
    }

    private int parseIntOrZero(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    HAProxyRecord(CSVRecord record) {
        proxyName = record.get("pxname");
        serviceName = record.get("svname");
        currentQueuedRequests = parseIntOrZero(record.get("qcur"));
        maxQueuedRequests = parseIntOrZero(record.get("qmax"));
        currentSessions = parseIntOrZero(record.get("scur"));
        maxSessions = parseIntOrZero(record.get("smax"));
        configuredSessionLimit = parseIntOrZero(record.get("slim"));
        cumulativeSessionNumber = parseIntOrZero(record.get("stot"));
        bytesIn = parseIntOrZero(record.get("bin"));
        bytesOut = parseIntOrZero(record.get("bout"));
        deniedRequests = parseIntOrZero(record.get("dreq"));
        deniedResponses = parseIntOrZero(record.get("dresp"));
        requestErrors = parseIntOrZero(record.get("ereq"));
        connectErrors = parseIntOrZero(record.get("econ"));
        responseErrors = parseIntOrZero(record.get("eresp"));
        retriedServerConnectsInRequest = parseIntOrZero(record.get("wretr"));
        redispatchedConnectsInRequest = parseIntOrZero(record.get("wredis"));
        status = record.get("status");
        weight = parseIntOrZero(record.get("weight"));
        activeServers = parseIntOrZero(record.get("act"));
        backupServers = parseIntOrZero(record.get("bck"));
        checkFailedNumber = parseIntOrZero(record.get("chkfail"));
        checkDownNumber = parseIntOrZero(record.get("chkdown"));
        lastStatusChange = Duration.ofSeconds(parseIntOrZero(record.get("lastchg")));
        downtime = Duration.ofSeconds(parseIntOrZero(record.get("downtime")));
        configuredMaxQueue = parseIntOrZero(record.get("qlimit"));
        processID = parseIntOrZero(record.get("pid"));
        proxyID = parseIntOrZero(record.get("iid"));
        serverID = parseIntOrZero(record.get("sid"));
        throttlePercentage = parseIntOrZero(record.get("throttle"));
        totalSelected = parseIntOrZero(record.get("lbtot"));
        trackID = parseIntOrZero(record.get("tracked"));
        type = getProxyTypeFromString(record.get("type"));
        sessionsInLastSecond = parseIntOrZero(record.get("rate"));
        sessionsPerSecondLimit = parseIntOrZero(record.get("rate_lim"));
        maxSessionsPerSecond = parseIntOrZero(record.get("rate_max"));
        checkStatus = getHCStatusFromString(record.get("check_status"));
        checkCode = parseIntOrZero(record.get("check_code"));
        checkDuration = Duration.ofMillis(parseIntOrZero(record.get("check_duration")));
        HTTPResponse1xx = parseIntOrZero(record.get("hrsp_1xx"));
        HTTPResponse2xx = parseIntOrZero(record.get("hrsp_2xx"));
        HTTPResponse3xx = parseIntOrZero(record.get("hrsp_3xx"));
        HTTPResponse4xx = parseIntOrZero(record.get("hrsp_4xx"));
        HTTPResponse5xx = parseIntOrZero(record.get("hrsp_5xx"));
        HTTPResponseOther = parseIntOrZero(record.get("hrsp_other"));
        HCDetails = record.get("hanafail");
        requestsInLastSecond = parseIntOrZero(record.get("req_rate"));
        requestsPerSecondLimit = parseIntOrZero(record.get("req_rate_max"));
        maxRequestsPerSecond = parseIntOrZero(record.get("req_tot"));
        clientAborts = parseIntOrZero(record.get("cli_abrt"));
        serverAborts = parseIntOrZero(record.get("srv_abrt"));
        compressorBytesIn = parseIntOrZero(record.get("comp_in"));
        compressorBytesOut = parseIntOrZero(record.get("comp_out"));
        compressorBytesBypassed = parseIntOrZero(record.get("comp_byp"));
        compressedResponses = parseIntOrZero(record.get("comp_rsp"));
        timeSinceLastSession = parseIntOrZero(record.get("lastsess"));
        lastHCContents = record.get("last_chk");
        lastAgentCheckContents = record.get("last_agt");
        averageQueueTime1024LastRequests = Duration.ofMillis(parseIntOrZero(record.get("qtime")));
        averageConnectTime1024LastRequests = Duration.ofMillis(parseIntOrZero(record.get("ctime")));
        averageResponseTime1024LastRequests = Duration.ofMillis(parseIntOrZero(record.get("rtime")));
        averageTotalSessionTime1024LastRequests = Duration.ofMillis(parseIntOrZero(record.get("ttime")));
        agentStatus = getAgentStatusFromString(record.get("agent_status"));
        agentCode = parseIntOrZero(record.get("agent_code"));
        agentCheckDuration = Duration.ofMillis(parseIntOrZero(record.get("agent_duration")));
        HCDescription = record.get("check_desc");
        AgentCheckDescription = record.get("agent_desc");
        checkRiseParameter = parseIntOrZero(record.get("check_rise"));
        checkFallParameter = parseIntOrZero(record.get("check_fall"));
        checkHealthResult = parseIntOrZero(record.get("check_health"));
        agentRiseParameter = parseIntOrZero(record.get("agent_rise"));
        agentFallParameter = parseIntOrZero(record.get("agent_fall"));
        agentHealthResult = parseIntOrZero(record.get("agent_health"));
        address = record.get("addr");
        cookie = record.get("cookie");
        mode = getProxyModeFromString(record.get("mode"));
        loadBalancingAlgorithm = record.get("algo");
        connectionsInLastSecond = parseIntOrZero(record.get("conn_rate"));
        maxConnectionPerSecond = parseIntOrZero(record.get("conn_rate_max"));
        totalConnections = parseIntOrZero(record.get("conn_tot"));
        interceptedConnections = parseIntOrZero(record.get("intercepted"));
        deniedRequestsTCPConnection = parseIntOrZero(record.get("dcon"));
        deniedRequestsTCPSession = parseIntOrZero(record.get("dses"));
    }

    public String getProxyName() {
        return proxyName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getCurrentQueuedRequests() {
        return currentQueuedRequests;
    }

    public int getMaxQueuedRequests() {
        return maxQueuedRequests;
    }

    public int getCurrentSessions() {
        return currentSessions;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public int getConfiguredSessionLimit() {
        return configuredSessionLimit;
    }

    public int getCumulativeSessionNumber() {
        return cumulativeSessionNumber;
    }

    public int getBytesIn() {
        return bytesIn;
    }

    public int getBytesOut() {
        return bytesOut;
    }

    public int getDeniedRequests() {
        return deniedRequests;
    }

    public int getDeniedResponses() {
        return deniedResponses;
    }

    public int getRequestErrors() {
        return requestErrors;
    }

    public int getConnectErrors() {
        return connectErrors;
    }

    public int getResponseErrors() {
        return responseErrors;
    }

    public int getRetriedServerConnectsInRequest() {
        return retriedServerConnectsInRequest;
    }

    public int getRedispatchedConnectsInRequest() {
        return redispatchedConnectsInRequest;
    }

    public String getStatus() {
        return status;
    }

    public int getWeight() {
        return weight;
    }

    public int getActiveServers() {
        return activeServers;
    }

    public int getBackupServers() {
        return backupServers;
    }

    public int getCheckFailedNumber() {
        return checkFailedNumber;
    }

    public int getCheckDownNumber() {
        return checkDownNumber;
    }

    public Duration getLastStatusChange() {
        return lastStatusChange;
    }

    public Duration getDowntime() {
        return downtime;
    }

    public int getConfiguredMaxQueue() {
        return configuredMaxQueue;
    }

    public int getProcessID() {
        return processID;
    }

    public int getProxyID() {
        return proxyID;
    }

    public int getServerID() {
        return serverID;
    }

    public int getThrottlePercentage() {
        return throttlePercentage;
    }

    public int getTotalSelected() {
        return totalSelected;
    }

    public int getTrackID() {
        return trackID;
    }

    public ProxyType getType() {
        return type;
    }

    public int getSessionsInLastSecond() {
        return sessionsInLastSecond;
    }

    public int getSessionsPerSecondLimit() {
        return sessionsPerSecondLimit;
    }

    public int getMaxSessionsPerSecond() {
        return maxSessionsPerSecond;
    }

    public HCStatus getCheckStatus() {
        return checkStatus;
    }

    public int getCheckCode() {
        return checkCode;
    }

    public Duration getCheckDuration() {
        return checkDuration;
    }

    public int getHTTPResponse1xx() {
        return HTTPResponse1xx;
    }

    public int getHTTPResponse2xx() {
        return HTTPResponse2xx;
    }

    public int getHTTPResponse3xx() {
        return HTTPResponse3xx;
    }

    public int getHTTPResponse4xx() {
        return HTTPResponse4xx;
    }

    public int getHTTPResponse5xx() {
        return HTTPResponse5xx;
    }

    public int getHTTPResponseOther() {
        return HTTPResponseOther;
    }

    public String getHCDetails() {
        return HCDetails;
    }

    public int getRequestsInLastSecond() {
        return requestsInLastSecond;
    }

    public int getRequestsPerSecondLimit() {
        return requestsPerSecondLimit;
    }

    public int getMaxRequestsPerSecond() {
        return maxRequestsPerSecond;
    }

    public int getClientAborts() {
        return clientAborts;
    }

    public int getServerAborts() {
        return serverAborts;
    }

    public int getCompressorBytesIn() {
        return compressorBytesIn;
    }

    public int getCompressorBytesOut() {
        return compressorBytesOut;
    }

    public int getCompressorBytesBypassed() {
        return compressorBytesBypassed;
    }

    public int getCompressedResponses() {
        return compressedResponses;
    }

    public int getTimeSinceLastSession() {
        return timeSinceLastSession;
    }

    public String getLastHCContents() {
        return lastHCContents;
    }

    public String getLastAgentCheckContents() {
        return lastAgentCheckContents;
    }

    public Duration getAverageQueueTime1024LastRequests() {
        return averageQueueTime1024LastRequests;
    }

    public Duration getAverageConnectTime1024LastRequests() {
        return averageConnectTime1024LastRequests;
    }

    public Duration getAverageResponseTime1024LastRequests() {
        return averageResponseTime1024LastRequests;
    }

    public Duration getAverageTotalSessionTime1024LastRequests() {
        return averageTotalSessionTime1024LastRequests;
    }

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public int getAgentCode() {
        return agentCode;
    }

    public Duration getAgentCheckDuration() {
        return agentCheckDuration;
    }

    public String getHCDescription() {
        return HCDescription;
    }

    public String getAgentCheckDescription() {
        return AgentCheckDescription;
    }

    public int getCheckRiseParameter() {
        return checkRiseParameter;
    }

    public int getCheckFallParameter() {
        return checkFallParameter;
    }

    public int getCheckHealthResult() {
        return checkHealthResult;
    }

    public int getAgentRiseParameter() {
        return agentRiseParameter;
    }

    public int getAgentFallParameter() {
        return agentFallParameter;
    }

    public int getAgentHealthResult() {
        return agentHealthResult;
    }

    public String getAddress() {
        return address;
    }

    public String getCookie() {
        return cookie;
    }

    public ProxyMode getMode() {
        return mode;
    }

    public String getLoadBalancingAlgorithm() {
        return loadBalancingAlgorithm;
    }

    public int getConnectionsInLastSecond() {
        return connectionsInLastSecond;
    }

    public int getMaxConnectionPerSecond() {
        return maxConnectionPerSecond;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public int getInterceptedConnections() {
        return interceptedConnections;
    }

    public int getDeniedRequestsTCPConnection() {
        return deniedRequestsTCPConnection;
    }

    public int getDeniedRequestsTCPSession() {
        return deniedRequestsTCPSession;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Field f : HAProxyRecord.class.getDeclaredFields()) {
            try {
                sb.append(f.getName()).append(" : ").append(f.get(this)).append(", ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
