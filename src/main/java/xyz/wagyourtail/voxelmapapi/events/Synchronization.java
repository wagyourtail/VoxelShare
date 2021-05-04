package xyz.wagyourtail.voxelmapapi.events;

public class Synchronization {
    boolean done = false;

    public synchronized void setDone() {
        done = true;
        this.notifyAll();
    }

    public synchronized void waitFor() throws InterruptedException {
        if (done) return;
        this.wait();
    }
}
