<h3>Библиотека для работы с JpaRepository для отображение в табличном представлении с фильтрацией и сортировкой</h3>

<p>Для работы с данной библиотекой необходимо в зависимости maven добавить данную библиотеку а затем создать бин GridService и заинжектить в него ModelMapper</p>

<p>Пример добавления зависимости:</p>

```xml
<dependency>
    <groupId>uz.salvadore.commons</groupId>
    <artifactId>datagrid</artifactId>
</dependency>
```

<p>Пример создания бина:</p>

```java
@Bean
public GridService gridService() {
  return new GridServiceImpl(mapper);
}
```
<h4>Использование сервиса:</h4>
<p>Для использования данной библиотеки необходимо создать свой репозиторий который наследуется от JpaSpecificationExecutor и реализовать метод</p>

```java
findAll(Specification<T> specification, Pageable pageable);
```
<p>Пример: </p>

```java
public interface ApplicationViewRepository extends JpaRepository<ApplicationViewEntity, Long>, JpaSpecificationExecutor<ApplicationViewEntity> {
  Page<ApplicationViewEntity> findAll(Specification<ApplicationViewEntity> specification, Pageable pageable);
}
```

<p>Затем можно инжектировать бин GridService в програмный код своего сервиса:</p>

```java
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonitoringServiceImpl implements MonitoringService {
  
  ApplicationViewRepository viewRepository;
  GridService gridService;

  @Override
  public DataGridResponse<ApplicationListItem> getAllApplications(final DataGridRequest request) {
    return gridService.grid(request, ApplicationListItem.class, viewRepository);
  }
}
```
<h3>Описание метода grid сервиса GridService</h3>

```java
<T, R> DataGridResponse<T> grid(DataGridRequest request, Class<?> mappedClass, JpaSpecificationExecutor<R> repository);
```
<p>Метод принимает 3 параметра:</p>
<ol>
    <li>request - модель содержащая данные для выборки записей из таблицы с фильтрацией, пагинацией и сортировкой</li>
    <li>mappedClass - имя класса для мапинга сущности в DTO</li>
    <li>repository - бин репозитория из которого будет происходить выборка данных</li>
</ol>

<h4>Важно знать!</h4>

<p>Если в DTO модели которая используется для выборки из таблицы присуствует enum то необходимо реализовать метод of который принимает строку и возвращает згачение enum</p>
<p>Пример:</p>

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationListItem {
  Long id;
  String claimCode;
  @JsonSerialize(using = StateSerializer.class)
  State state;
}
```

```java
public enum State {

  NEW("Новая заявка"),
  CHECK_MORATORIUM("Проверка на мораторий"),
  HAS_MORATORIUM("Имеется мораторий"),
  CHECK_CLIENT("Проверка клиента"),
  ERROR("Ошибка при обработке заявки");

  private final String title;

  State(final String title) {
    this.title = title;
  }
  
  // Метод of который нужно реализовать
  public static State of(final String state) {
    return Arrays.stream(State.values())
      .filter(v -> v.name().equals(state))
      .findFirst()
      .orElse(null);
  }

  public String getTitle() {
    return title;
  }

}
```

<h3>Описание моделей данных</h3>

<h4>Входящая модель</h4>

<p>DataGridRequest</p>

<table>
  <tr>
    <th>Имя поля</th>
    <th>Тип поля</th>
    <th>Описание поля</th>
  </tr>
  <tr>
    <td>filters</td>
    <td>List&#60;FilterCondition&#62;</td>
    <td>Массив с данными по фильтрации</td>
  </tr>
  <tr>
    <td>sort</td>
    <td>SortCondition</td>
    <td>Объект содержащий данные по сортировке</td>
  </tr>
  <tr>
    <td>page</td>
    <td>Integer</td>
    <td>Номер страницы</td>
  </tr>
  <tr>
    <td>size</td>
    <td>Integer</td>
    <td>Кол-во эллементов на странице</td>
  </tr>
</table>

<p>FilterCondition</p>

<table>
  <tr>
    <th>Имя поля</th>
    <th>Тип поля</th>
    <th>Описание поля</th>
  </tr>
  <tr>
    <td>field</td>
    <td>String</td>
    <td>Имя поля для фильтрации (соответсвует имени поля в DTO)</td>
  </tr>
  <tr>
    <td>operator</td>
    <td>Operator</td>
    <td>Объект содержащий значение фильтра</td>
  </tr>
  <tr>
    <td>value</td>
    <td>String</td>
    <td>Значение для фильтрации</td>
  </tr>
</table>

<p>SortCondition</p>

<table>
  <tr>
    <th>Имя поля</th>
    <th>Тип поля</th>
    <th>Описание поля</th>
  </tr>
  <tr>
    <td>selector</td>
    <td>String</td>
    <td>Имя поля для сортировки (соответсвует имени поля в DTO)</td>
  </tr>
  <tr>
    <td>desc</td>
    <td>Boolean</td>
    <td>Флаг определяющий направление сортировки ASC, DESC</td>
  </tr>
</table>

<h4>Исходящая модель</h4>

<p>DataGridResponse&#60;T&#62;</p>

<table>
  <tr>
    <th>Имя поля</th>
    <th>Тип поля</th>
    <th>Описание поля</th>
  </tr>
  <tr>
    <td>totalPages</td>
    <td>Integer</td>
    <td>Общее кол-во страниц</td>
  </tr>
  <tr>
    <td>totalItems</td>
    <td>Integer</td>
    <td>Общее кол-во записей</td>
  </tr>
  <tr>
    <td>items</td>
    <td>List&#60;? extends T&#62;</td>
    <td>Массив элементов соответсвующих критерию выборки</td>
  </tr>
</table>

<h3>Описание перечисления Operator</h3>

<table>
  <tr>
    <th>Значение</th>
    <th>Описание оператора</th>
    <th>Информация</th>
  </tr>
  <tr>
    <td>sw</td>
    <td>START WITH</td>
    <th>Сравнивает значение по начальным символам</th>
  </tr>
  <tr>
    <td>ew</td>
    <td>END WITH</td>
    <th>Сравнивает значение по конечным символам</th>
  </tr>
  <tr>
    <td>eq</td>
    <td>EQUAL</td>
    <th>Определяет равенство значений</th>
  </tr>
  <tr>
    <td>lk</td>
    <td>LIKE</td>
    <th>Сравнивает значение с похожими с помощью операторов подстановки</th>
  </tr>
  <tr>
    <td>ne</td>
    <td>NOT EQUAL</td>
    <th>Определяет неравенство значений</th>
  </tr>
  <tr>
    <td>nu</td>
    <td>IS NULL</td>
    <th>Определяет, является ли значение нулевым</th>
  </tr>
  <tr>
    <td>nn</td>
    <td>IS NOT NULL</td>
    <th>Определяет, является ли значение не нулевым</th>
  </tr>
  <tr>
    <td>gt</td>
    <td>GREATER</td>
    <th>Значение левого операнда больше значения правого операнда?</th>
  </tr>
  <tr>
    <td>ge</td>
    <td>GREATER AND EQUAL</td>
    <th>Значение левого операнда больше или равно значению правого операнда?</th>
  </tr>
  <tr>
    <td>ls</td>
    <td>LESS</td>
    <th>Значение левого операнда меньше значения правого операнда?</th>
  </tr>
  <tr>
    <td>lt</td>
    <td>LESS AND EQUAL</td>
    <th>Значение левого операнда меньше или равно значению правого операнда?</th>
  </tr>
  <tr>
    <td>bt</td>
    <td>BETWEEN</td>
    <th>Проверяет вхождение значения в диапазон от минимального до максимального</th>
  </tr>
  <tr>
    <td>in</td>
    <td>IN</td>
    <th>Выполняет поиск значения в списке значений</th>
  </tr>
  <tr>
    <td>;</td>
    <td>BETWEEN SEPARATOR</td>
    <th>Разделение значений для оператора BETWEEN</th>
  </tr>
  <tr>
    <td>,</td>
    <td>IN SEPARATOR</td>
    <th>Разделение значений для оператора IN</th>
  </tr>
</table>




