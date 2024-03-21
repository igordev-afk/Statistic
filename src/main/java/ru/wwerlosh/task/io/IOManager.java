package ru.wwerlosh.task.io;

public abstract class IOManager implements StreamReadable, StreamWritable {
    public abstract void read(String fileName);

    public abstract void write(Object o);
}
