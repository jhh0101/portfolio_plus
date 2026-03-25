package org.example.auctioncommon.domain.seller.dto

class SellerApplyCommand(
    var storeName: String,
    var bankName: String,
    var accountNumber: String,
    var accountHolder: String
)