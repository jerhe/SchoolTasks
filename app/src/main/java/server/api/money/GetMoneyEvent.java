package server.api.money;

import server.api.token.BaseTokenEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class GetMoneyEvent extends BaseTokenEvent {
    String money;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
