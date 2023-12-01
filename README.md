# MarketplaceOnStatefun

MarketplaceOnStatefun is the Statefun port of Online Marketplace, the application prescribed as part of a microservice-based
benchmark of same name being designed by the [Data Management Systems (DMS) group](https://di.ku.dk/english/research/sdps/research-groups/dms/) at the University of Copenhagen.
Further details about the benchmark can be found [here](https://github.com/diku-dk/EventBenchmark).

## Table of Contents
- [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [New Statefun Users](#statefun)
    * [Docker Basic](#docker-basic)
    * [Docker Advanced](#docker-advanced)
- [Online Marketplace](#running-benchmark)
    * [Marketplace APIs](#apis)
    * [Play Around](#play)


## <a name="getting-started"></a>Getting Started

### <a name="prerequisites"></a>Prerequisites

- Docker
- Docker Compose
- JDK 8 (if you want to modify the source code)
- Curl (if you want to play with the APIs)

### <a name="statefun"></a>New Statefun Users

[Statefun](https://github.com/apache/flink-statefun) provides a runtime to program functions that necessitate managing state as part of their execution. It is built on top of Apache Flink and inherits all the benefits brought about by the stream processing engine.
We highly recommend starting from the [Statefun documentation](https://nightlies.apache.org/flink/flink-statefun-docs-master/) and project examples, that can be found in the [Stateful Functions Playground repository](https://github.com/apache/flink-statefun-playground).

### <a name="docker"></a>Docker basic

This port runs based on the project [Statefun Playground](https://github.com/apache/flink-statefun-playground). This decision is made to facilitate the packaging of dependencies, the deployment scheme, and the submission of workload and collection of performance metrics through HTTP endpoints.

If you are interested on deploying MarketplaceOnStatefun for reproducing an experiment, please refer to the next [block](#docker-advanced).

To get up MarketplaceOnStatefun up and running, run the following commands in the project's root folder:

```
docker-compose build
```

```
docker-compose up
```

### <a name="docker-advanced"></a>Docker Advanced

The original [statefun-playground](https://hub.docker.com/r/apache/flink-statefun-playground/) Docker image runs with default Flink parameters, which can lead to performance issues. To circumvent this shortcoming, we suggest advanced users two options:

(a) Modify Flink configuration and generate a custom image from the [source code](https://github.com/apache/flink-statefun-playground/tree/main/playground-internal/statefun-playground-entrypoint) 

Flink configuration can be modified in the method createDefaultLocalEnvironmentFlinkConfiguration found in the class [LocalEnvironmentEntryPoint](flink-statefun-playground/playground-internal/statefun-playground-entrypoint/src/main/java/org/apache/flink/statefun/playground/internal)

An example configuration is provided below.

```
final Configuration flinkConfiguration = new Configuration();
flinkConfiguration.set(StateBackendOptions.STATE_BACKEND, "hashmap");
flinkConfiguration.set(StateBackendOptions.LATENCY_TRACK_ENABLED, false);

// task slots >= parallelism
ConfigOption<Integer> NUM_TASK_SLOTS = ConfigOptions.key("taskmanager.numberOfTaskSlots").intType().defaultValue(1);
flinkConfiguration.set(NUM_TASK_SLOTS, 6);

ConfigOption<Integer> PAR_DEFAULT = ConfigOptions.key("parallelism.default").intType().defaultValue(1);
flinkConfiguration.set(PAR_DEFAULT, 6);

ConfigOption<Integer> ASYNC_MAX_DEFAULT = ConfigOptions.key("statefun.async.max-per-task").intType().defaultValue(1024);
flinkConfiguration.set(ASYNC_MAX_DEFAULT, 16000);

return flinkConfiguration;
```

(b) Overwrite the [Flink parameters](https://github.com/apache/flink-statefun-playground/blob/main/playground-internal/statefun-playground-entrypoint/README.md).

By opting for (a) or (b), then proceed as follows:

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

## <a name="running-benchmark"></a>Online Marketplace On Statefun

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

There are two ways we can update a <b>product</b>: updating the price or overwriting a product.

To submit a <b>price update</b>, a user has to send the following request

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/UpdatePrice" -d '{ "sellerId" : 1, "productId" : 1, "price" : 100, "instanceId" : 1 }' localhost:8090/marketplace/product/1-1
```

That will lead to the following result

```
{ "tid" : "1", "type" : "PRICE_UPDATE", "actorId" : 1, "status" : "SUCCESS", "source" : "product"}
```

Querying the product again, we can see the updated price

```
Product{product_id=1, seller_id=1, name='productTest', sku='sku', category='categoryTest', description='descriptionTest', price=100.0, freight_value=0.0, status='approved', version='0', createdAt=2023-10-25T14:45:22.388, updatedAt=2023-10-25T14:45:40.637}
```

To overwrite a product, the client just needs to send a UpsertProduct payload like shown before. 

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

#### <a name="order"></a>Order Management

Customers can query their submitted order and understand their status
```
curl -X PUT -H "Content-Type: application/vnd.marketplace/GetOrders" -d '{}' localhost:8090/marketplace/order/1
```

That will result in the following payload
```
Order{id=3, customerId=1, status=READY_FOR_SHIPMENT, invoiceNumber='1-2023-10-25T14:34:40.595-3', purchaseTimestamp=2023-10-25T14:34:40.530, created_at=2023-10-25T14:34:40.595, updated_at=2023-10-25T14:34:40.778, paymentDate=null, delivered_carrier_date=null, delivered_customer_date=null, countItems=1, totalAmount=30.0, totalFreight=0.0, totalIncentive=0.0, totalInvoice=30.0, totalItems=30.0, data=''}
```

#### <a name="seller"></a>Seller Management

In a marketplace, sellers play a central role by offering varied products to customers. We can check how well a seller is doing by getting an overview of sales. *The results will be provided in binary format that requires being properly deserialized by the client.

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/QueryDashboard" -d '{"tid" : 1}' localhost:8090/marketplace/seller/1
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

It may be the case that the delivery company informs that some of the packages are delivered. They do it through the following API:

```
curl -X PUT -H "Content-Type: application/vnd.marketplace/UpdateShipment" -d '{"tid" : 1}' localhost:8090/marketplace/shipmentProxy/1
```

That will result in the output like it follows
```
{ "tid" : "1", "type" : "UPDATE_DELIVERY", "actorId" : 1, "status" : "SUCCESS", "source" : "shipment"}
```

After we update the open orders to delivered, we can see results are no longer shown in the seller dashboard

### <a name="play"></a>Play Around!

You can modify the source code to add new functionality. For instance, you can try adding stock to some item.

After modifying the code, you can perform a hot deploy by running the following command (make sure statefun-playground container has not been stopped):
```
docker-compose up -d --build marketplace
```


