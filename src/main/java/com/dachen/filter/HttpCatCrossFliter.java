package com.dachen.filter;

import com.dachen.cat.CatMsgConstants;
import com.dachen.cat.CatMsgContext;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.AbstractMessage;
import com.dianping.cat.message.internal.NullMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpCatCrossFliter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(HttpCatCrossFliter.class);

    private static final String DEFAULT_APPLICATION_NAME = "default";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String requestURL = request.getRequestURL().toString();

        Transaction t = Cat.newTransaction(CatMsgConstants.CROSS_SERVER, requestURL);

        try {
            Cat.Context context = new CatMsgContext();
            if (request.getHeader(Cat.Context.ROOT) == null) {
                Cat.logRemoteCallClient(context);
            } else {
                context.addProperty(Cat.Context.ROOT, request.getHeader(Cat.Context.ROOT));
                context.addProperty(Cat.Context.PARENT, request.getHeader(Cat.Context.PARENT));
                context.addProperty(Cat.Context.CHILD, request.getHeader(Cat.Context.CHILD));
                Cat.logRemoteCallServer(context);
            }

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            requestAttributes.setAttribute(Cat.Context.ROOT, context.getProperty(Cat.Context.ROOT), 0);
            requestAttributes.setAttribute(Cat.Context.PARENT, context.getProperty(Cat.Context.PARENT), 0);
            requestAttributes.setAttribute(Cat.Context.CHILD, context.getProperty(Cat.Context.CHILD), 0);

            this.createProviderCross(request, t);

            filterChain.doFilter(req, resp);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("------ Get cat msgtree error : ", e);

            Event event = Cat.newEvent("HTTP_REST_CAT_ERROR", requestURL);
            event.setStatus(e);
            completeEvent(event);
            t.addChild(event);
            t.setStatus(e.getClass().getSimpleName());
        } finally {
            t.complete();
        }

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    /**
     * 串联provider端消息树
     *
     * @param request
     * @param t
     */
    private void createProviderCross(HttpServletRequest request, Transaction t) {
        Event crossAppEvent = Cat.newEvent(CatMsgConstants.PROVIDER_CALL_APP, request.getHeader(CatMsgConstants.APPLICATION_KEY));    //clientName
        Event crossServerEvent = Cat.newEvent(CatMsgConstants.PROVIDER_CALL_SERVER, request.getRemoteAddr());    //clientIp
        crossAppEvent.setStatus(Event.SUCCESS);
        crossServerEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        completeEvent(crossServerEvent);
        t.addChild(crossAppEvent);
        t.addChild(crossServerEvent);
    }

    private void completeEvent(Event event) {
        if (event != NullMessage.EVENT) {
            AbstractMessage message = (AbstractMessage) event;
            message.setCompleted(true);
        }
    }

}
