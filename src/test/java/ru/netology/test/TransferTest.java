package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.*;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DataHelper.impossibleTransfer;
import static ru.netology.data.DataHelper.getTransfer;

public class TransferTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = true;
    }

    @AfterEach
    void clear() {
        clearBrowserCookies();
        clearBrowserLocalStorage();
    }

    DashboardPage login() {
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        return verificationPage.validVerify(verificationCode);
    }

    void balancesEquals(DashboardPage dashboardPage) {
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        if (firstCardBalance < secondCardBalance) {
            int transfer = (firstCardBalance + secondCardBalance) / 2 - firstCardBalance;
            dashboardPage.pressReplCard(0)
                    .replCardBalance(transfer, DataHelper.getSecondCardNumber());
        }
        if (firstCardBalance > secondCardBalance) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - secondCardBalance;
            dashboardPage.pressReplCard(1)
                    .replCardBalance(alignTransfer, DataHelper.getFirstCardNumber());
        }
    }

    @Test
    void shouldTransferFromFirstToSecondCard() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = getTransfer(firstCardBalance);
        dashboardPage.pressReplCard(1)
                .replCardBalance(transfer, DataHelper.getFirstCardNumber());
        assertEquals(firstCardBalance - transfer, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance + transfer, dashboardPage.extractBalance(String.valueOf(1)));
    }

    @Test
    void shouldNotTransferFromFirstCardIfCanceled() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = getTransfer(firstCardBalance);
        dashboardPage.pressReplCard(1)
                .replCardCancel(transfer, DataHelper.getFirstCardNumber());
        assertEquals(firstCardBalance, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance, dashboardPage.extractBalance(String.valueOf(1)));
    }

    @Test
    void shouldTransferFromSecondToFirstCard() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = getTransfer(secondCardBalance);
        dashboardPage.pressReplCard(0)
                .replCardBalance(transfer, DataHelper.getSecondCardNumber());
        assertEquals(firstCardBalance + transfer, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance - transfer, dashboardPage.extractBalance(String.valueOf(1)));
    }

    @Test
    void shouldNotTransferFromSecondCardIfCancelled() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = getTransfer(secondCardBalance);
        dashboardPage.pressReplCard(0)
                .replCardCancel(transfer, DataHelper.getSecondCardNumber());
        assertEquals(firstCardBalance, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance, dashboardPage.extractBalance(String.valueOf(1)));
    }

    @Test
    void shouldNotTransferFirstToSecondIfImpossibleTransfer() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = impossibleTransfer(firstCardBalance);
        dashboardPage.pressReplCard(1)
                .replCardBalance(transfer, DataHelper.getFirstCardNumber());
        assertEquals(firstCardBalance, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance, dashboardPage.extractBalance(String.valueOf(1)));
    }

    @Test
    void shouldNotTransferSecondToFirstIfImpossibleTransfer() {
        DashboardPage dashboardPage = login();
        balancesEquals(dashboardPage);
        var firstCardBalance = dashboardPage.extractBalance(String.valueOf(0));
        var secondCardBalance = dashboardPage.extractBalance(String.valueOf(1));
        int transfer = impossibleTransfer(secondCardBalance);
        dashboardPage.pressReplCard(0)
                .replCardBalance(transfer, DataHelper.getSecondCardNumber());
        assertEquals(firstCardBalance, dashboardPage.extractBalance(String.valueOf(0)));
        assertEquals(secondCardBalance, dashboardPage.extractBalance(String.valueOf(1)));
    }

}
