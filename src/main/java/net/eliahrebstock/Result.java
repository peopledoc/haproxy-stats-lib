package net.eliahrebstock;

import java.util.Date;
import java.util.Map;

/**
 * Not used
 */
@Deprecated
public class Result {


    private String product;

    private String frontend;

    private String frontend_fqdn;

    private String[] backends;

    private Map<String, String> backends_fqdn;

    private Map<String, String> backends_status;

    private Date checkDate;

    private int weight;

    public Result(String product, String frontend, String frontend_fqdn, String[] backends, Map<String, String> backends_fqdn, Map<String, String> backends_status, Date checkDate) {
        this.product = product;
        this.frontend = frontend;
        this.frontend_fqdn = frontend_fqdn;
        this.backends = backends;
        this.backends_fqdn = backends_fqdn;
        this.backends_status = backends_status;
        this.checkDate = checkDate;
    }

    public String getProduct() {
        return product;
    }

    public String getFrontend() {
        return frontend;
    }

    public String getFrontend_fqdn() {
        return frontend_fqdn;
    }

    public String[] getBackends() {
        return backends;
    }

    public Map<String, String> getBackends_fqdn() {
        return backends_fqdn;
    }

    public Map<String, String> getBackends_status() {
        return backends_status;
    }

    public Date getCheckDate() {
        return checkDate;
    }
}
