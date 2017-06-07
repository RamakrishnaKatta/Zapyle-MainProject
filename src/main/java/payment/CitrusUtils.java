package payment;

/**
 * Created by haseeb on 17/11/15.
 */
public class CitrusUtils {
    public enum PaymentType {
        LOAD_MONEY, CITRUS_CASH, PG_PAYMENT, DYNAMIC_PRICING
    }

    public enum DPRequestType {
        SEARCH_AND_APPLY, CALCULATE_PRICING, VALIDATE_RULE
    }
}
