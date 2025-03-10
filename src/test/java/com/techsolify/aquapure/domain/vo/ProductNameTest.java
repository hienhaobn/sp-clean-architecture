package com.techsolify.aquapure.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.techsolify.aquapure.domain.exception.InvalidProductException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductNameTest {

  @Test
  void shouldCreateValidProductName() {
    ProductName name = ProductName.of("Valid Product Name");
    assertEquals("Valid Product Name", name.getValue());
  }

  @Test
  void shouldTrimWhitespace() {
    ProductName name = ProductName.of("  Product Name  ");
    assertEquals("Product Name", name.getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "  "})
  void shouldThrowExceptionForEmptyOrBlankName(String input) {
    assertThrows(InvalidProductException.class, () -> ProductName.of(input));
  }

  @Test
  void shouldThrowExceptionForNullName() {
    assertThrows(InvalidProductException.class, () -> ProductName.of(null));
  }

  @Test
  void shouldThrowExceptionForShortName() {
    assertThrows(InvalidProductException.class, () -> ProductName.of("ab"));
  }

  @Test
  void shouldThrowExceptionForLongName() {
    String longName = "a".repeat(101);
    assertThrows(InvalidProductException.class, () -> ProductName.of(longName));
  }

  @Test
  void shouldBeEqual() {
    ProductName name1 = ProductName.of("Product Name");
    ProductName name2 = ProductName.of("Product Name");

    assertEquals(name1, name2);
    assertEquals(name1.hashCode(), name2.hashCode());
  }

  @Test
  void shouldNotBeEqual() {
    ProductName name1 = ProductName.of("Product Name 1");
    ProductName name2 = ProductName.of("Product Name 2");

    assertNotEquals(name1, name2);
    assertNotEquals(name1.hashCode(), name2.hashCode());
  }
}
