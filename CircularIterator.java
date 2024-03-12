/**
 * CS351 Julian Fong Arcade Game Project 3
 *
 * CircularIterator class: This class is made to be an iterator that will loop once the end is reached. This is
 * important for walking animations as it should just be a string of sprites on repeat.
 */

import javafx.scene.image.Image;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CircularIterator implements Iterator<Image> {

    private final List<Image> frames;
    private int currentIndex;

    public CircularIterator(List<Image> frames){
        this.frames = frames;
        this.currentIndex = 0;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return !frames.isEmpty();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Image next() {
        if(frames.isEmpty()){
            throw new IllegalStateException("This frame list is empty!");
        }
        Image frame = frames.get(currentIndex);
        currentIndex = (currentIndex + 1) % frames.size();
        return frame;
    }
}
