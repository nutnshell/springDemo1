package com.example.demo1.service;

import com.example.demo1.util.Helper;
import com.example.demo1.util.PaymentSerializer;
import com.google.gson.GsonBuilder;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentRetrieveParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentsService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsService.class);

    public final Environment env;

    public PaymentsService(Environment env) {
        this.env = env;
    }

    public void getPaymentByIntentId(String id) throws StripeException {
        logger.info("Started : Preparing to extract Payment Intent from Stripe.");

        Stripe.apiKey = env.getProperty("stripe.key");
        PaymentIntentRetrieveParams params = PaymentIntentRetrieveParams.builder().
                addExpand("charges.data.balance_transaction").build();

        PaymentIntent pi =  PaymentIntent.retrieve(id,params,null);
        String jsonResponse = processPaymentIntentResponse(Arrays.asList(pi));

        logger.info("Complete : Received Payment Intent from Stripe." +
                "\n Payment Intent Id: " +id);

        try {
            Helper.writeJSONToFile(jsonResponse, null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Serializes Payment Intent to DIYPayments
     */
    private String processPaymentIntentResponse(List<PaymentIntent> response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PaymentIntent.class, new PaymentSerializer())
                .serializeNulls()
                .setPrettyPrinting();

        return gsonBuilder.create().toJson(response);
    }
}
