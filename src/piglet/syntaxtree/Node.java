//
// Generated by JTB 1.3.2
//

package piglet.syntaxtree;

/**
 * The interface which all syntax tree classes must implement.
 */
public interface Node extends java.io.Serializable {
   public void accept(piglet.visitor.Visitor v);
   public <R,A> R accept(piglet.visitor.GJVisitor<R,A> v, A argu);
   public <R> R accept(piglet.visitor.GJNoArguVisitor<R> v);
   public <A> void accept(piglet.visitor.GJVoidVisitor<A> v, A argu);
}

