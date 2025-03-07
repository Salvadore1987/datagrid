package uz.salvadore.commons.datagrid.enums;

import java.util.Arrays;

public enum Operator {
  OPERATOR_STARTWITH("sw"),
  OPERATOR_ENDWITH("ew"),
  OPERATOR_EQUAL("eq"),
  OPERATOR_LIKE("lk"),
  OPERATOR_NOTEQUAL("ne"),
  OPERATOR_ISNULL("nu"),
  OPERATOR_ISNOTNULL("nn"),
  OPERATOR_GREATER("gt"),
  OPERATOR_GREATERANDEQUAL("ge"),
  OPERATOR_LESS("ls"),
  OPERATOR_LESSANDEQUAL("lt"),
  OPERATOR_BETWEEN("bt"),
  OPERATOR_IN("in"),
  BETWEEN_SEPARATOR(";"),
  IN_SEPARATOR(",");

  private String value;

  Operator(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Operator of (final String value) {
    return Arrays.stream(Operator.values())
      .filter(o -> o.getValue().equals(value))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

}
