package uz.salvadore.commons.datagrid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SortCondition {

  @Schema(title = "Поле по которому производится сортировка")
  @NotBlank(message = "{error.required}")
  String selector;
  @Schema(title = "Флаг по убыванию или возрастанию")
  boolean desc;

}
