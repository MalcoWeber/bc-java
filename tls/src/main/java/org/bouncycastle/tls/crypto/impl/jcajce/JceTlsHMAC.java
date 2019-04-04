package org.bouncycastle.tls.crypto.impl.jcajce;

import java.security.InvalidKeyException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.security.auth.DestroyFailedException;

import org.bouncycastle.jcajce.provider.asymmetric.DestroyableSecretKeySpec;
import org.bouncycastle.tls.crypto.TlsHMAC;
import org.bouncycastle.util.Integers;

/**
 * Wrapper class for a JCE MAC based on HMAC to provide the necessary operations for TLS.
 */
public class JceTlsHMAC
    implements TlsHMAC
{
	
	private static Logger LOG = Logger.getLogger(JceTlsHMAC.class.getName());
	
    private static final Hashtable internalBlockSizes = new Hashtable();

    static
    {
        internalBlockSizes.put("HmacMD5", Integers.valueOf(64));
        internalBlockSizes.put("HmacSHA1", Integers.valueOf(64));
        internalBlockSizes.put("HmacSHA256", Integers.valueOf(64));
        internalBlockSizes.put("HmacSHA384", Integers.valueOf(128));
        internalBlockSizes.put("HmacSHA512", Integers.valueOf(128));
    }

    private final Mac hmac;
    private final String algorithm;
    private final Integer internalBlockSize;

    /**
     * Base constructor.
     *
     * @param hmac MAC implementation.
     * @param algorithm algorithm name to use for keys and to get the internal block size.
     */
    public JceTlsHMAC(Mac hmac, String algorithm)
    {
        this(hmac, algorithm, getInternalBlockSize(algorithm));
    }

    private static int getInternalBlockSize(String algorithm)
    {
        if (!internalBlockSizes.containsKey(algorithm))
        {
            throw new IllegalArgumentException("HMAC " + algorithm + " unknown");
        }

        return ((Integer)internalBlockSizes.get(algorithm)).intValue();
    }

    /**
     * Base constructor specifying the internal block size.
     *
     * @param hmac MAC implementation.
     * @param algorithm algorithm name to use for keys and to get the internal block size.
     * @param internalBlockSize internal block size of the message digest underlying the HMAC.
     */
    public JceTlsHMAC(Mac hmac, String algorithm, int internalBlockSize)
    {
        this.hmac = hmac;
        this.algorithm = algorithm;
        this.internalBlockSize = Integers.valueOf(internalBlockSize);
    }

    public void setKey(byte[] key, int keyOff, int keyLen)
    {
        try
        {
        	DestroyableSecretKeySpec secretKeySpec = new DestroyableSecretKeySpec(key, keyOff, keyLen, algorithm);
			hmac.init(secretKeySpec);
		    try {
		    	secretKeySpec.destroy();
            } catch (DestroyFailedException e) {
                LOG.log(Level.FINE, "Could not destroy calculate SecretKey", e);
            }
			
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void update(byte[] input, int inOff, int length)
    {
        hmac.update(input, inOff, length);
    }

    public byte[] calculateMAC()
    {
        return hmac.doFinal();
    }

    public int getInternalBlockSize()
    {
        return internalBlockSize.intValue();
    }

    public int getMacLength()
    {
        return hmac.getMacLength();
    }

    public void reset()
    {
        hmac.reset();
    }
}
