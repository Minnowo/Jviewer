package nyaa.alice.jviewer.data;

public abstract class ParamRunnable implements Runnable
{
    protected Object[] params;

    public ParamRunnable(Object... args)
    {
        this.params = args;
    }

    public void setParams(Object... args)
    {
        this.params = args;
    }

    public Object[] getParams()
    {
        return this.params;
    }

    @Override
    public void run()
    {
        runWithParams(params);
    }

    protected abstract void runWithParams(Object... args);
}
