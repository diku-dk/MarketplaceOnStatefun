# MarketplaceOnStatefun

```
This is an IN-PROGRESS work. 
Details about how to appropriately configure, deploy, and execute 
the application are being added progressively.
```

MarketplaceOnStatefun is the Statefun port of Marketplace, the application prescribed as part of a microservice-based
benchmark being designed by the [Data Management Systems (DMS) group](https://di.ku.dk/english/research/sdps/research-groups/dms/) at the University of Copenhagen.
Further details about the benchmark can be found [here](https://github.com/diku-dk/EventBenchmark).

## Table of Contents
- [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [New Statefun Users](#statefun)
    * [Docker Preliminaries](#docker)
    * [Marketplace APIs](#apis)
    * [Play Around](#play)
- [Running the Benchmark](#running-benchmark)

## <a name="getting-started"></a>Getting Started

### <a name="prerequisites"></a>Prerequisites

- Docker
- Docker Compose
- JDK 8 (if you want to modify the source code)
- Curl (if you want to play with the APIs)

### <a name="statefun"></a>New Statefun Users

[Statefun](https://github.com/apache/flink-statefun) provides a runtime to program functions that necessitate managing state as part of their execution. It is built on top of Apache Flink and inherits all the benefits brought about by the stream processing engine.
We highly recommend starting from the [Statefun documentation](https://nightlies.apache.org/flink/flink-statefun-docs-master/) and project examples, that can be found in the [Stateful Functions Playground repository](https://github.com/apache/flink-statefun-playground).

### <a name="docker"></a>Docker Preliminaries

This port runs based on the project [Statefun Playground](https://github.com/apache/flink-statefun-playground). This decision is made to facilitate the packaging of dependencies, the deployment scheme, and the submission of workload and collection of performance metrics through HTTP endpoints.

To get up MarketplaceOnStatefun up and running, run the following commands in the project's root folder:

```
docker-compose build
```

```
docker-compose up
```

The original [statefun-playground](https://hub.docker.com/r/apache/flink-statefun-playground/) Docker image runs with default Flink and JVM parameters, which can lead to performance issues. This way, we suggest advanced users the following:

(a) Generate a custom image from the [source code](https://github.com/apache/flink-statefun-playground/tree/main/playground-internal/statefun-playground-entrypoint) (make sure to at least increase MANAGED_MEMORY_SIZE param in the method createDefaultLocalEnvironmentFlinkConfiguration found in the class LocalEnvironmentEntryPoint.java)

(b) Overwrite the [Flink parameters](https://github.com/apache/flink-statefun-playground/blob/main/playground-internal/statefun-playground-entrypoint/README.md).

In case you prefer to modify statefun-playground source code, proceed as follows:

In the flink-statefun-playground root's folder, run:
```
docker build -t flink-statefun-playground-custom .
```

Then go to MarketplaceOnStatefun root's folder and run:
```
docker-compose -f docker-compose-custom.yml build
```

```
docker-compose -f docker-compose-custom.yml up
```

After these commands, the application is probably up and running.

### <a name="api"></a>Marketplace APIs

#### <a name="product"></a>Product Management

Let's start adding a <b>product</b> to the Marketplace
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/UpsertProduct" -d '{"seller_id": "1", "product_id": "1", "name" : "productTest", "sku" : "sku", "category" : "categoryTest", "description": "descriptionTest", "price" : 10, "freight_value" : 0, "version": "0"}' localhost:8090/marketplace/product/1-1
```

Let's send a GET request to verify whether the function have successfully stored the state
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetProduct" -d '{}' localhost:8090/marketplace/product/1-1
```

Now let's get the results of the requests sent so far:
```
curl -X GET localhost:8091/receipts
```

If everything worked out, you should see an output like it follows.

```
Product{product_id=1, seller_id=1, name='productTest', sku='sku', category='categoryTest', description='descriptionTest', price=10.0, freight_value=0.0, status='approved', version='0', createdAt=2023-10-24T14:46:57.656, updatedAt=2023-10-24T14:46:57.656}
```

#### <a name="stock"></a>Stock Management

Let's add now the corresponding <b>stock</b> for this product and repeat the steps above, now for stock
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/SetStockItem" -d '{"seller_id": "1", "product_id": "1", "qty_available" : 10, "qty_reserved" : 0, "order_count" : 0, "ytd": 0, "data" : "", "version": "0"}' localhost:8090/marketplace/stock/1-1
```

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetStockItem" -d '{}' localhost:8090/marketplace/stock/1-1
```

You should get a result like below
```
StockItem{product_id=1, seller_id=1, qty_available=10, qty_reserved=0, order_count=0, ytd=0, data='', version='0', createdAt=2023-10-24T14:43:19.741, updatedAt=2023-10-24T14:43:19.741}
```

#### <a name="customer"></a>Customer Management

Now let's move on to an actual customer interaction by adding an item to a cart and checking out afterwards.

First, let's add a customer to our marketplace
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/SetCustomer" -d '{"id": 1, "first_name": "firstNameTest", "last_name" : "lastNameTest",  "address": "addressTest", "complement": "complementTest", "birth_date": "birthDateTest", "zip_code": "zipCodeTest", "city": "cityTest", "state": "stateTest", "card_number": "cardNumberTest", "card_security_number": "cardSecNumberTest", "card_expiration": "cardExpirationTest", "card_holder_name": "cardHolderNameTest", "card_type": "cardTypeTest", "data": ""}' localhost:8090/marketplace/customer/1
```

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetCustomer" -d '{}' localhost:8090/marketplace/customer/1
```

After submitting the above commands, you may get a response like from receipts:
```
Customer{id=1, firstName='firstNameTest', lastName='lastNameTest', address='addressTest', complement='complementTest', birthDate='birthDateTest', zipCode='zipCodeTest', city='cityTest', state='stateTest', cardNumber='cardNumberTest', cardSecurityNumber='cardSecNumberTest', cardExpiration='cardExpirationTest', cardHolderName='cardHolderNameTest', cardType='cardTypeTest', data='', successPaymentCount=0, failedPaymentCount=0, deliveryCount=0}
```

#### <a name="cart"></a>Cart Management

Next, let's make sure we add a cart item that actually exists in the stock
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/AddCartItem" -d '{"SellerId": "1", "ProductId": "1", "ProductName" : "test", "UnitPrice" : 10, "FreightValue" : 0, "Quantity": 3, "Voucher" : 0, "Version": "0"}' localhost:8090/marketplace/cart/1
```

Again, to make sure our cart has successfully accounted for the cart add item, we can verify by sending a GET request to the cart function
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetCart" -d '{}' localhost:8090/marketplace/cart/1
```

That will give us an output like below (through the GET receipts request)
```
CartState{status=OPEN, items=CartItem{sellerId=1, productId=1, productName='test', unitPrice=10.0, freightValue=0.0, quantity=3, voucher=0.0, version='0'}, createdAt=2023-10-24T14:57:25.753, updatedAt=2023-10-24T14:57:25.753}
```

Let's <b>checkout</b> the customer cart with the following command:
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/CustomerCheckout" -d '{"CustomerId": 1, "FirstName": "customerTest", "LastName" : "test", "Street" : "test", "Complement" : "test", "City" : "test", "State" : "test", "ZipCode" : "test", "PaymentType" : "BOLETO", "CardNumber" : "test", "CardNumber" : "test", "CardHolderName" : "test", "CardExpiration" : "test", "CardSecurityNumber" : "test", "CardBrand" : "test", "Installments" : 1, "instanceId" : "1" }' localhost:8090/marketplace/cart/1
```

The result of the checkout can be verified in the receipts' endpoint and it will be something like below

```
{ "tid" : "1", "type" : "CUSTOMER_SESSION", "actorId" : 1, "status" : "SUCCESS", "source" : "shipment"}
```

#### <a name="shipment"></a>Shipment Management

To verify the pending shipments, that is, the orders that have not been delivered yet, you can query the shipment function

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetShipments" -d '{"customerId" : 1}' localhost:8090/marketplace/shipment/0
```

Resulting in something like below

```
Shipment{shipmentId=1, orderId=1, customerId=1, packageCount=1, totalFreight=0.0, firstName='customerTest', lastName='test', street='test', zipCode='test', status=APPROVED, city='test', state='test', requestDate=2023-10-25T10:14:09.729}
```

### <a name="play"></a>Play Around!

You can modify the source code to add new functionality. For instance, you can try adding stock to some item.

After modifying the code, you can perform a hot deploy by running the following command (make sure statefun-playground container has not been stopped):
```
docker-compose up -d --build marketplace
```

## <a name="getting-started"></a>Running the Benchmark

1. Make sure that MarketplaceOnStatefun is up and running
2. 

