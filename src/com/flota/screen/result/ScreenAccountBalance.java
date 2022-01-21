package com.flota.screen.result;

import com.flota.screen.result.base.ScreenResult;
import com.flota.screen.utils.ScreenToolbar;
import com.wposs.flota.R;

public class ScreenAccountBalance extends ScreenResult {

    private long accountBalance;

    public ScreenAccountBalance(int timeout) {
        super(timeout, null);
    }

    public ScreenAccountBalance(int timeout, ScreenToolbar sToolbar) {
        super(timeout, sToolbar);
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public ScreenAccountBalance setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.screen_result_account_balance;
    }

    @Override
    public TypeScreen getTypeScreen() {
        return TypeScreen.ACCOUNT_BALANCE;
    }
}
