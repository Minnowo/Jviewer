package Util;

import java.io.File;
import java.util.LinkedList;
import java.util.Stack;

import Util.Comparators.NaturalOrderComparator;

class BareLinkedList
{
	public BareLinkedList next;
	public BareLinkedList prev;
	public AVLNode node;
}

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

public class AVL_FileTree 
{
	private static final NaturalOrderComparator NATURAL_COMPARE = new NaturalOrderComparator();
	
	private String directory;
	
	private AVLNode root;
	
	private int count;
	
	public AVL_FileTree()
	{
		this.count = 0;
	}
	
	public AVL_FileTree(String directory)
	{
		this.directory = directory;
		this.count = 0;
		loadDirectory(directory);
	}
	
	public String getDirectory()
	{
		return this.directory;
	}
	
	public boolean loadDirectory(String path)
	{
		File newDirectory = new File(path);
		
		if(!newDirectory.isDirectory())
			newDirectory = newDirectory.getParentFile();
		
		if(!newDirectory.exists())
			return false;
		
		this.clear();
		this.directory = newDirectory.getAbsolutePath();
		
		File[] files = newDirectory.listFiles();
		
		if(files == null)
			return false;
		
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isDirectory())
				continue;
			this.insert(files[i].getName());
		}
		 	
		return true;
	}
	
	
	
	private AVLNode terribleInOrderSuccessor(String path)
	{
		if(this.root == null)
			return null;
		
		if(this.root.filename.equals(path))
			return leftMost(this.root.right);
		
		BareLinkedList fakeLinkedList = new BareLinkedList(); 


		for(AVLNode node = root;;)
		{
			fakeLinkedList.node = node;
			fakeLinkedList.next = new BareLinkedList();
			fakeLinkedList.next.prev = fakeLinkedList;
			fakeLinkedList = fakeLinkedList.next;
			
			if(Comparators.naturalCompare(path, node.filename) < 0)
			{
				if(node.left == null)
					return null;
				
				// the current nodes left node is what we're looking for, easy 
				if(node.left.filename.equals(path))
				{
					if(node.left.right == null)
					{
						return node;
					}
					
					return leftMost(node.left.right);
				}
				
				node = node.left;
			}
			else if(Comparators.naturalCompare(path, node.filename) > 0)
			{
				if(node.right == null)
					return null;
				
				// the current nodes left node is what we're looking for, easy 
				if(node.right.filename.equals(path) && node.right.right != null)
				{
					return leftMost(node.right.right);
				}
				
				node = node.right;
			}
			else 
			{
				break;
			}
		}


		// remove empty last node
		fakeLinkedList = fakeLinkedList.prev;
		fakeLinkedList.next = null;
		
		
		// remove current node we found
		fakeLinkedList = fakeLinkedList.prev;
		fakeLinkedList.next = null;
		
	
		while(true)
		{

			// we're at the rightmost node in the tree 
			if(fakeLinkedList.prev == null)
				return null;

			if(fakeLinkedList.node.filename.equals(fakeLinkedList.prev.node.left.filename))
			{
				return fakeLinkedList.prev.node;
			}
			
			fakeLinkedList = fakeLinkedList.prev;
			fakeLinkedList.next = null;
		}
	}
	
	public void printTree()
	{
		printTree(this.root, "");
	}
	
	private void printTree(AVLNode root, String offset)
	{
		if(root == null)
			return;
		
		System.out.println(offset + root.filename + "\n");
		printTree(root.left, offset + "  ");
		printTree(root.right, offset + "  ");
	}
	
	public File inOrderSuccessor(File value)
	{
		if(value == null)
			return null;
		
		AVLNode val = terribleInOrderSuccessor(value.getName());
		
		if(val == null)
			return null;
		
		return new File(this.directory, val.filename);
	}
	
	public static AVLNode search(AVLNode root, String path)
	{
		if(root == null)
			return null;
		
		for(AVLNode node = root;;)
		{
			if(Comparators.naturalCompare(path, node.filename) < 0)
			{
				if(node.left == null)
					return null;
				
				node = node.left;
			}
			else if(Comparators.naturalCompare(path, node.filename) > 0)
			{
				if(node.right == null)
					return null;
				
				node = node.right;
			}
			
			return node;
		}
	}
	
	public void inOrderPrint()
	{
		inOrderPrint(this.root);
	}
	
	private void inOrderPrint(AVLNode node)
	{
		if(node == null)
			return;
		
		inOrderPrint(node.left);
		System.out.println(node.filename);
		inOrderPrint(node.right);
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
		if(this.root == null)
			return null;
		
		AVLNode leftMost = leftMost(this.root);
		
		return new File(this.directory, leftMost.filename);
	}
	
	public File getLastPath()
	{
		if(this.root == null)
			return null;
		
		AVLNode rightMost = rightMost(this.root);
		
		return new File(this.directory, rightMost.filename);
	}
	
	public static AVLNode leftMost(AVLNode node)
	{
		if(node == null)
			return null;
		
		for(; node.left != null; node = node.left);
		
		return node;
	}
	
	public static AVLNode rightMost(AVLNode node)
	{
		if(node == null)
			return null;
		
		for(; node.right != null; node = node.right);
		
		return node;
	}
	
	public void insert(String path)
	{
		this.root = insert(this.root, path);
		this.count++;
	}
	
	private AVLNode insert(AVLNode node, String path)
	{
		if(node == null)
			return new AVLNode(path);
		
		if(Comparators.naturalCompare(path, node.filename) < 0)
		{
			node.left = insert(node.left, path);
		}
		else if(Comparators.naturalCompare(path, node.filename) > 0)
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
		
		if(balanceFactor > 1)
		{
			if(Comparators.naturalCompare(path, node.left.filename) < 0)
			{
				return rightRotate(node);
			}
			else 
			{
				node.left = leftRotate(node.left);
				return rightRotate(node);
			}
		}
		
		if(balanceFactor < -1) 
		{
			if(Comparators.naturalCompare(path, node.right.filename) > 0)
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
	
	
	public static int getMax(int a, int b)
	{
		if(a > b)
			return a;
		return b;
	}
	
	
	public static int getHeight(AVLNode node)
	{
		if(node == null)
			return 0;
		
		return node.height;
	}
	
	public static int getBalanceFactor(AVLNode node)
	{
		if(node == null)
			return 0;
		
		return getHeight(node.left) - getHeight(node.right);
	}
	
	public static AVLNode rightRotate(AVLNode node)
    {
        AVLNode newRoot   = node.left;
        node.left      = newRoot.right;
        newRoot.right  = node;
        node.height    = getMax(   getHeight(node.left),    getHeight(node.right)) + 1;
        newRoot.height = getMax(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }
	
    public static AVLNode leftRotate(AVLNode node)
    {
        AVLNode newRoot   = node.right;
        node.right     = newRoot.left;
        newRoot.left   = node;
        node.height    = getMax(   getHeight(node.left),    getHeight(node.right)) + 1;
        newRoot.height = getMax(getHeight(newRoot.left), getHeight(newRoot.right)) + 1;
        return newRoot;
    }
}
