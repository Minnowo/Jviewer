package nyaa.alice.jviewer.threading;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class NotifyingThread extends Thread
{
    private final Set<ThreadCompleteListener> listeners = new CopyOnWriteArraySet<ThreadCompleteListener>();

    public final void addListener(final ThreadCompleteListener listener)
    {
        listeners.add(listener);
    }

    public final void removeListener(final ThreadCompleteListener listener)
    {
        listeners.remove(listener);
    }

    private final void notifyListeners()
    {
        for (ThreadCompleteListener listener : listeners)
        {
            listener.notifyOfThreadComplete(this);
        }
    }

    @Override
    public final void run()
    {
        try
        {
            doRun();
        }
        finally
        {
            notifyListeners();
        }
    }

    public void doRun()
    {

    }
}
