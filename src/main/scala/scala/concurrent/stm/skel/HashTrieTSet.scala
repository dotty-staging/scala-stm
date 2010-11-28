/* scala-stm - (c) 2009-2010, Stanford University, PPL */

package scala.concurrent.stm
package skel

private[stm] class HashTrieTSet[A] private (private val root: Ref.View[TxnHashTrie.SetNode[A]]) extends TSetViaClone[A] {

  def this() = this(Ref(TxnHashTrie.emptySetNode[A]).single)

  def this(xs: TraversableOnce[A]) = this(Ref(TxnHashTrie.buildSet(xs)).single)

  override def empty: TSet.View[A] = new HashTrieTSet[A]()

  override def clone(): HashTrieTSet[A] = new HashTrieTSet(TxnHashTrie.clone(root))

  override def isEmpty: Boolean = !TxnHashTrie.sizeGE(root, 1)

  override def size: Int = TxnHashTrie.size(root)

  override def iterator: Iterator[A] = TxnHashTrie.setIterator(root)

  override def foreach[U](f: A => U) { TxnHashTrie.setForeach(root, f) }

  def contains(elem: A): Boolean = TxnHashTrie.contains(root, elem)

  override def add(elem: A): Boolean = TxnHashTrie.put(root, elem, null).isEmpty

  override def += (elem: A): this.type = { TxnHashTrie.put(root, elem, null) ; this }

  override def remove(elem: A): Boolean = !TxnHashTrie.remove(root, elem).isEmpty

  override def -= (elem: A): this.type = { TxnHashTrie.remove(root, elem) ; this }

  override def clear() { root() = TxnHashTrie.emptySetNode[A] }
}