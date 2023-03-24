package nyaa.alice.jviewer.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javax.swing.SwingUtilities;

import org.tinylog.Level;
import org.tinylog.Logger;

import nyaa.alice.jviewer.system.ResourcePaths;

/**
 * SingleInstanceChecker v[(2), 2016-04-22 08:00 UTC] by
 * dreamspace-president.com
 * <p>
 * (file lock single instance solution by Robert
 * https://stackoverflow.com/a/2002948/3500521)
 */
public enum SingleInstanceChecker
{

    INSTANCE; // HAHA! The CONFUSION!

    final public static int POLLINTERVAL = 1000;
    final public static File LOCKFILE = new File(ResourcePaths.LOCAL_PATH.toString(), "SINGLE_INSTANCE_LOCKFILE");
    final public static File DETECTFILE = new File(ResourcePaths.LOCAL_PATH.toString(), "EXTRA_INSTANCE_DETECTFILE");

    private boolean hasBeenUsedAlready = false;

    private WatchService watchService = null;
    private RandomAccessFile randomAccessFileForLock = null;
    private FileLock fileLock = null;

    private static boolean lockInstance(final String lockFile)
    {
        try
        {
            final File file = new File(lockFile);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null)
            {
                Runtime.getRuntime().addShutdownHook(new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                        }
                        catch (Exception e)
                        {
                            Logger.error(e, "Failed to remove lock file");
                        }
                    }
                });
                return true;
            }
        }
        catch (Exception e)
        {
            Logger.error(e, "Failed to create and/or lock file {}", lockFile);
        }
        return false;
    }

    /**
     * CAN ONLY BE CALLED ONCE.
     * <p>
     * Assumes that the program will close if FALSE is returned: The
     * other-instance-tries-to-launch listener is not installed in that case.
     * <p>
     * Checks if another instance is already running (temp file lock /
     * shutdownhook). Depending on the accessibility of the temp file the return
     * value will be true or false. This approach even works even if the virtual
     * machine process gets killed. On the next run, the program can even detect if
     * it has shut down irregularly, because then the file will still exist. (Thanks
     * to Robert https://stackoverflow.com/a/2002948/3500521 for that solution!)
     * <p>
     * Additionally, the method checks if another instance tries to start. In a
     * crappy way, because as awesome as Java is, it lacks some fundamental
     * features. Don't worry, it has only been 25 years, it'll sure come eventually.
     *
     * @param codeToRunIfOtherInstanceTriesToStart Can be null. If not null and
     *                                             another instance tries to start
     *                                             (which changes the detect-file),
     *                                             the code will be executed. Could
     *                                             be used to bring the current
     *                                             (=old=only) instance to front. If
     *                                             null, then the watcher will not
     *                                             be installed at all, nor will the
     *                                             trigger file be created. (Null
     *                                             means that you just don't want to
     *                                             make use of this half of the
     *                                             class' purpose, but then you
     *                                             would be better advised to just
     *                                             use the 24 line method by
     *                                             Robert.)
     *                                             <p>
     *                                             BE CAREFUL with the code: It will
     *                                             potentially be called until the
     *                                             very last moment of the program's
     *                                             existence, so if you e.g. have a
     *                                             shutdown procedure or a window
     *                                             that would be brought to front,
     *                                             check if the procedure has not
     *                                             been triggered yet or if the
     *                                             window still exists / hasn't been
     *                                             disposed of yet. Or edit this
     *                                             class to be more comfortable.
     *                                             This would e.g. allow you to
     *                                             remove some crappy comments.
     *                                             Attribution would be nice,
     *                                             though.
     * @param executeOnAWTEventDispatchThread      Convenience function. If false,
     *                                             the code will just be executed.
     *                                             If true, it will be detected if
     *                                             we're currently on that thread.
     *                                             If so, the code will just be
     *                                             executed. If not so, the code
     *                                             will be run via
     *                                             SwingUtilities.invokeLater().
     * @return if this is the only instance
     */
    public boolean isOnlyInstance(final ParamRunnable codeToRunIfOtherInstanceTriesToStart,
            final boolean executeOnAWTEventDispatchThread, final String[] args)
    {
        if (hasBeenUsedAlready)
        {
            throw new IllegalStateException(
                    "This class/method can only be used once, which kinda makes sense if you think about it.");
        }
        hasBeenUsedAlready = true;
        
        Logger.info("Registering single instance service");

        final boolean ret = canLockFileBeCreatedAndLocked();

        if (codeToRunIfOtherInstanceTriesToStart != null)
        {
            if (ret)
            {
                // Only if this is the only instance, it makes sense to install a watcher for
                // additional instances.
                installOtherInstanceLaunchAttemptWatcher(codeToRunIfOtherInstanceTriesToStart,
                        executeOnAWTEventDispatchThread);
            }
            else
            {
                // Only if this is NOT the only instance, it makes sense to create&delete the
                // trigger file that will effect notification of the other instance.
                //
                // Regarding "codeToRunIfOtherInstanceTriesToStart != null":
                // While creation/deletion of the file concerns THE OTHER instance of the
                // program,
                // making it dependent on the call made in THIS instance makes sense
                // because the code executed is probably the same.
                createAndDeleteOtherInstanceWatcherTriggerFile(args);
            }
        }

        optionallyInstallShutdownHookThatCleansEverythingUp();

        return ret;
    }

    private void createAndDeleteOtherInstanceWatcherTriggerFile(final String[] args)
    {
        try
        {

            // delete existing DETECTFILE
            Files.deleteIfExists(DETECTFILE.toPath());

            final RandomAccessFile randomAccessFileForDetection = new RandomAccessFile(DETECTFILE, "rw");

            // write cmd args to the file by line
            for (String s : args)
            {
                randomAccessFileForDetection.writeBytes(s + System.lineSeparator());
            }

            randomAccessFileForDetection.close();

        }
        catch (IOException e)
        {
            Logger.error(e, "Error creating or deleting trigger file");
        }
    }

    private boolean canLockFileBeCreatedAndLocked()
    {

        try
        {
            randomAccessFileForLock = new RandomAccessFile(LOCKFILE, "rw");
            fileLock = randomAccessFileForLock.getChannel().tryLock();
            return fileLock != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private void installOtherInstanceLaunchAttemptWatcher(final ParamRunnable codeToRunIfOtherInstanceTriesToStart,
            final boolean executeOnAWTEventDispatchThread)
    {

        // PREPARE WATCHSERVICE AND STUFF
        try
        {
            watchService = FileSystems.getDefault().newWatchService();
        }
        catch (IOException e)
        {
            Logger.error(e, "Failed to create watch service");
            return;
        }

        final File appFolder = new File("").getAbsoluteFile(); // points to current folder
        final Path appFolderWatchable = appFolder.toPath();

        // REGISTER CURRENT FOLDER FOR WATCHING FOR FILE DELETIONS
        try
        {
            appFolderWatchable.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        }
        catch (IOException e)
        {
            Logger.error(e, "Failed to register the current folder with watch service");
            return;
        }

        // INSTALL WATCHER THAT LOOKS IF OUR detectFile SHOWS UP IN THE DIRECTORY
        // CHANGES. IF THERE'S A CHANGE, ANOTHER INSTANCE TRIED TO START, SO NOTIFY THE
        // CURRENT ONE OF THAT.
        final Thread t = new Thread(() -> watchForDirectoryChangesOnExtraThread(codeToRunIfOtherInstanceTriesToStart,
                executeOnAWTEventDispatchThread));
        t.setDaemon(true);
        t.setName("directory content change watcher");
        t.start();
    }

    private void optionallyInstallShutdownHookThatCleansEverythingUp()
    {

        if (fileLock == null && randomAccessFileForLock == null && watchService == null)
        {
            return;
        }

        final Thread shutdownHookThread = new Thread(() -> {
            try
            {
                if (fileLock != null)
                {
                    fileLock.release();
                }
                if (randomAccessFileForLock != null)
                {
                    randomAccessFileForLock.close();
                }
                Files.deleteIfExists(LOCKFILE.toPath());
            }
            catch (Exception ignore)
            {
            }

            if (watchService != null)
            {
                try
                {
                    watchService.close();
                }
                catch (IOException e)
                {
                    Logger.warn(e, "Error closing watch service in shutdown thread");
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }

    private void watchForDirectoryChangesOnExtraThread(final ParamRunnable codeToRunIfOtherInstanceTriesToStart,
            final boolean executeOnAWTEventDispatchThread)
    {
        // To eternity and beyond! Until the universe shuts down. (Should be a volatile
        // boolean, but this class only has absolutely required features.)
        while (true)
        {
            try
            {
                Thread.sleep(POLLINTERVAL);
            }
            catch (InterruptedException e)
            {
                Logger.warn(e, "Interrupted during Thread.sleep(POLLINTERVAL);");
            }

            final WatchKey wk;

            try
            {
                wk = watchService.poll();
            }
            catch (ClosedWatchServiceException e)
            {
                // This situation would be normal if the watcher has been closed, but our
                // application never does that.

                Logger.info(e, "Watch service closed, exiting watch thread mainloop");

                return;
            }

            if (wk == null || !wk.isValid())
            {
                continue;
            }

            for (WatchEvent<?> we : wk.pollEvents())
            {

                final WatchEvent.Kind<?> kind = we.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW)
                {
                    Logger.error("OVERFLOW of directory change events while watching for {} to be created", DETECTFILE);
                    continue;
                }

                final WatchEvent<Path> watchEvent = (WatchEvent<Path>) we;
                final File file = watchEvent.context().toFile();

                if (file.equals(DETECTFILE))
                {
                    try
                    {
                        List<String> lines = Files.readAllLines(DETECTFILE.toPath(), StandardCharsets.UTF_8);
                        codeToRunIfOtherInstanceTriesToStart.setParams(lines.toArray());
                    }
                    catch (IOException e)
                    {
                        Logger.error("Error while reading lines from {}: {}", DETECTFILE, e);
                        break;
                    }

                    if (!executeOnAWTEventDispatchThread || SwingUtilities.isEventDispatchThread())
                    {
                        codeToRunIfOtherInstanceTriesToStart.run();
                    }
                    else
                    {
                        SwingUtilities.invokeLater(codeToRunIfOtherInstanceTriesToStart);
                    }

                    try
                    {
                        Files.deleteIfExists(DETECTFILE.toPath());
                    }
                    catch (IOException e)
                    {
                        Logger.warn(e, "Error removing {}", DETECTFILE);
                    }

                    break;
                }
                else
                {
                    Logger.debug("{} was created in the watch folder", file);
                }

            }

            wk.reset();
        }
    }

}