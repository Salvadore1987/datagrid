package uz.salvadore.commons.datagrid.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import uz.salvadore.commons.datagrid.enums.Operator;
import uz.salvadore.commons.datagrid.serializers.OperatorDeserializer;
import uz.salvadore.commons.datagrid.serializers.OperatorSerializer;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterCondition {

  @Schema(title = "Имя поля для фильтрации")
  @NotBlank(message = "{error.required}")
  private String field;
  @Schema(title = "Оператор фильтрации", type = "string", allowableValues = {"cn", "nc", "sw", "ew", "eq", "lk", "nc", "nu", "nn", "gt", "ls", "ge", "lt", "bt", "in"})
  @NotNull(message = "{error.required}")
  @JsonDeserialize(using = OperatorDeserializer.class)
  @JsonSerialize(using = OperatorSerializer.class)
  private Operator operator;
  @Schema(title = "Значение фильтрации для between значение передается с разделителем ':' для in разделитель ','")
  @NotBlank(message = "{error.required}")
  private String value;

}
