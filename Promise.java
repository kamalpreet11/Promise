import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class Promise {

    private static final class Queue extends LinkedBlockingQueue<Task>{};

    public interface Task<IN, OUT> {

        OUT run(IN args) throws Throwable;
    }

    public interface Error {
        void error(Throwable t, Task promiseTask);
    }

    private Queue queue = new Queue();

    private Error error = null;

    private boolean stop = false;

    private Object previousArgs = null;

    @SuppressWarnings("unchecked")
    public Promise() {
        new Thread(() -> {
            while (!stop) {
                try {
                    Task task = queue.poll(250, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        try {
                            previousArgs = task.run(previousArgs);
                        } catch (Throwable throwable) {
                            if (this.error != null) {
                                this.error.error(throwable, task);
                                stop = true;
                            } else {
                                throw new RuntimeException(throwable);
                            }
                        }
                    } else {
                        stop = true;
                    }
                } catch (InterruptedException interrupted) {
                    Logger.d("Promise Interrupted No tasks added to promise. Clearing up");
                    stop = true;
                }
            }
            Logger.d("Promise No more tasks. Clearing up");
        }).start();
    }

    public Promise then(@NonNull Task target) {
        queue.offer(target);
        return this;
    }

    public void catchError(Error error) {
        this.error = error;
    }
}
