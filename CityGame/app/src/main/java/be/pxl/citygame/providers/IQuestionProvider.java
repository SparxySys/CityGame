package be.pxl.citygame.providers;

import java.util.NoSuchElementException;

import be.pxl.citygame.Question;

/**
 * Created by Lorenz Jolling on 2015-01-14.
 * Generic interface for loading questions
 */
public interface IQuestionProvider {

    Question loadQuestionById(int id) throws NoSuchElementException;

}
