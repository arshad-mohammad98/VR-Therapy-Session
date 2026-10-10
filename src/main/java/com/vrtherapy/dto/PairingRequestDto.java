package com.vrtherapy.dto;


public class PairingRequestDto {

    private String pairingCode;

    private String clientIdentifier;

    public PairingRequestDto() {
    }

    public PairingRequestDto(String pairingCode, String clientIdentifier) {
        this.pairingCode = pairingCode;
        this.clientIdentifier = clientIdentifier;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    public void setPairingCode(String pairingCode) {
        this.pairingCode = pairingCode;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }
}
