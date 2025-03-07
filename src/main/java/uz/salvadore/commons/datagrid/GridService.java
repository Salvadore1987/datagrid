package uz.salvadore.commons.datagrid;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.salvadore.commons.datagrid.dto.DataGridRequest;
import uz.salvadore.commons.datagrid.dto.DataGridResponse;

public interface GridService {

  <I, O> DataGridResponse<O> grid(DataGridRequest request, IDataGridMapper<I, O> mapper, JpaSpecificationExecutor<I> repository);

}
