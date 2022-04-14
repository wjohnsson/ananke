package io.palyvos.provenance.ananke.functions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.palyvos.provenance.util.TimestampedTuple;
import io.palyvos.provenance.util.TimestampedUIDTuple;
import io.palyvos.provenance.genealog.GenealogData;
import io.palyvos.provenance.genealog.GenealogTuple;
import io.palyvos.provenance.genealog.GenealogTupleType;
import java.io.Serializable;
import java.util.Objects;

public class ProvenanceTupleContainer<T extends TimestampedTuple> implements GenealogTuple {

  private GenealogData genealogData;
  private final T tuple;

  public ProvenanceTupleContainer(T tuple) {
    this.tuple = tuple;
  }

  @Override
  public void initGenealog(GenealogTupleType tupleType) {
    genealogData = new GenealogData();
    genealogData.init(tupleType);
  }

  public T tuple() {
    return tuple;
  }

  public void copyTimes(GenealogTuple other) {
    setTimestamp(other.getTimestamp());
    setStimulus(other.getStimulus());
  }

  public void copyTimes(GenealogTuple first, GenealogTuple second) {
    setTimestamp(Math.max(first.getTimestamp(), second.getTimestamp()));
    setStimulus(Math.max(first.getStimulus(), second.getStimulus()));
  }

  @Override
  public long getStimulus() {
    return tuple.getStimulus();
  }

  @Override
  public long getTimestamp() {
    return tuple.getTimestamp();
  }

  public void setTimestamp(long timestamp) {
    tuple.setTimestamp(timestamp);
  }

  public void setStimulus(long stimulus) {
    tuple.setStimulus(stimulus);
  }

  @Override
  public GenealogData getGenealogData() {
    return genealogData;
  }

  private void setGenealogData(GenealogData genealogData) {
    this.genealogData = genealogData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProvenanceTupleContainer<?> that = (ProvenanceTupleContainer<?>) o;
    return tuple.equals(that.tuple);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tuple);
  }

  @Override
  public String toString() {
    return tuple.toString();
  }

  public static class KryoSerializer extends Serializer<ProvenanceTupleContainer>
      implements Serializable {

    private final Serializer<GenealogData> genealogDataSerializer;
    private final CustomGenericSerializer customGenericSerializer;

    public KryoSerializer(
        Serializer<GenealogData> genealogDataSerializer, CustomGenericSerializer customGenericSerializer) {
      this.genealogDataSerializer = genealogDataSerializer;
      this.customGenericSerializer = customGenericSerializer;
    }

    public KryoSerializer(Serializer<GenealogData> genealogDataSerializer) {
      this(genealogDataSerializer, new CustomGenericSerializer());
    }

    @Override
    public void write(Kryo kryo, Output output, ProvenanceTupleContainer object) {
      customGenericSerializer.write(kryo, output, object.tuple);
      genealogDataSerializer.write(kryo, output, object.genealogData);
    }

    @Override
    public ProvenanceTupleContainer read(Kryo kryo, Input input, Class<ProvenanceTupleContainer> type) {
      TimestampedTuple tuple = (TimestampedTuple) customGenericSerializer.read(kryo, input);
      ProvenanceTupleContainer<TimestampedTuple> tupleContainer = new ProvenanceTupleContainer<>(tuple);
      tupleContainer.setGenealogData(genealogDataSerializer.read(kryo, input, GenealogData.class));
      return tupleContainer;
    }
  }

  @Override
  public TimestampedUIDTuple getSourceTuple() {
    //Will FAIL except if the stream has the correct type
    //Not validating for performance reasons
    return (TimestampedUIDTuple) tuple;
  }
}
