//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.common.java.platform;

import static com.microsoft.identity.common.java.exception.ClientException.ANDROID_KEYSTORE_UNAVAILABLE;
import static com.microsoft.identity.common.java.exception.ClientException.BAD_KEY_SIZE;
import static com.microsoft.identity.common.java.exception.ClientException.BAD_PADDING;
import static com.microsoft.identity.common.java.exception.ClientException.INTERRUPTED_OPERATION;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_ALG;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_ALG_PARAMETER;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_BLOCK_SIZE;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_KEY;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_KEY_MISSING;
import static com.microsoft.identity.common.java.exception.ClientException.INVALID_PROTECTION_PARAMS;
import static com.microsoft.identity.common.java.exception.ClientException.JSON_CONSTRUCTION_FAILED;
import static com.microsoft.identity.common.java.exception.ClientException.JWT_SIGNING_FAILURE;
import static com.microsoft.identity.common.java.exception.ClientException.KEYSTORE_NOT_INITIALIZED;
import static com.microsoft.identity.common.java.exception.ClientException.NO_SUCH_ALGORITHM;
import static com.microsoft.identity.common.java.exception.ClientException.NO_SUCH_PADDING;
import static com.microsoft.identity.common.java.exception.ClientException.SIGNING_FAILURE;
import static com.microsoft.identity.common.java.exception.ClientException.THUMBPRINT_COMPUTATION_FAILURE;
import static com.microsoft.identity.common.java.exception.ClientException.UNKNOWN_EXPORT_FORMAT;
import static com.microsoft.identity.common.java.marker.PerfConstants.CodeMarkerConstants.GENERATE_AT_POP_ASYMMETRIC_KEYPAIR_END;
import static com.microsoft.identity.common.java.marker.PerfConstants.CodeMarkerConstants.GENERATE_AT_POP_ASYMMETRIC_KEYPAIR_START;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.identity.common.java.AuthenticationConstants;
import com.microsoft.identity.common.java.crypto.IDevicePopManager;
import com.microsoft.identity.common.java.crypto.IKeyStoreKeyManager;
import com.microsoft.identity.common.java.crypto.SecureHardwareState;
import com.microsoft.identity.common.java.crypto.SigningAlgorithm;
import com.microsoft.identity.common.java.exception.ClientException;
import com.microsoft.identity.common.java.logging.Logger;
import com.microsoft.identity.common.java.marker.CodeMarkerManager;
import com.microsoft.identity.common.java.util.StringUtil;
import com.microsoft.identity.common.java.util.TaskCompletedCallbackWithError;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cz.msebera.android.httpclient.extras.Base64;
import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.NonNull;

public abstract class AbstractDevicePopManager implements IDevicePopManager {
    private static final String TAG = AbstractDevicePopManager.class.getSimpleName();

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * The PoP alias in the designated KeyStore -- default val used by non-OneAuth Android platform.
     */
    protected static final String DEFAULT_KEYSTORE_ENTRY_ALIAS = "microsoft-device-pop";

    /**
     * The NIST advised min keySize for RSA pairs.
     */
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * Log message when private key material cannot be found.
     */
    private static final String PRIVATE_KEY_NOT_FOUND = "Not an instance of a PrivateKeyEntry";
    public static final Type MAP_STRING_STRING_TYPE = TypeToken.getParameterized(Map.class, String.class, String.class).getType();
    public static final Gson GSON = new Gson();

    /**
     * Error message from underlying KeyStore that StrongBox HAL is unavailable.
     */
    public static final String STRONG_BOX_UNAVAILABLE_EXCEPTION = "StrongBoxUnavailableException";

    /**
     * Manager class for interacting with key storage mechanism.
     */
    protected final IKeyStoreKeyManager<KeyStore.PrivateKeyEntry> mKeyManager;

    /**
     * The name of the KeyStore to use.
     */
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    /**
     * A background worker to service async tasks.
     */
    private static final ExecutorService sThreadExecutor = Executors.newFixedThreadPool(5);

    /**
     * Reference to our perf-marker object.
     */
    private static final CodeMarkerManager sCodeMarkerManager = CodeMarkerManager.getInstance();

    /**
     * Properties used by the self-signed certificate.
     */
    private static final class CertificateProperties {

        /**
         * The certification validity duration.
         */
        static final int CERTIFICATE_VALIDITY_YEARS = 99;

        /**
         * The serial number of the certificate.
         */
        static final BigInteger SERIAL_NUMBER = BigInteger.ONE;

        /**
         * The Common Name of the certificate.
         */
        static final String COMMON_NAME = "CN=device-pop";
    }

    /**
     * Properties embedded in the SignedHttpRequest.
     * Roughly conforms to: https://tools.ietf.org/html/draft-ietf-oauth-signed-http-request-03
     */
    private static final class SignedHttpRequestJwtClaims {

        /**
         * The access_token.
         */
        private static final String ACCESS_TOKEN = "at";

        /**
         * The timestamp.
         */
        private static final String TIMESTAMP = "ts";

        /**
         * The HTTP method.
         */
        private static final String HTTP_METHOD = "m";

        /**
         * The host part of the resource server URL.
         */
        private static final String HTTP_HOST = "u";

        /**
         * The path part of the resource server URL.
         */
        private static final String HTTP_PATH = "p";

        /**
         * The JWK signed into this JWT.
         */
        private static final String CNF = "cnf";

        /**
         * A random value used for replay protection.
         */
        private static final String NONCE = "nonce";

        /**
         * Arbitrary string data supplied by the caller to be embedded in the resulting SHR.
         */
        private static final String CLIENT_CLAIMS = "client_claims";

        /**
         * JWK for inner object.
         */
        public static final String JWK = "jwk";
    }

    /**
     * Algorithms supported by this KeyPairGenerator.
     */
    protected static final class KeyPairGeneratorAlgorithms {
        public static final String RSA = "RSA";
    }

    @Override
    public IKeyStoreKeyManager<KeyStore.PrivateKeyEntry> getKeyManager() {
        return mKeyManager;
    }

    public AbstractDevicePopManager(@NonNull final IKeyStoreKeyManager<KeyStore.PrivateKeyEntry> keyManager) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        mKeyManager = keyManager;
    }

    @Override
    public boolean asymmetricKeyExists() {
        return mKeyManager.exists();
    }

    @Override
    public boolean asymmetricKeyExists(@NonNull final String thumbprint) {
        return mKeyManager.hasThumbprint(thumbprint.getBytes(UTF8));
    }

    @Override
    public String getAsymmetricKeyThumbprint() throws ClientException {
        final Exception exception;
        final String errCode;

        try {
            final KeyStore.PrivateKeyEntry entry = mKeyManager.getEntry();
            return getRsaThumbprint(entry);
        } catch (final KeyStoreException e) {
            exception = e;
            errCode = KEYSTORE_NOT_INITIALIZED;
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final UnrecoverableEntryException e) {
            exception = e;
            errCode = INVALID_PROTECTION_PARAMS;
        } catch (final JOSEException e) {
            exception = e;
            errCode = THUMBPRINT_COMPUTATION_FAILURE;
        }

        throw new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );
    }

    /**
     * Given an RSA private key entry, get the RSA thumbprint.
     *
     * @param entry the entry to compute the thumbprint for.
     * @return A String that would be identicative of this specific key.
     * @throws JOSEException If there is a computation problem.
     */
    public static String getRsaThumbprint(@NonNull final KeyStore.PrivateKeyEntry entry) throws JOSEException {
        final KeyPair rsaKeyPair = getKeyPairForEntry(entry);
        final RSAKey rsaKey = getRsaKeyForKeyPair(rsaKeyPair);
        return getThumbprintForRsaKey(rsaKey);
    }

    @Override
    public void generateAsymmetricKey(@NonNull final TaskCompletedCallbackWithError<String, ClientException> callback) {
        sThreadExecutor.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onTaskCompleted(generateAsymmetricKey());
                        } catch (final ClientException e) {
                            callback.onError(e);
                        }
                    }
                }
        );
    }

    @Override
    public String generateAsymmetricKey() throws ClientException {
        final Exception exception;
        final String errCode;

        try {
            sCodeMarkerManager.markCode(GENERATE_AT_POP_ASYMMETRIC_KEYPAIR_START);
            final KeyPair keyPair = generateNewRsaKeyPair(RSA_KEY_SIZE);
            final RSAKey rsaKey = getRsaKeyForKeyPair(keyPair);
            return getThumbprintForRsaKey(rsaKey);
        } catch (final UnsupportedOperationException e) {
            exception = e;
            errCode = BAD_KEY_SIZE;
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final NoSuchProviderException e) {
            exception = e;
            errCode = ANDROID_KEYSTORE_UNAVAILABLE;
        } catch (final InvalidAlgorithmParameterException e) {
            exception = e;
            errCode = INVALID_ALG;
        } catch (final JOSEException e) {
            exception = e;
            errCode = THUMBPRINT_COMPUTATION_FAILURE;
        } finally {
            sCodeMarkerManager.markCode(GENERATE_AT_POP_ASYMMETRIC_KEYPAIR_END);
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG,
                clientException.getMessage(),
                clientException
        );

        throw clientException;
    }

    public abstract KeyPair generateNewRsaKeyPair(int keySize) throws UnsupportedOperationException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException;

    @Override
    @Nullable
    public Date getAsymmetricKeyCreationDate() throws ClientException {
        return mKeyManager.getCreationDate();
    }

    @Override
    public boolean clearAsymmetricKey() {
        return mKeyManager.clear();
    }

    @Override
    public String getRequestConfirmation() throws ClientException {
        // The sync API is a wrapper around the async API
        // This likely shouldn't be called on the UI thread to avoid ANR
        // Device perf may vary, however -- some devices this may be OK.
        // YMMV
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1];
        final ClientException[] errorResult = new ClientException[1];

        getRequestConfirmation(new TaskCompletedCallbackWithError<String, ClientException>() {
            @Override
            public void onTaskCompleted(@NonNull final String reqCnf) {
                result[0] = reqCnf;
                latch.countDown();
            }

            @Override
            public void onError(@NonNull final ClientException error) {
                errorResult[0] = error;
                latch.countDown();
            }
        });

        // Wait for the async op to complete...
        try {
            latch.await();

            if (null != result[0]) {
                return result[0];
            } else {
                throw errorResult[0];
            }
        } catch (final InterruptedException e) {
            Logger.error(
                    TAG,
                    "Interrupted while waiting on callback.",
                    e
            );

            throw new ClientException(
                    INTERRUPTED_OPERATION,
                    e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void getRequestConfirmation(@NonNull final TaskCompletedCallbackWithError<String, ClientException> callback) {
        sThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // Vars for error handling...
                final Exception exception;
                final String errCode;

                try {
                    final KeyStore.PrivateKeyEntry keyEntry = mKeyManager.getEntry();
                    final KeyPair rsaKeyPair = getKeyPairForEntry(keyEntry);
                    final RSAKey rsaKey = getRsaKeyForKeyPair(rsaKeyPair);
                    final String base64UrlEncodedJwkJsonStr = getReqCnfForRsaKey(rsaKey);

                    callback.onTaskCompleted(base64UrlEncodedJwkJsonStr);

                    // We're done.
                    return;
                } catch (final KeyStoreException e) {
                    exception = e;
                    errCode = KEYSTORE_NOT_INITIALIZED;
                } catch (final NoSuchAlgorithmException e) {
                    exception = e;
                    errCode = NO_SUCH_ALGORITHM;
                } catch (final UnrecoverableEntryException e) {
                    exception = e;
                    errCode = INVALID_PROTECTION_PARAMS;
                } catch (final JOSEException e) {
                    exception = e;
                    errCode = THUMBPRINT_COMPUTATION_FAILURE;
                } catch (final JSONException e) {
                    exception = e;
                    errCode = JSON_CONSTRUCTION_FAILED;
                }

                final ClientException clientException = new ClientException(
                        errCode,
                        exception.getMessage(),
                        exception
                );

                Logger.error(
                        TAG,
                        clientException.getMessage(),
                        clientException
                );

                callback.onError(clientException);
            }
        });
    }

    @Override
    public @NonNull
    String sign(@NonNull final SigningAlgorithm alg,
                @NonNull final String input) throws ClientException {
        return Base64.encodeToString(sign(alg, input.getBytes(UTF8)), Base64.NO_WRAP);
    }

    @Override
    public byte[] sign(@NonNull SigningAlgorithm alg,
                       @NonNull final byte[] inputBytesToSign) throws ClientException {
        Exception exception;
        String errCode;
        final String methodName = ":sign";
        try {
            final KeyStore.Entry keyEntry = mKeyManager.getEntry();

            if (!(keyEntry instanceof KeyStore.PrivateKeyEntry)) {
                Logger.warn(
                        TAG + methodName,
                        PRIVATE_KEY_NOT_FOUND
                );
                throw new ClientException(INVALID_KEY_MISSING);
            }

            final Signature signature = Signature.getInstance(alg.toString());
            signature.initSign(((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey());
            signature.update(inputBytesToSign);
            return signature.sign();
        } catch (final KeyStoreException e) {
            exception = e;
            errCode = KEYSTORE_NOT_INITIALIZED;
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final UnrecoverableEntryException e) {
            exception = e;
            errCode = INVALID_PROTECTION_PARAMS;
        } catch (final InvalidKeyException e) {
            exception = e;
            errCode = INVALID_KEY;
        } catch (final SignatureException e) {
            exception = e;
            errCode = SIGNING_FAILURE;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG + methodName,
                clientException.getMessage(),
                clientException
        );

        throw clientException;
    }

    @Override
    public boolean verify(@NonNull final SigningAlgorithm alg,
                          @NonNull final String plainText,
                          @NonNull final String signatureStr) {
        // TODO: Base64.decode can throw an illegal argument.
        return verify(alg, plainText.getBytes(UTF8), Base64.decode(signatureStr, Base64.NO_WRAP));
    }

    @Override
    public boolean verify(@NonNull final SigningAlgorithm alg,
                          @NonNull final byte[] inputBytesToVerify,
                          @NonNull final byte[] signatureBytes) {
        String methodName = ":verify";
        String errCode;
        Exception exception;
        try {
            final KeyStore.PrivateKeyEntry keyEntry = mKeyManager.getEntry();

            if (keyEntry == null) {
                Logger.warn(
                        TAG + methodName,
                        PRIVATE_KEY_NOT_FOUND
                );
                return false;
            }

            final Signature signature = Signature.getInstance(alg.toString());
            signature.initVerify(keyEntry.getCertificate());
            signature.update(inputBytesToVerify);
            return signature.verify(signatureBytes);
        } catch (final NoSuchAlgorithmException e) {
            errCode = NO_SUCH_ALGORITHM;
            exception = e;
        } catch (final KeyStoreException e) {
            errCode = KEYSTORE_NOT_INITIALIZED;
            exception = e;
        } catch (final UnrecoverableEntryException e) {
            errCode = INVALID_PROTECTION_PARAMS;
            exception = e;
        } catch (final InvalidKeyException e) {
            errCode = INVALID_KEY;
            exception = e;
        } catch (final SignatureException e) {
            errCode = SIGNING_FAILURE;
            exception = e;
        }

        Logger.error(
                TAG + methodName,
                errCode,
                exception
        );

        return false;
    }

    @Override
    public String encrypt(@NonNull final Cipher cipher,
                          @NonNull final String plaintext) throws ClientException {
        return Base64.encodeToString(encrypt(cipher, plaintext.getBytes(UTF8)), Base64.NO_PADDING | Base64.NO_WRAP);
    }

    @Override
    public byte[] encrypt(@NonNull final Cipher cipher, @NonNull final byte[] plaintext) throws ClientException {
        String errCode;
        Exception exception;
        final String methodName = ":encrypt";
        try {
            // Load our key material
            final KeyStore.PrivateKeyEntry privateKeyEntry = mKeyManager.getEntry();

            // Get a ref to our public key
            final PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            // Init our Cipher
            final javax.crypto.Cipher input = javax.crypto.Cipher.getInstance(cipher.toString());
            if (cipher.getParameters() != null) {
                input.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey, cipher.getParameters());
            } else {
                input.init(javax.crypto.Cipher.ENCRYPT_MODE, publicKey);
            }

            return input.doFinal(plaintext);
        } catch (final InvalidKeyException e) {
            errCode = INVALID_KEY;
            exception = e;
        } catch (final UnrecoverableEntryException e) {
            errCode = INVALID_PROTECTION_PARAMS;
            exception = e;
        } catch (final NoSuchAlgorithmException e) {
            errCode = NO_SUCH_ALGORITHM;
            exception = e;
        } catch (final KeyStoreException e) {
            errCode = KEYSTORE_NOT_INITIALIZED;
            exception = e;
        } catch (final NoSuchPaddingException e) {
            errCode = NO_SUCH_PADDING;
            exception = e;
        } catch (final InvalidAlgorithmParameterException e) {
            errCode = INVALID_ALG_PARAMETER;
            exception = e;
        } catch (final BadPaddingException e) {
            errCode = BAD_PADDING;
            exception = e;
        } catch (final IllegalBlockSizeException e) {
            errCode = INVALID_BLOCK_SIZE;
            exception = e;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG + methodName,
                errCode,
                exception
        );

        throw clientException;
    }

    @Override
    public String decrypt(@NonNull final Cipher cipher,
                          @NonNull final String ciphertext) throws ClientException {
        return new String(decrypt(cipher, Base64.decode(ciphertext, Base64.NO_PADDING | Base64.NO_WRAP)), UTF8);
    }

    @Override
    public byte[] decrypt(@NonNull Cipher cipher, byte[] ciphertext) throws ClientException {
        String errCode;
        Exception exception;
        final String methodName = ":decrypt";
        try {
            // Load our key material
            final KeyStore.PrivateKeyEntry privateKeyEntry = mKeyManager.getEntry();

            // Get a reference to our private key (will not be loaded into app process)
            final PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            // Init our cipher instance, don't use a named provider as there seems to be a mix of
            // BoringSSL & AndroidOpenSSL
            // https://issuetracker.google.com/issues/37091211
            final javax.crypto.Cipher outputCipher = javax.crypto.Cipher.getInstance(cipher.toString());
            if (cipher.getParameters() != null) {
                outputCipher.init(javax.crypto.Cipher.DECRYPT_MODE, privateKey, cipher.getParameters());
            } else {
                outputCipher.init(javax.crypto.Cipher.DECRYPT_MODE, privateKey);
            }
            return outputCipher.doFinal(ciphertext);
        } catch (final NoSuchAlgorithmException e) {
            errCode = NO_SUCH_ALGORITHM;
            exception = e;
        } catch (final InvalidKeyException e) {
            errCode = INVALID_KEY;
            exception = e;
        } catch (final UnrecoverableEntryException e) {
            errCode = INVALID_PROTECTION_PARAMS;
            exception = e;
        } catch (final NoSuchPaddingException e) {
            errCode = NO_SUCH_ALGORITHM;
            exception = e;
        } catch (final KeyStoreException e) {
            errCode = KEYSTORE_NOT_INITIALIZED;
            exception = e;
        } catch (final BadPaddingException e) {
            errCode = BAD_PADDING;
            exception = e;
        } catch (final IllegalBlockSizeException e) {
            errCode = INVALID_BLOCK_SIZE;
            exception = e;
        } catch (final InvalidAlgorithmParameterException e) {
            errCode = INVALID_ALG_PARAMETER;
            exception = e;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG + methodName,
                errCode,
                exception
        );

        throw clientException;
    }

    @Override
    public SecureHardwareState getSecureHardwareState() throws ClientException {
        final String errCode;
        final Exception exception;

        try {
            final KeyPair rsaKeyPair = getKeyPairForEntry(mKeyManager.getEntry());
            return getSecureHardwareState(rsaKeyPair);
        } catch (final KeyStoreException e) {
            errCode = KEYSTORE_NOT_INITIALIZED;
            exception = e;
        } catch (final NoSuchAlgorithmException e) {
            errCode = NO_SUCH_ALGORITHM;
            exception = e;
        } catch (final UnrecoverableEntryException e) {
            errCode = INVALID_PROTECTION_PARAMS;
            exception = e;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG + ":getSecureHardwareState",
                errCode,
                exception
        );

        throw clientException;
    }

    protected abstract SecureHardwareState getSecureHardwareState(@NonNull final KeyPair kp);

    @Override
    public @NonNull
    String getPublicKey(@NonNull final PublicKeyFormat format) throws ClientException {
        final String methodName = ":getPublicKey";

        switch (format) {
            case X_509_SubjectPublicKeyInfo_ASN_1:
                return getX509SubjectPublicKeyInfo();
            case JWK:
                return getJwkPublicKey();
            default:
                final String errMsg = "Unrecognized or unsupported key format: " + format;
                final ClientException clientException = new ClientException(
                        UNKNOWN_EXPORT_FORMAT,
                        errMsg
                );

                Logger.error(
                        TAG + methodName,
                        errMsg,
                        clientException
                );

                throw clientException;
        }
    }

    @Override
    public Certificate[] getCertificateChain() throws ClientException {
        return mKeyManager.getCertificateChain();
    }

    private @NonNull
    String getJwkPublicKey() throws ClientException {
        final Exception exception;
        final String errCode;

        try {
            final Map<String, Object> jwkMap = getDevicePopJwkMinifiedJson();
            return GSON.toJson(jwkMap.get(SignedHttpRequestJwtClaims.JWK), MAP_STRING_STRING_TYPE);
        } catch (final UnrecoverableEntryException e) {
            exception = e;
            errCode = INVALID_PROTECTION_PARAMS;
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final KeyStoreException e) {
            exception = e;
            errCode = KEYSTORE_NOT_INITIALIZED;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG,
                clientException.getMessage(),
                clientException
        );

        throw clientException;
    }

    private String getX509SubjectPublicKeyInfo() throws ClientException {
        final Exception exception;
        final String errCode;

        try {
            final KeyStore.PrivateKeyEntry keyEntry = mKeyManager.getEntry();
            final KeyPair rsaKeyPair = getKeyPairForEntry(keyEntry);
            final PublicKey publicKey = rsaKeyPair.getPublic();
            final byte[] publicKeybytes = publicKey.getEncoded();
            final byte[] bytesBase64Encoded = Base64.encode(publicKeybytes, Base64.DEFAULT);
            return new String(bytesBase64Encoded, AuthenticationConstants.CHARSET_UTF8);
        } catch (final KeyStoreException e) {
            exception = e;
            errCode = KEYSTORE_NOT_INITIALIZED;
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final UnrecoverableEntryException e) {
            exception = e;
            errCode = INVALID_PROTECTION_PARAMS;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG,
                clientException.getMessage(),
                clientException
        );

        throw clientException;
    }

    @Override
    public String mintSignedAccessToken(@Nullable final String httpMethod,
                                        final long timestamp,
                                        @NonNull final URL requestUrl,
                                        @NonNull final String accessToken,
                                        @Nullable final String nonce) throws ClientException {
        return mintSignedAccessToken(
                httpMethod,
                timestamp,
                requestUrl,
                accessToken,
                nonce,
                null
        );
    }

    @Override
    public String mintSignedAccessToken(@Nullable String httpMethod,
                                        final long timestamp,
                                        @NonNull final URL requestUrl,
                                        @NonNull final String accessToken,
                                        @Nullable final String nonce,
                                        @Nullable final String clientClaims) throws ClientException {
        return mintSignedHttpRequestInternal(
                httpMethod,
                timestamp,
                requestUrl,
                accessToken,
                nonce,
                clientClaims
        );
    }

    @Override
    public String mintSignedHttpRequest(@Nullable final String httpMethod,
                                        final long timestamp,
                                        @NonNull final URL requestUrl,
                                        @Nullable final String nonce,
                                        @Nullable final String clientClaims) throws ClientException {
        return mintSignedHttpRequestInternal(
                httpMethod,
                timestamp,
                requestUrl,
                null, // No AT used in this flow (generateShr)
                nonce,
                clientClaims
        );
    }

    private String mintSignedHttpRequestInternal(@Nullable final String httpMethod,
                                                 final long timestamp,
                                                 @NonNull final URL requestUrl,
                                                 @Nullable final String accessToken,
                                                 @Nullable final String nonce,
                                                 @Nullable final String clientClaims) throws ClientException {
        final Exception exception;
        final String errCode;

        try {
            final JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();

            // This is supported/allowed only to support the generateShr API. By definition, all
            // AT/PoP requests will contain an access token, but an SPO signed-cookie will not.
            if (!StringUtil.isNullOrEmpty(accessToken)) {
                claimsBuilder.claim(
                        SignedHttpRequestJwtClaims.ACCESS_TOKEN,
                        accessToken
                );
            }

            claimsBuilder.claim(
                    SignedHttpRequestJwtClaims.TIMESTAMP,
                    timestamp
            );
            claimsBuilder.claim(
                    SignedHttpRequestJwtClaims.HTTP_HOST,
                    // Use Authority to include port number, if supplied
                    requestUrl.getAuthority()
            );
            claimsBuilder.claim(
                    SignedHttpRequestJwtClaims.CNF,
                    getDevicePopJwkMinifiedJson()
            );

            if (!StringUtil.isNullOrEmpty(requestUrl.getPath())) {
                claimsBuilder.claim(
                        SignedHttpRequestJwtClaims.HTTP_PATH,
                        requestUrl.getPath()
                );
            }

            if (!StringUtil.isNullOrEmpty(httpMethod)) {
                claimsBuilder.claim(
                        SignedHttpRequestJwtClaims.HTTP_METHOD,
                        httpMethod
                );
            }

            if (!StringUtil.isNullOrEmpty(nonce)) {
                claimsBuilder.claim(
                        SignedHttpRequestJwtClaims.NONCE,
                        nonce
                );
            }

            if (!StringUtil.isNullOrEmpty(clientClaims)) {
                claimsBuilder.claim(
                        SignedHttpRequestJwtClaims.CLIENT_CLAIMS,
                        clientClaims
                );
            }

            final JWTClaimsSet claimsSet = claimsBuilder.build();

            final KeyStore.PrivateKeyEntry entry = mKeyManager.getEntry();
            final PrivateKey privateKey = entry.getPrivateKey();
            final RSASSASigner signer = new RSASSASigner(privateKey);

            final SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(getAsymmetricKeyThumbprint())
                            .build(),
                    claimsSet
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (final NoSuchAlgorithmException e) {
            exception = e;
            errCode = NO_SUCH_ALGORITHM;
        } catch (final KeyStoreException e) {
            exception = e;
            errCode = KEYSTORE_NOT_INITIALIZED;
        } catch (final JOSEException e) {
            exception = e;
            errCode = JWT_SIGNING_FAILURE;
        } catch (final UnrecoverableEntryException e) {
            exception = e;
            errCode = INVALID_PROTECTION_PARAMS;
        }

        final ClientException clientException = new ClientException(
                errCode,
                exception.getMessage(),
                exception
        );

        Logger.error(
                TAG,
                clientException.getMessage(),
                clientException
        );

        throw clientException;
    }

    //region Internal Functions

    /**
     * Gets the current time as a {@link Date}.
     *
     * @param calendar The {@link Calendar} implementation to use.
     * @return The current time.
     */
    private static Date getNow(@NonNull final Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * For the supplied {@link KeyStore.Entry}, get a corresponding {@link KeyPair} instance.
     *
     * @param entry The Keystore.Entry to use.
     * @return The resulting KeyPair.
     */
    private static KeyPair getKeyPairForEntry(@NonNull final KeyStore.PrivateKeyEntry entry) {
        final PrivateKey privateKey = entry.getPrivateKey();
        final PublicKey publicKey = entry.getCertificate().getPublicKey();
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * @return the public key for this particular keyStore entry.
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public PublicKey getPublicKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        final KeyStore.PrivateKeyEntry keyEntry = mKeyManager.getEntry();
        return keyEntry.getCertificate().getPublicKey();
    }

    /**
     * Gets the corresponding {@link RSAKey} for the supplied {@link KeyPair}.
     *
     * @param keyPair The KeyPair to use.
     * @return The resulting RSAKey.
     */
    private static RSAKey getRsaKeyForKeyPair(@NonNull final KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyUse(null)
                .build();
    }

    /**
     * Gets the base64url encoded public jwk for the supplied RSAKey.
     *
     * @param rsaKey The input key material.
     * @return The base64url encoded jwk.
     */
    private static String getReqCnfForRsaKey(@NonNull final RSAKey rsaKey)
            throws JOSEException, JSONException {
        final String thumbprintStr = getThumbprintForRsaKey(rsaKey);
        final String thumbprintMinifiedJson =
                new JSONObject()
                        .put("kid", thumbprintStr)
                        .toString();

        return base64UrlEncode(thumbprintMinifiedJson);
    }

    private static String getThumbprintForRsaKey(@NonNull RSAKey rsaKey) throws JOSEException {
        final Base64URL thumbprint = rsaKey.computeThumbprint();
        return thumbprint.toString();
    }

    /**
     * Encodes a String with Base64Url and no padding.
     *
     * @param input String to be encoded.
     * @return Encoded result from input.
     */
    private static String base64UrlEncode(@NonNull final String input) {
        byte[] encodeBytes = input.getBytes(UTF8);
        return Base64.encodeToString(
                encodeBytes,
                Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE
        );
    }

    /**
     * Returns the cnf claim used in SHRs (Signed HTTP Requests); format is JSON.
     *
     * @return The cnf claim value.
     * @throws UnrecoverableEntryException If the queried key cannot be found.
     * @throws NoSuchAlgorithmException    If the KeyStore is unable to use the designated alg.
     * @throws KeyStoreException           If the KeyStore experiences an error during read.
     */
    private Map<String, Object> getDevicePopJwkMinifiedJson()
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        final KeyStore.PrivateKeyEntry keyEntry = mKeyManager.getEntry();
        final KeyPair rsaKeyPair = getKeyPairForEntry(keyEntry);
        final RSAKey rsaKey = getRsaKeyForKeyPair(rsaKeyPair);
        final RSAKey publicRsaKey = rsaKey.toPublicJWK();
        final Map<String, Object> jwkContents = publicRsaKey.toJSONObject();
        final Map<String, Object> wrappedJwk = new HashMap<>();
        wrappedJwk.put(SignedHttpRequestJwtClaims.JWK, jwkContents);

        return wrappedJwk;
    }
}