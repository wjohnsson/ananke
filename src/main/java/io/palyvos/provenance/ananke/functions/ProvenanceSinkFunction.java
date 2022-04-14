package io.palyvos.provenance.ananke.functions;

import io.palyvos.provenance.util.TimestampedTuple;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class ProvenanceSinkFunction<IN extends TimestampedTuple> implements
    SinkFunction<ProvenanceTupleContainer<IN>> {

  private final SinkFunction<IN> delegate;

  public ProvenanceSinkFunction(SinkFunction<IN> delegate) {
    this.delegate = delegate;
  }

  @Override
  public void invoke(ProvenanceTupleContainer<IN> value, Context context) throws Exception {
    delegate.invoke(value.tuple(), context);
  }
}
