package uz.salvadore.commons.datagrid.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uz.salvadore.commons.datagrid.enums.Operator;

import java.io.IOException;

public class OperatorSerializer extends StdSerializer<Operator> {

  public OperatorSerializer() {
    super(Operator.class);
  }

  @Override
  public void serialize(final Operator operator, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(operator.getValue());

  }
}
