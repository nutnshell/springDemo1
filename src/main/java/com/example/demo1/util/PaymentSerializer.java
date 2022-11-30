package com.example.demo1.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer
 */
public class PaymentSerializer implements JsonSerializer<PaymentIntent> {

    private final int LEGAL_ENTITY_CODE = 12222;
    private final int TAX_PRODUCT_CODE = 070410;
    private final String SHIPPING_CHARGES = "0.00$";

    @Override
    public JsonElement serialize(PaymentIntent src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jObject = new JsonObject();
        jObject.addProperty("legalEntityCode", LEGAL_ENTITY_CODE);
        jObject.addProperty("extractDate", getExtractDate());
        jObject.addProperty("customerName", src.getReceiptEmail());
        jObject.addProperty("customerNumber", src.getCustomer());
        //jObject.addProperty("amount", src.getAmount());

        if(!src.getCharges().getData().isEmpty()){
            Charge  charge= src.getCharges().getData().get(0);

            PaymentMethod.BillingDetails billingDetails = charge.getBillingDetails();
            jObject.addProperty("shipToAddress", billingDetails.getAddress().getLine1());
            jObject.addProperty("shipToCity", billingDetails.getAddress().getCity());
            jObject.addProperty("shipToState", billingDetails.getAddress().getState());
            jObject.addProperty("shipToPostalCode", billingDetails.getAddress().getPostalCode());
            jObject.addProperty("shipToCountry", billingDetails.getAddress().getCountry());
            jObject.addProperty("invoiceDate", convertToSimpleDate(charge.getCreated()));

            BalanceTransaction transaction = charge.getBalanceTransactionObject();
            if(transaction != null) {
                jObject.addProperty("invoiceTotalAmount", transaction.getAmount());
                jObject.addProperty("netPrice", transaction.getNet());
                jObject.addProperty("fee", transaction.getFee());
            }
        }
        jObject.addProperty("invoiceNumber", src.getMetadata().get("diy_order_id"));
        jObject.addProperty("glPeriod", getGlPeriod());
        jObject.addProperty("productNumber", src.getMetadata().get("diy_product_ids"));
        jObject.addProperty("productDescription", src.getDescription());
        jObject.addProperty("taxProductCode", TAX_PRODUCT_CODE);

        jObject.addProperty("currency", src.getCurrency().toUpperCase());
        jObject.addProperty("totalSalesTax", src.getMetadata().get("tax"));
        jObject.addProperty("shippingCharges", SHIPPING_CHARGES);

        //tax engine details
        jObject.addProperty("taxEngineState",  src.getMetadata().get("taxCode-MW1"));
        jObject.addProperty("taxEngineCounty",  src.getMetadata().get("taxCode-MW2"));
        jObject.addProperty("taxEngineCity",  src.getMetadata().get("taxCode-MW3"));
        jObject.addProperty("taxEngineDistrict",  src.getMetadata().get("taxCode-MW4"));

        return jObject;
    }
    public String convertToSimpleDate(Long input) {
        LocalDate simpleDate = Instant.ofEpochSecond(input).atZone(ZoneId.systemDefault()).toLocalDate();
        return simpleDate.toString();
    }
    public String getGlPeriod() {
        LocalDate date = LocalDate.now().minusMonths(1);
        return date.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    public String getExtractDate() {
        return (LocalDate.now()).toString();
    }
}