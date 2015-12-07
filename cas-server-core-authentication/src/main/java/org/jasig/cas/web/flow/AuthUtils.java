/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jasig.cas.web.flow;

import java.net.URL;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sin
 */
public final class AuthUtils {

  //The www declaration.
  public static final String WWW = "www";

  //The self tenant id.
  public static final String SELF = "self";

  //The logger.
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtils.class);

  /**
   * Extracts the tenant id from the request.
   *
   * @param request The HTTP request.
   * @return Tenant id, if any.
   */
  public static String extractTenantID(ServletRequest request) {
    String tenantID = null;
    String domain = "wavity.com"; //TODO: see if it doesn't need to be hard-coded.
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String uri = httpRequest.getParameter("service");
      if(uri==null) {
        //TODO: Think of an appropriate error.
        return "self";
      }
      LOGGER.info("Request URI for tenant extraction:{}", uri);
      //URI is http://tenantid.ziontech.com:8080/oneteam/..
      try {
        URL url = new URL(uri);
        String host = url.getHost();
        int idx = host.indexOf('.');
        if (idx != -1) {
          String subDomain = host.substring(0, idx);
          if (host.length() > idx) {
            String theDomain = host.substring(idx + 1);
            if (theDomain.equalsIgnoreCase(domain) && subDomain != null && !subDomain.equalsIgnoreCase(WWW)) {
              tenantID = subDomain;
            }
          }
        }
      } catch (Exception ex) {
        LOGGER.warn("URL parsing for tenant failed, trying again", ex);
        try {
          int startIndex = uri.indexOf("://");
          if (startIndex != -1) {
            String host = uri.substring(startIndex + 3);
            int idx = host.indexOf('.');
            if (idx != -1) {
              String subDomain = host.substring(0, idx);
              if (host.length() > idx) {
                String theDomain = host.substring(idx + 1);
                if (theDomain.equalsIgnoreCase(domain) && subDomain != null
                        && !subDomain.equalsIgnoreCase(WWW)) {
                  tenantID = subDomain;
                }
              }
            }
          }
        } catch (Exception e) {
          LOGGER.warn("URL parsing for tenant failed again", e);
        }
      }
      if (tenantID == null) {
        LOGGER.warn("Using fallback tenant ID:{}", SELF);
        tenantID = SELF;
      } else {
        LOGGER.info("extracted tenant ID:{}", tenantID);
      }
    }
    return tenantID;
  }


  /**
   * Sets the tenant id.
   *
   * @param tenantId The tenant id.
   */
  public static void setTenantId(String tenantId) {
    //Unset what we have.
    TenantContextHolder.unset();
    TenantContextHolder.set(tenantId);
  }


  /**
   * Returns the tenant id.
   *
   * @return The tenant id.
   */
  public static String getTenantId() {
    String tenantId = TenantContextHolder.get();
    return tenantId;
  }


  //This class maintains the tenant context into a threadlocal object.
  private static final class TenantContextHolder {
    //Thread local.
     final static ThreadLocal<String> threadLocal =
             new ThreadLocal<>();

     /**
      *
      * @param tennatId
      */
    public static void set(String tennatId) {
        threadLocal.set(tennatId);
    }

    /**
     *
     */
    public static void unset() {
        threadLocal.remove();
    }

    /**
     *
     * @return
     */
    public static String get() {
        return threadLocal.get();
    }
  }
}
