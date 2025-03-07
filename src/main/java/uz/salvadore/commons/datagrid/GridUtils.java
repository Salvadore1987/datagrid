package uz.salvadore.commons.datagrid;

import jakarta.validation.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import uz.salvadore.commons.datagrid.dto.DataGridRequest;
import uz.salvadore.commons.datagrid.dto.SortCondition;

@UtilityClass
public class GridUtils {

  public static PageRequest buildPageRequest(final DataGridRequest request) {
    if (request.getPage() < 1)
      throw new ValidationException("Page size can't be less than 1");
    return PageRequest.of(request.getPage() - 1,
      request.getSize() == -1
        ? Integer.MAX_VALUE : request.getSize(), getSort(request.getSort()));
  }

  public static Sort getCustomSort(final String sortField, final boolean isDesc) {
    return Sort.by(getDirection(isDesc), sortField);
  }

  public static Sort getSort(final SortCondition sortCondition) {
    if (sortCondition != null) {
      return Sort.by(getDirection(sortCondition.isDesc()), sortCondition.getSelector());
    }
    return setDefaultSort();
  }

  private Sort setDefaultSort() {
    return Sort.by(Sort.Direction.DESC, "id");
  }

  private Sort.Direction getDirection(final Boolean direction) {
    if (Boolean.TRUE.equals(direction)) {
      return Sort.Direction.DESC;
    } else {
      return Sort.Direction.ASC;
    }
  }

}
