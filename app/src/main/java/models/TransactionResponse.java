package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haseeb on 6/10/15.
 */
public class TransactionResponse {
    @SerializedName("transaction_id")
    @Expose
    private Integer transactionId;

    @SerializedName("error")
    @Expose
    private String error;

    /**
     *
     * @return
     * The transactionId
     */
    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     *
     * @param transactionId
     * The transaction_id
     */
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    /**
     *
     * @return
     * The transactionId
     */
    public String getError() {
        return error;
    }

    /**
     *
     * @param transactionId
     * The transaction_id
     */
    public void setError(String error) {
        this.error = error;
    }
}
