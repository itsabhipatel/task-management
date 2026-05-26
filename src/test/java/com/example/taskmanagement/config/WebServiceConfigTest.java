package com.example.taskmanagement.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchema;

class WebServiceConfigTest {

    private final WebServiceConfig webServiceConfig = new WebServiceConfig();

    @Test
    void shouldRegisterMessageDispatcherServletUnderWsPath() {
        var registration = webServiceConfig.messageDispatcherServlet(mock(ApplicationContext.class));

        assertNotNull(registration.getServlet());
        assertEquals(MessageDispatcherServlet.class, registration.getServlet().getClass());
        assertEquals("/ws/*", registration.getUrlMappings().iterator().next());
    }

    @Test
    void shouldCreateTaskSummarySchemaAndWsdlDefinition() {
        XsdSchema schema = webServiceConfig.taskSummarySchema();

        DefaultWsdl11Definition wsdl = webServiceConfig.taskSummaryWsdl(schema);

        assertNotNull(schema);
        assertNotNull(wsdl);
    }
}
