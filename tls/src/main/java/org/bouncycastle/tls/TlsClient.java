package org.bouncycastle.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Interface describing a TLS client endpoint.
 */
public interface TlsClient
    extends TlsPeer
{
    void init(TlsClientContext context);

    /**
     * Return the session this client wants to resume, if any. Note that the peer's certificate
     * chain for the session (if any) may need to be periodically revalidated.
     * 
     * @return A {@link TlsSession} representing the resumable session to be used for this
     *         connection, or null to use a new session.
     * @see SessionParameters#getPeerCertificate()
     */
    TlsSession getSessionToResume();

    boolean isFallback();

    int[] getCipherSuites();

    // Hashtable is (Integer -> byte[])
    Hashtable getClientExtensions()
        throws IOException;

    void notifyServerVersion(ProtocolVersion selectedVersion)
        throws IOException;

    /**
     * Notifies the client of the session_id sent in the ServerHello.
     *
     * @param sessionID
     * @see TlsContext#getSession()
     */
    void notifySessionID(byte[] sessionID);

    void notifySelectedCipherSuite(int selectedCipherSuite);

    // Hashtable is (Integer -> byte[])
    void processServerExtensions(Hashtable serverExtensions)
        throws IOException;

    // Vector is (SupplementalDataEntry)
    void processServerSupplementalData(Vector serverSupplementalData)
        throws IOException;

    TlsPSKIdentity getPSKIdentity() throws IOException;

    TlsSRPIdentity getSRPIdentity() throws IOException;

    TlsDHGroupVerifier getDHGroupVerifier() throws IOException;

    TlsSRPConfigVerifier getSRPConfigVerifier() throws IOException;

    TlsAuthentication getAuthentication()
        throws IOException;

    // Vector is (SupplementalDataEntry)
    Vector getClientSupplementalData()
        throws IOException;

    /**
     * RFC 5077 3.3. NewSessionTicket Handshake Message
     * <p>
     * This method will be called (only) when a NewSessionTicket handshake message is received. The
     * ticket is opaque to the client and clients MUST NOT examine the ticket under the assumption
     * that it complies with e.g. <i>RFC 5077 4. Recommended Ticket Construction</i>.
     *
     * @param newSessionTicket The ticket.
     * @throws IOException
     */
    void notifyNewSessionTicket(NewSessionTicket newSessionTicket)
        throws IOException;
}
