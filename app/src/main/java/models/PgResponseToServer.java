package models;

/**
 * Created by haseeb on 19/11/15.
 */
public class PgResponseToServer {
    int transaction;
    Boolean payment_success;
    String payment_id;
    String payment_transaction_id;
    String pg_transaction_id;
    String transaction_ref_id;
    String status;
    String payment_gateway;
    String amount;
    String payment_mode;
    int payment_trial_no;
    String currency;
    String payment_time;
    String whole_response;
    String error_message;
    String zapcash_used;
    String zapcash_transId;





    public PgResponseToServer() {}
    public PgResponseToServer(int transaction,
                              Boolean payment_success,
                              String payment_id,
                              String payment_transaction_id,
                              String pg_transaction_id,
                              String transaction_ref_id,
                              String status,
                              String payment_gateway,
                              String amount,
                              String payment_mode,
                              int payment_trial_no,
                              String currency,
                              String payment_time,
                              String whole_response,
                              String error_message,
                              String zapcash_used,
                              String zapcash_transId) {
        this.transaction = transaction;
        this.payment_success = payment_success;
        this.payment_id = payment_id;
        this.payment_transaction_id = payment_transaction_id;
        this.pg_transaction_id = pg_transaction_id;
        this.transaction_ref_id = transaction_ref_id;
        this.status = status;
        this.payment_gateway = payment_gateway;
        this.amount = amount;
        this.payment_mode = payment_mode;
        this.payment_trial_no = payment_trial_no;
        this.currency = currency;
        this.payment_time = payment_time;
        this.whole_response = whole_response;
        this.error_message = error_message;
        this.zapcash_used = zapcash_used;
        this.zapcash_transId = zapcash_transId;

    }


}
