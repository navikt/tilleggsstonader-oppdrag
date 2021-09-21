package no.nav.familie.oppdrag.common

import org.slf4j.LoggerFactory
import javax.xml.ws.soap.SOAPFaultException

private val secureLogger = LoggerFactory.getLogger("secureLogger")

fun logSoapFaultException(e: Exception) {
    if (e is SOAPFaultException) {
        secureLogger.error("SOAPFaultException -" +
                           " faultCode=${e.fault.faultCode}" +
                           " faultString=${e.fault.faultString}"
        )
    }
}