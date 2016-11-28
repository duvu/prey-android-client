package com.prey.activities.bt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.braintreepayments.api.AndroidPay;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.TokenizationParametersListener;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.WalletConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.prey.PreyLogger;
import com.prey.R;
import com.prey.activities.LoginActivity;
import com.prey.activities.WelcomeBatchActivity;

import cz.msebera.android.httpclient.Header;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.ThreeDSecure;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.AndroidPayCardNonce;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.braintreepayments.api.models.VenmoAccountNonce;

import java.util.Collection;
import java.util.Collections;


/**
 * Created by oso on 24-11-16.
 */

public class MainActivity  extends Activity implements PaymentMethodNonceCreatedListener,
        BraintreeCancelListener, BraintreeErrorListener {
    private String mAuthorization = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJjYzA3YmQ0ZTg5YmUzZGIwMWNhY2U5Mjk1YzczYjU4NTMzYTI5MGJlMzQ5ZjIzMmU0MjhhZDgyMzgxMmUzZjMxfGNyZWF0ZWRfYXQ9MjAxNi0xMS0yN1QxOToxNDoxOS44NDQ0Nzk2MjYrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=";
    private BraintreeFragment mBraintreeFragment;


    static final String EXTRA_PAYMENT_METHOD_NONCE = "payment_method_nonce";
    static final String EXTRA_DEVICE_DATA = "device_data";
    static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";
    static final String EXTRA_ANDROID_PAY_CART = "android_pay_cart";

    private static final int DROP_IN_REQUEST = 100;
    private static final int CUSTOM_REQUEST = 200;
    private static final int PAYPAL_REQUEST = 300;

    private static final String KEY_NONCE = "nonce";


    private Button mDropInButton;
    private Button mPayPalButton;
    private Button mCustomButton;


    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        mPayPalButton = (Button) findViewById(R.id.paypal);
        mDropInButton = (Button) findViewById(R.id.drop_in);
        mCustomButton = (Button) findViewById(R.id.custom);
/*
        try {



            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
            // mBraintreeFragment is ready to use!
            mBraintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception e) {
                    PreyLogger.i("onError:"+e.getMessage());
                }
            });
            CardBuilder cardBuilder = new CardBuilder()
                    .cardNumber("411111111111")
                    .expirationDate("08/20")
                    .validate(true);

            Card.tokenize(mBraintreeFragment, cardBuilder);





            AndroidPay.isReadyToPay(mBraintreeFragment, new BraintreeResponseListener<Boolean>() {
                @Override
                public void onResponse(Boolean isReadyToPay) {
                    PreyLogger.i("onResponse:"+isReadyToPay);
                }
            });

        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
            PreyLogger.i("error:"+e.getMessage());
        }
*/
    }


    public void launchDropIn(View v) {

        startActivityForResult(getDropInRequest().getIntent(this), DROP_IN_REQUEST);
    }

    public void launchPayPal(View v) {
        Intent intent = new Intent(this, PayPalActivity.class)
                .putExtra(EXTRA_COLLECT_DEVICE_DATA, Settings.shouldCollectDeviceData(this));
        startActivityForResult(intent, PAYPAL_REQUEST);
    }

    int REQUEST_CODE=2001;
    String TOKENIZATION_KEY="";

    public void launchCustom(View v) {


        AndroidPay.isReadyToPay(mBraintreeFragment, new BraintreeResponseListener<Boolean>() {
            @Override
            public void onResponse(Boolean isReadyToPay) {
                PreyLogger.i("isReadyToPay:"+isReadyToPay);
                if (isReadyToPay) {
                    // show Android Pay
                }
            }
        });


        AndroidPay.getTokenizationParameters(mBraintreeFragment, new TokenizationParametersListener() {
            @Override
            public void onResult(PaymentMethodTokenizationParameters parameters, Collection<Integer> allowedCardNetworks) {
                MaskedWalletRequest maskedWalletRequest = MaskedWalletRequest.newBuilder()
                 //       .setPaymentMethodTokenizationParameter(parameters)
                        .addAllowedCardNetworks(allowedCardNetworks)
                        .build();
            }
        });


        Cart cart = Cart.newBuilder()
                .setCurrencyCode("USD")
                .setTotalPrice("150.00")
                .addLineItem(LineItem.newBuilder()
                        .setCurrencyCode("USD")
                        .setDescription("Description")
                        .setQuantity("1")
                        .setUnitPrice("150.00")
                        .setTotalPrice("150.00")
                        .build())
                .build();
        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey(TOKENIZATION_KEY)
                .androidPayCart(cart);

        // REQUEST_CODE is arbitrary and is only used within this activity
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PreyLogger.i("onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            // send result.getPaymentMethodNonce().getNonce() to your server
        } else {
            // handle errors here, an error may be available in
            //Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
        }
    }


    private DropInRequest getDropInRequest() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(mAuthorization)
                .collectDeviceData(Settings.shouldCollectDeviceData(this))
                .androidPayCart(getAndroidPayCart())
                .androidPayShippingAddressRequired(Settings.isAndroidPayShippingAddressRequired(this))
                .androidPayPhoneNumberRequired(Settings.isAndroidPayPhoneNumberRequired(this))
                .androidPayAllowedCountriesForShipping(Settings.getAndroidPayAllowedCountriesForShipping(this));

        if (Settings.isPayPalAddressScopeRequested(this)) {
            dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
        }

        return dropInRequest;
    }

    private Cart getAndroidPayCart() {
        return Cart.newBuilder()
                .setCurrencyCode(Settings.getAndroidPayCurrency(this))
                .setTotalPrice("1.00")
                .addLineItem(LineItem.newBuilder()
                        .setCurrencyCode("USD")
                        .setDescription("Description")
                        .setQuantity("1")
                        .setUnitPrice("1.00")
                        .setTotalPrice("1.00")
                        .build())
                .build();
    }

    private void enableButtons(boolean enable) {
        mDropInButton.setEnabled(enable);
        mPayPalButton.setEnabled(enable);
        mCustomButton.setEnabled(enable);
    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

    }



    private void displayResult(PaymentMethodNonce paymentMethodNonce, String deviceData) {

    }
}