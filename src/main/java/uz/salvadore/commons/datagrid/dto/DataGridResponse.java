package uz.salvadore.commons.datagrid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class DataGridResponse<T> {

  @Schema(title = "Кол-во страниц в выборке")
  int totalPages;
  @Schema(title = "Кол-во элементов в выборке")
  long totalItems;
  @Schema(title = "Эллементы выборки")
  List<? extends T> items;

}
