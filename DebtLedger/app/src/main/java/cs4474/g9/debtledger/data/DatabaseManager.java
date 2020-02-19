package cs4474.g9.debtledger.data;

import java.util.List;

public interface DatabaseManager<T> {

    Result<T> create(T object);

    Result<T> delete(T object);

    Result<T> update(T object);

    List<T> getAll();

}
