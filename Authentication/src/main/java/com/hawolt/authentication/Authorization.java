package com.hawolt.authentication;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created: 25/08/2023 11:46
 * Author: Twitter @hawolt
 **/

public class Authorization extends JSONObject {

    public Authorization(Builder builder) {
        put("acr_values", builder.acr == null ? "" : builder.acr);
        put("claims", builder.claims == null ? "" : builder.claims);
        put("client_id", builder.clientID.toString());
        put("code_challenge", builder.challenge == null ? "" : builder.challenge);
        put("code_challenge_method", builder.challenge == null ? "" : "S256");
        put("nonce", ICookieSupplier.generateClientNonce());
        put("redirect_uri", builder.redirectURI == null ? "" : builder.redirectURI);
        put("response_type", Arrays.stream(builder.responseTypes).map(ResponseType::toString).collect(Collectors.joining(" ")));
        put("scope", Arrays.stream(builder.scopes).map(ClientScope::toString).collect(Collectors.joining(" ")));
    }

    public static class Builder {

        private String acr, claims, challenge, redirectURI;
        private ResponseType[] responseTypes;
        private ClientScope[] scopes;
        private ClientID clientID;

        public Builder setACR(String acr) {
            this.acr = acr;
            return this;
        }

        public Builder setClaims(String claims) {
            this.claims = claims;
            return this;
        }

        public Builder setClientID(ClientID clientID) {
            this.clientID = clientID;
            return this;
        }

        public Builder setChallenge(String challenge) {
            this.challenge = challenge;
            return this;
        }

        public Builder setRedirectURI(String redirectURI) {
            this.redirectURI = redirectURI;
            return this;
        }

        public Builder setResponseTypes(ResponseType... responseType) {
            this.responseTypes = responseType;
            return this;
        }

        public Builder setScopes(ClientScope... scopes) {
            this.scopes = scopes;
            return this;
        }

        public Authorization build() {
            if (clientID == null || scopes == null || responseTypes == null) {
                throw new RuntimeException("Unable to initialize Authorization without clientID, ClientScope, or ResponseType");
            }
            return new Authorization(this);
        }
    }
}
