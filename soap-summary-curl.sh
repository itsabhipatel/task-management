curl -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
                  xmlns:soap=\"http://example.com/taskmanagement/soap\">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:GetTaskSummaryRequest/>
  </soapenv:Body>
</soapenv:Envelope>"
