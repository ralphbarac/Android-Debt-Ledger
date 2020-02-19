package cs4474.g9.debtledger.data;

/**
 * A generic class that holds a result, either success with data, or error with exception
 */
public class Result<T> {

    private Result() {
    }

    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
