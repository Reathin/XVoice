package com.rair.xvoice.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * @author Rair
 * @date 2018/4/20
 * <p>
 * desc:
 */
public class Record {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String amount;
    private String content;
    private boolean isAccount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAccount() {
        return isAccount;
    }

    public void setAccount(boolean account) {
        isAccount = account;
    }
}
