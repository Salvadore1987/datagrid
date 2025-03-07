package uz.salvadore.commons.datagrid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.salvadore.commons.datagrid.dto.DataGridRequest;
import uz.salvadore.commons.datagrid.dto.DataGridResponse;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GridServiceImpl implements GridService {

  @Override
  public <I, O> DataGridResponse<O> grid(final DataGridRequest request, final IDataGridMapper<I, O> mapper, final JpaSpecificationExecutor<I> repository) {
    final Page<I> page = findAll(request, repository);
    final List<O> list = page.stream()
      .map(mapper::fromJpaEntity)
      .toList();
    return DataGridResponse.<O>builder()
      .totalItems(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .items(list)
      .build();
  }

  private <T> Page<T> findAll(final DataGridRequest request, final JpaSpecificationExecutor<T> repository) {
    if (request.getFilters() == null) {
      request.setFilters(Collections.emptyList());
    }
    return repository.findAll(new JpaSpecificationImpl<>(request.getFilters()), GridUtils.buildPageRequest(request));
  }

}
