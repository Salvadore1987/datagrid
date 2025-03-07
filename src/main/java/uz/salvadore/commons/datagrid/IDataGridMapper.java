package uz.salvadore.commons.datagrid;

public interface IDataGridMapper<I, O> {

  O fromJpaEntity(I input);

}
