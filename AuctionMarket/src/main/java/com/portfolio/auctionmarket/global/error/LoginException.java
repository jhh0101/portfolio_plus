package com.portfolio.auctionmarket.global.error;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
