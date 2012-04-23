# Uses JDK6 and JAXWS 2.2 to generate client-side code.
# Make sure that the JAXWS 2.2 jars are in the endorsed directory when
# generating the code and not there when running it.

../bin/wsimport.sh -b jaxb_bindings/om_bindings_v3.xml -extension -clientjar om_service_v3_en.jar http://geckois.gecko.no/nCore/Services/ObjectModel/V3/En/ObjectModelService.svc?wsdl

../bin/wsimport.sh -b jaxb_bindings/doc_bindings_v3.xml -extension -clientjar doc_service_v3.jar http://geckois.gecko.no/nCore/Services/Documents/V3/DocumentService.svc?wsdl

../bin/wsimport.sh -b jaxb_bindings/func_bindings_v2.xml -extension -clientjar func_service_v2.jar http://geckois.gecko.no/nCore/services/functions/v2/FunctionsService.svc?wsdl

