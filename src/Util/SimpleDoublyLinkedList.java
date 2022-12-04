package Util;

public class SimpleDoublyLinkedList<T> 
{
	public SimpleDoublyLinkedList<T>  next;
	public SimpleDoublyLinkedList<T>  prev;
	public T node;
	
	public SimpleDoublyLinkedList()
	{
		
	}
	
	public SimpleDoublyLinkedList(T node)
	{
		this.node = node;
	}
	
	/**
	 * sets this link's next pointer to a new @SimpleDoublyLinkedList 
	 */
	public SimpleDoublyLinkedList<T> createNext()
	{
		this.next = new SimpleDoublyLinkedList<T>();
		this.next.prev = this;
		return this.next;
	}
	
	/**
	 * sets this link's next pointer to a new @SimpleDoublyLinkedList with the given node
	 */
	public SimpleDoublyLinkedList<T> createNext(T node)
	{
		this.next = new SimpleDoublyLinkedList<T>(node);
		this.next.prev = this;
		return this.next;
	}
	
	public String toString()
	{
		return this.node.toString();
	}
}
