package io.palyvos.provenance.ananke.functions;

import io.palyvos.provenance.util.TimestampedTuple;
import org.apache.flink.api.common.functions.FilterFunction;

public class ProvenanceFilterFunction<T extends TimestampedTuple> implements FilterFunction<ProvenanceTupleContainer<T>> {

  private final FilterFunction<T> delegate;

  public ProvenanceFilterFunction(FilterFunction<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean filter(ProvenanceTupleContainer<T> value) throws Exception {
    return delegate.filter(value.tuple());
  }
}
