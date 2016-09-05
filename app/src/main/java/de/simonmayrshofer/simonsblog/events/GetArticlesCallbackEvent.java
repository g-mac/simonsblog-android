package de.simonmayrshofer.simonsblog.events;

public class GetArticlesCallbackEvent {

    public final boolean success;

    public GetArticlesCallbackEvent(boolean success) {
        this.success = success;
    }

}
