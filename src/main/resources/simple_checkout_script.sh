#!/bin/bash

curl -X PUT -H "Content-Type: application/vnd.marketplace/SetStockItem" -d '{"seller_id": "1", "product_id": "1", "qty_available" : 10, "qty_reserved" : 0, "order_count" : 0, "ytd": 0, "data" : "", "version": "0"}' localhost:8090/marketplace/stock/1-1

curl -X PUT -H "Content-Type: application/vnd.marketplace/SetCustomer" -d '{"id": 1, "first_name": "firstNameTest", "last_name" : "lastNameTest",  "address": "addressTest", "complement": "complementTest", "birth_date": "birthDateTest", "zip_code": "zipCodeTest", "city": "cityTest", "state": "stateTest", "card_number": "cardNumberTest", "card_security_number": "cardSecNumberTest", "card_expiration": "cardExpirationTest", "card_holder_name": "cardHolderNameTest", "card_type": "cardTypeTest", "data": ""}' localhost:8090/marketplace/customer/1

curl -X PUT -H "Content-Type: application/vnd.marketplace/AddCartItem" -d '{"SellerId": "1", "ProductId": "1", "ProductName" : "test", "UnitPrice" : 10, "FreightValue" : 0, "Quantity": 3, "Voucher" : 0, "Version": "0"}' localhost:8090/marketplace/cart/1

curl -X PUT -H "Content-Type: application/vnd.marketplace/CustomerCheckout" -d '{"CustomerId": 1, "FirstName": "customerTest", "LastName" : "test", "Street" : "test", "Complement" : "test", "City" : "test", "State" : "test", "ZipCode" : "test", "PaymentType" : "BOLETO", "CardNumber" : "test", "CardNumber" : "test", "CardHolderName" : "test", "CardExpiration" : "test", "CardSecurityNumber" : "test", "CardBrand" : "test", "Installments" : 1, "instanceId" : "1" }' localhost:8090/marketplace/cart/1