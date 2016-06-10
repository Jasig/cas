package org.apereo.cas.web.v3;

import org.apereo.cas.ticket.proxy.ProxyHandler;
import org.apereo.cas.validation.ValidationSpecification;
import org.apereo.cas.web.AbstractServiceValidateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Misagh Moayyed
 * @since 4.2
 */
@RefreshScope
@Controller("v3ServiceValidateController")
public class V3ServiceValidateController extends AbstractServiceValidateController {
    /**
     * Handle model and view.
     *
     * @param request the request
     * @param response the response
     * @return the model and view
     * @throws Exception the exception
     */
    @RequestMapping(path="/p3/serviceValidate", method = RequestMethod.GET)
    protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response)
        throws Exception {
        return super.handleRequestInternal(request, response);
    }

    @Override
    @Autowired
    @Scope("prototype")
    public void setValidationSpecification(
            @Qualifier("cas20WithoutProxyProtocolValidationSpecification")
            final ValidationSpecification validationSpecification) {
        super.setValidationSpecification(validationSpecification);
    }

    @Override
    @Autowired
    public void setFailureView(@Qualifier("cas3ServiceFailureView") final View failureView) {
        super.setFailureView(failureView);
    }

    @Override
    @Autowired
    public void setSuccessView(@Qualifier("cas3ServiceSuccessView") final View successView) {
        super.setSuccessView(successView);
    }

    @Override
    @Autowired
    public void setProxyHandler(@Qualifier("proxy20Handler") final ProxyHandler proxyHandler) {
        super.setProxyHandler(proxyHandler);
    }
}
