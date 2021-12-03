package org.vaadin.vcamera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FileService {

    public static Map<String, List<Consumer<File>>> consumerMap = new ConcurrentHashMap<>();

    public static void notify(String key, File file) {
        List<Consumer<File>> consumers = consumerMap.get(key);
        if (consumers != null) {
            List<Consumer<File>> copy;
            synchronized (consumers) {
                copy = new ArrayList<>(consumers);
            }
            // The consumers must handle their own locking
            copy.forEach(c -> c.accept(file));
        }

    }

    public static Registration register(String key, Consumer<File> consumer) {
        List<Consumer<File>> consumers = consumerMap.get(key);
        if (consumers != null) {
            synchronized (consumers) {
                consumers.add(consumer);
                return new Registration(consumers, consumer);
            }
        } else {
            consumers = new ArrayList<>();
            consumers.add(consumer);
            consumerMap.put(key, consumers);
            return new Registration(consumers, consumer);
        }
    }

    public static class Registration {

        private List<Consumer<File>> consumers;
        private Consumer<File> consumer;

        public Registration(List<Consumer<File>> consumers,
                Consumer<File> consumer) {
            this.consumer = consumer;
            this.consumers = consumers;
        }

        public void unregister() {
            synchronized (consumers) {
                consumers.remove(consumer);
            }
        }
    }
}
