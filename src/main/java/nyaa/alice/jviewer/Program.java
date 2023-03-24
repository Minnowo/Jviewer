package nyaa.alice.jviewer;

import java.awt.EventQueue;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.StringJoiner;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;
import org.tinylog.Logger;

import nyaa.alice.jviewer.data.ParamRunnable;
import nyaa.alice.jviewer.data.SingleInstanceChecker;
import nyaa.alice.jviewer.system.OS;
import nyaa.alice.jviewer.system.ResourceLoader;
import nyaa.alice.jviewer.system.ResourcePaths;
import nyaa.alice.jviewer.ui.MainWindow;

public class Program extends JFrame
{
    public static MainWindow frame;
    
    private static class TinylogHandler implements UncaughtExceptionHandler 
    {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) 
        {
            Logger.error("! === Unhandled Exception === !");
            Logger.error(ex);
        }
    }
  
    public static void main(String[] args)
    {   
        ResourceLoader.loadTinyLogConfig();
        Thread.setDefaultUncaughtExceptionHandler(new TinylogHandler());
        
        ParamRunnable ru = new ParamRunnable()
        {
            @Override
            protected void runWithParams(Object... args)
            {
                if (frame == null)
                    return;

                StringJoiner sb = new StringJoiner(" ");

                for (Object o : args)
                {
                    if (o instanceof String)
                    {
                        frame.handleStartArgument((String) o);

                        sb.add("'" + (String) o + "'");
                    }
                }

                Logger.info("Recieved start arguments: [{}]", sb.toString());

                frame.bringToFront();
            }
        };

        // ENSURE SINGLE INSTANCE
        if (!SingleInstanceChecker.INSTANCE.isOnlyInstance(ru, true, args))
        {
            Logger.error("Failed single instance check, start arguments have been passed to running instance");
            System.exit(0);
        }

        Logger.info("OS: {}", OS.getOperatingSystemType());
        Logger.info("Config: {}", ResourcePaths.CONFIG_PATH);

        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    if (ProcessStarter.getGlobalSearchPath() == null)
                    {
                        Logger.warn("IM4JAVA_TOOLPATH environment variable not set. Using PATH instead");

                        ProcessStarter.setGlobalSearchPath(System.getenv("PATH"));
                    }
                    
                    Logger.info("Global Magick search path: {}", ProcessStarter.getGlobalSearchPath());

                    frame = new MainWindow();

                    frame.setVisible(true);

                    Logger.info("Start arguments: [{}]", args);
                    frame.handleStartArguments(args);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.error(e);
                }
            }
        });
    }
}