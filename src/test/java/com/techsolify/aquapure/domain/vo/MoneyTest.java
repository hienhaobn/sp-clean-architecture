package com.techsolify.aquapure.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.techsolify.aquapure.domain.exception.InvalidProductException;

class MoneyTest {

    @Test
    void shouldCreateValidMoney() {
        Money money = Money.of(BigDecimal.valueOf(100.00));
        assertEquals(BigDecimal.valueOf(100.00), money.getAmount());
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        assertThrows(InvalidProductException.class, () -> Money.of(BigDecimal.valueOf(-100.00)));
    }

    @Test
    void shouldThrowExceptionForZeroAmount() {
        assertThrows(InvalidProductException.class, () -> Money.of(BigDecimal.ZERO));
    }

    @Test
    void shouldAddMoneyCorrectly() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(50.00));

        Money result = money1.add(money2);
        assertEquals(BigDecimal.valueOf(150.00), result.getAmount());
    }

    @Test
    void shouldSubtractMoneyCorrectly() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(50.00));

        Money result = money1.subtract(money2);
        assertEquals(BigDecimal.valueOf(50.00), result.getAmount());
    }

    @Test
    void shouldBeEqual() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(100.00));

        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void shouldNotBeEqual() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(200.00));

        assertNotEquals(money1, money2);
        assertNotEquals(money1.hashCode(), money2.hashCode());
    }
}