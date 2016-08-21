package com.sharing.Handlers.Tasks;


/**
 * Abstraction create and run any runnable/task (cleaning up the code)
 */
public class CreatorTask {
    public static void Start(Runnable todo) {
        Thread task = new Thread(todo);
        task.start();
    }
}
