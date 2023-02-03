package nyaa.alice.jviewer;

import java.awt.EventQueue;
import java.util.StringJoiner;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.im4java.process.ProcessStarter;

import nyaa.alice.jviewer.data.ParamRunnable;
import nyaa.alice.jviewer.data.SingleInstanceChecker;
import nyaa.alice.jviewer.data.logging.WrappedLogger;
import nyaa.alice.jviewer.system.GeneralSettings;
import nyaa.alice.jviewer.system.OS;
import nyaa.alice.jviewer.ui.MainWindow;

public class Program extends JFrame
{
    public static MainWindow frame;

    public static void main(String[] args)
    {
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

                WrappedLogger.info(String.format("recieved %d arguments [%s]", args.length, sb.toString()));

                frame.bringToFront();
            }
        };

        // ENSURE SINGLE INSTANCE
        if (!SingleInstanceChecker.INSTANCE.isOnlyInstance(ru, true, args))
        {
            System.exit(0);
        }

        WrappedLogger.info("OS: " + OS.getOperatingSystemType());

        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    if (ProcessStarter.getGlobalSearchPath() == null)
                    {
                        WrappedLogger.log(Level.INFO,
                                "could not find environmental variable IM4JAVA_TOOLPATH, using PATH instead");

                        ProcessStarter.setGlobalSearchPath(System.getenv("PATH"));

                        if (GeneralSettings.DEBUG_MODE)
                        {
                            String s = "D:\\tmp\\JViewer\\magick";

                            ProcessStarter.setGlobalSearchPath(s);

                            WrappedLogger.log(Level.ALL, String
                                    .format("debug mode enabled, setting ProcessStarter global search path to %s", s));
                        }
                    }
                    else
                    {
                        WrappedLogger.log(Level.INFO,
                                "global magick search path set [" + ProcessStarter.getGlobalSearchPath() + "]");
                    }

                    frame = new MainWindow();

                    frame.setVisible(true);

                    frame.handleStartArguments(args);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}