package io.componenttesting.testmanager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Minimal OpenID Connect provider for component tests.
 */
public final class AuthorizationServerWireMock {

    private static final String JWKS_PATH = "/jwks";

    private static final String OPENID_CONFIGURATION_PATH =
            "/.well-known/openid-configuration";

    private static final RSAKey SIGNING_KEY = createSigningKey();

    private static final WireMockServer SERVER =
            new WireMockServer(
                    WireMockConfiguration.options().dynamicPort());

    private AuthorizationServerWireMock() {
    }

    public static synchronized void start() {
        if (SERVER.isRunning()) {
            return;
        }

        SERVER.start();

        stubOpenIdConfiguration();
        stubJwksEndpoint();
    }

    public static synchronized void stop() {
        if (SERVER.isRunning()) {
            SERVER.stop();
        }
    }

    public static String issuerUri() {
        start();
        return SERVER.baseUrl();
    }

    public static String jwkSetUri() {
        start();
        return SERVER.baseUrl() + JWKS_PATH;
    }

    public static String generateJwtToken(String... scopes) {
        try {
            Instant now = Instant.now();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .type(JOSEObjectType.JWT)
                            .keyID(SIGNING_KEY.getKeyID())
                            .build(),
                    new JWTClaimsSet.Builder()
                            .issuer(issuerUri())
                            .subject("component-test-user")
                            .issueTime(Date.from(now))
                            .expirationTime(Date.from(now.plusSeconds(300)))
                            .claim("scope", String.join(" ", scopes))
                            .claim("roles", Arrays.asList(scopes))
                            .build());

            jwt.sign(
                    new RSASSASigner(
                            SIGNING_KEY.toPrivateKey()));

            return jwt.serialize();

        } catch (JOSEException exception) {
            throw new IllegalStateException(
                    "Unable to create a test JWT",
                    exception);
        }
    }

    private static void stubOpenIdConfiguration() {
        String issuer = SERVER.baseUrl();

        SERVER.stubFor(
                get(urlEqualTo(OPENID_CONFIGURATION_PATH))
                        .willReturn(
                                okJson(
                                        """
                                        {
                                          "issuer": "%s",
                                          "jwks_uri": "%s%s",
                                          "response_types_supported": ["token"],
                                          "subject_types_supported": ["public"],
                                          "id_token_signing_alg_values_supported": ["RS256"]
                                        }
                                        """
                                                .formatted(
                                                        issuer,
                                                        issuer,
                                                        JWKS_PATH))));
    }

    private static void stubJwksEndpoint() {
        JWKSet jwkSet =
                new JWKSet(
                        SIGNING_KEY.toPublicJWK());

        SERVER.stubFor(
                get(urlEqualTo(JWKS_PATH))
                        .willReturn(
                                okJson(jwkSet.toString())));
    }

    private static RSAKey createSigningKey() {
        try {
            return new RSAKeyGenerator(2048)
                    .keyID("component-test-key")
                    .generate();

        } catch (JOSEException exception) {
            throw new IllegalStateException(
                    "Unable to create signing key",
                    exception);
        }
    }
}