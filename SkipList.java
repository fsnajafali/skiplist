// Fatemah Najafali
// June 2019

// This code implement probabilistic SkipLists, which has the Node class to keep
// track of each node and the SkipList class for insertion, deletion and search
// algorithms.

import java.util.*;
import java.io.*;

// This is a Node class that is used in the SkipList class
// Keeps track of height, data and next references for each node
class Node<T>
{
	private T data;
	ArrayList<Node<T>> nextNode = new ArrayList<>();
	private int height;

	// Constructor
	// Add null to the next reference and setting height that is passed as parameter
	Node(int height)
	{
		this.height = height;

		for (int i = 0; i < height; i++)
			this.nextNode.add(null);
	}

	// Constructor
	// Add null to the next reference and setting height and data that is passed as parameter
	Node(T data, int height)
	{
		this.data = data;
		this.height = height;

		for (int i = 0; i < height; i++)
			this.nextNode.add(null);
	}

	// Returns the value of the node
	public T value()
	{
		return this.data;
	}

	// Returns the height of the node
	public int height()
	{
		return this.height;
	}

	// Returns a reference to the next node at the particular level
	public Node<T> next(int level)
	{
		// Making sure level is inbounds
		if (level < 0 || level > (this.height-1))
			return null;

		return this.nextNode.get(level);
	}

	// Set the next reference at the given level
	public void setNext(int level, Node<T> node)
	{
		 this.nextNode.set(level, node);
	}

	// Grows the height on the node by 1
	public void grow()
	{
		this.nextNode.add(this.height, null);
		this.height += 1;
	}

	// Increases the height of the node with a probability of 50%
	public boolean maybeGrow()
	{
		if (Math.random() < 0.5)
		{
			grow();
			return true;
		}

		return false;
	}

	// Trims the height of the node, used for delete()
	public void trim(int height)
	{
		for (int i = height; i < this.height; i++)
			this.nextNode.set(i, null);

		this.height = height;
	}
}

// Uses the Node class to insert and delete nodes into the SkipList
public class SkipList<AnyType extends Comparable<AnyType>>
{
	private Node<AnyType> head;
	private int height;
	private int size;

	// Constructor, the height starts at 1
	SkipList()
	{
		this.height = 1;
		this.head = new Node<>(height);
	}

	// Constructor, uses the height passed in tne parameter
	SkipList(int height)
	{
		this.height = height;
		this.head = new Node<>(height);
	}

	// Returns the size of the SkipList
	public int size()
	{
		return this.size;
	}

	// Returns the height of the SkipList
	public int height()
	{
		return this.height;
	}

	// Returns the head of the SkipList
	public Node<AnyType> head()
	{
		return this.head;
	}

	// New node that is inserted has a randomized height
	public void insert(AnyType data)
	{
		insert(data, generateRandomHeight(getMaxHeight(size+1)));
	}

	// Inserts a node with a given height in the SkipList
	public void insert(AnyType data, int height)
	{
		this.size++;

		int newHeight = getMaxHeight(this.size);

		// Grow SkipList if the height is same as new height
		if (this.height < newHeight)
		{
			this.height = newHeight;
			growSkipList();
		}

		int level = this.height - 1;

		Node<AnyType> current = this.head;
		Node<AnyType> nextNodeReference = this.head.next(level);

		// Keeps track of the node reference in each level
		LinkedList<Node<AnyType>> update = new LinkedList<>();

		// Go through each level and compare data to see where the node can be inserted
		while (level >= 0)
		{
			if (nextNodeReference == null || (nextNodeReference.value()).compareTo(data) >= 0)
			{
				update.addFirst(current);
				level--;
			}

			else if ((nextNodeReference.value()).compareTo(data) < 0)
			{
				current = nextNodeReference;
				nextNodeReference = current.next(level);
			}
			nextNodeReference = current.next(level);
		}

		// New node that is inserted with a given height
		Node<AnyType> newNode = new Node<>(data, height);

		// Adjust node refereneces after new node is inserted
		int newLevel = height - 1;
		while (newLevel >= 0)
		{
			newNode.setNext(newLevel, update.get(newLevel).next(newLevel));
			update.get(newLevel).setNext(newLevel, newNode);
			newLevel--;
		}
	}

	// Deletes a node from the SkipList, then trims the SkipList if needed
	public void delete(AnyType data)
	{
		int level = this.height - 1;

		Node<AnyType> current = this.head;
		Node<AnyType> nextNodeReference = this.head.next(level);

		// Keeps track of the node reference in each level
		LinkedList<Node<AnyType>> update = new LinkedList<>();

		// Looking for the value that needs to be deleted using the levels
		while (level >= 0)
		{
			if (nextNodeReference == null || (nextNodeReference.value()).compareTo(data) > 0)
			{
				level--;
			}
			else if ((nextNodeReference.value()).compareTo(data) < 0)
			{
				current = nextNodeReference;
				nextNodeReference = current.next(level);
			}
			else
			{
				update.addFirst(current);
				level--;
			}
			nextNodeReference = current.next(level);
		}

		// Adjust the levels in the SkipList after deletion
		if (nextNodeReference != null && (nextNodeReference.value()).compareTo(data) == 0)
		{
			this.size--;

			int newHeight = getMaxHeight(this.size);

			// Trim SkipList if height is same as new height
			if (newHeight < this.height)
			{
				this.height = newHeight;
				this.trimSkipList();
			}

			int newLevel = height - 1;
			while (newLevel >= 0)
			{
				update.get(newLevel).setNext(newLevel, nextNodeReference.next(newLevel));
				newLevel--;
			}
		}
	}

	// Return true if the SkipList contains data. Otherwise, return false
	public boolean contains(AnyType data)
	{
		int level = height - 1;

		Node<AnyType> current = this.head;
		Node<AnyType> nextNodeReference = this.head.next(level);

		// Checking which node contains data
		while (level >= 0)
		{
			if (nextNodeReference == null || (nextNodeReference.value()).compareTo(data) > 0)
			{
				level--;
			}
			else if ((nextNodeReference.value()).compareTo(data) < 0)
			{
				current = nextNodeReference;
				nextNodeReference = current.next(level);
			}
			else
			{
				return true;
			}
			nextNodeReference = current.next(level);
		}

		return false;
	}

	// Return a reference to a node in the SkipList that contains data,
	// if no such node exists, return null
	public Node<AnyType> get(AnyType data)
	{
		int level = height - 1;

		Node<AnyType> current = this.head;
		Node<AnyType> nextNodeReference = this.head.next(level);

		// Checking which node contains data
		while (level >= 0)
		{
			if (nextNodeReference == null || (nextNodeReference.value()).compareTo(data) > 0)
			{
				level--;
			}
			else if ((nextNodeReference.value()).compareTo(data) < 0)
			{
				current = nextNodeReference;
				nextNodeReference = current.next(level);
			}
			else
			{
				return current;
			}
			nextNodeReference = current.next(level);
		}

		return null;
	}

	// Genarates the random height of the node
	// Returns 1 with 50% probability, 2 with 25% probability, 3 with 12.5% probability, and so on, without
	// exceeding maxHeight
	private static int generateRandomHeight(int maxheight)
	{
		int height = 1;
		while (Math.random() < 0.5 && height < maxheight)
			height++;

		return height;
	}

	// Returns the max height of the node
	private static int getMaxHeight(int n)
	{
		return (int)Math.ceil((Math.log(n) / Math.log(2)));
	}

	// Grows the SkipList by increasing the height of the nodes
	// Used for the insert()
	private void growSkipList()
	{
		int level = this.height - 1;
		int newLevel = level - 1;

		// Head node will always have the max height
		this.head.grow();

		Node<AnyType> current = this.head;
		Node<AnyType> nextNodeReference = current.next(newLevel);

		while (nextNodeReference != null)
		{
			if (nextNodeReference.maybeGrow())
			{
				current.setNext(level, nextNodeReference);
				current = current.next(level);
			}

			nextNodeReference = nextNodeReference.next(newLevel);
		}
	}

	// Trims the SkipList by trimming the height of the node
	private void trimSkipList()
	{
		int level = this.height - 1;

		Node<AnyType> current = this.head;
		Node<AnyType> placeNode = this.head;

		while (current != null)
		{
			placeNode = current.next(level);
			current.trim(this.height);
			current = placeNode;
		}
	}

	// Returns the difficulty rating for the assignment
	public static double difficultyRating()
	{
		return 4.5;
	}

	// Returns the hours spent on the assignment
	public static double hoursSpent()
	{
		return 25;
	}
}
