package com.po.app;

/**
 * QUESTIONS:
 * -> 2nd functionality - current value or value for given point in time???
 * ->
 */
public class App {
    public static void main(String[] args) {
        Command cmd = new Command(args);

        Presenter presenter = new Presenter(cmd);
        presenter.run();
    }
}
