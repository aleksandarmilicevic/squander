package examples.bintree;

import kodkod.engine.satlab.SATFactory;

import org.junit.Test;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

@SpecField("nodes : set BinTreeNode | this.nodes = this.root.*(left+right) - null")
public class BinTree {

	private BinTreeNode root;

	@Requires("nodeToRemove in this.nodes")
    @Ensures("this.nodes = @old(this.nodes) - nodeToRemove")
    @Modifies("this.root, BinTreeNode.left, BinTreeNode.right")
    public void removeNode(BinTreeNode nodeToRemove) {
        Squander.exe(this, new Class<?>[] {BinTreeNode.class}, new Object[] {nodeToRemove});
    }
 
	@Test
	public void test_removeNode() {
		
		SquanderGlobalOptions.INSTANCE.sat_solver = SATFactory.MiniSat;
		
		BinTreeNode n0 = new BinTreeNode();
		BinTreeNode n1 = new BinTreeNode();
		BinTreeNode n2 = new BinTreeNode();
		BinTreeNode n3 = new BinTreeNode();
		BinTreeNode n4 = new BinTreeNode();
		BinTreeNode n5 = new BinTreeNode();
		BinTreeNode n6 = new BinTreeNode();
		BinTreeNode n7 = new BinTreeNode();
		BinTreeNode n8 = new BinTreeNode();
		BinTreeNode n9 = new BinTreeNode();
		
		n0.key = -5;
		n0.right = n1;
		
		n1.key= -2;
		n1.left = n2;
		n1.right = n3;
		
		n2.key = -3;
		n2.left = n4;
		
		n3.key = -1;
		n3.right = n5;
		
		n4.key = -4;
		
		n5.key = 2;
		n5.left = n6;
		n5.right = n7;
		
		n6.key = 1;
		n6.left = n8;
		
		n7.key = 4;
		n7.left = n9;
		
		n8.key = 0;
		
		n9.key = 3;
		
		BinTreeNode parent = n7;
		for (int i=5; i<15; i++ ) {
			BinTreeNode node = new BinTreeNode();
			node.key = i;
			parent.right = node;
			parent = node;
		}
		
		BinTree bt = new BinTree();
		bt.root = n0;
		
		System.out.println("PRE:");
		System.out.println(bt.toString());
		
		bt.removeNode(n2);

		System.out.println("POST:");
		System.out.println(bt.toString());

		
	}

	public String toString() {
		return printInOrder(this.root);
	}

	private String printInOrder(BinTreeNode node) {
		if (node==null)
			return "null";
		else
			return "{" + node.key + "(" + printInOrder(node.left) + "," + printInOrder(node.right) + ")" + "}";
	}
	
}
