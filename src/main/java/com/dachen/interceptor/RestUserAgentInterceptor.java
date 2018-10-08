package com.dachen.interceptor;

import com.dachen.cat.CatMsgConstants;
import com.dachen.cat.CatMsgContext;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.AbstractMessage;
import com.dianping.cat.message.internal.NullMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

public class RestUserAgentInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("RestUserAgentInterceptor");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        Transaction t = Cat.newTransaction(CatMsgConstants.CROSS_SERVER, request.getURI().toString());

        try {
            Cat.Context context = new CatMsgContext();


            Cat.logRemoteCallClient(context);

            if (requestAttributes.getAttribute(Cat.Context.ROOT,0) != null) {
                context.addProperty(Cat.Context.ROOT, requestAttributes.getAttribute(Cat.Context.ROOT,0).toString());
                context.addProperty(Cat.Context.PARENT, requestAttributes.getAttribute(Cat.Context.CHILD,0).toString());
                //context.addProperty(Cat.Context.CHILD, request.getHeader(Cat.Context.CHILD));
            }

            HttpHeaders headers = request.getHeaders();
            headers.add(Cat.Context.ROOT,context.getProperty(Cat.Context.ROOT));
            headers.add(Cat.Context.PARENT,context.getProperty(Cat.Context.PARENT));
            headers.add(Cat.Context.CHILD,context.getProperty(Cat.Context.CHILD));

            this.createProviderCross(request, t);

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            //logger.error("------ Get cat msgtree error : ", e);

            Event event = Cat.newEvent("HTTP_REST_CAT_ERROR", request.getURI().toString());
            event.setStatus(e);
            completeEvent(event);
            t.addChild(event);
            t.setStatus(e.getClass().getSimpleName());
        } finally {
            t.complete();
        }

        return execution.execute(request, body);
    }

    /**
     * 串联provider端消息树
     *
     * @param request
     * @param t
     */
    private void createProviderCross(HttpRequest request, Transaction t) {
        HttpHeaders headers = request.getHeaders();

        Event crossAppEvent = Cat.newEvent(CatMsgConstants.PROVIDER_CALL_APP, headers.get(CatMsgConstants.APPLICATION_KEY).toString());    //clientName
        //Event crossServerEvent = Cat.newEvent(CatMsgConstants.PROVIDER_CALL_SERVER, request.getRemoteAddr());    //clientIp
        crossAppEvent.setStatus(Event.SUCCESS);
        //crossServerEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        //completeEvent(crossServerEvent);
        t.addChild(crossAppEvent);
        //t.addChild(crossServerEvent);
    }

    private void completeEvent(Event event) {
        if (event != NullMessage.EVENT) {
            AbstractMessage message = (AbstractMessage) event;
            message.setCompleted(true);
        }
    }



}
