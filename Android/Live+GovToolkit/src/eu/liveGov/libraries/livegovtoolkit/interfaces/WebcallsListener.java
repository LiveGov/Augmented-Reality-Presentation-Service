package eu.liveGov.libraries.livegovtoolkit.interfaces;

import org.apache.http.HttpResponse;

public interface WebcallsListener
{
    public void webcallReady(HttpResponse response);
}
