package me.comu.client.plugin;

import java.util.List;

public class ElementManager<T> {

    protected List<T> elements;

    public List<T> getElements() {
        return elements;
    }

    public void register(T element) {
        elements.add(element);
    }

    public void unregister(T element) {
        elements.remove(element);
    }

    public T get(T element) {
        if (elements.contains(element))
            return element;
        return null;
    }



}
