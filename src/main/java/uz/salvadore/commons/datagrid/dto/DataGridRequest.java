package uz.salvadore.commons.datagrid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataGridRequest {

  @Valid
  @Schema(title = "Список условий для фильтрации")
  List<FilterCondition> filters;
  @Valid
  @Schema(title = "Условия сортировки")
  SortCondition sort;
  @NotNull(message = "{error.required}")
  @Schema(title = "Номер страницы начиная с 1", example = "1", required = true)
  Integer page;
  @NotNull(message = "{error.required}")
  @Schema(title = "Кол-во элементов", example = "10", required = true)
  Integer size;

}
