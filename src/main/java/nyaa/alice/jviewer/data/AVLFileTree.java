package nyaa.alice.jviewer.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.tinylog.Logger;

import nyaa.alice.jviewer.system.GeneralSettings;

class AVLNode
{
    public String filename;

    public AVLNode left;
    public AVLNode right;

    public int height;

    public AVLNode(String path)
    {
        this.filename = path;
        this.height = 1;
    }

    public AVLNode(File path)
    {
        this.filename = path.getName();
        this.height = 1;
    }
}

public class AVLFileTree
{
    public final Comparator<String> COMPARATOR;

    public String fileFilter = "*.*";

    private String directory;

    private AVLNode root;

    private int count;

    public AVLFileTree()
    {
        this.count = 0;
        COMPARATOR = GeneralSettings.FILENAME_COMPARATOR;
    }

    public AVLFileTree(String directory)
    {
        this.directory = directory;
        this.count = 0;
        COMPARATOR = GeneralSettings.FILENAME_COMPARATOR;
        loadDirectory(directory);
    }

    public String getDirectory()
    {
        return this.directory;
    }

    private Thread loadDirectoryThread = null;
    private boolean isLoading = false;

    public synchronized void loadDirectoryAsync(String path)
    {
        waitUntilLoadFinished();

        loadDirectoryThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                isLoading = true;
                loadDirectory(path);
                isLoading = false;
                Logger.debug("Async directory load finished");
            }
        });

        loadDirectoryThread.start();
    }

    public synchronized void waitUntilLoadFinished()
    {
        if (!isLoading)
        {
            return;
        }

        if (loadDirectoryThread != null)
        {
            try
            {
                loadDirectoryThread.join();
            }
            catch (InterruptedException e)
            {
                Logger.warn(e, "Load directory thread interrupted on join");
            }
        }
    }

    public boolean loadDirectory(String path)
    {
        File newDirectory = new File(path);

        if (!newDirectory.isDirectory())
            newDirectory = newDirectory.getParentFile();

        if (newDirectory == null || !newDirectory.exists())
            return false;

        this.clear();
        this.directory = newDirectory.getAbsolutePath();

        /*
         * 
         * NOTE: do NOT check if each path is a directory here
         * 
         * it is suuuuper slow if you do that, just trust the file filter
         * 
         */

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(newDirectory.getAbsolutePath()),
                GeneralSettings.IMAGE_FILTER))// /* BAD DON'T DO THIS , entry -> !Files.isDirectory(entry))) */
        {
            for (Path entry : stream)
            {
                this.insert(entry.toFile().getName());
            }
        }
        catch (IOException ex)
        {
            Logger.error(ex, "Error while loading directory into AVL tree");
            // An I/O problem has occurred
            return false;
        }

        return true;
    }

    private AVLNode terribleInOrderPredessosor(String path)
    {
        if (this.root == null)
            return null;

        if (this.root.filename.equals(path))
            return rightMost(this.root.left);

        // this acts like a stack if a certain case appears
        SimpleDoublyLinkedList<AVLNode> fakeLinkedList = new SimpleDoublyLinkedList<>();

        for (AVLNode node = this.root;;)
        {
            fakeLinkedList.node = node;
            fakeLinkedList.createNext();
            fakeLinkedList = fakeLinkedList.next;

            if (COMPARATOR.compare(path, node.filename) < 0)
            {
                if (node.left == null)
                    return null;

                // Case 2, the current node has a left child, with a left child
                // get it's rightmost child and we're done
                if (node.left.left != null && node.left.filename.equals(path))
                {
                    return rightMost(node.left.left);
                }

                node = node.left;
            }
            else if (COMPARATOR.compare(path, node.filename) > 0)
            {
                if (node.right == null)
                    return null;

                // the current nodes right node is what we're looking for, easy
                // Case 1, the current node is the parent of a right child
                if (node.right.filename.equals(path))
                {
                    // if the right child has no left nodes, return the parent
                    if (node.right.left == null)
                    {
                        return node;
                    }

                    // otherwise we can get the rightmost left node
                    return rightMost(node.right.left);
                }

                node = node.right;
            }
            else
            {
                // worst case here
                break;
            }
        }

        // this is worst case for this function, since it's more steps and ram usage by
        // the linked list

        // remove empty last node, created at the start of the loop above
        fakeLinkedList = fakeLinkedList.prev;
        fakeLinkedList.next = null;

        // remove current node we found, this node we were looking for
        fakeLinkedList = fakeLinkedList.prev;
        fakeLinkedList.next = null;

        while (true)
        {
            // we're at the rightmost node in the tree
            if (fakeLinkedList.prev == null)
                return null;

            // need to keep looking up at the parent nodes until we find where we're on the
            // right
            // if we can't find a spot we're on the left, we have the leftmost node in the
            // tree
            if (fakeLinkedList.node == fakeLinkedList.prev.node.right)
            {
                return fakeLinkedList.prev.node;
            }

            fakeLinkedList = fakeLinkedList.prev;
            fakeLinkedList.next = null;
        }
    }

    private AVLNode terribleInOrderSuccessor(String path)
    {
        if (this.root == null)
            return null;

        if (this.root.filename.equals(path))
            return leftMost(this.root.right);

        // this acts like a stack if a certain case appears
        SimpleDoublyLinkedList<AVLNode> fakeLinkedList = new SimpleDoublyLinkedList<>();

        for (AVLNode node = this.root;;)
        {
            fakeLinkedList.node = node;
            fakeLinkedList.createNext();
            fakeLinkedList = fakeLinkedList.next;

            if (COMPARATOR.compare(path, node.filename) < 0)
            {
                if (node.left == null)
                    return null;

                // the current nodes left node is what we're looking for, easy
                // Case 1, the current node is the parent of a left child
                if (node.left.filename.equals(path))
                {
                    // if the left child has no right nodes, return the parent
                    if (node.left.right == null)
                    {
                        return node;
                    }

                    // otherwise we can get the leftmost right node
                    return leftMost(node.left.right);
                }

                node = node.left;
            }
            else if (COMPARATOR.compare(path, node.filename) > 0)
            {
                if (node.right == null)
                    return null;

                // Case 2, the current node has a right child, with a right child
                // get it's leftmost child and we're done
                if (node.right.right != null && node.right.filename.equals(path))
                {
                    return leftMost(node.right.right);
                }

                node = node.right;
            }
            else
            {
                // worst case here
                break;
            }
        }

        // this is worst case for this function, since it's more steps and ram usage by
        // the linked list

        // remove empty last node, created at the start of the loop above
        fakeLinkedList = fakeLinkedList.prev;
        fakeLinkedList.next = null;

        // remove current node we found, this node we were looking for
        fakeLinkedList = fakeLinkedList.prev;
        fakeLinkedList.next = null;

        while (true)
        {
            // we're at the rightmost node in the tree
            if (fakeLinkedList.prev == null)
                return null;

            // need to keep looking up at the parent nodes until we find where we're on the
            // left
            // if we can't find a spot we're on the left, we have the rightmost node in the
            // tree
            if (fakeLinkedList.node == fakeLinkedList.prev.node.left)
            {
                return fakeLinkedList.prev.node;
            }

            fakeLinkedList = fakeLinkedList.prev;
            fakeLinkedList.next = null;
        }
    }

    public File inOrderPredessor(File value)
    {
        if (value == null)
            return null;

        AVLNode val = terribleInOrderPredessosor(value.getName());

        if (val == null)
            return null;

        return new File(this.directory, val.filename);
    }

    public File inOrderSuccessor(File value)
    {
        if (value == null)
            return null;

        AVLNode val = terribleInOrderSuccessor(value.getName());

        if (val == null)
            return null;

        return new File(this.directory, val.filename);
    }

    public void inOrderPrint()
    {
        inOrderPrint(this.root);
    }

    private void inOrderPrint(AVLNode node)
    {
        if (node == null)
            return;

        inOrderPrint(node.left);
        System.out.println(node.filename);
        inOrderPrint(node.right);
    }

    public void printTree()
    {
        printTree(this.root, "");
    }

    private void printTree(AVLNode root, String offset)
    {
        if (root == null)
            return;

        System.out.println(offset + root.filename + "\n");
        printTree(root.left, offset + "  ");
        printTree(root.right, offset + "  ");
    }

    public void clear()
    {
        // i'm hoping this actually lets gc do stuff
        // and that it doesn't just hold all the nodes in memory forever
        this.root = null;
        this.count = 0;
    }

    public int getSize()
    {
        return this.count;
    }

    public File getFirstPath()
    {
        if (this.root == null)
            return null;

        AVLNode leftMost = leftMost(this.root);

        return new File(this.directory, leftMost.filename);
    }

    public File getLastPath()
    {
        if (this.root == null)
            return null;

        AVLNode rightMost = rightMost(this.root);

        return new File(this.directory, rightMost.filename);
    }

    public void insert(String path)
    {
        this.root = insert(this.root, path);
        this.count++;
    }

    private AVLNode insert(AVLNode node, String path)
    {
        if (node == null)
            return new AVLNode(path);

        if (COMPARATOR.compare(path, node.filename) < 0)
        {
            node.left = insert(node.left, path);
        }
        else if (COMPARATOR.compare(path, node.filename) > 0)
        {
            node.right = insert(node.right, path);
        }
        else
        {
            this.count--;
            return node;
        }

        node.height = getMax(getHeight(node.left), getHeight(node.right)) + 1;

        int balanceFactor = getBalanceFactor(node);

        if (balanceFactor > 1)
        {
            if (COMPARATOR.compare(path, node.left.filename) < 0)
            {
                return rightRotate(node);
            }
            else
            {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }

        if (balanceFactor < -1)
        {
            if (COMPARATOR.compare(path, node.right.filename) > 0)
            {
                return leftRotate(node);
            }
            else
            {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }

        return node;
    }

    public boolean contains(String path)
    {
        return search(this.root, path, COMPARATOR) != null;
    }

    public static AVLNode search(AVLNode root, String path, Comparator<String> comparator)
    {
        if (root == null)
            return null;

        for (AVLNode node = root;;)
        {
            if (comparator.compare(path, node.filename) < 0)
            {
                if (node.left == null)
                    return null;

                node = node.left;
            }
            else if (comparator.compare(path, node.filename) > 0)
            {
                if (node.right == null)
                    return null;

                node = node.right;
            }

            return node;
        }
    }

    public static AVLNode leftMost(AVLNode node)
    {
        if (node == null)
            return null;

        for (; node.left != null; node = node.left)
            ;

        return node;
    }

    public static AVLNode rightMost(AVLNode node)
    {
        if (node == null)
            return null;

        for (; node.right != null; node = node.right)
            ;

        return node;
    }

    public static int getMax(int a, int b)
    {
        if (a > b)
            return a;
        return b;
    }

    public static int getHeight(AVLNode node)
    {
        if (node == null)
            return 0;

        return node.height;
    }

    public static int getBalanceFactor(AVLNode node)
    {
        if (node == null)
            return 0;

        return getHeight(node.left) - getHeight(node.right);
    }

    public static AVLNode rightRotate(AVLNode node)
    {
        AVLNode newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        node.height = getMax(getHeight(node.left), getHeight(node.right)) + 1;
        newRoot.height = getMax(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }

    public static AVLNode leftRotate(AVLNode node)
    {
        AVLNode newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        node.height = getMax(getHeight(node.left), getHeight(node.right)) + 1;
        newRoot.height = getMax(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }
}
