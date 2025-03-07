package uz.salvadore.commons.datagrid;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;
import uz.salvadore.commons.datagrid.dto.FilterCondition;
import uz.salvadore.commons.datagrid.enums.Operator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaSpecificationImpl<T> implements Specification<T> {

  List<FilterCondition> conditions;

  public JpaSpecificationImpl(final List<FilterCondition> conditions) {
    this.conditions = conditions == null ? List.of() : conditions;
  }

  private static final String PATTERN = "yyyy-MM-dd";
  private static final String DOT_PATTERN = "dd.MM.yyyy";

  @Override
  public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
    final List<Predicate> predicates = new ArrayList<>();
    conditions.forEach(condition -> {
      if (condition.getOperator().equals(Operator.OPERATOR_GREATERANDEQUAL)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final LocalDateTime date = LocalDateTime.of(LocalDate.parse(condition.getValue(), DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MIN);
          predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(condition.getField()), date));
        } else {
          predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(condition.getField()), condition.getValue()));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_LESSANDEQUAL)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final LocalDateTime date = LocalDateTime.of(LocalDate.parse(condition.getValue(), DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MAX);
          predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(condition.getField()), date));
        } else {
          predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(condition.getField()), condition.getValue()));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_EQUAL)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final String value = condition.getValue();
          final LocalDateTime from;
          final LocalDateTime to;
          if (value.matches("\\d{2}.\\d{2}.\\d{4}")) {
            from = LocalDateTime.of(LocalDate.parse(value, DateTimeFormatter.ofPattern(DOT_PATTERN)), LocalTime.MIN);
            to = LocalDateTime.of(LocalDate.parse(value, DateTimeFormatter.ofPattern(DOT_PATTERN)), LocalTime.MAX);
          } else {
            from = LocalDateTime.of(LocalDate.parse(value, DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MIN);
            to = LocalDateTime.of(LocalDate.parse(value, DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MAX);
          }
          predicates.add(criteriaBuilder.between(root.get(condition.getField()), from, to));
        } else if (root.get(condition.getField()).getJavaType().isEnum()) {
          predicates.add(criteriaBuilder.equal(root.get(condition.getField()).as(String.class), condition.getValue()));
        } else if (root.get(condition.getField()).getJavaType() == Boolean.class || root.get(condition.getField()).getJavaType() == boolean.class) {
          final Boolean value = Boolean.TRUE.toString().equalsIgnoreCase(condition.getValue()) ? Boolean.TRUE : Boolean.FALSE.toString().equalsIgnoreCase(condition.getValue()) ? Boolean.FALSE : null;
          predicates.add(criteriaBuilder.equal(root.get(condition.getField()), value));
        } else {
          predicates.add(criteriaBuilder.equal(root.get(condition.getField()), condition.getValue()));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_GREATER)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final LocalDateTime date = LocalDateTime.of(LocalDate.parse(condition.getValue(), DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MIN);
          predicates.add(criteriaBuilder.greaterThan(root.get(condition.getField()), date));
        } else {
          predicates.add(criteriaBuilder.greaterThan(root.get(condition.getField()), condition.getValue()));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_LESS)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final LocalDateTime date = LocalDateTime.of(LocalDate.parse(condition.getValue(), DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MIN);
          predicates.add(criteriaBuilder.lessThan(root.get(condition.getField()), date));
        } else {
          predicates.add(criteriaBuilder.lessThan(root.get(condition.getField()), condition.getValue()));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_NOTEQUAL)) {
        predicates.add(criteriaBuilder.notEqual(root.get(condition.getField()), condition.getValue()));
      } else if (condition.getOperator().equals(Operator.OPERATOR_ISNOTNULL)) {
        predicates.add(criteriaBuilder.isNotNull(root.get(condition.getField())));
      } else if (condition.getOperator().equals(Operator.OPERATOR_ISNULL)) {
        predicates.add(criteriaBuilder.isNull(root.get(condition.getField())));
      } else if (condition.getOperator().equals(Operator.OPERATOR_BETWEEN)) {
        if (root.get(condition.getField()).getJavaType() == LocalDateTime.class) {
          final String[] arr = condition.getValue().split(Operator.BETWEEN_SEPARATOR.getValue());
          if (arr.length < 2)
            throw new IllegalArgumentException();
          final LocalDateTime from;
          final LocalDateTime to;
          if (arr[0].matches("\\d{2}.\\d{2}.\\d{4}")) {
            from = LocalDateTime.of(LocalDate.parse(arr[0], DateTimeFormatter.ofPattern(DOT_PATTERN)), LocalTime.MIN);
          } else {
            from = LocalDateTime.of(LocalDate.parse(arr[0], DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MIN);
          }
          if (arr[1].matches("\\d{2}.\\d{2}.\\d{4}")) {
            to = LocalDateTime.of(LocalDate.parse(arr[1], DateTimeFormatter.ofPattern(DOT_PATTERN)), LocalTime.MAX);
          } else {
            to = LocalDateTime.of(LocalDate.parse(arr[1], DateTimeFormatter.ofPattern(PATTERN)), LocalTime.MAX);
          }
          predicates.add(criteriaBuilder.between(root.get(condition.getField()), from, to));
        } else {
          final String[] arr = condition.getValue().split(Operator.BETWEEN_SEPARATOR.getValue());
          if (arr.length < 2)
            throw new IllegalArgumentException();
          final String from = arr[0];
          final String to = arr[1];
          predicates.add(criteriaBuilder.between(root.get(condition.getField()), from, to));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_IN)) {
        if (root.get(condition.getField()).getJavaType().isEnum()) {
          final Class<?> javaType = root.get(condition.getField()).getJavaType();
          predicates.add(criteriaBuilder.in(root.get(condition.getField()))
            .value(
              Arrays.stream(condition.getValue().split(","))
                .map(getValueOf(javaType))
                .collect(Collectors.toList())));
        } else {
          predicates.add(criteriaBuilder.in(root.get(condition.getField())).value(Arrays.stream(condition.getValue().split(",")).collect(Collectors.toList())));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_LIKE)) {
        if (isEnum(condition, root)) {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField()).as(String.class)), "%" + condition.getValue().toUpperCase() + "%"));
        } else {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField())), "%" + condition.getValue().toUpperCase() + "%"));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_STARTWITH)) {
        if (isEnum(condition, root)) {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField()).as(String.class)), condition.getValue().toUpperCase() + "%"));
        } else {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField())), condition.getValue().toUpperCase() + "%"));
        }
      } else if (condition.getOperator().equals(Operator.OPERATOR_ENDWITH)) {
        if (isEnum(condition, root)) {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField()).as(String.class)), "%" + condition.getValue().toUpperCase()));
        } else {
          predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(condition.getField())), "%" + condition.getValue().toUpperCase()));
        }
      }
    });
    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private Number extractNumber(final String value) {
    try {
      return NumberFormat.getInstance().parse(value);
    } catch (final ParseException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  private boolean isEnum(final FilterCondition condition, final Root<T> root) {
    return root.get(condition.getField()).getJavaType().isEnum();
  }

  private static Function<String, Object> getValueOf(final Class<?> javaType) {
    return v -> {
      try {
        final Method valueOf = javaType.getMethod("of", String.class);
        return valueOf.invoke(null, v);
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
        throw new IllegalArgumentException(ex);
      }
    };
  }
}
