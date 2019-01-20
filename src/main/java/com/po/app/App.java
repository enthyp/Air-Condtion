package com.po.app;

/**
 *
 */
public class App {
    public static void main(String[] args) {
        Command cmd = new Command(args);

        Presenter presenter = new Presenter(cmd);
        presenter.run();
    }
}
