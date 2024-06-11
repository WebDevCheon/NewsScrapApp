package com.myapp.backend.dto;

import java.io.Serializable;
import java.util.Map;

public class GoogleReponse implements OAuth2Response, Serializable {
    private final Map<String, Object> attribute;

    public GoogleReponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public Map<String, Object> getAttribute() {
        return attribute;
    }
}
